#include <jni.h>
#include <PhotoMaker.hpp>
#include <android/log.h>


static vl::PhotoMaker photoMaker;

extern "C"
JNIEXPORT void JNICALL
Java_co_humaniq_views_CameraActivity_initPhotoMaker(JNIEnv *env, jobject instance, jstring path) {
    const char *nativePath = env->GetStringUTFChars(path, JNI_FALSE);

    if (!photoMaker.load(nativePath)) {
        __android_log_print(ANDROID_LOG_ERROR, "PhotoMaker", "load error");
        return;
    }

    __android_log_print(ANDROID_LOG_DEBUG, "PhotoMaker", "load success");
}


extern "C"
JNIEXPORT void JNICALL
Java_co_humaniq_views_CameraActivity_onFrame(JNIEnv *env, jobject instance,
                                             jint width, jint height, jbyteArray NV21FrameData)
{
    jbyte *pNV21FrameData = (jbyte *) env->GetPrimitiveArrayCritical(
            NV21FrameData, 0);
    vl::ImageView capturedImageView(
            pNV21FrameData,
            width,
            height, vl::FMT_YUV420sp);
    photoMaker.submit(capturedImageView);
    env->ReleasePrimitiveArrayCritical(NV21FrameData, pNV21FrameData, 0);
}
