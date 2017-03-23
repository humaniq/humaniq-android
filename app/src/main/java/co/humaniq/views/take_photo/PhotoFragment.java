package co.humaniq.views.take_photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.humaniq.App;
import co.humaniq.Config;
import co.humaniq.R;
import co.humaniq.Preferences;
import co.humaniq.VLUtils;
import co.humaniq.views.widgets.FaceBoundSurfaceView;
import ru.visionlab.faceenginemobile.PhotoProcessor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


public class PhotoFragment extends Fragment implements Camera.PreviewCallback,
        PhotoProcessor.Listener, PhotoProcessor.DebugListener
{
    protected static final int TIMEOUT = 15;
    protected static final int LIVENESS_TIMEOUT = 5;

    @BindView(R.id.warning)
    TextView warning;

    @BindView(R.id.overlight)
    TextView overlight;

    @BindView(R.id.maskedView)
    ImageView mask;

    @BindView(R.id.preview)
    SurfaceView preview;

    @BindView(R.id.faceBoundView)
    FaceBoundSurfaceView faceBoundView;

    @BindView(R.id.sendPlaceholder)
    FrameLayout sendPlaceholder;

    @BindView(R.id.layout1)
    LinearLayout layout1;

    PhotoProcessor photoProcessor;

    HolderCallback holderCallback;
    Camera camera;

    int maskTop;
    int maskBottom;
    int maskLeft;
    int maskRight;
    boolean faceInGoodState;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.timelog)
    TextView timelog;
    private Listener listener;
    private boolean ignoreTimeout;
    private boolean ignoreTimeoutLiveness;

    Preferences preferences = null;

    private boolean cameraFlashIsSupported = false;
    public PhotoFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        preferences = App.getPreferences(activity);
        preferences.setStartTime(String.valueOf(System.currentTimeMillis()));

        super.onAttach(activity);
    }

    public static PhotoFragment newInstance(Context context) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        fragment.photoProcessor = new PhotoProcessor.Builder()
                .pathToData(context.getFilesDir() + VLUtils.PATH_TO_EXTRACTED_VL_DATA)
                .build(context);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void showWaitState() {
        mask.setVisibility(View.GONE);
        sendPlaceholder.setVisibility(View.VISIBLE);
        camera.stopPreview();
    }

    public void hideWaitState() {
        mask.setVisibility(preferences.getShowDetection() ? View.INVISIBLE : View.VISIBLE);
        sendPlaceholder.setVisibility(View.GONE);
    }

    private void detectMaskWidth(Bitmap bitmap) {
        boolean start = true;
        final int center = (maskTop + maskBottom) / 2;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            final int pixel = Color.alpha(bitmap.getPixel(i, center));
            if (start) {
                if (pixel == Color.TRANSPARENT) {
                    maskLeft = i;
                    start = false;
                }
            } else {
                if (pixel != Color.TRANSPARENT) {
                    maskRight = i;
                    break;
                }
            }

        }
    }

    private void detectMaskHeight(Bitmap bitmap) {
        boolean start = true;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            final int pixel = Color.alpha(bitmap.getPixel(bitmap.getWidth() / 2, i));
            if (start) {
                if (pixel == Color.TRANSPARENT) {
                    maskTop = i;
                    start = false;
                }
            } else {
                if (pixel != Color.TRANSPARENT) {
                    maskBottom = i;
                    break;
                }
            }

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                final Bitmap bitmap = ((BitmapDrawable) mask.getDrawable()).getBitmap();
                detectMaskHeight(bitmap);
                detectMaskWidth(bitmap);
            }

            @Override
            public void onError() {

            }
        };

        Picasso.with(getContext())
                .load(R.drawable.mask)
                .centerCrop()
                .fit()
                .into(mask, callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        ignoreTimeout = false;
//        final long delay = TIMEOUT * DateUtils.SECOND_IN_MILLIS - (System.currentTimeMillis() - Long.parseLong(preferences.getStartTime()));
//        Observable.timer(Math.max(delay, 0), TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(bindToLifecycle())
//                .subscribe(value -> {
//                    if (listener != null && !ignoreTimeout) {
//                        listener.onTimeout(FaceNotFoundFragment.Reason.NOT_FOUND);
//                    }
//                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            startCapture();
        }
        photoProcessor.setListener(this);
        photoProcessor.setNeedPortrait(preferences.getNeedPortrait());
        hideWaitState();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onBestShotReady(Bitmap bitmap);
//        void onTimeout(FaceNotFoundFragment.Reason reason);
        void onNeedCameraPermission();
    }

    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (camera != null) {
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            startCameraPreview(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    private void startCapture() {
        camera = Camera.open(getCameraID());
        configureCamera();
        final ViewTreeObserver viewTreeObserver = preview.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                preview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setPreviewSize(true);
                holderCallback = new HolderCallback();
                preview.getHolder().addCallback(holderCallback);
            }
        });
    }

    void setPreviewSize(boolean fullScreen) {
        final int width = preview.getMeasuredWidth();
        final int height = preview.getMeasuredHeight();
        boolean widthIsMax = width > height;

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, width, height);

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, photoProcessor.getPreviewWidth(), photoProcessor.getPreviewHeight());
        } else {
            // превью в вертикальной ориентации
            //noinspection SuspiciousNameCombination
            rectPreview.set(0, 0, photoProcessor.getPreviewHeight(), photoProcessor.getPreviewWidth());
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран
            matrix.setRectToRect(rectPreview, rectDisplay, Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью
            matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        final ViewGroup.LayoutParams layoutParams = preview.getLayoutParams();
        if (layoutParams.width != (int) rectPreview.right || layoutParams.height != (int) rectPreview.bottom) {
            layoutParams.height = (int) (rectPreview.bottom);
            layoutParams.width = (int) (rectPreview.right);
            preview.setLayoutParams(layoutParams);
        } else {
            startCameraPreview(preview.getHolder());
        }
    }

    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            photoProcessor.setMainCamera(true);
            result = ((360 - degrees) + info.orientation);
        } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            photoProcessor.setMainCamera(false);
            result = ((360 - degrees) - info.orientation);
            result += 360;
        }
        result = result % 360;
        camera.setDisplayOrientation(result);
        photoProcessor.setImageRotation(result);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        photoProcessor.processFrame(data);
        camera.addCallbackBuffer(photoProcessor.getCallbackBuffer());
    }

    @Override
    public void onFrameProcessed(long duration) {
        timelog.setText(String.format("duration: %dms", duration));
    }

    @Override
    public void onFaceArea(boolean detected, Rect rect, Boolean fastMove, Boolean isFrontalPose) {

        boolean showDetectionPreference = preferences.getShowDetection();
        boolean showDetectionRect = showDetectionPreference;

        if (detected) {
            final int width = preview.getMeasuredWidth();
            final int height = preview.getMeasuredHeight();

            float widthCorrection = ((float) width) / photoProcessor.getPreviewHeight();
            float heightCorrection = ((float) height) / photoProcessor.getPreviewWidth();

            int left = (int) (rect.left * widthCorrection);
            int top = (int) (rect.top * heightCorrection);

            int right = (int) (rect.right * widthCorrection);
            int bottom = (int) (rect.bottom * heightCorrection);

            if (showDetectionRect)
            {
                faceBoundView.setFaceRect(left, top, right, bottom);
            }

            // if detection's not shown, it must be within face mask
            faceInGoodState = showDetectionPreference || !(left < maskLeft || right > maskRight ||
                    top < maskTop || bottom > maskBottom);

            if (faceInGoodState) {
                if (!isFrontalPose) {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText(R.string.look_straight_at_the_camer);
                    faceInGoodState = false;
                } else if (fastMove) {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText(R.string.moving_too_fast);
                    faceInGoodState = false;
                } else {
                    warning.setVisibility(View.INVISIBLE);
                }
            } else {
                warning.setVisibility(showDetectionPreference ? View.INVISIBLE : View.VISIBLE);
                warning.setText(R.string.put_your_head_into_oval);
            }
        } else {
                warning.setVisibility(showDetectionPreference ? View.INVISIBLE : View.VISIBLE);
                warning.setText(R.string.put_your_head_into_oval);

            showDetectionRect = false;
        }

        faceBoundView.setVisibility(showDetectionRect ? View.VISIBLE : View.INVISIBLE);

        if (showDetectionRect) {
            faceBoundView.setFaceRectColor(faceInGoodState ? Color.GREEN : Color.RED);
            faceBoundView.invalidate();
        }
    }

    @Override
    public void onBestShotReady() {
        if (faceInGoodState && listener != null) {
            ignoreTimeout = true;

            // if liveness on, start checking it, else finish
            if (preferences.getLivenessAuth()) {
                Toast.makeText(getContext(), "Close the eyes please", Toast.LENGTH_SHORT).show();

                photoProcessor.startCheckLiveness();
                ignoreTimeoutLiveness = false;

                final long delay = LIVENESS_TIMEOUT * DateUtils.SECOND_IN_MILLIS;
//                Observable.timer(Math.max(delay, 0), TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .compose(bindToLifecycle())
//                        .subscribe(value -> {
//                            if (listener != null && !ignoreTimeoutLiveness) {
//                                listener.onTimeout(FaceNotFoundFragment.Reason.LIVENESS);
//                            }
//                        });

            } else {
                submitBestShot();
            }
        } else {
            photoProcessor.resumeSearch();
        }
    }

    @Override
    public void onLivenessSucceed() {
        submitBestShot();
    }

    private void submitBestShot()
    {
        listener.onBestShotReady(photoProcessor.getBestShot());
    }

    @Override
    public void onLuminanceState(PhotoProcessor.LuminanceState state)
    {
        overlight.setVisibility(state.isOverLight ? View.VISIBLE : View.INVISIBLE);
        overlight.setText(R.string.overlight);

        if (!cameraFlashIsSupported)
        {
            return;
        }

        // TODO:

        final String flashModeNew = state.darknessState == 0 ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF;
        Camera.Parameters parameters = camera.getParameters();

        if (!flashModeNew.equals(parameters.getFlashMode()))
        {
            parameters.setFlashMode(flashModeNew);
            camera.setParameters(parameters);

            photoProcessor.setFlashTorchState(state.darknessState == 0 ? true : false);
        }
    }

    private Camera.Size getBestPreviewSize() {
        final List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        final List<Camera.Size> filteredSizes = new ArrayList<>();
        for (Camera.Size previewSize : supportedPreviewSizes) {
            if (previewSize.width <= Config.PREVIEW_WIDTH && previewSize.height <= Config.PREVIEW_HEIGHT) {
                filteredSizes.add(previewSize);
            }
        }

        Camera.Size bestSize = filteredSizes.get(0);
        for (int i = 1; i < filteredSizes.size(); i++) {
            Camera.Size currentSize = filteredSizes.get(i);
            if (currentSize.width >= bestSize.width && currentSize.height >= bestSize.height) {
                bestSize = currentSize;
            }
        }

        return bestSize;
    }

    private void configureCamera() {
        final Camera.Parameters parameters = camera.getParameters();
        try {
            parameters.setPreviewFormat(ImageFormat.NV21);

            // set focus for video if present
            List<String> focusModes = parameters.getSupportedFocusModes();

            if (null != focusModes && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            // check if torch is present
            List<String> flashModes = parameters.getSupportedFlashModes();

            cameraFlashIsSupported = null != flashModes && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH);

            final Camera.Size bestPreviewSize = getBestPreviewSize();
            photoProcessor.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            camera.setParameters(parameters);
        } catch (RuntimeException exception) {
            Toast.makeText(getContext(), R.string.camera_configuration_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void startCameraPreview(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            setCameraDisplayOrientation(getCameraID());
            camera.addCallbackBuffer(photoProcessor.getCallbackBuffer());
            camera.setPreviewCallbackWithBuffer(PhotoFragment.this);

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            preview.getHolder().removeCallback(holderCallback);
            holderCallback = null;
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        photoProcessor.removeListeners();
    }

    @Override
    public void onDestroy() {
        photoProcessor.release();
        super.onDestroy();
    }

    private int getCameraID() {
        if (preferences.getUseFrontCamera()) {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }
}
