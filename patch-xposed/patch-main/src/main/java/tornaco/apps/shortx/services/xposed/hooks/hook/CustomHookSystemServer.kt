package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookUtil.compileExpressions
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookUtil.installHookForRules

object CustomHookSystemServer {
    private const val IS_FEATURE_ENABLED = true
    private const val LOG_REDIRECT = "method_hook"

    fun hookAndroidSystem(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!IS_FEATURE_ENABLED) return
        ShortXService.shortXContext.serviceLifecycle.registerLifecycleSystemReadyCallback {
            val log: (String) -> Unit = {
                ShortXService.serviceBinder.log(it, LOG_REDIRECT)
            }
            log("System ready.")
            installHookForRules(
                rules = ShortXService.shortXContext.rule.getAllEnabledRules(),
                das = ShortXService.shortXContext.rule.getAllDirectAction(),
                classLoader = lpparam.classLoader,
                currentProcessPkgName = "android",
                onMethodCall = { param, hook ->
                    val contextDataList = hook.compileExpressions(param, log)
                    ShortXService.shortXContext.rule.publishFactChecked(
                        factDefaultInstance = MethodHook.getDefaultInstance(),
                    ) {
                        FactPublishData(
                            reason = "Custom hook",
                            fact = MethodHook.newBuilder(hook).build()
                        ) {
                            contextDataList.associate {
                                it.first to it.second
                            }
                        }
                    }
                },
                lifecycle = MethodHookLifecycle.LoadedPackage,
                log = log
            )
        }
    }
}