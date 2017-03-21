package co.humaniq.faceenginemobile;


public class Utils {

    /**
     * Поворачивает изображение по часовой стрелке на 90 градусов
     * если каждый раз для обработки кадра выделять память, то это приведет к подтормаживанию из-за постоянной работы GC
     * поэтому заранее выделяем память и под результат и под промежуточные операции и переиспользуем их
     *
     * @param result    результат поворотоа
     * @param tempArray массив для промежуточных результатов, должен быть по размеру таким же как и result
     * @param width     ширина поворачиваемого изображения
     * @param height    высота поворачиваемого изображения
     */
    public static void rotateClockwise90(int[] result, int[] tempArray, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int pix = result[x + (y * width)];
                final int newX = height - 1 - y;
                tempArray[newX + (height * x)] = pix;
            }
        }

        System.arraycopy(tempArray, 0, result, 0, tempArray.length);
    }

    // TODO:
    /**
     * Преобразует массив byte в массив int
     *
     * @param pixels массив byte
     */
    public static void byteToIntArray(byte[] pixels, int[] result) {
        for (int pixel = 0, resInd = 0; pixel < pixels.length; pixel += 4) {
            result[resInd++] = (((int)pixels[pixel + 3] & 0xff) << 24) | (((int)pixels[pixel + 2] & 0xff) << 16) | (((int)pixels[pixel + 1] & 0xff) << 8) | ((int)pixels[pixel + 0] & 0xff);
        }
    }

    /**
     * Поворот на 180 градусов относительно оси X
     *
     * @param picture изображение для поворота
     * @param width   ширина изображения
     * @param height  высота изображения
     */
    public static void flip(int[] picture, int width, int height) {
        for (int row = 0; row < width / 2; row++) {
            for (int col = 0; col < height; col++) {
                final int curItemIndex = row * height + col;
                final int flipItemIndex = (width - row - 1) * height + col;
                final int tmp = picture[curItemIndex];
                picture[curItemIndex] = picture[flipItemIndex];
                picture[flipItemIndex] = tmp;
            }
        }
    }

    /**
     * Считает количество пикселей бОльших threshold
     *
     */
    public static int countUpper(byte[] buffer, int width, android.graphics.Rect rect, int threshold) {
        int count = 0;

        for (int row = rect.top; row < rect.bottom; row++)
            for (int col = rect.left; col < rect.right; col++) {
                    if (((int) buffer[row * width + col] & 0xff) > threshold)
                        ++count;
                }

        return count;
    }

    /**
     * Считает количество пикселей мЕньших threshold
     *
     */
    public static int countLower(byte[] buffer, int width, android.graphics.Rect rect, int threshold) {
        int count = 0;

        for (int row = rect.top; row < rect.bottom; row++)
            for (int col = rect.left; col < rect.right; col++) {
                    if (((int) buffer[row * width + col] & 0xff) < threshold)
                ++count;
        }

        return count;
    }
}