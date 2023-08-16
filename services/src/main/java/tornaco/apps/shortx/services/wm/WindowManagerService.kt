package tornaco.apps.shortx.services.wm

import android.app.UiAutomation.OnAccessibilityEventListener
import android.content.ComponentName
import android.content.Context
import android.graphics.BitmapFactory
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
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.ocr.Ocr
import tornaco.apps.shortx.ui.overlay.UniversalCopyView
import java.io.File

class WindowManagerService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "ShortXWMS"), OnAccessibilityEventListener {

    private val automationManager by lazy { UiAutomationManager(handler) }
    private var screenSize: Pair<Int, Int>? = null

    private val uiAutoLock = "UiAutoLock"

    private val showingHints: MutableSet<UniversalCopyView> = mutableSetOf()

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
        onAccessibilityEvent?.invoke(event)
    }

    fun getAllTextsFromScreen(): List<TextViewNode> {
        val screenTexts = mutableListOf<TextViewNode>()
        val root: AccessibilityNodeInfo? = automationManager.rootInActiveWindow
        logger.d("rootInActiveWindow: $root")
        val discoverStartTimeMillis = System.currentTimeMillis()
        if (root != null) {
            discoverTextFromAccessibilityNode(screenTexts, root)
        }
        logger.d("discoverTextFromAccessibilityNode take ${System.currentTimeMillis() - discoverStartTimeMillis}ms")
        return screenTexts
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

            showingHints.add(guideLineView)
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

    private fun disableUniversalCopy(action: suspend () -> Unit) {
        mainHandler.postSafety {
            showingHints.forEach {
                kotlin.runCatching { it.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()
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

    private fun installOcr() {
        runCatching {
            val appResources = AppResources(context, BuildProp.APP_PACKAGE_NAME)
            val appContext = appResources.appContext
            val assets = appContext.assets
            val destDataDir = File(Dirs.baseServerCacheDir(), "tess${File.separator}tessdata")
            require(destDataDir.exists() || destDataDir.mkdirs()) {
                "Fail create tess destDataDir"
            }
            val engFile = File(destDataDir, "eng.traineddata")
            assets.open("tess/eng.traineddata").use { input ->
                engFile.outputStream().use { out -> input.copyTo(out) }
            }
            val imageFile = File(destDataDir, "sample.jpg")
            assets.open("tess/sample.jpg").use { input ->
                imageFile.outputStream().use { out -> input.copyTo(out) }
            }
            logger.d("Ocr engFile: $engFile")
            val installSuccess = Ocr.install(destDataDir.parentFile.absolutePath)
            logger.d("Ocr installSuccess: $installSuccess")
            val text = Ocr.getText(BitmapFactory.decodeFile(imageFile.absolutePath))
            logger.d("Ocr text: $text")
        }.onFailure {
            logger.e(it, "Ocr")
        }
    }
}
