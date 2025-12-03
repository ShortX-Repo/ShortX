package tornaco.apps.shortx.services.wm

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.os.Handler
import android.view.InputEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.rule.script.api.ScriptShortXApi
import tornaco.apps.shortx.core.util.Logger

class UiAutomationApiImpl(
    private val wms: WindowManagerService,
) : ScriptShortXApi.UiAutomationApi {
    private val logger = Logger("UiAutomationApi")

    override fun isConnected(): Boolean {
        return wms.automationManager.isConnected
    }

    override fun connect() {
        runBlocking {
            wms.withUiAutomation("UiAutomationApi.connect") {
                logger.w("withUiAutomation. connected.")
            }
        }
    }

    override fun disconnect() {
        wms.ensureAutomationDisconnected("UiAutomationApi.disconnect")
    }

    override fun getRootInActiveWindow(): AccessibilityNodeInfo? {
        return wms.automationManager.rootInActiveWindow
    }

    override fun performAccessibilityAction(
        sourceNodeId: Long, windowId: Int, action: Int
    ) {
        wms.automationManager.performAccessibilityAction(sourceNodeId, windowId, action)
    }

    override fun getServiceInfo(): AccessibilityServiceInfo? {
        return wms.automationManager.serviceInfo
    }

    override fun injectInputEvent(
        event: InputEvent?, sync: Boolean
    ): Boolean {
        return wms.automationManager.injectInputEvent(event, sync)
    }

    override fun performGlobalAction(action: Int): Boolean {
        return wms.automationManager.performGlobalAction(action)
    }

    override fun dispatchGesture(
        gesture: GestureDescription,
        callback: AccessibilityService.GestureResultCallback?,
        handler: Handler?
    ): Boolean {
        return wms.automationManager.dispatchGesture(gesture, callback, handler)
    }

    override fun findFocus(focus: Int): AccessibilityNodeInfo? {
        return wms.automationManager.findFocus(focus)
    }

    override fun clearCache(): Boolean {
        return wms.automationManager.clearCache()
    }

    override fun getWindows(): List<AccessibilityWindowInfo?>? {
        return wms.automationManager.windows
    }

    override fun waitForIdle(idleTimeoutMillis: Long, globalTimeoutMillis: Long) {
        wms.waitForIdle()
    }
}