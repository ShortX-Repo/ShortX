package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.ApplicationInfo
import io.github.libxposed.api.XposedInterface

object Api101Runtime {
    private var xposedInterface: XposedInterface? = null

    var isSystemServer: Boolean = false
        private set

    val xposed: XposedInterface
        get() = checkNotNull(xposedInterface) { "XposedInterface is not initialized" }

    val applicationInfo: ApplicationInfo?
        get() = runCatching { xposedInterface?.moduleApplicationInfo }.getOrNull()

    fun init(base: XposedInterface, processName: String?, systemServer: Boolean) {
        xposedInterface = base
        isSystemServer = systemServer
        HookRuntime.useApi101()
    }
}
