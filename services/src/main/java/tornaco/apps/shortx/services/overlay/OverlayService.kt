package tornaco.apps.shortx.services.overlay

import android.content.Context
import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.ui.overlay.ComposeOverlay

internal data class ShowingOverlay(
    val token: ComposeOverlay,
    val settings: List<OverlayButtonSetting>,
    val tag: String
)

class OverlayService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "OverlayService") {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var showingOverlays: MutableList<ShowingOverlay> = mutableListOf()

    override fun start(context: Context) {
        super.start(context)
    }

    fun postShowOverlay(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        action: ShowOverlayButton
    ) = mainHandler.postSafety {
        logger.d("showOverlay: ${action.tag}, current showing: ${showingOverlays.map { it.tag }} local vars: ${localVars.all.joinToString { it.name }}")

        if (showingOverlays.any {
                it.tag == action.tag
            }) {
            logger.d("showOverlay skip since has showing for same owner.")
            return@postSafety
        }

        val posX = store.read(Settings.IntSettings.OverlayButtonPosX)
        val posY = store.read(Settings.IntSettings.OverlayButtonPosY)

        val overlayIns = ComposeOverlay(
            context = context,
            action = action,
            localVars = localVars,
            initialPos = posX to posY,
            onPosChange = { x, y ->
                handler.postSafety {
                    logger.d("onPosChange, write settings: $x-$y")
                    store.write(Settings.IntSettings.OverlayButtonPosX, x)
                    store.write(Settings.IntSettings.OverlayButtonPosY, y)
                }
            },
            onButtonClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.actionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                )
            },
            onButtonLongClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.longClickActionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                )
            },
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

    fun postHideAllOverlay() = mainHandler.postSafety {
        logger.d("postHideAllOverlay")
        hideAllOverlay()
    }

    fun postHideOverlay(tags: List<String>) = mainHandler.postSafety {
        logger.d("postHideOverlay: $tags")
        showingOverlays.filter { tags.contains(it.tag) }.apply {
            showingOverlays.removeAll(this)
            forEach {
                it.token.dismiss()
            }
        }
    }

    private fun hideAllOverlay() {
        logger.d("hideAllOverlay")
        showingOverlays.forEach {
            it.token.dismiss()
        }
        showingOverlays.clear()
    }
}