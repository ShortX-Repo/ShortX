//
// Created by Hao Guo on 2025/5/2.
//

#include <string>
#include <fstream>
#include <sstream>

#include <vector>
#include <cstdint>   // for uint8_t
#include "util/Log.h"

// 获取 APK 路径（从 /proc/self/maps 读取）
std::string getApkPath() {
    std::ifstream maps("/proc/self/maps");
    std::string line;
    while (std::getline(maps, line)) {
        if (line.find(".apk") != std::string::npos) {
            size_t start = line.find('/');
            size_t end = line.find(".apk");
            if (start != std::string::npos && end != std::string::npos) {
                return line.substr(start, end - start + 4); // 包括 ".apk"
            }
        }
    }
    return "";
}

