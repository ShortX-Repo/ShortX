package tornaco.apps.shortx.ui.dav

import tornaco.apps.shortx.ui.MMKVExt

class DavPref {
    companion object {
        private const val KEY_USER_NAME = "dav.user"
        private const val KEY_USER_PASSWORD = "dav.password"
        private const val KEY_SERVER_URL = "dav.server"
    }

    fun setUserName(user: String) {
        MMKVExt.encMMKV().encode(KEY_USER_NAME, user)
    }

    fun getUserName(): String? {
        return MMKVExt.encMMKV().decodeString(KEY_USER_NAME)
    }

    fun setPassword(password: String) {
        MMKVExt.encMMKV().encode(KEY_USER_PASSWORD, password)
    }

    fun getPassword(): String? {
        return MMKVExt.encMMKV().decodeString(KEY_USER_PASSWORD)
    }

    fun setServerUrl(server: String) {
        MMKVExt.encMMKV().encode(KEY_SERVER_URL, server)
    }

    fun getServerUrl(): String? {
        return MMKVExt.encMMKV().decodeString(KEY_SERVER_URL)
    }
}