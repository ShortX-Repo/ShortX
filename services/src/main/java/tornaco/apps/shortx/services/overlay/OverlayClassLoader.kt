package tornaco.apps.shortx.services.overlay

import android.content.Context
import dalvik.system.PathClassLoader
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import util.Reflector
import java.io.File

class OverlayClassLoader(private val context: Context) {
    private val overlayClass by lazy {
        kotlin.runCatching {
            val apkPath: File = PkgUtils.getApkPath(BuildProp.APP_PACKAGE_NAME)
            overlayLogger.d("Apk path $apkPath")
            val appCl: ClassLoader = PathClassLoader(
                apkPath.absolutePath, Thread.currentThread().contextClassLoader
            )
            val overlayClass =
                appCl.loadClass("tornaco.apps.shortx.ui.overlay.ComposeOverlay")
            overlayLogger.d("overlayClass: $overlayClass")
            overlayClass
        }.getOrElse {
            overlayLogger.e(it, "Load overlayClass")
            null
        }
    }

    fun newComposeOverlayObject(settings: List<OverlayButtonSetting>): Any? {
        return overlayClass?.let {
            kotlin.runCatching {
                val constructor =
                    it.getConstructor(Context::class.java, List::class.java)
                val obj = constructor.newInstance(context, settings)
                overlayLogger.d("getComposeOverlayObject: $obj")
                obj
            }.getOrElse {
                overlayLogger.e(it, "Load ComposeOverlay object")
                null
            }
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