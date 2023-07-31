package tornaco.apps.shortx.services.wm

import android.app.UiAutomation.OnAccessibilityEventListener
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
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
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.gesture.HotSeat
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.UniversalCopyView
import tornaco.apps.shortx.services.ui.i18NIns

class WindowManagerService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "WindowManagerService"), OnAccessibilityEventListener {

    private val automationManager by lazy { UiAutomationManager(handler) }
    private var screenSize: Pair<Int, Int>? = null

    private val uiAutoLock = "UiAutoLock"

    private val showingHints: MutableSet<UniversalCopyView> = mutableSetOf()

    override fun systemReady() {
        super.systemReady()
        registerListeners()
    }

    private fun registerListeners() {

    }

    suspend fun withUiAutomation(reason: String, action: suspend (WindowManagerService) -> Unit) {
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
                    automationManager.setOnAccessibilityEventListener(this)
                }
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "ensureAutomationConnected")
            }
        }
    }

    private fun disableThanoxProfileAutoEngine() {
        kotlin.runCatching {
            if (ThanosManagerNative.isServiceInstalled()) {
                val thanox = ThanosManagerNative.getDefault()
                val currentEnabled = thanox.profileManager.isProfileEngineUiAutomationEnabled
                logger.w("disableThanoxProfileAutoEngine, current thanox enable state: $currentEnabled")
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

    suspend fun waitForIdle(): Boolean {
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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        logger.p("OnAccessibilityEvent: $event")
    }

    fun enableUniversalCopy() {
        systemServiceScope.launch {
            // Dismiss first.
            showingHints.forEach {
                kotlin.runCatching { it.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView =
                UniversalCopyView(
                    context = context,
                    hotSeats = Edge.values().filterNot { it == Edge.UNRECOGNIZED }.map {
                        HotSeat(it, Color.BLUE)
                    },
                    areaSize = 4._dp,
                    onClick = { x, y ->
                        disableUniversalCopy {
                            lookUpTextFromClick(x, y)
                        }
                    }
                ).apply {
                    attach()
                }

            showingHints.add(guideLineView)
        }
    }

    private fun lookUpTextFromClick(x: Float, y: Float) {
        val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
        logger.d("rootInActiveWindow: $root")
        val screenTexts = mutableListOf<kotlin.Pair<RectF, String>>()
        if (root != null) {
            discoverTextFromAccessibilityNode(screenTexts, root)
        }
        logger.p("UniversalCopyView click: $screenTexts")

        val matchText = screenTexts.firstOrNull {
            it.first.contains(x, y)
        }
        logger.p("matchText [$x $y]: $matchText")

        systemServiceScope.launchSafety {
            if (matchText == null) {
                shortXContext.ui.showDanmu(
                    null,
                    i18NIns["ui.action.enable.universal.copy.text.not.found"]
                )
            } else {
                UniversalCopyEditDialog(context, matchText.second).show()
            }
        }
    }

    private fun disableUniversalCopy(action: suspend () -> Unit) {
        systemServiceScope.launch {
            showingHints.forEach {
                kotlin.runCatching { it.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()
            action()
        }
    }

    private fun discoverTextFromAccessibilityNode(
        list: MutableList<kotlin.Pair<RectF, String>>,
        root: AccessibilityNodeInfo
    ) {
        if (root.childCount <= 0) {
            if (root.isVisibleToUser && root.text != null) {
                val rect = root.boundsInScreen
                list.add(
                    RectF(
                        rect.left.toFloat(),
                        rect.top.toFloat(),
                        rect.right.toFloat(),
                        rect.bottom.toFloat()
                    ) to root.text.toString()
                )
            }
        } else {
            for (i in 0 until root.childCount) {
                val child = root.getChild(i)
                if (child != null && child.isVisibleToUser) discoverTextFromAccessibilityNode(
                    list,
                    child
                )
            }
        }
    }
}
