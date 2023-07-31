package tornaco.apps.shortx.services.wm

import android.content.ComponentName
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Pair
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.uiautomator.imp.UiDevice
import androidx.test.uiautomator.imp.UiSelector
import androidx.test.uiautomator.imp.adapter.InstrumentationAdapter
import github.tornaco.android.thanos.core.app.ThanosManagerNative
import kotlinx.coroutines.delay
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService

class WindowManagerService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "WindowManagerService") {

    private val automationManager by lazy { UiAutomationManager(handler) }
    private var screenSize: Pair<Int, Int>? = null

    private val uiAutoLock = "UiAutoLock"

    override fun systemReady() {
        super.systemReady()
        registerListeners()
    }

    private fun registerListeners() {

    }

    suspend fun withUiAutomation(reason: String, action: (WindowManagerService) -> Unit) {
        ensureAutomationConnected(reason)
        waitUntilConnected()
        action.invoke(this)
    }

    suspend fun waitUntilConnected(timeout: Long = 3000): Boolean {
        val eachDelay = 100
        val totalWaitTimes = timeout / eachDelay
        var waitTimes = 0
        while (!automationManager.isConnected) {
            if (waitTimes > totalWaitTimes) {
                logger.w("waitUntilConnected timeout after: ${timeout}ms")
                return false
            }
            logger.w("Not connected, wait ${eachDelay}ms")
            delay(100)
            waitTimes += 1
        }
        return true
    }

    private fun ensureAutomationConnected(reason: String) {
        synchronized(uiAutoLock) {
            kotlin.runCatching {
                if (!automationManager.isConnected) {
                    logger.w("ensureAutomationConnected, connect: $reason")
                    disableThanoxProfileAutoEngine()
                    automationManager.connect()
                    automationManager.setOnAccessibilityEventListener {
                        logger.d("OnAccessibilityEvent: $it")
                        if (it.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                            val root = automationManager.rootInActiveWindow
                            if (root != null) {
                                val texts = mutableListOf<kotlin.Pair<Rect, String>>()
                                discoverText(texts, root)
                                logger.d("texts: ${texts.joinToString(System.lineSeparator())}")
                            }
                        }
                    }
                }
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "ensureAutomationConnected")
            }
        }
    }

    private fun discoverText(
        list: MutableList<kotlin.Pair<Rect, String>>,
        root: AccessibilityNodeInfo
    ) {
        if (root.childCount <= 0) {
            if (root.isVisibleToUser && root.text != null) {
                list.add(root.boundsInScreen to root.text.toString())
            }
        } else {
            for (i in 0 until root.childCount) {
                val child = root.getChild(i)
                if (child != null) discoverText(list, child)
            }
        }
    }

    private fun disableThanoxProfileAutoEngine() {
        kotlin.runCatching {
            if (ThanosManagerNative.isServiceInstalled()) {
                val thanox = ThanosManagerNative.getDefault()
                val currentEnabled = thanox.profileManager.isProfileEngineUiAutomationEnabled
                logger.w("disableThanoxProfileAutoEngine, current thanox enable state: ${currentEnabled}")
                if (currentEnabled) {
                    thanox.profileManager.isProfileEngineUiAutomationEnabled = false
                    logger.w("disableThanoxProfileAutoEngine success.")
                }
            }
        }
    }

    private fun ensureAutomationDisconnected(reason: String) {
        synchronized(uiAutoLock) {
            kotlin.runCatching {
                logger.w("ensureAutomationDisConnected:  $reason")
                automationManager.disconnect()
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "ensureAutomationDisConnected")
            }
        }
    }

    fun waitForIdle(): Boolean {
        synchronized(uiAutoLock) {
            if (!isSystemReady) return false
            if (!automationManager.isConnected) {
                logger.w("findAndClickViewByViewIdInternal, not connected.")
                return false
            }
            val uiDevice = UiDevice.getInstance(
                InstrumentationAdapter(
                    context,
                    automationManager
                )
            )
            uiDevice.waitForIdle()
            logger.d("waitForIdle finish")
            return true
        }
    }

    fun findAndClickViewByText(
        text: String,
        isRegex: Boolean = false,
        timeout: Long = 1000
    ): Boolean {
        synchronized(uiAutoLock) {
            if (!isSystemReady) return false
            if (!automationManager.isConnected) {
                logger.w("findAndClickViewByViewIdInternal, not connected.")
                return false
            }

            val uiDevice = UiDevice.getInstance(
                InstrumentationAdapter(
                    context,
                    automationManager
                )
            )
            val uiObject = if (isRegex) {
                uiDevice.findObject(UiSelector().textMatches(text))
            } else {
                uiDevice.findObject(UiSelector().textContains(text))
            }
            logger.d("findAndClickViewByTextInternal: $uiObject")
            if (!uiObject.waitForExists(timeout)) {
                logger.d("findAndClickViewByTextInternal: waitForExists timeout")
                return false
            }
            logger.d("findAndClickViewByTextInternal, call click")
            return uiObject.click()
        }
    }

    fun findAndClickViewByViewId(
        viewId: String,
        targetComponent: ComponentName,
        timeout: Long,
    ): Boolean {
        synchronized(uiAutoLock) {
            if (!isSystemReady) return false
            if (!automationManager.isConnected) {
                logger.w("findAndClickViewByViewIdInternal, not connected.")
                return false
            }
            logger.d("findAndClickViewByViewIdInternal: $viewId")

            val uiDevice = UiDevice.getInstance(
                InstrumentationAdapter(
                    context,
                    automationManager
                )
            )
            val uiObject = uiDevice.findObject(
                UiSelector().resourceId(viewId)
            )
            logger.d("findAndClickViewByViewIdInternal: $uiObject")
            if (!uiObject.waitForExists(timeout)) {
                logger.d("findAndClickViewByViewIdInternal: waitForExists timeout")
                return false
            }
            return uiObject.click()
        }
    }

    fun click(x: Int, y: Int) {
        synchronized(uiAutoLock) {
            if (!isSystemReady) return
            logger.d("click ($x, $y), execute.")
            if (!automationManager.isConnected) {
                logger.w("click ($x, $y), not connected.")
                return
            }
            val uiDevice =
                UiDevice.getInstance(
                    InstrumentationAdapter(
                        context,
                        automationManager
                    )
                )
            uiDevice.click(x, y)
        }
    }

    fun swipe(startX: Int, startY: Int, endX: Int, endY: Int, time: Long): Boolean {
        val steps = time / 5
        synchronized(uiAutoLock) {
            if (!isSystemReady) return false
            logger.d("swipe ($startX, $startY) -> ($endX, $endY), $steps steps, execute.")
            if (!automationManager.isConnected) {
                logger.w("swipe, not connected.")
                return false
            }
            val uiDevice =
                UiDevice.getInstance(
                    InstrumentationAdapter(
                        context,
                        automationManager
                    )
                )
            return uiDevice.swipe(startX, startY, endX, endY, steps.toInt())
        }
    }

    fun getScreenSize(): IntArray {
        if (screenSize == null) {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            screenSize = Pair(width, height)
        }
        return intArrayOf(screenSize!!.first, screenSize!!.second)
    }
}