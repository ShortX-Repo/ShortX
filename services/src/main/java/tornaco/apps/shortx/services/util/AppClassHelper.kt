package tornaco.apps.shortx.services.util


import android.content.Context
import android.os.Build
import android.system.ErrnoException
import android.system.Os
import android.text.TextUtils
import dalvik.system.DexClassLoader
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.data.Dirs
import java.io.File


object AppClassHelper {
    private val logger = Logger("AppClassHelper")

    fun newInstance(androidContext: Context, className: String): Any {
        val clazz = loadAppClass(androidContext, className)
        val constructor =
            requireNotNull(clazz.constructors.firstOrNull { it.parameters.isEmpty() }) {
                "Missing default constructor"
            }
        val instance = constructor.newInstance()
        return instance
    }

    fun loadAppClass(androidContext: Context, className: String): Class<*> {
        val appInfo =
            PkgUtils.getApplicationInfo(androidContext, BuildPropInternal.APP_PACKAGE_NAME)
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
                Dirs.baseServerCacheDir(),
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