package tornaco.apps.shortx.services.overlay

import android.annotation.ColorInt
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import com.android.internal.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.ShowFloatWindow
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.overlay.flash.ScreenFlash
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.floating.FloatApi
import tornaco.apps.shortx.services.ui.floating.ScreenUtil
import tornaco.apps.shortx.services.ui.floating.controller.FloatButtonWindowController
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.ui.overlay.ComposeOverlay
import tornaco.apps.shortx.ui.sidebar.SideBarDialogView


private const val SIDE_BAR_WIDTH = 70

internal data class ShowingOverlay(
    val token: ComposeOverlay,
    val settings: List<OverlayButtonSetting>,
    val tag: String
)

internal data class ShowingFloats(
    val token: FloatApi,
    val settings: List<OverlayButtonSetting>,
    val tag: String
)

class OverlayService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "OverlayService") {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var showingOverlays: MutableList<ShowingOverlay> = mutableListOf()
    private var showingFloats: MutableList<ShowingFloats> = mutableListOf()

    private val screenFlash: ScreenFlash by lazy { ScreenFlash(context) }

    private var sideBarDialog: Dialog? = null

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        systemServiceScope.launchSafety("OverlayService.systemReady") {
            shortXContext.systemSettings.currentConfig.collectLatest {
                showingFloats.forEach {
                    it.token.onConfigurationChanged()
                }
            }
        }
    }

    fun flashScreen(@ColorInt color: Int, times: Int) {
        systemServiceScope.launchSafety("flashScreen") {
            repeat(times) {
                delay(100)
                screenFlash.flash(color)
            }
        }
    }

    fun postShowOverlay(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        contextData: Map<String, String>,
        parameters: List<FuncParameterInput>,
        action: ShowOverlayButton
    ) = mainHandler.postSafety("postShowOverlay") {
        logger.d("showOverlay: ${action.tag}, current showing: ${showingOverlays.map { it.tag }}")
        logger.logOnDebug("local vars: ${localVars.all.joinToString { it.name }}")
        logger.logOnDebug("parameters: ${parameters.joinToString { it.toLogString() }}")

        if (showingOverlays.any {
                it.tag == action.tag
            }) {
            logger.d("showOverlay skip since has showing for same owner.")
            return@postSafety
        }

        val posX = store.read(Settings.IntSettings.OverlayButtonPosX(action.tag))
        val posY = store.read(Settings.IntSettings.OverlayButtonPosY(action.tag))

        val overlayIns = ComposeOverlay(
            context = context,
            action = action,
            localVars = localVars,
            contextData = contextData,
            parameters = parameters,
            initialPos = posX to posY,
            onPosChange = { x, y ->
                handler.postSafety("onPosChange") {
                    logger.d("OverlayButton onPosChange, write settings: $x-$y")
                    store.write(Settings.IntSettings.OverlayButtonPosX(action.tag), x)
                    store.write(Settings.IntSettings.OverlayButtonPosY(action.tag), y)
                }
            },
            onButtonClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.actionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
                if (action.closeOnAction) {
                    postHideOverlay(listOf(action.tag))
                }
            },
            onButtonLongClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.longClickActionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
                if (action.closeOnAction) {
                    postHideOverlay(listOf(action.tag))
                }
            },
            onTouchOutside = {
                logger.d("onTouchOutside: ${action.tag}")
                if (action.closeOnTouchOutside) {
                    postHideOverlay(listOf(action.tag))
                }
            }
        )

        showingOverlays.add(
            ShowingOverlay(
                token = overlayIns,
                settings = action.buttonSettingsList,
                tag = action.tag
            ).apply {
                token.show()
            })
    }

    fun postShowFloatWindow(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowFloatWindow
    ) = mainHandler.postSafety("postShowFloatWindow") {
        logger.d("postShowFloatWindow: ${action.tag}, current showing: ${showingFloats.map { it.tag }}")
        logger.logOnDebug("local vars: ${localVars.all.joinToString { it.name }}")
        logger.logOnDebug("parameters: ${parameters.joinToString { it.toLogString() }}")

        if (showingFloats.any {
                it.tag == action.tag
            }) {
            logger.d("postShowFloatWindow skip since has showing for same owner.")
            return@postSafety
        }

        val (fixedPosXP, fixedPosYP) = getInitialFloatWindowPos(action.tag, true)
        val (fixedPosXL, fixedPosYL) = getInitialFloatWindowPos(action.tag, false)

        val float = FloatApi(
            context, action,
            initialPosP = fixedPosXP to fixedPosYP,
            initialPosL = fixedPosXL to fixedPosYL,
            onPosChange = { x, y ->
                handler.postSafety("onPosChange.2") {
                    writeFloatWindowPos(action.tag, x, y)
                }
            },
            onButtonClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.actionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            },
            onButtonLongClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.longClickActionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            },
        )

        showingFloats.add(
            ShowingFloats(
                token = float,
                settings = action.buttonSettingsList,
                tag = action.tag
            ).apply {
                token.showFloatWindow()
            })
    }

    fun postShowSideBar(
        evaluateContext: EvaluateContext,
        contextData: Map<String, String>,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowSideBar
    ) = mainHandler.postSafety("postShowSideBar") {
        logger.d("postShowSideBar: ${action.tag}, current showing: $")
        logger.logOnDebug("local vars: ${localVars.all.joinToString { it.name }}")
        logger.logOnDebug("parameters: ${parameters.joinToString { it.toLogString() }}")

        val currentShowingSideBarDialog = sideBarDialog
        if (currentShowingSideBarDialog != null && currentShowingSideBarDialog.isShowing) {
            logger.d("postShowSideBar skip since has showing.")
            return@postSafety
        }

        val dialogView = SideBarDialogView(
            context = context,
            i18N = i18NIns,
            action = action,
            localVars = localVars,
            contextData = contextData,
            parameters = parameters,
            onButtonClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.actionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            },
            onButtonLongClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.longClickActionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            }
        )

        val androidDialog = Dialog(uiContext)
        dialogView.initView()
        dialogView.setAlpha(0f)

        val isWindowGravityLeft = true

        androidDialog.setOnShowListener {
            logger.d("androidDialog.setOnShowListener")
            dialogView.translationX =
                (if (isWindowGravityLeft) -1 else 1) * dialogView.width / 2.0f

            dialogView.setAlpha(0f)

            dialogView.animate()
                .alpha(1f)
                .translationX(16._dp.toFloat())
                .setDuration(466)
                .setInterpolator(LogDecelerateInterpolator())
                .start()
        }

        androidDialog.showInSystemServer(view = dialogView,
            cancelable = true,
            gravity = Gravity.START or Gravity.CENTER_VERTICAL,
            width = { SIDE_BAR_WIDTH._dp },
            onWindow = {
                val params = attributes

                clearFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                )
                addFlags(
                    (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                )
                addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                setWindowAnimations(R.style.Animation_Toast)
                params.format = PixelFormat.TRANSLUCENT
                params.title = "ShortX-SideBar"
                params.windowAnimations = -1

                attributes = params
            })


        sideBarDialog = androidDialog
    }

    fun postHideAllOverlay() = mainHandler.postSafety("postHideAllOverlay") {
        logger.d("postHideAllOverlay")
        showingOverlays.forEach {
            it.token.dismiss()
        }
        showingOverlays.clear()
    }

    fun postHideOverlay(tags: List<String>) = mainHandler.postSafety("postHideOverlay") {
        logger.d("postHideOverlay: $tags")
        showingOverlays.filter { tags.contains(it.tag) }.apply {
            showingOverlays.removeAll(this)
            forEach {
                it.token.dismiss()
            }
        }
    }

    fun postHideAllFloatWindow() = mainHandler.postSafety("postHideAllFloatWindow") {
        logger.d("postHideAllFloatWindow")
        showingFloats.forEach {
            it.token.removeFloatWindow()
        }
        showingFloats.clear()
    }

    fun postHideFloatWindow(tags: List<String>) = mainHandler.postSafety("postHideFloatWindow") {
        logger.d("postHideFloatWindow: $tags")
        showingFloats.filter { tags.contains(it.tag) }.apply {
            showingFloats.removeAll(this)
            forEach {
                it.token.removeFloatWindow()
            }
        }
    }

    fun postHideAllSideBar() = mainHandler.postSafety("postHideAllSideBar") {
        logger.d("postHideAllSideBar")
        sideBarDialog?.dismiss()
        sideBarDialog = null
    }

    private fun writeFloatWindowPos(tag: String, x: Int, y: Int) {
        val currentOrientation = context.resources.configuration.orientation
        logger.d("writeFloatWindowPos currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT
        val storeKey = flowWindowPosStoreKey(tag, isPortrait)
        logger.d("writeFloatWindowPos, write settings: $x-$y")
        store.write(Settings.IntSettings.FloatWindowPosX(storeKey), x)
        store.write(Settings.IntSettings.FloatWindowPosY(storeKey), y)
    }

    private fun getInitialFloatWindowPos(tag: String, isPortrait: Boolean): Pair<Int, Int> {
        val storeKey = flowWindowPosStoreKey(tag, isPortrait)
        val posX = store.read(Settings.IntSettings.FloatWindowPosX(storeKey))
        val posY = store.read(Settings.IntSettings.FloatWindowPosY(storeKey))

        logger.d("getInitialFloatWindowPos, read settings: $storeKey $posX-$posY")

        val iconSize = ScreenUtil.dip2px(context, FloatButtonWindowController.SIZE)
        val screenSize = context.screenSize()

        val fixedPosX = if (isPortrait) {
            if (posX > 0 && posX <= screenSize.x - iconSize) {
                posX
            } else {
                screenSize.x / 2
            }
        } else {
            if (posX > 0 && posX <= screenSize.y - iconSize) {
                posX
            } else {
                screenSize.y / 2
            }
        }

        val fixedPosY = if (isPortrait) {
            if (posY > 0 && posY <= screenSize.y - iconSize) {
                posY
            } else {
                screenSize.y / 2
            }
        } else {
            if (posY > 0 && posY <= screenSize.x - iconSize) {
                posY
            } else {
                screenSize.x / 2
            }
        }
        logger.d("getInitialFloatWindowPos, fixed settings: $$storeKey $fixedPosX-$fixedPosY")
        return fixedPosX to fixedPosY
    }

    private fun flowWindowPosStoreKey(tag: String, isPortrait: Boolean): String {
        return "$tag-" + isPortrait.select(
            "P",
            "L"
        )
    }
}