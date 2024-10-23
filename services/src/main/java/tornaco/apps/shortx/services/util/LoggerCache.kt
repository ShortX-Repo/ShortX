package tornaco.apps.shortx.services.util

import tornaco.apps.shortx.core.util.Logger

object LoggerCache {
    private val cachedLoggers = mutableMapOf<String, Logger>()

    @Synchronized
    fun getLogger(tag: String): Logger {
        return requireNotNull(
            cachedLoggers.putIfAbsent(tag, Logger(tag))
        ) { "getLogger returns null." }
    }
}