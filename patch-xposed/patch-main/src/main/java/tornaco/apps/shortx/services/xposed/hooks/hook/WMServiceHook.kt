package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.platform.Classes.windowManagerServiceClass
import tornaco.apps.shortx.core.platform.Classes.windowStateClass
import tornaco.apps.shortx.core.platform.Methods.systemReady
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionUtils
import util.Reflections
import java.lang.reflect.Method

object WMServiceHook {
    private val logger = Logger("ShortXHook.WMServiceHook")

    fun hook(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        hookSystemReady(lpparam)
        hookNotifyFocusChanged(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    private fun hookSystemReady(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.windowManagerServiceClass(),
                systemReady
            ) { param ->
                logger.d("systemReady: ${param.thisObject}")
                ServiceHolder.windowManagerService = param.thisObject ?: return@afterMethod
            }
        }.onFailure {
            logger.e(it, "hookSystemReady")
        }

    private fun hookNotifyFocusChanged(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.windowManagerServiceClass(),
                "notifyFocusChanged"
            ) { param ->
                logger.d("notifyFocusChanged: ${param.args.map { it }}")
                val focused = GetFocusedWindowMethodCall.call(param.thisObject ?: return@afterMethod)
                logger.d("notifyFocusChanged: $focused")
                if (focused != null) {
                    val ownerUid = Reflections.getIntField(focused, "mOwnerUid")
                    val windowTag =
                        Reflections.callMethod(focused, "getWindowTag")?.toString().orEmpty()
                    logger.d("notifyFocusChanged: $focused, owner: $ownerUid, tag: $windowTag")
                    ShortXService.shortXContext.wm.onWindowFocusChanged(ownerUid, windowTag)
                }
            }
        }.onFailure {
            logger.e(it, "hookNotifyFocusChanged")
            // Alt hook.
            hookReportFocusChangedSerialized(lpparam)
        }

    // WindowState.
    // For OneUI 7, no WMS.notifyFocusChanged method...
    private fun hookReportFocusChangedSerialized(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.windowStateClass(),
                "reportFocusChangedSerialized"
            ) { param ->
                logger.d("reportFocusChangedSerialized: ${param.args.map { it }}")
                val wms = Reflections.getObjectField(param.thisObject ?: return@afterMethod, "mWmService")
                val focused = GetFocusedWindowMethodCall.call(wms)
                logger.d("reportFocusChangedSerialized: $focused")
                if (focused != null) {
                    val ownerUid = Reflections.getIntField(focused, "mOwnerUid")
                    val windowTag =
                        Reflections.callMethod(focused, "getWindowTag")?.toString().orEmpty()
                    logger.d("reportFocusChangedSerialized: $focused, owner: $ownerUid, tag: $windowTag")
                    ShortXService.shortXContext.wm.onWindowFocusChanged(ownerUid, windowTag)
                }
            }
        }.onFailure {
            logger.e(it, "hookReportFocusChangedSerialized")
        }
}

object GetFocusedWindowMethodCall {
    private val logger = Logger("ShortXHook.WMServiceHook.GetFocusedWindowMethodCall")
    private var methodToCall: Method? = null
    fun call(wmsIns: Any): Any? {
        if (methodToCall != null) {
            return ReflectionUtils.invokeMethod(methodToCall, wmsIns)
        }

        methodToCall = ReflectionUtils.findMethod(
            wmsIns.javaClass.classLoader.windowManagerServiceClass(),
            "getFocusedWindow"
        )
        logger.d("methodToCall: $methodToCall")
        return ReflectionUtils.invokeMethod(methodToCall, wmsIns)
    }
}

// OPLUS is private super method.
// ShortXHookUtil [android.display]
// afterHookedMethod-class com.android.server.wm.WindowManagerService-notifyFocusChanged
// --- java.lang.NoSuchMethodError: com.android.server.wm.OplusWindowManagerService#getFocusedWindow()#bestmatch
// at util.Reflections.findMethodBestMatch(Reflections.java:335)
// at util.Reflections.findMethodBestMatch(Reflections.java:389)
// at util.Reflections.callMethod(Reflections.java:1198)
// at tornaco.apps.shortx.services.xposed.hooks.hook.WMServiceHook$hookNotifyFocusChanged$1$1.invoke(WMServiceHook.kt:25)
// at tornaco.apps.shortx.services.xposed.hooks.hook.WMServiceHook$hookNotifyFocusChanged$1$1.invoke(WMServiceHook.kt:20)
// at tornaco.apps.shortx.services.xposed.hooks.hook.HooksKt$afterMethod$1.afterHookedMethod(Hooks.kt:80)
// at gIi.ur.Vq.MDTRxfwdvwHv.XposedBridge$LegacyApiSupport.handleAfter(Unknown Source:33)
// at J.callback(Unknown Source:292)
// at LSPHooker_.notifyFocusChanged(Unknown Source:8)
// at com.android.server.wm.WindowManagerService.reportFocusChanged(WindowManagerService.java:5549)
// at com.android.server.wm.InputManagerCallback$$ExternalSyntheticLambda0.accept(Unknown Source:6)
// at com.android.internal.util.function.pooled.PooledLambdaImpl.doInvoke(PooledLambdaImpl.java:281)
// at com.android.internal.util.function.pooled.PooledLambdaImpl.invoke(PooledLambdaImpl.java:204)
// at com.android.internal.util.function.pooled.OmniFunction.run(OmniFunction.java:97)
// at android.os.Handler.handleCallback(Handler.java:942)
// at android.os.Handler.dispatchMessage(Handler.java:99)
// at android.os.Looper.loopOnce(Looper.java:240)
// at android.os.Looper.loop(Looper.java:351)
// at android.os.HandlerThread.run(HandlerThread.java:67)
// at com.android.server.ServiceThread.run(ServiceThread.java:44)
