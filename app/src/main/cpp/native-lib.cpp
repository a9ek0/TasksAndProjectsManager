#include <jni.h>
#include <string.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_a9ek0_tasksandprojectsmanager_MainActivity_stringFromJNI(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("Hello from C");
}