package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.platform.Classes.a11WMClass
import tornaco.apps.shortx.core.platform.Classes.abstractAccessibilityServiceConnectionClass
import tornaco.apps.shortx.core.platform.Classes.accessibilitySecurityPolicyClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object ASPHook {
    private val logger = Logger("ShortXHook.ASPHook")

    fun hook(lpparam: ISystemServerLoaded.Param) = runCatching {
        hookRWL(lpparam)
        hookAA(lpparam)
        hookGW(lpparam)
        hookGWBL(lpparam)
        hookGDL(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    private fun hookRWL(lpparam: ISystemServerLoaded.Param) =
        runCatching {
            afterMethod(
                lpparam.classLoader.accessibilitySecurityPolicyClass(),
                "canRetrieveWindowsLocked"
            ) { param ->
                param.logParamsAndResOnDebug(logger)
                param.returnAndSkip(true)
            }
        }.onFailure {
            logger.e(it, "hookRWL")
        }

    private fun hookAA(lpparam: ISystemServerLoaded.Param) =
        runCatching {
            afterMethod(
                lpparam.classLoader.accessibilitySecurityPolicyClass(),
                "checkAccessibilityAccess"
            ) { param ->
                param.logParamsAndResOnDebug(logger)
                param.returnAndSkip(true)
            }
        }.onFailure {
            logger.e(it, "hookAA")
        }

    private fun hookGW(lpparam: ISystemServerLoaded.Param) =
        runCatching {
            afterMethod(
                lpparam.classLoader.abstractAccessibilityServiceConnectionClass(),
                "getWindows"
            ) { param ->
                param.logParamsAndResOnDebug(logger)
            }
        }.onFailure {
            logger.e(it, "hookGW")
        }

    private fun hookGWBL(lpparam: ISystemServerLoaded.Param) =
        runCatching {
            afterMethod(
                lpparam.classLoader.abstractAccessibilityServiceConnectionClass(),
                "getWindowsByDisplayLocked"
            ) { param ->
                param.logParamsAndResOnDebug(logger)
            }
        }.onFailure {
            logger.e(it, "hookGWBL")
        }

    private fun hookGDL(lpparam: ISystemServerLoaded.Param) =
        runCatching {
            afterMethod(
                lpparam.classLoader.a11WMClass(),
                "getDisplayListLocked"
            ) { param ->
                param.logParamsAndResOnDebug(logger)
            }
        }.onFailure {
            logger.e(it, "hookGDL")
        }
}
