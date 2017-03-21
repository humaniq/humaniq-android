package co.humaniq.faceenginemobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import co.humaniq.faceenginemobile.bindings.ImageView;
import co.humaniq.faceenginemobile.bindings.PhotoMaker;
import co.humaniq.faceenginemobile.bindings.Rect;


public class PhotoProcessor {

    public enum EyeCheckStage {
        WAIT_OPEN1,
        WAIT_CLOSED,
        WAIT_OPEN2,
        DONE
    }

    private final PhotoMaker photoMaker = new PhotoMaker();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Object lock = new Object();

    private int bestShotHeight;
    private int bestShotWidth;

    private ByteBuffer buffer;
    private volatile boolean found = false;
    private volatile boolean checkEyes = false;
    private EyeCheckStage eyesCheckStage;
    private volatile int eyesClosenessCounter = 0;
    private byte[] previewInRGBAFormat;
    private int[] previewInARGBFormat;
    private int[] tempArray;

    private byte[] previewYData;

    private android.graphics.Rect lastFaceBound = null;
    private AtomicBoolean flashTorchEnabled = new AtomicBoolean(false);
    private long luminanceStateCheckingLastTime = -1;

    private RenderScript renderScript;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Allocation renderScriptInAllocation;
    private Allocation renderScriptOutAllocation;

    private int previewWidth;
    private int previewHeight;
    private byte[] firstCallbackBuffer;
    private byte[] secondCallbackBuffer;
    float[] lastScores;
    private IntBuffer bestShotData;
    private boolean needPortrait = false;
    @Nullable
    private Listener listener;
    @Nullable
    private DebugListener debugListener;
    private int imageRotation;
    private boolean mainCamera;
    private final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            PhotoProcessor.this.work();
        }
    }, "photoProcessor");

    /**
     * @apiNote Объект должен создаваться используя PhotoProcessor.Builder
     */
    private PhotoProcessor(@NonNull Context context) {
        thread.start();
        photoMaker.reset();
        photoMaker.setStopAfterBestShot(false);
        renderScript = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));
    }

    public class LuminanceState
    {
        public LuminanceState(int darknessState, boolean isOverLight)
        {
            this.darknessState = darknessState;
            this.isOverLight = isOverLight;
        }

        public int darknessState;
        public boolean isOverLight;
    }

    /**
     * Поток обработки изображения. Выполняет:
     * 1 преобразование из NV21 в RGBA
     * 2 поворот изображения (превью обычно приходит перевернутым)
     * 3 передачу изображения в PhotoMaker
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void work() {
        while (!thread.isInterrupted()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    break;
                }

                if (thread.isInterrupted()) {
                    break;
                }
                convertToRGBA(secondCallbackBuffer);

                System.arraycopy(secondCallbackBuffer, 0, previewYData, 0, previewYData.length);
            }

            long start = System.currentTimeMillis();

            Utils.byteToIntArray(previewInRGBAFormat, previewInARGBFormat);

            int width = previewWidth;
            int height = previewHeight;

            if ((imageRotation == 90 && !mainCamera) || (mainCamera && imageRotation == 270)) {
                Utils.rotateClockwise90(previewInARGBFormat, tempArray, previewWidth, previewHeight);
                Utils.flip(previewInARGBFormat, previewWidth, previewHeight);
                width = previewHeight;
                height = previewWidth;
            }

            if ((imageRotation == 180 && !mainCamera) || (mainCamera && imageRotation == 0)) {
                Utils.flip(previewInARGBFormat, previewWidth, previewHeight);
            }

            if ((imageRotation == 270 && !mainCamera) || (mainCamera && imageRotation == 90)) {
                Utils.rotateClockwise90(previewInARGBFormat, tempArray, previewWidth, previewHeight);
                width = previewHeight;
                height = previewWidth;
            }

            buffer.clear();
            IntBuffer intBuffer = buffer.asIntBuffer();
            intBuffer.put(previewInARGBFormat);

            sendFrameToPhotoMaker(start, width, height);

            if (null != lastFaceBound) {
                processFrameLuminance(lastFaceBound);
            }
        }
    }

    public void setFlashTorchState(boolean enabled)
    {
        flashTorchEnabled.set(enabled);
    }

    /**
     * Оценивает примерное состояние освещенности (0 - темно, 1 - светло)
     */

    private LuminanceState calcLuminanceState(android.graphics.Rect rect) {

        final android.graphics.Rect correctedRect = new android.graphics.Rect();

        correctedRect.set(Math.max(0, rect.left), Math.max(0, rect.top), Math.min(rect.right, previewWidth), Math.min(rect.bottom, previewHeight));

        // TODO:

        boolean isFlashTorchEnabled = flashTorchEnabled.get();

        double lowerRate = isFlashTorchEnabled ? 0.05 : 0.15;

        int nonBlackPixelsCount = Utils.countUpper(previewYData, previewWidth, correctedRect, 20);

        int overLightCount = Utils.countUpper(previewYData, previewWidth, correctedRect, 220);
        int blackPixelsCount = Utils.countLower(previewYData, previewWidth, correctedRect, 20);

        int rectPixelsCount = (correctedRect.right - correctedRect.left) * (correctedRect.bottom - correctedRect.top);

        int darknessState = blackPixelsCount >= (int)(rectPixelsCount * lowerRate) ? 0 : 1;
        boolean isOverLight = overLightCount > (int)(nonBlackPixelsCount * 0.07);

        return new LuminanceState(darknessState, isOverLight);
    }

    /**
     * Передаем изображение в VL Face Engine
     * После окончания обработки передаем через механизм handler'ов результаты
     *
     * @param start  для отладочных целей
     * @param width  ширина изображения
     * @param height высота изображения
     */

    private void sendFrameToPhotoMaker(long start, int width, int height) {
        final ImageView frame = new ImageView(buffer, width, height);
        try {
            photoMaker.submit(frame);
            photoMaker.update();
            final long delay = System.currentTimeMillis() - start;
            if (debugListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (debugListener != null) {
                            debugListener.onFrameProcessed(delay);
                        }
                    }
                });
            }
            updateArea();

            if (photoMaker.haveBestShot() && !found) {
                found = true;
                processBestShot();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFrameLuminance(android.graphics.Rect rect) {

        // TODO: do via post

        if (luminanceStateCheckingLastTime == -1 || System.currentTimeMillis() - luminanceStateCheckingLastTime > 2000) {
            luminanceStateCheckingLastTime = System.currentTimeMillis();

            final LuminanceState luminanceState = calcLuminanceState(rect);

            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onLuminanceState(luminanceState);
                        }
                    }
                });
            }
        }
    }

    /**
     * Подписываемся на уведомления о длительности обработки фрейма
     *
     * @param debugListener будет вызван в UI потоке когда закончится обработка фрейма
     */

    public void setDebugListener(@Nullable DebugListener debugListener) {
        this.debugListener = debugListener;
    }

    /**
     * Подписываемся на уведомления и начинаем поиск лучшего кадра
     *
     * @param listener будет вызван в UI потоке когда закончится обработка фрейма
     */
    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
        photoMaker.reset();
        resumeSearch();
    }

    /**
     * Продолжаем поиск лучшего кадра
     * Данный метод может понадобиться в случае когда кроме определения лица необходимо чтобы это лицо находилось в определенной области
     */
    public void resumeSearch() {
        found = false;
        checkEyes = false;
    }

    /**
     * Старт проверки liveness
     */
    public void startCheckLiveness() {
        checkEyes = true;
        eyesCheckStage = EyeCheckStage.WAIT_OPEN1;
        eyesClosenessCounter = 0;
    }

    /**
     * Отписываемcя от уведомлений
     */
    public void removeListeners() {
        listener = null;
        debugListener = null;
        handler.removeCallbacksAndMessages(null);
    }

    /** Возвращает ширину обрабатываемого кадра заданную методом setPreviewSize
     * @return ширина обрабатываемого кадра
     */
    public int getPreviewWidth() {
        return previewWidth;
    }

    /** Возвращает высоту обрабатываемого кадра заданную методом setPreviewSize
     * @return высота обрабатываемого кадра
     */
    public int getPreviewHeight() {
        return previewHeight;
    }

    /**
     * Инициализируем VL face engine
     *
     * @param path путь до данных VL face engine
     * @return true если библиотека успешно загружена, false если не удалось загрузить данные
     */
    private boolean loadData(String path) {
        photoMaker.load(path);
        return photoMaker.isLoaded();
    }

    /**
     * Задаем размер кадра, который будет обрабатываться библиотекой
     *
     * @param width  ширина кадра
     * @param height высота кадра
     */
    public void setPreviewSize(int width, int height) {
        previewWidth = width;
        previewHeight = height;

        final int previewNV21ArraySize = previewWidth * previewHeight * 3 / 2;
        final int previewRGBAArraySize = previewWidth * previewHeight * 4;
        previewInARGBFormat = new int[previewWidth * previewHeight];

        tempArray = new int[previewInARGBFormat.length];

        renderScriptAllocate(previewNV21ArraySize);

        previewInRGBAFormat = new byte[previewRGBAArraySize];
        firstCallbackBuffer = new byte[previewNV21ArraySize];
        secondCallbackBuffer = new byte[previewNV21ArraySize];
        previewYData = new byte[previewWidth * previewHeight];

        buffer = ByteBuffer.allocateDirect(previewRGBAArraySize).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Освобождает используемые ресурсы
     * Останавливает поток обработки информации
     */
    public void release() {
        listener = null;
        handler.removeCallbacksAndMessages(null);
        thread.interrupt();
        renderScriptInAllocation.destroy();
        renderScriptOutAllocation.destroy();
        renderScript.destroy();
        yuvToRgbIntrinsic.destroy();
    }

    /**
     * Анализ кадра с камеры
     * Неблокирующий метод
     *
     * @param data данные с камеры (к примеру результат onPreviewFrame) изображение в формате NV21
     */
    public void processFrame(byte[] data) {
        if (!found || checkEyes) {
            synchronized (lock) {
                System.arraycopy(data, 0, secondCallbackBuffer, 0, secondCallbackBuffer.length);
                lock.notify();
            }
        }
    }

    /**
     * C помощью RenderScript выполняем преобразование NV21 -> RGBA на GPU
     *
     * @param data кадр с камеры в формате NV21
     */
    private void convertToRGBA(byte[] data) {
        renderScriptInAllocation.copyFrom(data);

        yuvToRgbIntrinsic.setInput(renderScriptInAllocation);
        yuvToRgbIntrinsic.forEach(renderScriptOutAllocation);

        renderScriptOutAllocation.copyTo(previewInRGBAFormat);
    }

    /**
     * Инициализация RenderScript
     *
     * @param previewNV21ArraySize размер массива который нужно будет обрабатывать
     */
    private void renderScriptAllocate(int previewNV21ArraySize) {
        Type.Builder yuvType = new Type.Builder(renderScript, Element.U8(renderScript))
                .setX(previewNV21ArraySize);
        renderScriptInAllocation = Allocation.createTyped(renderScript, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(renderScript, Element.RGBA_8888(renderScript))
                .setX(previewWidth)
                .setY(previewHeight);
        renderScriptOutAllocation = Allocation.createTyped(renderScript, rgbaType.create(), Allocation.USAGE_SCRIPT);
    }

    private void processEyesState()
    {
        int eyesState = photoMaker.getEyesState();

        if (eyesCheckStage == EyeCheckStage.WAIT_OPEN1 && eyesState == 2)
        {
            eyesCheckStage = EyeCheckStage.WAIT_CLOSED;
            return;
        }

        if (eyesCheckStage == EyeCheckStage.WAIT_CLOSED && eyesState == 1)
        {
            eyesCheckStage = EyeCheckStage.WAIT_OPEN2;
            return;
        }

        if (eyesCheckStage == EyeCheckStage.WAIT_OPEN2 && eyesState == 2)
        {
            eyesCheckStage = EyeCheckStage.DONE;
            return;
        }
    }
    /**
     * Обработка результатов работы Photo Maker'a
     * Результаты будеут переданы слушателям в UI потоке
     */
    private void updateArea() {
        if (photoMaker.haveFaceDetection()) {
            final Rect faceDetection = photoMaker.getFaceDetection();

            final android.graphics.Rect rect = new android.graphics.Rect(faceDetection.getLeft(),
                    faceDetection.getTop(), faceDetection.getBottom(), faceDetection.getRight());

            if (lastFaceBound == null) {
                lastFaceBound = new android.graphics.Rect();
            }

            lastFaceBound.set(rect);

            final boolean fastMovement = !photoMaker.isSlowMovement();
            final boolean isFrontalPose = photoMaker.isFrontalPose();

            if (checkEyes) {
                if (fastMovement || !isFrontalPose)
                {
                    eyesCheckStage = EyeCheckStage.WAIT_OPEN1;
                }
                processEyesState();
               // lastScores = photoMaker.getScores();

                if (eyesCheckStage == EyeCheckStage.DONE)
                {
                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onLivenessSucceed();
                                }
                            }
                        });
                    }
                }
            }

            //final float [] scores = lastScores != null? new float[6] : null;

            //if (lastScores != null) {
                //System.arraycopy(lastScores, 0, scores, 0, 6);
            //}

            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFaceArea(true, rect, fastMovement, isFrontalPose);
                        }
                    }
                });
            }
        } else {
            if (checkEyes) {
                eyesCheckStage = EyeCheckStage.WAIT_OPEN1;
            }

            lastFaceBound = null;

            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFaceArea(false, null, null, null);
                        }
                    }
                });
            }
        }

    }

    /** Возвращает массив байт, который может быть использован в Camera API для получения изображения
     * @return массив байт для размещения preview
     */
    public byte[] getCallbackBuffer() {
        return firstCallbackBuffer;
    }

    /**
     * Получаем из библиотеки лучшее изображение, преобразуем его в ARGB и сообщаем слушателю, что изображение готово
     */
    private void processBestShot() {
        final ImageView imageView = needPortrait ? photoMaker.getBestShot() : photoMaker.getBestFrame();
        bestShotHeight = imageView.getHeight();
        bestShotWidth = imageView.getWidth();

        final ByteBuffer bestShotByteBuffer = ByteBuffer.allocateDirect(bestShotHeight * bestShotWidth * 4);
        imageView.getRecPixels(bestShotByteBuffer);
        imageView.delete();

        byte[] res = new byte[bestShotByteBuffer.capacity()];

        bestShotByteBuffer.position(0);
        bestShotByteBuffer.get(res);

        int[] result = new int[bestShotHeight * bestShotWidth];
        Utils.byteToIntArray(res, result);

        bestShotData = IntBuffer.wrap(result);
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onBestShotReady();
                    }
                }
            });
        }
    }

    /** Возвращает лучший кадр или null в случае если лучший кадр не был получен
     * @return Лучший кадр или null в случае если лучший кадр не был получен
     */
    @Nullable
    public Bitmap getBestShot() {
        final Bitmap bestShotBitmap = Bitmap.createBitmap(bestShotWidth, bestShotHeight, Bitmap.Config.ARGB_8888);
        bestShotData.position(0);
        bestShotBitmap.copyPixelsFromBuffer(bestShotData);
        return bestShotBitmap;
    }

    public void setNeedPortrait(boolean value) {
        needPortrait = value;
    }

    /** Передаем информацию о том на какой угол повернуто изображение
     * @param imageRotation угол на который повернуто изображение
     */
    public void setImageRotation(int imageRotation) {
        this.imageRotation = imageRotation;
    }

    /** Передаем информацию о том какая камера используется для получения снимков
     * @param mainCamera если изображение получено с основной камеры - true
     *                   если изображение получено с фронтальной камеры - false
     */
    public void setMainCamera(boolean mainCamera) {
        this.mainCamera = mainCamera;
    }

    /**
     * Set maximum head rotation about all three coordiante axes.
     * If head rotation angles do not exceed the given limit, the
     * head pose is considered frontal, thus eligible for the best
     * shot procedure.
     * Absolute value should be specified; this will allow
     * rotations within [-rotationLimit, rotationLimit] range.
     * Default is 10 degrees.
     *
     * @param rotationLimit rotation limit (in degrees).
     */
    private void setRotationLimit(float rotationLimit) {
        photoMaker.setRotationThreshold(rotationLimit);
    }

    /**
     * Set minimum movement value to prevent blure shots
     *
     * @param movementThreshold movement threshold.
     */
    private void setMovementThreshold(float movementThreshold) {
        photoMaker.setMovementThreshold(movementThreshold);
    }

    /**
     * Set detector's minimum confidence score value
     *
     * @param confidenceScore confidence score.
     */
    private void setConfidenceScore(float confidenceScore) {
        photoMaker.setConfidenceScore(confidenceScore);
    }

    /**
     * Set minimum best shot detection score value.
     *
     * @param scoreThreshold score value.
     */
    private void setBestShotScoreThreshold(float scoreThreshold) {
        photoMaker.setBestShotScoreThreshold(scoreThreshold);
    }

    /**
     * Set maximum portrait (for best shot) height.
     *
     * @param height - portrait (best shot) max height.
     */
    private void setPortraitMaxHeight(int height) {
        photoMaker.setPortraitMaxHeight(height);
    }

    /**
     * Set minimum best shot detection score value.
     *
     * @param enabled score value.
     */
    private void setSaveBestFrameEnabled(boolean enabled) {
        photoMaker.setSaveBestFrameEnabled(enabled);
    }

    /**
     * Set maximum number of frames to keep track of the face when good face
     * detection is not available.
     * Tracking is used to predict fac position in case of obscurrance and/or
     * rapid movements of the face.
     *
     * @param numberOfFrames number of frames to track.
     */
    private void setMaxNumberOfFramesWithoutDetection(int numberOfFrames) {
        photoMaker.setMaxNumberOfFramesWithoutDetection(numberOfFrames);
    }

    /**
     * Set scale factor for input video frames.
     * Used to downscale input image to improve processing speed.
     *
     * @param scaleFactor the scale factor; shoud be within (0, 1] range.
     */
    private void setScaleFactor(float scaleFactor) {
        photoMaker.setFrameScaleFactor(scaleFactor);
    }

    /**
     * listener который будет вызван после окончания обработки кадра
     */
    public interface Listener {
        /**
         * listener который будет вызван после окончания обработки кадра
         *
         * @param detected true если на кадре было обнаружено лицо, false если лицо не было обнаружено
         *                 если лицо не было обнаружено в кадре - остальные параметры - NULL
         * @param rect     Если лицо было обнаружено, то зона в которой было обнаружено лицо
         * @param fastMove Если лицо было обнаружено: если было зафиксировано быстрое движение и кадр может быть смазан - true
         * @param rotate   Если лицо былы обнаружено в кадре и зафиксирован поворот головы
         */

        void onFaceArea(boolean detected, @Nullable android.graphics.Rect rect, @Nullable Boolean fastMove, @Nullable Boolean rotate);

        /**
         * listener который будет вызван после получения лучшего кадра
         */
        void onBestShotReady();

        /**
         * listener который будет вызван после окончания проверки liveness
         */
        void onLivenessSucceed();

        /**
         * listener который передает новое состояние освещенности
         */
        void onLuminanceState(PhotoProcessor.LuminanceState state);
    }

    /**
     * listener который будет вызван после окончания обработки кадра
     * может быть использован для оценки производительности VL Face Engine
     */
    public interface DebugListener {
        /**
         * будет вызван после окончания обработки кадра
         * @param duration время в ms потраченное на обработку кадра
         */
        void onFrameProcessed(long duration);
    }

    /**
     * Билдер для создания сконфигурированного PhotoProcessor'a
     */
    public static class Builder {
        private float scaleFactor = 0.5f;
        private int numberOfFrames = 1;
        private float scoreThreshold = 0.05f;
        private float confidenceScore = 0.01f;
        private float movementThreshold = 0.06f;
        private float rotationLimit = 15.0f;
        private int portraitMaxHeight = 640;
        private boolean saveBestFrameEnabled = true;
        private String path;

        /**
         * Создает PhotoProcessor.Builder для создания PhotoProcessor'a с различными настройками
         */
        public Builder() {

        }

        /**
         * Set scale factor for input video frames.
         * Used to downscale input image to improve processing speed.
         *
         * @param scaleFactor the scale factor; shoud be within (0, 1] range.
         */
        public Builder frameScaleFactor(float scaleFactor) {
            this.scaleFactor = scaleFactor;
            return this;
        }

        /**
         * Set maximum number of frames to keep track of the face when good face
         * detection is not available.
         * Tracking is used to predict fac position in case of obscurrance and/or
         * rapid movements of the face.
         *
         * @param numberOfFrames number of frames to track.
         */
        public Builder maxNumberOfFramesWithoutDetection(int numberOfFrames) {
            this.numberOfFrames = numberOfFrames;
            return this;
        }

        /**
         * Set minimum best shot detection score value.
         *
         * @param scoreThreshold score value.
         */
        public Builder bestShotScoreThreshold(float scoreThreshold) {
            this.scoreThreshold = scoreThreshold;
            return this;
        }

        /**
         * Set minimum movement value to prevent blure shots
         *
         * @param movementThreshold movement threshold.
         */
        public Builder movementThreshold(float movementThreshold) {
            this.movementThreshold = movementThreshold;
            return this;
        }

        /**
         * Set maximum head rotation about all three coordiantes axes.
         * If head rotation angles do not exceed the given limit, the
         * head pose is considered frontal, thus eligible for the best
         * shot procedure.
         * Absolute value should be specified; this will allow
         * rotations within [-rotationLimit, rotationLimit] range.
         * Default is 10 degrees.
         *
         * @param rotationLimit rotation limit (in degrees).
         */
        public Builder rotationLimit(float rotationLimit) {
            this.rotationLimit = rotationLimit;
            return this;
        }

        /**
         * Set path to VL engine files
         *
         * @param path path to VL engine files
         */
        public Builder pathToData(String path) {
            this.path = path;
            return this;
        }

        /**
         * Создает сконфигурированный PhotoProcessor
         */
        public PhotoProcessor build(Context context) {
            final PhotoProcessor photoProcessor = new PhotoProcessor(context);
            if (TextUtils.isEmpty(path)) {
                photoProcessor.loadData(context.getFilesDir() + "/vl/data");
            } else {
                photoProcessor.loadData(path);
            }
            photoProcessor.setScaleFactor(scaleFactor);
            photoProcessor.setMaxNumberOfFramesWithoutDetection(numberOfFrames);
            photoProcessor.setBestShotScoreThreshold(scoreThreshold);
            photoProcessor.setMovementThreshold(movementThreshold);
            photoProcessor.setConfidenceScore(confidenceScore);
            photoProcessor.setRotationLimit(rotationLimit);
            photoProcessor.setPortraitMaxHeight(portraitMaxHeight);
            photoProcessor.setSaveBestFrameEnabled(saveBestFrameEnabled);
            return photoProcessor;
        }

    }
}
