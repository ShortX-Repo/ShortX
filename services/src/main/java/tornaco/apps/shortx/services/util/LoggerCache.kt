package tornaco.apps.shortx.services.util

import tornaco.apps.shortx.core.util.Logger

object LoggerCache {
    private val cachedLoggers = mutableMapOf<String, Logger>()

    @Synchronized
    fun getLogger(tag: String): Logger {
        val existing = if (cachedLoggers.contains(tag)) cachedLoggers[tag] else null
        if (existing != null) {
            return existing
        } else {
            val logger = Logger(tag).also {
                cachedLoggers[tag] = it
            }
            return logger
        }
    }
}