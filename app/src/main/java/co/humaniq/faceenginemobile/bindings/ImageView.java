/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package co.humaniq.faceenginemobile.bindings;

public final class ImageView {
    private transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ImageView(long cPtr, boolean cMemoryOwn) {
        swigCMemOwn = cMemoryOwn;
        swigCPtr = cPtr;
    }

    public ImageView() {
        this(WrapperJNI.new_ImageView__SWIG_0(), true);
    }

    public ImageView(java.nio.ByteBuffer pixels, int width, int height, int format) {
        this(ImageView.SwigConstructImageView(pixels, width, height, format), true);
    }

    public ImageView(java.nio.ByteBuffer pixels, int width, int height) {
        this(ImageView.SwigConstructImageView(pixels, width, height), true);
    }

    static long getCPtr(ImageView obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    static private long SwigConstructImageView(java.nio.ByteBuffer pixels, int width, int height, int format) {
        assert pixels.isDirect() : "Buffer must be allocated direct.";
        return WrapperJNI.new_ImageView__SWIG_1(pixels, width, height, format);
    }

    static private long SwigConstructImageView(java.nio.ByteBuffer pixels, int width, int height) {
        assert pixels.isDirect() : "Buffer must be allocated direct.";
        return WrapperJNI.new_ImageView__SWIG_2(pixels, width, height);
    }

    protected void finalize() throws Throwable {
        delete();
        super.finalize();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                WrapperJNI.delete_ImageView(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    public java.nio.ByteBuffer getPixels() {
        return WrapperJNI.ImageView_pixels_get(swigCPtr, this);
    }

    public void setPixels(java.nio.ByteBuffer value) {
        assert value.isDirect() : "Buffer must be allocated direct.";
        {
            WrapperJNI.ImageView_pixels_set(swigCPtr, this, value);
        }
    }

    public int getWidth() {
        return WrapperJNI.ImageView_width_get(swigCPtr, this);
    }

    public void setWidth(int value) {
        WrapperJNI.ImageView_width_set(swigCPtr, this, value);
    }

    public int getHeight() {
        return WrapperJNI.ImageView_height_get(swigCPtr, this);
    }

    public void setHeight(int value) {
        WrapperJNI.ImageView_height_set(swigCPtr, this, value);
    }

    public int getFormat() {
        return WrapperJNI.ImageView_format_get(swigCPtr, this);
    }

    public void setFormat(int value) {
        WrapperJNI.ImageView_format_set(swigCPtr, this, value);
    }

    public boolean isValid() {
        return WrapperJNI.ImageView_isValid(swigCPtr, this);
    }

    public void getRecPixels(java.nio.ByteBuffer pixels) {
        assert pixels.isDirect() : "Buffer must be allocated direct.";
        {
            WrapperJNI.ImageView_getRecPixels(swigCPtr, this, pixels);
        }
    }

    public Rect getRect() {
        return new Rect(WrapperJNI.ImageView_getRect(swigCPtr, this), true);
    }

    public void copyTo(ImageView destination) {
        WrapperJNI.ImageView_copyTo(swigCPtr, this, ImageView.getCPtr(destination), destination);
    }

}
