package tornaco.apps.shortx.services.wm

import android.content.ComponentName
import android.content.Context
import android.util.DisplayMetrics
import android.util.Pair
import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.adapter.InstrumentationAdapter
import kotlinx.coroutines.delay
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.postSafety

class WindowManagerService(shortXContext: ShortXContext) : SystemService(shortXContext) {

    private val automationManager = UiAutomationManager()
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
        ensureAutomationDisconnected(reason)
        // Just incase.
        delay(200)
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

    fun ensureAutomationConnected(reason: String) {
        synchronized(uiAutoLock) {
            kotlin.runCatching {
                logger.w("ensureAutomationConnected: $reason")
                automationManager.connect()
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "ensureAutomationConnected")
            }
        }
    }

    fun ensureAutomationDisconnected(reason: String) {
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
            val uiDevice = UiDevice.getInstance(InstrumentationAdapter(context, automationManager))
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

            val uiDevice = UiDevice.getInstance(InstrumentationAdapter(context, automationManager))
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

            val uiDevice = UiDevice.getInstance(InstrumentationAdapter(context, automationManager))
            val uiObject = uiDevice.findObject(UiSelector().resourceId(viewId))
            logger.d("findAndClickViewByViewIdInternal: $uiObject")
            if (!uiObject.waitForExists(timeout)) {
                logger.d("findAndClickViewByViewIdInternal: waitForExists timeout")
                return false
            }
            return uiObject.click()
        }
    }

    fun clickDelayed(x: Int, y: Int, delay: Long) {
        synchronized(uiAutoLock) {
            logger.d("clickDelayed ($x, $y) delay: $delay, execute.")
            if (!isSystemReady) return
            handler.postSafety(delay = delay) {
                logger.d("clickDelayed ($x, $y), execute.")
                if (!automationManager.isConnected) {
                    logger.w("clickDelayed ($x, $y), not connected.")
                    return@postSafety
                }
                val uiDevice =
                    UiDevice.getInstance(InstrumentationAdapter(context, automationManager))
                uiDevice.click(x, y)
            }
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