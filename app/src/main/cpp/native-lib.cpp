#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_carpartsshop_ui_home_gifts_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}