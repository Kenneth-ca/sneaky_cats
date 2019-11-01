#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_trojancats_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Please be more careful with your security";
    return env->NewStringUTF(hello.c_str());
}
