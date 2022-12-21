package tornaco.apps.shortx.services.overlay

import android.content.Context
import dalvik.system.PathClassLoader
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import java.io.File
import java.util.function.Consumer

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

    fun newComposeOverlayObject(
        initialPos: android.util.Pair<Int, Int>,
        onPosChange: Consumer<android.util.Pair<Int, Int>>,
        autoCollapse: Boolean,
        settings: List<String>,
        onClick: (Int) -> Unit
    ): Any? {
        return overlayClass?.let {
            kotlin.runCatching {
                val constructor =
                    it.getConstructor(
                        Context::class.java,
                        android.util.Pair::class.java,
                        Consumer::class.java,
                        List::class.java,
                        Boolean::class.java,
                        Consumer::class.java
                    )
                val obj = constructor.newInstance(
                    context,
                    initialPos,
                    onPosChange,
                    settings,
                    autoCollapse,
                    object : Consumer<Int> {
                        override fun accept(button: Int) {
                            onClick(button)
                        }
                    })
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

fun java.util.function.Function<String, Boolean>.show() {
    runCatching { apply("show") }.onFailure {
        overlayLogger.e(it, "show")
    }
}

fun java.util.function.Function<String, Boolean>.isShow(): Boolean {
    return runCatching { return apply("isShow") }
        .getOrElse {
            overlayLogger.e(it, "isShow")
            false
        }
}

fun java.util.function.Function<String, Boolean>.dismiss() {
    runCatching { apply("dismiss") }.onFailure {
        overlayLogger.e(it, "dismiss")
    }
}