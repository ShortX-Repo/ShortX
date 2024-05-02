package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

object CustomHook {
    private val logger = Logger("CustomHook")

    fun hookAndroidSystem(lpparam: XC_LoadPackage.LoadPackageParam) {
        ShortXService.registerLifecycleSystemReadyCallback {
            logger.d("System ready.")
            ShortXService.shortXContext.rule.getAllEnabledRules().forEach { rule ->
                rule.factsAndQuitFacts.forEach { proto ->
                    if (proto is_ MethodHook::class.java) {
                        val mh = proto unpack_ MethodHook::class.java
                        logger.d("method hook for package: ${mh.packageName}")
                        if (mh.packageName == "android") {
                            logger.d("android method hook: ${mh.className}-${mh.methodName}")
                            runCatching { mh.install(lpparam) }.onFailure {
                                logger.e(it, "hook method fail.")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun MethodHook.install(lpparam: XC_LoadPackage.LoadPackageParam) {
        val clazz = Reflections.findClass(className, lpparam.classLoader)
        afterMethod(clazz, methodName) {
            logger.d("afterMethod $className-$methodName")
        }
    }
}