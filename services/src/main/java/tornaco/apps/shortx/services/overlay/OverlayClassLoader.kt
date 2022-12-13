package tornaco.apps.shortx.services.overlay

import android.content.Context
import dalvik.system.PathClassLoader
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import util.Reflector
import java.io.File

class OverlayClassLoader(private val context: Context) {
    fun getShortXObject(): Any? {
        val apkPath: File? = PkgUtils.getApkPath(BuildProp.APP_PACKAGE_NAME)
        overlayLogger.d("Apk path $apkPath")
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
            overlayLogger.d("overlayClass: $overlayClass")
            val constructor = overlayClass.getConstructor(Context::class.java)
            val obj = constructor.newInstance(context)
            obj
        } catch (e: Throwable) {
            overlayLogger.e(e, "Error getShortXObject")
            null
        }
    }
}

private val overlayLogger = Logger("Overlay")

fun Any.show() {
    runCatching { Reflector().callMethod(this, "show") }.onFailure {
        overlayLogger.e(it, "show")
    }
}

fun Any.isShow(): Boolean {
    return runCatching { return Reflector().callMethod(this, "isShow") as Boolean }
        .getOrElse {
            overlayLogger.e(it, "isShow")
            false
        }
}

fun Any.dismiss() {
    runCatching { Reflector().callMethod(this, "dismiss") }.onFailure {
        overlayLogger.e(it, "dismiss")
    }
}