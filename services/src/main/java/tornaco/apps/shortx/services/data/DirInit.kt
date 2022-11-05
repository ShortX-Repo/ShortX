package tornaco.apps.shortx.services.data

import android.os.Binder
import android.os.Environment
import lang3.RandomStringUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

internal object DirInit {
    private const val THANOS_BASE_SERVER_DIR_NAME = "shortx"
    private val logger = Logger("DataSystemServerDir")

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
                    logger.i("ServiceConfigs DataSystemServerDir baseServerDirMayInit, rename to: $this")
                } else {
                    val deleted = legacyBaseServerDir.deleteRecursively()
                    logger.w("ServiceConfigs DataSystemServerDir baseServerDirMayInit, rename to: $this failed. deleted it anyway, deleted? $deleted")
                }
            }
        }

        // Search any existing
        systemDir().listFiles()?.forEach {
            if (it.isDirectory && it.name.startsWith(THANOS_BASE_SERVER_DIR_NAME)) {
                logger.i("ServiceConfigs DataSystemServerDir baseServerDirMayInit, found shortx dir: $it")
                baseServerDir = it
            }
        }

        if (baseServerDir == null) {
            baseServerDir = randomBaseServerDir()
            logger.i("ServiceConfigs DataSystemServerDir baseServerDirMayInit, new random dir: $baseServerDir")
        }

        baseServerDirInitialized.set(true)
        logger.i("ServiceConfigs DataSystemServerDir baseServerDirMayInit, finally we will use: $baseServerDir")
        return baseServerDir!!
    }


    private fun randomBaseServerDir(): File {
        return File(
            systemDir(),
            "${THANOS_BASE_SERVER_DIR_NAME}_${RandomStringUtils.randomAlphabetic(16)}"
        )
    }

    private fun legacyBaseServerDir(): File {
        return File(systemDir(), THANOS_BASE_SERVER_DIR_NAME)
    }

    private fun systemDir(): File {
        val callingUid = Binder.getCallingUid()
        if (PkgUtils.isShell(callingUid)) {
            val dataLocalTmpFile = File("/data/local/tmp")
            val res = File(dataLocalTmpFile, "system")
            logger.w("Pick dir for Shell: $res")
            return res
        }
        val res = File(Environment.getDataDirectory(), "system")
        logger.w("Pick dir for Root: $res")
        return res
    }
}