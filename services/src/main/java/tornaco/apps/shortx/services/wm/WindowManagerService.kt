package tornaco.apps.shortx.services.wm

import android.app.UiAutomation.OnAccessibilityEventListener
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
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
import tornaco.apps.shortx.core.ui.TextViewNode
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.overlay.IdViewerView
import tornaco.apps.shortx.ui.overlay.UniversalCopyView

class WindowManagerService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "ShortXWMS"), OnAccessibilityEventListener {

    private val automationManager by lazy { UiAutomationManager(handler) }
    private var screenSize: Pair<Int, Int>? = null

    private val uiAutoLock = "UiAutoLock"

    private val showingUCHints: MutableSet<UniversalCopyView> = mutableSetOf()
    private val showingIVHints: MutableSet<IdViewerView> = mutableSetOf()

    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var onAccessibilityEvent: ((AccessibilityEvent) -> Unit)? = null

    fun setOnAccessibilityEvent(onAccessibilityEvent: ((AccessibilityEvent) -> Unit)?) {
        this.onAccessibilityEvent = onAccessibilityEvent
    }

    override fun systemReady() {
        super.systemReady()
        registerListeners()
    }

    private fun registerListeners() {

    }

    fun performAccessibilityAction(sourceNodeId: Long, windowId: Int, action: Int) {
        automationManager.performAccessibilityAction(sourceNodeId, windowId, action)
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
        isRegex: Boolean = false,
        timeout: Long,
    ): Boolean {
        synchronized(uiAutoLock) {
            if (!isSystemReady) return false
            if (!automationManager.isConnected) {
                logger.w("findAndClickViewByViewId, not connected.")
                return false
            }
            logger.d("findAndClickViewByViewId: $viewId")

            val uiDevice = UiDevice.getInstance(
                InstrumentationAdapter(
                    context,
                    automationManager
                )
            )
            val uiObject = if (isRegex) {
                uiDevice.findObject(UiSelector().resourceIdMatches(viewId))
            } else {
                uiDevice.findObject(UiSelector().resourceId(viewId))
            }
            logger.d("findAndClickViewByViewId: $uiObject")
            if (!uiObject.waitForExists(timeout)) {
                logger.d("findAndClickViewByViewId: waitForExists timeout")
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
        onAccessibilityEvent?.invoke(event)
    }

    fun getAllTextsFromScreen(): List<TextViewNode> {
        val screenTexts = mutableListOf<TextViewNode>()
        val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
        logger.d("getAllTextsFromScreen rootInActiveWindow: $root")
        val discoverStartTimeMillis = System.currentTimeMillis()
        if (root != null) {
            discoverTextFromAccessibilityNode(screenTexts, root)
        }
        logger.d("discoverTextFromAccessibilityNode take ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
        return screenTexts
    }

    fun getAllNodesFromScreen(): List<ViewNode> {
        val nodes = mutableListOf<ViewNode>()
        val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
        logger.d("getAllNodesFromScreen rootInActiveWindow: $root")
        val discoverStartTimeMillis = System.currentTimeMillis()
        if (root != null) {
            discoverViewIdFromAccessibilityNode(nodes, root)
        }
        logger.d("discoverViewIdFromAccessibilityNode take ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
        return nodes
    }

    fun enableUniversalCopy() {
        disableUniversalCopy {
            val screenTexts = mutableListOf<TextViewNode>()

            val guideLineView =
                UniversalCopyView(
                    context = context,
                    onClick = { x, y ->
                        disableUniversalCopy {
                            lookUpTextFromClick(screenTexts, x, y)
                        }
                    }
                )

            showingUCHints.add(guideLineView)
            mainHandler.postSafety {
                guideLineView.attach()
            }

            val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
            logger.d("rootInActiveWindow: $root")

            val discoverStartTimeMillis = System.currentTimeMillis()
            if (root != null) {
                discoverTextFromAccessibilityNode(screenTexts, root)
            }
            logger.p("enableUniversalCopy, time taken: ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
            logger.p("enableUniversalCopy, screenTexts: $screenTexts")

            mainHandler.postSafety {
                guideLineView.loadComplete()
            }
        }
    }

    fun enableIdViewer() {
        disableIVViewer {
            val idNodes = mutableListOf<ViewNode>()

            val guideLineView =
                IdViewerView(
                    context = context,
                    onClick = { x, y ->
                        disableIVViewer {
                            lookUpViewNodeFromClick(idNodes, x, y)
                        }
                    }
                )

            showingIVHints.add(guideLineView)

            val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
            logger.d("rootInActiveWindow: $root")

            val discoverStartTimeMillis = System.currentTimeMillis()
            if (root != null) {
                discoverViewIdFromAccessibilityNode(idNodes, root)
            }
            logger.p("enableIdViewer, time taken: ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
            logger.p("enableIdViewer, screenTexts: $idNodes")

            mainHandler.postSafety {
                guideLineView.attach(idNodes)
                guideLineView.loadComplete()
            }
        }
    }

    private fun lookUpTextFromClick(
        screenTexts: List<TextViewNode>,
        x: Float,
        y: Float
    ) {
        val matchText = screenTexts.firstOrNull {
            it.position.contains(x, y)
        }
        logger.p("matchText [$x $y]: $matchText")

        systemServiceScope.launchSafety {
            if (matchText == null) {
                shortXContext.ui.showDanmu(
                    null,
                    i18NIns["ui.action.enable.universal.copy.text.not.found"]
                )
            } else {
                UniversalCopyEditDialog(context, matchText.text).show()
            }
        }
    }


    private fun lookUpViewNodeFromClick(
        nodes: List<ViewNode>,
        x: Float,
        y: Float
    ) {
        // Find child first.
        val snapshotMatchNodes = nodes.filter {
            it.position.contains(x, y)
        }
        val matchNodes = snapshotMatchNodes.toMutableList().apply {
            removeIf { nodeToCheck ->
                snapshotMatchNodes.any {
                    nodeToCheck != it && nodeToCheck.position.contains(it.position)
                }
            }
        }

        logger.p("matchNode [$x $y]: $matchNodes")

        systemServiceScope.launchSafety {
            if (matchNodes.isEmpty()) {
            } else {
                ViewIdViewerNodeSelectorDialog(context, matchNodes).show()
            }
        }
    }

    private fun disableUniversalCopy(action: suspend () -> Unit) {
        mainHandler.postSafety {
            showingUCHints.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "disableUniversalCopy detach fail") }
            }
            showingUCHints.clear()
            systemServiceScope.launchSafety {
                action()
            }
        }
    }

    private fun disableIVViewer(action: suspend () -> Unit) {
        mainHandler.postSafety {
            showingIVHints.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "disableIVViewer detach fail") }
            }
            showingIVHints.clear()
            systemServiceScope.launchSafety {
                action()
            }
        }
    }

    private fun discoverTextFromAccessibilityNode(
        list: MutableList<TextViewNode>,
        root: AccessibilityNodeInfo
    ) {
        if (root.childCount <= 0) {
            if (root.isVisibleToUser && root.text != null) {
                val rect = root.boundsInScreen
                list.add(
                    TextViewNode(
                        text = root.text.toString(),
                        viewId = root.viewIdResourceName ?: "",
                        position = RectF(
                            rect.left.toFloat(),
                            rect.top.toFloat(),
                            rect.right.toFloat(),
                            rect.bottom.toFloat()
                        )
                    )
                )
            }
        } else {
            for (i in 0 until root.childCount) {
                val child = root.getChild(i)
                if (child != null && child.isVisibleToUser) {
                    discoverTextFromAccessibilityNode(
                        list,
                        child
                    )
                }
            }
        }
    }

    private fun discoverViewIdFromAccessibilityNode(
        list: MutableList<ViewNode>,
        root: AccessibilityNodeInfo
    ) {
        if (root.isVisibleToUser) {
            val viewId: String = root.viewIdResourceName ?: ""
            val rect = root.boundsInScreen
            val index = list.size
            list.add(
                ViewNode(
                    viewId = viewId,
                    className = root.className.toString(),
                    text = root.text ?: root.contentDescription,
                    rootPackageName = root.packageName.toString(),
                    sourceNodeId = root.sourceNodeId,
                    windowId = root.windowId,
                    position = RectF(
                        rect.left.toFloat(),
                        rect.top.toFloat(),
                        rect.right.toFloat(),
                        rect.bottom.toFloat()
                    ),
                    color = ColorPalette[index % ColorPalette.size]
                )
            )

            if (root.childCount > 0) {
                for (i in 0 until root.childCount) {
                    val child = root.getChild(i)
                    if (child != null) {
                        discoverViewIdFromAccessibilityNode(list, child)
                    }
                }
            }
        }
    }
}


private val ColorPalette = arrayOf(
    // MTR
    Color(0xffAE213B),
    Color(0xff11254A),
    Color(0xffFF0000),
    Color(0xff1A9431),
    Color(0xff0860A8),
    Color(0xffB5BD00),
    Color(0xffFE7F1D),
    Color(0xffF550A6),
    Color(0xff1C7670),
    Color(0xff5EB6E4),
    Color(0xffA40084),
    Color(0xff9A3B26),

    // Nintendo Switch
    Color(0xffFF5145),
    Color(0xff00BBDF),
    Color(0xffF8B62E),
    Color(0xffBAD600),
    Color(0xff19D719),
    Color(0xffF02D7D),
    Color(0xffFFDC00),
    Color(0xffC88C32),

    // Google
    Color(0xff4586F3),
    Color(0xffEB4334),
    Color(0xffFBBD06),
    Color(0xff35AA53),
)

private fun Color(colorValue: Long) = Color.argb(
    (colorValue shr 24).toInt(),
    ((colorValue shr 16) and 0xFF).toInt(),
    ((colorValue shr 8) and 0xFF).toInt(),
    (colorValue and 0xFF).toInt()
)