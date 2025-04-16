package tornaco.apps.shortx.services.xposed.hooks.hook


import android.content.Context
import android.os.Build
import android.system.ErrnoException
import android.system.Os
import android.text.TextUtils
import dalvik.system.DexClassLoader
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import java.io.File


object ExtAppClassHelper {
    private val logger = Logger("ExtAppClassHelper")

    fun newInstance(srcContext: Context, className: String): Any {
        val clazz = loadAppClass(srcContext, className)
        return requireNotNull(
            clazz.constructors.firstOrNull { it.parameters.size == 1 }?.newInstance(srcContext)
                ?: clazz.constructors.firstOrNull { it.parameters.isEmpty() }?.newInstance()
        ) {
            "No Context or empty constructor."
        }
    }

    private fun loadAppClass(androidContext: Context, className: String): Class<*> {
        val appInfo =
            PkgUtils.getApplicationInfo(androidContext, BuildPropInternal.SHORTX_EXT_APP_PKG_NAME)
        logger.d("appInfo: $appInfo")

        val sourceDir = appInfo.sourceDir
        logger.d("sourceDir: $sourceDir")

        var librarySearchPath: String = sourceDir + "!/lib/" + Build.SUPPORTED_ABIS[0]
        val systemLibrarySearchPath = System.getProperty("java.library.path")
        if (!TextUtils.isEmpty(systemLibrarySearchPath)) {
            librarySearchPath += File.pathSeparatorChar + systemLibrarySearchPath
        }
        logger.d("librarySearchPath: $librarySearchPath")

        var optimizedDirectory = "."
        var optimizedDirectoryFile = File(optimizedDirectory)
        if (!optimizedDirectoryFile.exists()
            || !optimizedDirectoryFile.isDirectory
            || !optimizedDirectoryFile.canWrite()
            || !optimizedDirectoryFile.canExecute()
        ) {
            optimizedDirectory = File(
                androidContext.cacheDir,
                "opt-${BuildPropInternal.APP_VERSION_CODE}"
            ).absolutePath
            optimizedDirectoryFile = File(optimizedDirectory)
            optimizedDirectoryFile.mkdirs()

            logger.d("optimizedDirectory: $optimizedDirectory")

            try {
                Os.chmod(optimizedDirectory, 457)
            } catch (e: ErrnoException) {
                System.err.println(e.message)
            }
        }

        return kotlin.runCatching {
            val classLoader = DexClassLoader(
                sourceDir,
                optimizedDirectory,
                librarySearchPath,
                ClassLoader.getSystemClassLoader()
            )
            classLoader.loadClass(className)
        }.getOrElse {
            logger.e(it, "Load error")
            throw Error(it)
        }
    }
}