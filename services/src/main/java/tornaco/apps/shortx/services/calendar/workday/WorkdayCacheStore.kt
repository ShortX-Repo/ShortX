package tornaco.apps.shortx.services.calendar.workday

import java.io.File

internal object WorkdayCacheStore {
    private const val unifiedFileName = "holidayAPI.json"

    fun updateCachedPayload(
        cacheDir: File,
        payload: String,
        onInvalidate: () -> Unit,
        logInfo: (String) -> Unit = {},
        logDebug: (String) -> Unit = {},
        logError: (String, Throwable?) -> Unit = { _, _ -> },
    ): File {
        logInfo("updateCachedPayload: regionDir=${cacheDir.absolutePath}, exists=${cacheDir.exists()}, canWrite=${cacheDir.canWrite()}")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        cacheDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "json" && file.name != unifiedFileName) {
                runCatching { file.delete() }
                    .onSuccess { deleted ->
                        logInfo("updateCachedPayload: deleted stale cache file=${file.absolutePath}, deleted=$deleted")
                    }
                    .onFailure {
                        logError("updateCachedPayload: failed deleting stale cache file=${file.absolutePath}", it)
                    }
            }
        }

        val file = File(cacheDir, unifiedFileName)
        file.writeText(payload)
        onInvalidate()

        logInfo(
            "updateCachedPayload: wrote file=${file.absolutePath}, exists=${file.exists()}, length=${file.length()}, parent=${file.parentFile?.absolutePath}"
        )
        runCatching { file.readText() }
            .onSuccess {
                logDebug("updateCachedPayload: verify read success, chars=${it.length}")
            }
            .onFailure {
                logError("updateCachedPayload: verify read failed for ${file.absolutePath}", it)
            }
        return file
    }
}