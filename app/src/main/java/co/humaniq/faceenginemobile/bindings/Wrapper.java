/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package co.humaniq.faceenginemobile.bindings;

public final class Wrapper {
    public static String getVERSION_HASH() {
        return WrapperJNI.VERSION_HASH_get();
    }

    public static int getNumBytesPerPixel(int format) {
        return WrapperJNI.getNumBytesPerPixel(format);
    }

    public static int getNumBytesPerImageRow(int format, int size) {
        return WrapperJNI.getNumBytesPerImageRow(format, size);
    }

    public static int getNumBytesPerImageView(ImageView view) {
        return WrapperJNI.getNumBytesPerImageView(ImageView.getCPtr(view), view);
    }

}
