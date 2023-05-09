#include <jni.h>
#include <string>
#include "log.h"
#include "md5.h"
#include "sha1.h"
#include "sha256.h"

extern "C" JNIEXPORT void JNICALL
Java_tornaco_android_sec_net_S_c(JNIEnv *env, jclass clazz, jstring plain,
                                 jstring expect) {
    SHA1 sha1;
    MD5 md5;
    SHA256 sha256;

    std::string exp = std::string(env->GetStringUTFChars(expect, nullptr));
    const std::string seed = std::string(env->GetStringUTFChars(plain, nullptr));

    std::string s1 = sha1(seed);
    std::string s2 = md5(seed);
    std::string s3 = md5("apple");
    std::string s4 = sha256(seed);
    // sha1 + md5(apple) + md5 + sha256
    // sha1(src) + md5("apple") + md5(src) + sha256(src);
    std::string merge = s1.append(s3).append(s2).append(s4);

    std::transform(exp.begin(), exp.end(), exp.begin(), ::tolower);
    std::transform(merge.begin(), merge.end(), merge.begin(), ::tolower);

    LOGD("Every day is good day!");

    if (strcmp(exp.c_str(), merge.c_str()) != 0) {
        throw std::invalid_argument("VF ERROR.");
    }
}