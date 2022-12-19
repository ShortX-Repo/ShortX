#include <jni.h>
#include <string>
#include "util/Log.h"

jobject RequestSystemServerBinder(JNIEnv *env) {
    jclass serviceManagerClass = env->FindClass("android/os/ServiceManager");
    jmethodID getServiceMethod = env->GetStaticMethodID(serviceManagerClass, "getService",
                                                        "(Ljava/lang/String;)Landroid/os/IBinder;");
    jstring serviceName = env->NewStringUTF("print");
    jobject binder = env->CallStaticObjectMethod(serviceManagerClass, getServiceMethod,
                                                 serviceName);
    if (!binder) {
        LOGD("can't get service");
        return nullptr;
    }

    return binder;
}

extern "C" JNIEXPORT jobject JNICALL
Java_tornaco_apps_shortx_app_binder_retriever_Retriever_retrieveServiceBinder(
        JNIEnv *env,
        jobject /* this */) {
    return RequestSystemServerBinder(env);
}
