package tornaco.apps.shortx.services.overlay

import android.content.Context
import dalvik.system.PathClassLoader
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import util.Reflections
import java.io.File

class OverlayClassLoader(private val context: Context) {
    private val logger = Logger("OverlayClassLoader")

    fun getShortXObject(): Any? {
        val apkPath: File? = PkgUtils.getApkPath(BuildProp.APP_PACKAGE_NAME)
        logger.d("Apk path $apkPath")
        if (apkPath != null) {
            return getShortXObject(apkPath)
        }
        return null
    }

    private fun getShortXObject(apkPath: File): Any? {
        return try {
            val appCl: ClassLoader = PathClassLoader(
                apkPath.absolutePath, Thread.currentThread().contextClassLoader
            )
            val overlayClass =
                appCl.loadClass("tornaco.apps.shortx.ui.overlay.ComposeOverlay")
            logger.d("overlayClass: $overlayClass")
            val constructor = overlayClass.getConstructor(Context::class.java)
            val obj = constructor.newInstance(context)
            obj
        } catch (e: Throwable) {
            logger.e(e, "Error getShortXObject")
            null
        }
    }

    fun Any.show() {
        runCatching { Reflections.callMethod(this, "show") }.onFailure {
            logger.e(it, "show")
        }
    }

    fun Any.dismiss() {
        runCatching { Reflections.callMethod(this, "dismiss") }.onFailure {
            logger.e(it, "dismiss")
        }
    }
}