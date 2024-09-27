package tornaco.apps.shortx.services.data

import android.os.Environment
import lang3.RandomStringUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

internal object DirInit {
    private const val BASE_SERVER_DIR_NAME = "shortx"
    private val logger = Logger("DirInit")

    private var baseServerDir: File? = null
    private val baseServerDirInitialized = AtomicBoolean(false)

    fun baseServerDirMayInit(): File {
        if (baseServerDirInitialized.get()) {
            return baseServerDir!!
        }

        val legacyBaseServerDir = legacyBaseServerDir()

        // Rename to random one for legacy.
        if (legacyBaseServerDir.exists()) {
            with(randomBaseServerDir()) {
                if (legacyBaseServerDir.renameTo(this)) {
                    logger.i("baseServerDirMayInit, rename to: $this")
                } else {
                    val deleted = legacyBaseServerDir.deleteRecursively()
                    logger.w("baseServerDirMayInit, rename to: $this failed. deleted it anyway, deleted? $deleted")
                }
            }
        }

        // Search any existing
        systemDir().listFiles()?.forEach {
            if (it.isDirectory && it.name.startsWith(BASE_SERVER_DIR_NAME)) {
                logger.i("baseServerDirMayInit, found shortx dir: $it")
                baseServerDir = it
            }
        }

        if (baseServerDir == null) {
            baseServerDir = randomBaseServerDir()
            logger.i("baseServerDirMayInit, new random dir: $baseServerDir")
        }

        baseServerDirInitialized.set(true)
        logger.i("baseServerDirMayInit, finally we will use: $baseServerDir")
        return baseServerDir!!
    }


    private fun randomBaseServerDir(): File {
        return File(
            systemDir(),
            "${BASE_SERVER_DIR_NAME}_${RandomStringUtils.randomAlphabetic(16)}"
        )
    }

    private fun legacyBaseServerDir(): File {
        return File(systemDir(), BASE_SERVER_DIR_NAME)
    }

    private fun systemDir(): File {
        val res = File(Environment.getDataDirectory(), "system")
        logger.w("Pick dir for Root: $res")
        return res
    }
}