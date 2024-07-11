package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.system.ErrnoException
import android.system.Os
import android.text.TextUtils
import com.google.protobuf.Message
import dalvik.system.DexClassLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File


@OptIn(ExperimentalCoroutinesApi::class)
object FindImagePositionFromScreen : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindImagePositionFromScreen when device is not interactive"
        }

        val target = requireNotNull(BitmapUtils.base64ToBitmap(d.imageBase64)) {
            "Fail to decode bitmap from base64."
        }
        logger.d("target: $target")

        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.destroy()
        }
        logger.d("screen: $screen")

        val cvApiClass


        val pointStatus = point.toString()

        logger.d("pointStatus: $pointStatus")
    }
}


object AppClassHelper {
    private val logger = Logger("AppClassHelper")

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
        if (!optimizedDirectoryFile.exists() || !optimizedDirectoryFile.isDirectory || !optimizedDirectoryFile.canWrite() || !optimizedDirectoryFile.canExecute()) {
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