#pragma once

#include <string>
#include <unordered_map>

class KVStore {
public:
    KVStore(bool autoSave = true);

    void setAutoSave(bool autoSave);

    void setSavePath(const std::string &path);

    void putString(const std::string &key, const std::string &value);

    void putInt(const std::string &key, int value);

    void putBool(const std::string &key, bool value);

    std::string getString(const std::string &key, const std::string &defaultValue = "") const;

    int getInt(const std::string &key, int defaultValue = 0) const;

    bool getBool(const std::string &key, bool defaultValue = false) const;

    void save();

    void load();

private:
    void saveToFile(const std::string &path);

    void loadFromFile(const std::string &path);

    bool autoSave_;
    std::string savePath_;
    std::unordered_map<std::string, std::string> store_;
};
