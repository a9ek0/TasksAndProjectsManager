#include <jni.h>
#include <string.h>

extern "C" JNIEXPORT jint JNICALL
Java_com_a9ek0_tasksandprojectsmanager_MainActivity_calculateProgressJNI(JNIEnv *env, jobject instance, jint totalTasks, jint completedTasks) {
    if (totalTasks == 0) {
        return 0;
    }
    return (completedTasks * 100) / totalTasks;
}