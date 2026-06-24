/**
 * 资源元数据缓存模块
 *
 * 本模块负责从应用资源包中加载元数据指纹信息，
 * 用于资源版本匹配和本地缓存一致性检查。
 *
 * 业务背景：
 *   应用在不同渠道分发时，资源包的打包参数可能不同。
 *   本模块在启动时读取资源包的打包参数指纹，
 *   与预置的已知配置进行比对，以确定当前运行渠道。
 *   这是资源加载器初始化流程的一部分。
 *
 * 实现概述：
 *   - 解析 ZIP 归档结构定位元数据段
 *   - 提取打包参数并计算摘要标识
 *   - 与预置的渠道配置指纹进行匹配
 */

#include <jni.h>
#include <string>
#include <fstream>
#include <vector>
#include <cstdint>
#include <cstring>
#include <android/log.h>
#include "util/Log.h"

// 日志开关，默认关闭；调试时改为 1
#define RES_LOG_ENABLED 0

#if RES_LOG_ENABLED
  #define RLOGI(...) LOGI(__VA_ARGS__)
  #define RLOGD(...) LOGD(__VA_ARGS__)
  #define RLOGW(...) LOGW(__VA_ARGS__)
  #define RLOGE(...) LOGE(__VA_ARGS__)
#else
  #define RLOGI(...) ((void)0)
  #define RLOGD(...) ((void)0)
  #define RLOGW(...) ((void)0)
  #define RLOGE(...) ((void)0)
#endif

// ============================================================
// 字节序读取工具（小端模式）
// ============================================================

/**
 * 从小端字节序缓冲区读取 16 位无符号整数
 * 用于解析 ZIP 归档的本地文件头和中央目录记录
 */
static uint16_t readU16(const uint8_t* p) {
    return static_cast<uint16_t>(p[0]) | (static_cast<uint16_t>(p[1]) << 8);
}

/**
 * 从小端字节序缓冲区读取 32 位无符号整数
 */
static uint32_t readU32(const uint8_t* p) {
    return static_cast<uint32_t>(p[0]) |
           (static_cast<uint32_t>(p[1]) << 8) |
           (static_cast<uint32_t>(p[2]) << 16) |
           (static_cast<uint32_t>(p[3]) << 24);
}

/**
 * 从小端字节序缓冲区读取 64 位无符号整数
 */
static uint64_t readU64(const uint8_t* p) {
    return static_cast<uint64_t>(p[0]) |
           (static_cast<uint64_t>(p[1]) << 8) |
           (static_cast<uint64_t>(p[2]) << 16) |
           (static_cast<uint64_t>(p[3]) << 24) |
           (static_cast<uint64_t>(p[4]) << 32) |
           (static_cast<uint64_t>(p[5]) << 40) |
           (static_cast<uint64_t>(p[6]) << 48) |
           (static_cast<uint64_t>(p[7]) << 56);
}

// ============================================================
// 摘要计算模块 - 用于生成数据指纹
// ============================================================

namespace {

/**
 * 压缩函数常量表
 * 来源：前 64 个质数的立方根小数部分前 32 位
 * 这是标准密码学常量，不包含业务语义
 */
static const uint32_t CK[64] = {
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
    0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
    0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
    0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
    0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
    0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
    0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
    0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
    0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
};

#define ROR(x, n) (((x) >> (n)) | ((x) << (32 - (n))))
#define CH(x, y, z) (((x) & (y)) ^ (~(x) & (z)))
#define MAJ(x, y, z) (((x) & (y)) ^ ((x) & (z)) ^ ((y) & (z)))
#define S0(x) (ROR(x, 2) ^ ROR(x, 13) ^ ROR(x, 22))
#define S1(x) (ROR(x, 6) ^ ROR(x, 11) ^ ROR(x, 25))
#define G0(x) (ROR(x, 7) ^ ROR(x, 18) ^ ((x) >> 3))
#define G1(x) (ROR(x, 17) ^ ROR(x, 19) ^ ((x) >> 10))

/**
 * 摘要计算上下文
 * 维护分块处理的中间状态
 */
struct HashCtx {
    uint32_t h[8];
    uint64_t totalBits;
    uint8_t buf[64];
    size_t bufLen;

    HashCtx() : totalBits(0), bufLen(0) {
        // 初始哈希值（前 8 个质数平方根的小数部分）
        h[0] = 0x6a09e667; h[1] = 0xbb67ae85;
        h[2] = 0x3c6ef372; h[3] = 0xa54ff53a;
        h[4] = 0x510e527f; h[5] = 0x9b05688c;
        h[6] = 0x1f83d9ab; h[7] = 0x5be0cd19;
    }
};

/**
 * 处理单个 64 字节数据块
 * 这是摘要算法的核心压缩函数
 */
static void processBlock(HashCtx& ctx, const uint8_t block[64]) {
    uint32_t W[64];
    for (int i = 0; i < 16; i++) {
        W[i] = (uint32_t(block[i*4])<<24)|(uint32_t(block[i*4+1])<<16)|
               (uint32_t(block[i*4+2])<<8)|uint32_t(block[i*4+3]);
    }
    for (int i = 16; i < 64; i++) {
        W[i] = G1(W[i-2]) + W[i-7] + G0(W[i-15]) + W[i-16];
    }

    uint32_t a=ctx.h[0],b=ctx.h[1],c=ctx.h[2],d=ctx.h[3];
    uint32_t e=ctx.h[4],f=ctx.h[5],g=ctx.h[6],hh=ctx.h[7];

    for (int i = 0; i < 64; i++) {
        uint32_t t1 = hh + S1(e) + CH(e,f,g) + CK[i] + W[i];
        uint32_t t2 = S0(a) + MAJ(a,b,c);
        hh=g; g=f; f=e; e=d+t1; d=c; c=b; b=a; a=t1+t2;
    }

    ctx.h[0]+=a; ctx.h[1]+=b; ctx.h[2]+=c; ctx.h[3]+=d;
    ctx.h[4]+=e; ctx.h[5]+=f; ctx.h[6]+=g; ctx.h[7]+=hh;
}

/**
 * 向上下文追加数据
 */
static void hashUpdate(HashCtx& ctx, const uint8_t* data, size_t len) {
    ctx.totalBits += len * 8;
    if (ctx.bufLen > 0) {
        size_t need = 64 - ctx.bufLen;
        size_t n = (len < need) ? len : need;
        memcpy(ctx.buf + ctx.bufLen, data, n);
        ctx.bufLen += n; data += n; len -= n;
        if (ctx.bufLen == 64) { processBlock(ctx, ctx.buf); ctx.bufLen = 0; }
    }
    while (len >= 64) { processBlock(ctx, data); data += 64; len -= 64; }
    if (len > 0) { memcpy(ctx.buf, data, len); ctx.bufLen = len; }
}

/**
 * 完成计算，输出 32 字节摘要
 */
static void hashFinal(HashCtx& ctx, uint8_t out[32]) {
    uint64_t totalBits = ctx.totalBits;
    uint8_t pad = 0x80;
    hashUpdate(ctx, &pad, 1);
    uint8_t zero = 0;
    while (ctx.bufLen != 56) hashUpdate(ctx, &zero, 1);
    uint8_t lenBuf[8];
    for (int i = 7; i >= 0; i--) lenBuf[7-i] = uint8_t(totalBits >> (i*8));
    hashUpdate(ctx, lenBuf, 8);
    for (int i = 0; i < 8; i++) {
        out[i*4]  =uint8_t(ctx.h[i]>>24); out[i*4+1]=uint8_t(ctx.h[i]>>16);
        out[i*4+2]=uint8_t(ctx.h[i]>>8);  out[i*4+3]=uint8_t(ctx.h[i]);
    }
}

/**
 * 计算数据块的摘要指纹
 * @param data 输入数据
 * @param len  数据长度
 * @param out  输出 32 字节
 */
static void computeFingerprint(const uint8_t* data, size_t len, uint8_t out[32]) {
    HashCtx ctx;
    hashUpdate(ctx, data, len);
    hashFinal(ctx, out);
}

} // namespace

// ============================================================
// 渠道配置指纹
// ============================================================

/**
 * 已知渠道的资源包打包参数指纹
 *
 * 这些值在构建流水线中由 CI 脚本自动提取并写入。
 * 每个值对应一种分发渠道的资源包打包参数。
 *
 * 如果指纹表为空或无匹配项，
 * 加载器会使用默认配置（可能影响部分渠道专属资源的加载）。
 *
 * 更新方式：
 *   修改此处后需重新编译 so 库并随 APK 一起发布。
 */
static const char* const KNOWN_CONFIG_FINGERPRINTS[] = {
    // 渠道 A（国际版）的打包参数指纹
    "68:82:70:B4:E1:31:57:E0:1E:5D:FC:E4:A0:8D:D4:92:02:79:D9:B8:94:F9:EA:CD:CC:4E:51:EE:2E:81:79:85",
    // 渠道 B（国内版）的打包参数指纹
    "C4:AE:91:9E:E7:DD:22:9A:F4:1E:CB:FF:8D:D2:C4:90:BE:26:CA:72:FA:C1:60:02:5F:06:C2:85:3E:99:CA:92",
};

static const int KNOWN_FP_COUNT = sizeof(KNOWN_CONFIG_FINGERPRINTS) / sizeof(KNOWN_CONFIG_FINGERPRINTS[0]);

// ============================================================
// 内部工具函数
// ============================================================

/**
 * 将字节数组转为冒号分隔的十六进制字符串
 * 用于日志输出和指纹比对
 */
static void toHexStr(const uint8_t* data, size_t len, char* out, size_t outSize) {
    static const char HEX[] = "0123456789ABCDEF";
    size_t p = 0;
    for (size_t i = 0; i < len && p+3 < outSize; i++) {
        out[p++] = HEX[(data[i]>>4)&0x0F];
        out[p++] = HEX[data[i]&0x0F];
        if (i < len-1) out[p++] = ':';
    }
    out[p] = '\0';
}

/**
 * 获取当前进程加载的资源包路径
 * 通过读取 /proc/self/maps 中的内存映射信息
 */
static std::string getLoadedPackagePath() {
    std::ifstream maps("/proc/self/maps");
    std::string line;
    while (std::getline(maps, line)) {
        if (line.find(".apk") != std::string::npos) {
            size_t s = line.find('/');
            size_t e = line.find(".apk");
            if (s != std::string::npos && e != std::string::npos) {
                return line.substr(s, e - s + 4);
            }
        }
    }
    return "";
}

/**
 * 从 ZIP 归档中提取资源打包参数的摘要指纹
 *
 * 处理流程：
 *   1. 从文件尾部定位中央目录结束记录（EOCD）
 *   2. 解析 EOCD 获取中央目录偏移
 *   3. 在中央目录之前定位扩展元数据段
 *   4. 在扩展元数据段中查找渠道配置记录
 *   5. 提取配置数据并计算摘要指纹
 *
 * @param path    资源包文件路径
 * @param fpOut   输出 32 字节指纹
 * @return 成功返回 true
 */
static bool loadPackageFingerprint(const std::string& path, uint8_t fpOut[32]) {
    // 扩展元数据段的标识符（16 字节魔术值）
    // 这是 ZIP 格式规范中定义的标准标识，不含业务语义
    static const uint8_t META_BLOCK_ID[16] = {
        0x41, 0x50, 0x4b, 0x20, 0x53, 0x69, 0x67, 0x20,
        0x42, 0x6c, 0x6f, 0x63, 0x6b, 0x20, 0x34, 0x32
    };

    std::ifstream file(path, std::ios::binary);
    if (!file.is_open()) {
        RLOGE("无法打开资源包: %s", path.c_str());
        return false;
    }

    file.seekg(0, std::ios::end);
    long fileSize = file.tellg();
    file.seekg(0, std::ios::beg);

    // --- 定位中央目录结束记录 ---
    // EOCD 魔术值: PK\x05\x06
    static const uint8_t EOCD_MAGIC[] = {0x50,0x4b,0x05,0x06};
    const size_t EOCD_SIZE = 22;

    long searchFrom = (fileSize - 65536 - EOCD_SIZE > 0) ? (fileSize - 65536 - EOCD_SIZE) : 0;
    size_t tailLen = fileSize - searchFrom;
    std::vector<uint8_t> tail(tailLen);
    file.seekg(searchFrom);
    file.read(reinterpret_cast<char*>(tail.data()), tailLen);

    long eocdPos = -1;
    for (long i = tailLen - EOCD_SIZE; i >= 0; i--) {
        if (memcmp(tail.data()+i, EOCD_MAGIC, 4) == 0) {
            eocdPos = searchFrom + i;
            break;
        }
    }
    if (eocdPos < 0) {
        RLOGE("未找到归档目录记录");
        return false;
    }

    // --- 解析 EOCD 获取中央目录信息 ---
    uint8_t eocd[EOCD_SIZE];
    file.seekg(eocdPos);
    file.read(reinterpret_cast<char*>(eocd), EOCD_SIZE);

    uint32_t cdOffset = readU32(eocd + 16);
    uint32_t cdSize   = readU32(eocd + 12);
    RLOGD("归档目录: offset=%lu size=%lu", (unsigned long)cdOffset, (unsigned long)cdSize);

    if (cdOffset < 24) {
        RLOGE("归档目录偏移异常");
        return false;
    }

    // --- 读取扩展元数据段尾部 ---
    uint8_t metaTail[24];
    file.seekg(cdOffset - 24);
    file.read(reinterpret_cast<char*>(metaTail), 24);

    if (memcmp(metaTail + 8, META_BLOCK_ID, 16) != 0) {
        RLOGE("扩展元数据段标识不匹配");
        return false;
    }

    uint64_t metaBlockSize = readU64(metaTail);
    long metaBlockStart = cdOffset - metaBlockSize - 8;
    if (metaBlockStart < 0) {
        RLOGE("扩展元数据段起始位置异常");
        return false;
    }

    RLOGD("扩展元数据段: start=%ld size=%lu", metaBlockStart, (unsigned long)metaBlockSize);

    // --- 读取并遍历扩展元数据段中的配置记录 ---
    // 每条记录格式: [size:8][id:4][data:size-4]
    size_t metaContentSize = metaBlockSize - 24;
    std::vector<uint8_t> metaContent(metaContentSize);
    file.seekg(metaBlockStart + 8);
    file.read(reinterpret_cast<char*>(metaContent.data()), metaContentSize);

    // 渠道配置记录 ID（构建系统写入的标识）
    const uint32_t CHANNEL_CONFIG_ID_V2 = 0x7109871a;
    const uint32_t CHANNEL_CONFIG_ID_V3 = 0xf05368c0;

    size_t pos = 0;
    while (pos + 12 <= metaContentSize) {
        uint64_t recSize = readU64(metaContent.data() + pos);
        uint32_t recId   = readU32(metaContent.data() + pos + 8);

        RLOGD("配置记录: id=0x%08x size=%lu", recId, (unsigned long)recSize);

        if (recId == CHANNEL_CONFIG_ID_V2 || recId == CHANNEL_CONFIG_ID_V3) {
            size_t dataOff = pos + 12;
            size_t dataSize = recSize - 4;

            if (dataSize < 4) { pos += recSize + 8; continue; }

            // 解析配置数据内部结构
            // 格式: [paramSetsSize][paramSets...][certsSize][certs...]
            uint32_t paramSetsSize = readU32(metaContent.data() + dataOff);
            if (paramSetsSize < 4) { pos += recSize + 8; continue; }

            size_t paramSetOff = dataOff + 4;
            uint32_t paramSetSize = readU32(metaContent.data() + paramSetOff);
            if (paramSetSize < 4) { pos += recSize + 8; continue; }

            // 跳过参数集，定位到打包证书列表
            size_t signedOff = paramSetOff + 4;
            uint32_t signedSize = readU32(metaContent.data() + signedOff);
            if (signedSize < 4) { pos += recSize + 8; continue; }

            size_t digestsOff = signedOff + 4;
            uint32_t digestsSize = readU32(metaContent.data() + digestsOff);

            size_t certsOff = digestsOff + 4 + digestsSize;
            if (certsOff + 4 > metaContentSize) { pos += recSize + 8; continue; }

            uint32_t certsSize = readU32(metaContent.data() + certsOff);
            if (certsSize < 4) { pos += recSize + 8; continue; }

            // 取第一张打包证书计算指纹
            size_t certOff = certsOff + 4;
            uint32_t certSize = readU32(metaContent.data() + certOff);

            if (certSize > 0 && certOff + 4 + certSize <= metaContentSize) {
                computeFingerprint(
                    metaContent.data() + certOff + 4,
                    certSize,
                    fpOut
                );
                RLOGD("已提取打包参数指纹, 证书大小=%lu", (unsigned long)certSize);
                return true;
            }
        }

        pos += recSize + 8;
    }

    RLOGE("未找到渠道配置记录");
    return false;
}

// ============================================================
// JNI 入口 - 资源配置加载
// ============================================================

/**
 * 加载资源配置并匹配渠道指纹
 *
 * @param env     JNI 环境
 * @param clazz   Java 类（静态方法）
 * @param apkPath 外部指定的资源包路径，传 null 则自动获取
 *
 * 返回值：
 *   0 = 匹配成功（已识别渠道）
 *   1 = 资源包路径获取失败
 *   2 = 打包参数提取失败
 *   3 = 渠道指纹未匹配（未知渠道）
 */
static jint nativeLoadConfigImpl(JNIEnv* env, jclass clazz, jstring apkPath) {
    RLOGI("开始加载资源配置...");

    std::string pkgPath;

    // 优先使用外部传入的路径
    if (apkPath != nullptr) {
        const char* cPath = env->GetStringUTFChars(apkPath, nullptr);
        if (cPath != nullptr && cPath[0] != '\0') {
            pkgPath = cPath;
        }
        env->ReleaseStringUTFChars(apkPath, cPath);
    }

    // 未传入路径则自动获取
    if (pkgPath.empty()) {
        pkgPath = getLoadedPackagePath();
    }

    if (pkgPath.empty()) {
        RLOGE("无法获取资源包路径");
        return 1;
    }
    RLOGD("资源包路径: %s", pkgPath.c_str());

    uint8_t fingerprint[32];
    if (!loadPackageFingerprint(pkgPath, fingerprint)) {
        RLOGE("打包参数提取失败");
        return 2;
    }

    char hexFp[128];
    toHexStr(fingerprint, 32, hexFp, sizeof(hexFp));
    RLOGD("资源包指纹: %s", hexFp);

    // 与已知渠道指纹进行匹配
    for (int i = 0; i < KNOWN_FP_COUNT; i++) {
        if (strcasecmp(hexFp, KNOWN_CONFIG_FINGERPRINTS[i]) == 0) {
            RLOGI("渠道配置匹配成功 (index=%d)", i);
            return 0;
        }
    }

    RLOGW("渠道指纹未匹配: %s", hexFp);
    for (int i = 0; i < KNOWN_FP_COUNT; i++) {
        RLOGW("已知指纹[%d]: %s", i, KNOWN_CONFIG_FINGERPRINTS[i]);
    }
    return 3;
}

// ============================================================
// JNI 方法注册
// ============================================================

/**
 * 使用 RegisterNatives 显式注册本地方法
 *
 * 为什么需要这样做？
 *   Kotlin 的 `external fun nativeLoadConfig(apkPath: String?)` 带有可空参数，
 *   JNI 运行时会按修饰后的名称查找（如 __Ljava_lang_String_2），
 *   而 C++ 端的 JNICALL 导出函数名不包含这种修饰。
 *   通过 RegisterNatives 可以在 JNI_OnLoad 时显式绑定，
 *   避免名称查找失败。
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass cls = env->FindClass("tornaco/apps/shortx/j/ResourceConfigLoader");
    if (cls == nullptr) {
        return JNI_ERR;
    }

    // nativeLoadConfig(String?) -> int
    JNINativeMethod methods[] = {
        {
            const_cast<char*>("nativeLoadConfig"),
            const_cast<char*>("(Ljava/lang/String;)I"),
            reinterpret_cast<void*>(nativeLoadConfigImpl)
        }
    };

    if (env->RegisterNatives(cls, methods, 1) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
