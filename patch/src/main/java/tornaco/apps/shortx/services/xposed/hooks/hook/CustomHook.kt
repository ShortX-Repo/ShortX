package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

object CustomHook {
    private const val LOG_REDIRECT = "method_hook"

    fun hookAndroidSystem(lpparam: XC_LoadPackage.LoadPackageParam) {
        ShortXService.registerLifecycleSystemReadyCallback {
            ShortXService.serviceBinder.log("System ready.", LOG_REDIRECT)
            installHookForRules(
                rules = ShortXService.shortXContext.rule.getAllEnabledRules(),
                classLoader = lpparam.classLoader,
                currentProcessPkgName = "android",
                onMethodCall = { param, hook ->
                    ShortXService.shortXContext.rule.publishFact(
                        "Custom hook",
                        MethodHook.newBuilder(hook).build()
                    ) {
                        emptyMap()
                    }
                },
                lifecycle = MethodHookLifecycle.LoadedPackage,
                log = {
                    ShortXService.serviceBinder.log(it, LOG_REDIRECT)
                }
            )
        }
    }

    fun hookApp(
        classLoader: ClassLoader?,
        currentProcessPkgName: String,
        lifecycle: MethodHookLifecycle
    ) {
        installHookForRules(
            rules = shortXManager.getAllRules(),
            classLoader = classLoader,
            currentProcessPkgName = currentProcessPkgName,
            lifecycle = lifecycle,
            onMethodCall = { param, hook ->
                shortXManager.onHookedMethodCalled(hook, emptyList())
            },
            log = {
                shortXManager.log(it, LOG_REDIRECT)
            }
        )
    }

    private fun installHookForRules(
        rules: List<Rule>,
        classLoader: ClassLoader?,
        currentProcessPkgName: String,
        lifecycle: MethodHookLifecycle,
        onMethodCall: (MethodHookParam, MethodHook) -> Unit,
        log: (String) -> Unit
    ) {
        log("Checking rules for app: $currentProcessPkgName - $lifecycle")
        rules.filter { it.isEnabled }.forEach { rule ->
            rule.factsAndQuitFacts.forEach { proto ->
                if (proto is_ MethodHook::class.java) {
                    val mh = proto unpack_ MethodHook::class.java
                    if (mh.packageName == currentProcessPkgName && mh.lifecycle == lifecycle) {
                        log("Installing method hook: ${mh.className}-${mh.methodName}")
                        mh.install(classLoader, onMethodCall, log)
                    }
                }
            }
        }
    }


    private fun MethodHook.install(
        classLoader: ClassLoader?,
        onMethodCall: (MethodHookParam, MethodHook) -> Unit,
        log: (String) -> Unit
    ) {
        kotlin.runCatching {
            val clazz = Reflections.findClass(className, classLoader)
            log("Class instance: $clazz")
            afterMethod(clazz, methodName) {
                log("----> onMethodCall $className-$methodName")
                onMethodCall(it, this)
            }
        }.onFailure {
            log(it.stackTraceToString())
        }
    }
}