package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import android.widget.Toast
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.ActionEvaluator
import tornaco.apps.shortx.core.rule.engine.StopExecuteException
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.rule.actor.MVELExecutor
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.DataImpl
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl
import tornaco.apps.shortx.services.util.ContextMapUtil.inflateContextMap

class ActionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val handler: Handler
) : ActionEvaluator {
    private val hw by lazy {
        HwImpl(context, shortXContext)
    }
    private val data by lazy {
        DataImpl(context, shortXContext)
    }

    override suspend fun evaluate(action: ProtoAny, contextMap: Map<String, Any>) {
        if (action is_ StartActivity::class.java) {
            val startActivity = action unpack_ StartActivity::class.java
            tornaco.apps.shortx.services.rule.actor.StartActivity(
                context, startActivity.componentNameAsString
            ).invoke()
        } else if (action is_ TakeScreenshot::class.java) {
            tornaco.apps.shortx.services.rule.actor.TakeScreenshot(context, handler).invoke()
        } else if (action is_ ExpandNotification::class.java) {
            tornaco.apps.shortx.services.rule.actor.ExpandNotification(context).invoke()
        } else if (action is_ ShellCommand::class.java) {
            val sc = action unpack_ ShellCommand::class.java
            tornaco.apps.shortx.services.rule.actor.ShellCommand(context, shortXContext, sc.command)
                .invoke()
        } else if (action is_ InputText::class.java) {
            val sc = action unpack_ InputText::class.java
            tornaco.apps.shortx.services.rule.actor.InputText(context, shortXContext, sc.text)
                .invoke()
        } else if (action is_ ShowToast::class.java) {
            val t = action unpack_ ShowToast::class.java
            handler.postSafety {
                Toast.makeText(context, t.message.inflateContextMap(contextMap), Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (action is_ Delay::class.java) {
            val d = action unpack_ Delay::class.java
            delay(maxOf(0, d.time))
        } else if (action is_ ExecuteMVEL::class.java) {
            val d = action unpack_ ExecuteMVEL::class.java
            MVELExecutor(context, shortXContext, d.expression).invoke()
        } else if (action is_ ShowOverlayButton::class.java) {
            val d = action unpack_ ShowOverlayButton::class.java
            val buttonSettingsList = d.buttonSettingsList
            val autoCollapse = d.autoCollapse
            shortXContext.overlay.postShowOverlay(autoCollapse, buttonSettingsList)
        } else if (action is_ HideOverlayButton::class.java) {
            shortXContext.overlay.postHideAllOverlay()
        } else if (action is_ LaunchApp::class.java) {
            val app = action unpack_ LaunchApp::class.java
            shortXContext.appState.launchApp(app.appPkg)
        } else if (action is_ SetWifiEnabled::class.java) {
            val d = action unpack_ SetWifiEnabled::class.java
            if (d.enable) hw.enableWifi() else hw.disableWifi()
        } else if (action is_ SetHotSpotEnabled::class.java) {
            val d = action unpack_ SetHotSpotEnabled::class.java
            if (d.enable) hw.enableTethering() else hw.disableTethering()
        } else if (action is_ SetBTEnabled::class.java) {
            val d = action unpack_ SetBTEnabled::class.java
            if (d.enable) hw.enableBT() else hw.disableBT()
        } else if (action is_ SetGPSEnabled::class.java) {
            val d = action unpack_ SetGPSEnabled::class.java
            if (d.enable) hw.enableLocation() else hw.disableLocation()
        } else if (action is_ SetNFCEnabled::class.java) {
            val d = action unpack_ SetNFCEnabled::class.java
            if (d.enable) hw.enableNfc() else hw.disableNfc()
        } else if (action is_ SetFlashLightEnabled::class.java) {
            val d = action unpack_ SetFlashLightEnabled::class.java
            if (d.enable) hw.enabledFlashlight() else hw.disableFlashlight()
        } else if (action is_ SetDataEnabled::class.java) {
            val d = action unpack_ SetDataEnabled::class.java
            if (d.hasSpecificSlotId) {
                data.setDataEnabledForSlot(d.slotId, d.enable)
            } else {
                data.isDataEnabled = d.enable
            }
        } else if (action is_ StopApp::class.java) {
            val d = action unpack_ StopApp::class.java
            val pkgs = if (d.pkgSetsList.isNotEmpty()) {
                d.pkgSetsList.pkgSetIdsToPkgs(shortXContext)
            } else {
                d.appPkgList
            }
            shortXContext.appState.forceStopApp(pkgs, "Action Eva- StopApp")
        } else if (action is_ StartAppProcess::class.java) {
            val d = action unpack_ StartAppProcess::class.java
            val pkgs = if (d.pkgSetsList.isNotEmpty()) {
                d.pkgSetsList.pkgSetIdsToPkgs(shortXContext)
            } else {
                d.appPkgList
            }
            shortXContext.appState.startAppProcess(pkgs, "Action Eva- StartAppProcess")
        } else if (action is_ SetAppEnabled::class.java) {
            val d = action unpack_ SetAppEnabled::class.java
            val pkgs = if (d.pkgSetsList.isNotEmpty()) {
                d.pkgSetsList.pkgSetIdsToPkgs(shortXContext)
            } else {
                d.appPkgList
            }
            shortXContext.appState.setAppEnabled(pkgs, d.enable, "Action Eva- SetAppEnabled")
        } else if (action is_ SetAppSuspend::class.java) {
            val d = action unpack_ SetAppSuspend::class.java
            val pkgs = if (d.pkgSetsList.isNotEmpty()) {
                d.pkgSetsList.pkgSetIdsToPkgs(shortXContext)
            } else {
                d.appPkgList
            }
            shortXContext.appState.setAppSuspend(pkgs, d.suspend, "Action Eva- SetAppSuspend")
        } else if (action is_ BreakActionExecute::class.java) {
            throw StopExecuteException("BreakActionExecute action")
        }
    }
}