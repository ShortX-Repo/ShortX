package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.os.AndroidAppHelper
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookUtil.compileExpressions
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookUtil.installHookForRules

object CustomHookApp {
    private const val IS_FEATURE_ENABLED = true
    private const val LOG_REDIRECT = "method_hook"

    fun hookApp(
        classLoader: ClassLoader?,
        currentProcessPkgName: String?,
        lifecycle: MethodHookLifecycle
    ) {
        // Do not hook self.
        Log.d("hookApp", "currentProcessPkgName: $currentProcessPkgName")
        if (currentProcessPkgName == BuildPropInternal.APP_PACKAGE_NAME) return
        if (!IS_FEATURE_ENABLED) return

        if (currentProcessPkgName == null) {
            val currentPkgNameOnHook = AndroidAppHelper.currentPackageName()
            Log.d(
                "hookApp",
                "currentPkgNameOnHook: $currentPkgNameOnHook"
            )
            if (currentPkgNameOnHook == BuildPropInternal.APP_PACKAGE_NAME) return
        }

        val log: (String) -> Unit = {
            ShortXManagerNative.shortX?.log(it, LOG_REDIRECT)
        }
        installHookForRules(
            rules = shortXManager.getAllRules(),
            das = shortXManager.getAllDirectAction(),
            classLoader = classLoader,
            currentProcessPkgName = currentProcessPkgName,
            lifecycle = lifecycle,
            onMethodCall = { param, hook ->
                val currentPkgName = currentProcessPkgName ?: AndroidAppHelper.currentPackageName()
                if (currentPkgName != BuildPropInternal.APP_PACKAGE_NAME) {
                    val contextDataList = hook.compileExpressions(param, log)
                    shortXManager.onHookedMethodCalled(hook, contextDataList)
                } else {
                    // Prevent hook ShortX
                }
            },
            log = log
        )
    }
}
