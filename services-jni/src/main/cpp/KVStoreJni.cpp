#include <jni.h>
#include "KVStore.h"

static KVStore g_store(true);  // 默认开 autoSave

extern "C" JNIEXPORT void JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativeInit(JNIEnv *env, jobject thiz, jstring path) {
    const char *cpath = env->GetStringUTFChars(path, nullptr);
    g_store.setSavePath(cpath);
    g_store.load();
    env->ReleaseStringUTFChars(path, cpath);
}

extern "C" JNIEXPORT void JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativePutString(JNIEnv *env, jobject thiz, jstring key,
                                                   jstring value) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    const char *cvalue = env->GetStringUTFChars(value, nullptr);
    g_store.putString(ckey, cvalue);
    env->ReleaseStringUTFChars(key, ckey);
    env->ReleaseStringUTFChars(value, cvalue);
}



extern "C" JNIEXPORT void JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativePutInt(JNIEnv *env, jobject thiz, jstring key,
                                                jint value) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    g_store.putInt(ckey, value);
    env->ReleaseStringUTFChars(key, ckey);
}

extern "C" JNIEXPORT void JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativePutBool(JNIEnv *env, jobject thiz, jstring key,
                                                 jboolean value) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    g_store.putBool(ckey, value == JNI_TRUE);
    env->ReleaseStringUTFChars(key, ckey);
}

extern "C" JNIEXPORT jstring JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativeGetString(JNIEnv *env, jobject thiz, jstring key,
                                                   jstring defaultValue) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    const char *cdefault = env->GetStringUTFChars(defaultValue, nullptr);
    std::string result = g_store.getString(ckey, cdefault);
    env->ReleaseStringUTFChars(key, ckey);
    env->ReleaseStringUTFChars(defaultValue, cdefault);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativeGetInt(JNIEnv *env, jobject thiz, jstring key,
                                                jint defaultValue) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    int result = g_store.getInt(ckey, defaultValue);
    env->ReleaseStringUTFChars(key, ckey);
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_tornaco_apps_shortx_j_KVStore_nativeGetBool(JNIEnv *env, jobject thiz, jstring key,
                                                 jboolean defaultValue) {
    const char *ckey = env->GetStringUTFChars(key, nullptr);
    bool result = g_store.getBool(ckey, defaultValue == JNI_TRUE);
    env->ReleaseStringUTFChars(key, ckey);
    return result ? JNI_TRUE : JNI_FALSE;
}
