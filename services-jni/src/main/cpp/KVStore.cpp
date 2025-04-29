#include "KVStore.h"
#include <fstream>
#include <sstream>

KVStore::KVStore(bool autoSave) : autoSave_(autoSave) {}

void KVStore::setAutoSave(bool autoSave) {
    autoSave_ = autoSave;
}

void KVStore::setSavePath(const std::string &path) {
    savePath_ = path;
}

void KVStore::putString(const std::string &key, const std::string &value) {
    store_[key] = "S:" + value;
    if (autoSave_) save();
}

void KVStore::putInt(const std::string &key, int value) {
    store_[key] = "I:" + std::to_string(value);
    if (autoSave_) save();
}

void KVStore::putBool(const std::string &key, bool value) {
    store_[key] = "B:" + std::string(value ? "1" : "0");
    if (autoSave_) save();
}

std::string KVStore::getString(const std::string &key, const std::string &defaultValue) const {
    auto it = store_.find(key);
    if (it != store_.end() && it->second.rfind("S:", 0) == 0) {
        return it->second.substr(2);
    }
    return defaultValue;
}

int KVStore::getInt(const std::string &key, int defaultValue) const {
    auto it = store_.find(key);
    if (it != store_.end() && it->second.rfind("I:", 0) == 0) {
        return std::stoi(it->second.substr(2));
    }
    return defaultValue;
}

bool KVStore::getBool(const std::string &key, bool defaultValue) const {
    auto it = store_.find(key);
    if (it != store_.end() && it->second.rfind("B:", 0) == 0) {
        return it->second.substr(2) == "1";
    }
    return defaultValue;
}

void KVStore::save() {
    if (!savePath_.empty()) {
        saveToFile(savePath_);
    }
}

void KVStore::load() {
    if (!savePath_.empty()) {
        loadFromFile(savePath_);
    }
}

void KVStore::saveToFile(const std::string &path) {
    std::ofstream ofs(path, std::ios::trunc);
    for (const auto &pair: store_) {
        ofs << pair.first << "=" << pair.second << "\n";
    }
}

void KVStore::loadFromFile(const std::string &path) {
    std::ifstream ifs(path);
    std::string line;
    while (std::getline(ifs, line)) {
        size_t pos = line.find('=');
        if (pos != std::string::npos) {
            std::string key = line.substr(0, pos);
            std::string value = line.substr(pos + 1);
            store_[key] = value;
        }
    }
}
