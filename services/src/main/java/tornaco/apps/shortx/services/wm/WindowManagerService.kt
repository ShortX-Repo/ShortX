package tornaco.apps.shortx.services.wm

import android.app.UiAutomation.OnAccessibilityEventListener
import android.content.ClipData
import android.graphics.Rect
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.Surface
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.test.uiautomator.imp.UiDevice
import androidx.test.uiautomator.imp.UiSelector
import androidx.test.uiautomator.imp.adapter.InstrumentationAdapter
import github.tornaco.android.thanos.core.app.ThanosManagerNative
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus
import tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus
import tornaco.apps.shortx.core.proto.fact.BackNavDone
import tornaco.apps.shortx.core.proto.fact.BackNavStart
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.ui.TextViewNode
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.ui.XRectF
import tornaco.apps.shortx.core.unKnownPkg
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.windowManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.rule.fact.trigger.rotationToDegree
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.ui.clipbord.ClipboardView
import tornaco.apps.shortx.ui.draw.DrawBoardView
import tornaco.apps.shortx.ui.overlay.IdViewerView
import tornaco.apps.shortx.ui.overlay.UniversalCopyView
import tornaco.apps.shortx.ui.screencap.AreaSelectorView

class WindowManagerService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "ShortXWMS"), OnAccessibilityEventListener {
    val automationManager by lazy { UiAutomationManager(handler) }

    val screenSize by lazy { context.screenSize() }

    private val uiAutoLock = "UiAutoLock"

    private val showingUCHints: MutableSet<UniversalCopyView> = mutableSetOf()
    private val showingIVHints: MutableSet<IdViewerView> = mutableSetOf()
    private val showingDrawerBoard: MutableSet<DrawBoardView> = mutableSetOf()
    private val showingClipboard: MutableSet<ClipboardView> = mutableSetOf()
    private val showingAreaSelector: MutableSet<AreaSelectorView> = mutableSetOf()

    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var _currentFocusedWindowOwner: AppPkg = unKnownPkg
    val currentFocusedWindowOwner get() = _currentFocusedWindowOwner

    private var _currentFocusedWindowTag: String = ""
    val currentFocusedWindowTag get() = _currentFocusedWindowTag

    private var _isNotificationPanelExpanded: Boolean = false
    val isNotificationPanelExpanded get() = _isNotificationPanelExpanded

    var screenRotateDegree: ScreenRotateDegree = ScreenRotateDegree.ScreenRotateDegree_0
        set(value) {
            field = value
            logger.i("screenRotateDegree--> $value")
        }

    private var onAccessibilityEvent: ((AccessibilityEvent) -> Unit)? = null

    val viewScroller by lazy { ViewScroller(this) }

    private var hasUserPresentBeenCalled: Boolean = false

    fun setOnAccessibilityEvent(onAccessibilityEvent: ((AccessibilityEvent) -> Unit)?) {
        this.onAccessibilityEvent = onAccessibilityEvent
    }

    override fun systemReady() {
        super.systemReady()
        registerListeners()
        screenRotateDegree =
            context.windowManager.defaultDisplay.rotation.rotationToDegree().apply {
                logger.i("systemReady, set screenRotateDegree to: $this")
            }
    }

    override fun userPresent(isFirstTime: Boolean) {
        super.userPresent(isFirstTime)
        hasUserPresentBeenCalled = true
    }

    override fun screenOff() {
        super.screenOff()
        logger.d("disableAllOverlayViews since screen off.")
        disableAllOverlayViews { }
    }

    private fun registerListeners() {

    }

    fun onProposedRotationChanged(@Surface.Rotation rotation: Int) {
        systemServiceScope.launchSafety {
            val degree = rotation.rotationToDegree()
            if (degree != ScreenRotateDegree.UNRECOGNIZED) {
                if (degree != shortXContext.wm.screenRotateDegree) {
                    logger.d("onProposedRotationChanged: $rotation $degree")
                    shortXContext.wm.screenRotateDegree = degree
                    shortXContext.rule.publishFactChecked(ScreenRotate.getDefaultInstance()) {
                        FactPublishData(
                            reason = "onProposedRotationChanged",
                            fact = ScreenRotate.newBuilder().setDegree(degree).build()
                        )
                    }
                }
            }
        }
    }

    fun performAccessibilityAction(sourceNodeId: Long, windowId: Int, action: Int) {
        automationManager.performAccessibilityAction(sourceNodeId, windowId, action)
    }

    suspend fun withUiAutomation(reason: String, action: suspend (WindowManagerService) -> Unit) {
        ensureUserPresent()
        ensureAutomationConnected(reason)
        waitUntilConnected()
        action.invoke(this)
    }

    private suspend fun ensureUserPresent() {
        // Note: We must not registerUiAutomation before switchUser of AccManagerService called, or it will
        // not trigger Acc List update.
        // Missing below call:
        // ProxyList [main] clear: java.lang.Throwable
        //	at tornaco.apps.shortx.services.xposed.hooks.hook.ProxyList.clear(Unknown Source:4)
        //	at com.android.server.accessibility.AccessibilityManagerService.readInstalledAccessibilityServiceLocked(AccessibilityManagerService.java:2109)
        //	at com.android.server.accessibility.AccessibilityManagerService.readConfigurationForUserStateLocked(AccessibilityManagerService.java:2854)
        //	at com.android.server.accessibility.AccessibilityManagerService.switchUser(AccessibilityManagerService.java:1820)
        while (!isSystemReady || !hasUserPresentBeenCalled) {
            delay(1000)
            logger.d("ensureUserPresent, user not present, wait.")
        }
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
                    // java.lang.IllegalStateException: UiAutomationService android.accessibilityservice.IAccessibilityServiceClient$Stub$Proxy@1548193already registered!
                    //	at com.android.server.accessibility.UiAutomationManager.registerUiTestAutomationServiceLocked(UiAutomationManager.java:104)
                    //	at com.android.server.accessibility.AccessibilityManagerService.registerUiTestAutomationService(AccessibilityManagerService.java:1249)
                    //	at android.app.UiAutomationConnection.registerUiTestAutomationServiceLocked(UiAutomationConnection.java:558)
                    //	at android.app.UiAutomationConnection.connect(UiAutomationConnection.java:112)
                    automationManager.connect()
                    automationManager.setOnAccessibilityEventListener(this)
                }
            }.onFailure {
                if (it is IllegalStateException && it.message.orEmpty()
                        .contains("already registered")
                ) {
                    shortXContext.watchDog.onUiAutomationAlreadyRegisterError()
                } else {
                    shortXContext.watchDog.onFatalError(it, "ensureAutomationConnected")
                }
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
            logger.i("click ($x, $y), execute@uid - ${Binder.getCallingUid()}.")
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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        logger.p("OnAccessibilityEvent: $event")
        runCatching {
            onAccessibilityEvent?.invoke(event)
        }.onFailure {
            shortXContext.watchDog.onFatalError(it, "onAccessibilityEvent")
        }
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
        root?.recycle()
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
        root?.recycle()
        return nodes
    }

    fun hasFoundNodeOnScreen(check: (ViewNode) -> Boolean): Boolean {
        val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
        logger.d("hasFoundNodeOnScreen rootInActiveWindow: $root")
        val discoverStartTimeMillis = System.currentTimeMillis()
        var result = false
        if (root != null) {
            result = hasFoundViewNode(root, check)
            root.recycle()
        }
        logger.d("hasFoundNodeOnScreen take ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
        return result
    }

    fun enableUniversalCopy() {
        disableAllOverlayViews {
            val screenTexts = mutableListOf<TextViewNode>()

            val guideLineView =
                UniversalCopyView(
                    context = context,
                    onClick = { x, y ->
                        disableAllOverlayViews {
                            lookUpTextFromClick(screenTexts, x, y)
                        }
                    },
                    onDetach = {
                        showingUCHints.remove(it)
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

            root?.recycle()

            mainHandler.postSafety {
                guideLineView.loadComplete()
            }
        }
    }

    fun enableDrawBoard() {
        disableAllOverlayViews {
            val guideLineView =
                DrawBoardView(
                    context = context,
                    i18N = i18NIns
                ) {
                    showingDrawerBoard.remove(it)
                }
            showingDrawerBoard.add(guideLineView)
            mainHandler.postSafety {
                guideLineView.attach()
            }
        }
    }

    fun enableAreaSelector(
        onSelect: (Rect) -> Unit,
        onSelectFullScreen: () -> Unit,
        onDetach: () -> Unit
    ) {
        disableAllOverlayViews {
            val guideLineView =
                AreaSelectorView(
                    context = context,
                    i18N = i18NIns,
                    onDetach = {
                        showingAreaSelector.remove(it)
                        onDetach()
                    },
                    onDragEnd = {
                        val everShowTips = store.read(Settings.BoolSettings.AreaSelectorTipsShown)
                        if (!everShowTips) {
                            showAreaSelectorTips()
                        }
                    },
                    onSelectFullScreen = {
                        onSelectFullScreen()
                    },
                    selected = {
                        onSelect(it)
                        store.write(Settings.BoolSettings.AreaSelectorTipsShown, true)
                    }
                )
            showingAreaSelector.add(guideLineView)
            mainHandler.postSafety {
                guideLineView.attach()
            }
        }
    }

    private fun showAreaSelectorTips() {
        systemServiceScope.launchSafety {
            shortXContext.ui.showDanmu(
                null,
                i18NIns["ui.action.select.screen.area.tip"]
            )
        }
    }

    fun disableAreaSelector() {
        disableAllOverlayViews { }
    }

    fun showClipboard() {
        logger.p("showClipboard: ${shortXContext.input.getAllClipboardText()}")
        disableAllOverlayViews {
            val guideLineView =
                ClipboardView(
                    context = context,
                    i18N = i18NIns,
                    clips = shortXContext.input.getAllClipboardText().reversed(),
                    onDetach = {
                        showingClipboard.remove(it)
                    },
                    removeText = {
                        shortXContext.input.removeClipboardText(it)
                    },
                    addText = {
                        shortXContext.input.addClipboardText(it)
                    },
                    inputText = { text ->
                        systemServiceScope.launchSafety {
                            logger.d("ClipboardView input text: $text")
                            var isSuccess = false
                            var retryCount = 0
                            while (!isSuccess && retryCount < 10) {
                                retryCount++
                                runCatching {
                                    shortXContext.input.submitText(text)
                                    isSuccess = true
                                }.onFailure {
                                    logger.d("Input fail, will try again.")
                                    delay(100)
                                }
                            }
                        }
                    },
                    clearAll = {
                        shortXContext.input.clearAllClipboardText()
                    }
                )
            showingClipboard.add(guideLineView)
            mainHandler.postSafety {
                guideLineView.attach()
            }
        }
    }

    fun enableIdViewer() {
        disableAllOverlayViews {
            val idNodes = mutableListOf<ViewNode>()

            val guideLineView =
                IdViewerView(
                    context = context,
                    onClick = { x, y ->
                        disableAllOverlayViews {
                            lookUpViewNodeFromClick(idNodes, x, y)
                        }
                    },
                    onDetach = {
                        showingIVHints.remove(it)
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

            root?.recycle()

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
                Toast.makeText(
                    context,
                    "Not found",
                    Toast.LENGTH_LONG
                ).show()
            } else if (matchNodes.size == 1) {
                context.clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        "ShortX-ViewId",
                        matchNodes[0].viewId
                    )
                )
                Toast.makeText(
                    context,
                    i18NIns["ui.common.copied.to.clipboard"] + "\t" + matchNodes[0].viewId,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                ViewIdViewerNodeSelectorDialog(context, matchNodes).show()
            }
        }
    }

    private fun disableAllOverlayViews(action: suspend () -> Unit) {
        mainHandler.postSafety {
            showingDrawerBoard.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "disableDrawBoard detach fail") }
            }
            showingDrawerBoard.clear()

            showingAreaSelector.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "showingAreaSelector detach fail") }
            }
            showingAreaSelector.clear()

            showingUCHints.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "showingUCHints detach fail") }
            }
            showingUCHints.clear()

            showingIVHints.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "disableIVViewer detach fail") }
            }
            showingIVHints.clear()

            showingClipboard.forEach {
                kotlin.runCatching { it.detach() }
                    .onFailure { logger.e(it, "disableClipboard detach fail") }
            }
            showingClipboard.clear()

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
                        position = XRectF(
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

    // 	at android.view.accessibility.AccessibilityNodeInfo.init(AccessibilityNodeInfo.java:4036)
    //	at android.view.accessibility.AccessibilityNodeInfo.<init>(AccessibilityNodeInfo.java:870)
    //	at android.view.accessibility.AccessibilityCache.getNode(AccessibilityCache.java:283)
    //	at android.view.accessibility.AccessibilityInteractionClient.findAccessibilityNodeInfoByAccessibilityId(AccessibilityInteractionClient.java:468)
    //	at android.view.accessibility.AccessibilityNodeInfo.getChild(AccessibilityNodeInfo.java:1121)
    // 	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.j(Unknown Source:147)
    //	at qi.u.k(Unknown Source:40)
    //	at tornaco.apps.shortx.services.rule.fact.trigger.d.invoke(Unknown Source:330)
    //	at qi.u.onAccessibilityEvent(Unknown Source:19)
    //	at androidx.appcompat.app.m0.run(Unknown Source:18)
    //	at android.os.Handler.handleCallback(Handler.java:938)
    //	at android.os.Handler.dispatchMessage(Handler.java:99)
    //	at android.os.Looper.loopOnce(Looper.java:210)
    //	at android.os.Looper.loop(Looper.java:299)
    //	at android.os.HandlerThread.run(HandlerThread.java:67)
    private fun discoverViewIdFromAccessibilityNode(
        list: MutableList<ViewNode>,
        root: AccessibilityNodeInfo
    ) {
        if (root.isVisibleToUser) {
            val index = list.size
            list.add(
                root.toBasicViewNode().copy(
                    color = ColorPalette[index % ColorPalette.size]
                )
            )
            if (root.childCount > 0) {
                for (i in 0 until root.childCount) {
                    val child = root.getChild(i)
                    if (child != null && child != root) {
                        discoverViewIdFromAccessibilityNode(list, child)
                    }
                }
            }
        }
    }

    private fun hasFoundViewNode(
        root: AccessibilityNodeInfo,
        check: (ViewNode) -> Boolean
    ): Boolean {
        if (!root.isVisibleToUser) {
            return false
        }
        val node = root.toBasicViewNode()
        if (check(node)) {
            return true
        }
        if (root.childCount > 0) {
            for (i in 0 until root.childCount) {
                val child = root.getChild(i)
                if (child != null && child != root) {
                    if (hasFoundViewNode(child, check)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun onWindowFocusChanged(ownerUid: Int, windowTag: String) {
        systemServiceScope.launchSafety {
            windowOwnerUIDChanged(ownerUid)
            windowTagChanged(windowTag)
        }
    }

    private fun windowOwnerUIDChanged(ownerUid: Int) {
        val app = shortXContext.appState.uidToApp(ownerUid)
        app?.let {
            val newAppPkg = app.pkg
            if (_currentFocusedWindowOwner != newAppPkg) {
                // Lost current
                shortXContext.rule.publishFactChecked(AppLostWindowFocus.getDefaultInstance()) {
                    FactPublishData(
                        reason = "onWindowFocusChanged",
                        fact = AppLostWindowFocus.newBuilder()
                            .addApps(_currentFocusedWindowOwner)
                            .build()
                    ) {
                        mapOf(
                            ContextDataMapping.AppLostWindowFocus.pkgName.name to _currentFocusedWindowOwner.pkgName,
                            ContextDataMapping.AppLostWindowFocus.userId.name to _currentFocusedWindowOwner.userId,
                        )
                    }
                }

                // Gain new
                shortXContext.rule.publishFactChecked(AppGainWindowFocus.getDefaultInstance()) {
                    FactPublishData(
                        reason = "onWindowFocusChanged",
                        fact = AppGainWindowFocus.newBuilder()
                            .addApps(newAppPkg)
                            .build()
                    ) {
                        mapOf(
                            ContextDataMapping.AppGainWindowFocus.pkgName.name to newAppPkg.pkgName,
                            ContextDataMapping.AppGainWindowFocus.userId.name to newAppPkg.userId,
                        )
                    }
                }

                _currentFocusedWindowOwner = newAppPkg
                logger.d("onWindowFocusChanged: $ownerUid - ${_currentFocusedWindowOwner.appPkgToString()}")
            }
        }
    }

    private fun windowTagChanged(windowTag: String) {
        _currentFocusedWindowTag = windowTag

        val isNotificationPanelExpandedNew = "NotificationShade" == _currentFocusedWindowTag
        if (isNotificationPanelExpandedNew != _isNotificationPanelExpanded) {
            _isNotificationPanelExpanded = isNotificationPanelExpandedNew
        }
    }

    fun onBackNavDone(triggerBack: Boolean, type: Int) {
        if (triggerBack) {
            shortXContext.input.updateLastInputKey(KeyEvent.KEYCODE_BACK, "onBackNavDone")
        }

        shortXContext.rule.publishFactChecked(BackNavDone.getDefaultInstance()) {
            FactPublishData(
                reason = "onBackNavDone",
                fact = BackNavDone.newBuilder()
                    .setBackType(triggerBack)
                    .build()
            )
        }
    }

    fun onBackNavStart() {
        shortXContext.rule.publishFactChecked(BackNavStart.getDefaultInstance()) {
            FactPublishData(
                reason = "onBackNavStart",
                fact = BackNavStart.newBuilder().build()
            )
        }
    }
}

