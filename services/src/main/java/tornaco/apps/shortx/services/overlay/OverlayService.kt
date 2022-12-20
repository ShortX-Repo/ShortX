package tornaco.apps.shortx.services.overlay

import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.postSafety

internal data class ShowingOverlay(
    val token: java.util.function.Function<String, Boolean>,
    val settings: List<OverlayButtonSetting>
)

class OverlayService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val overlayClassLoader by lazy {
        OverlayClassLoader(context)
    }
    private var showingOverlays: MutableList<ShowingOverlay> = mutableListOf()

    override fun systemReady() {
        super.systemReady()
        shortXContext.appState.onPackageChanged {
            logger.d("Dispose overlay object since apk updated")
            postHideAllOverlay()
        }
    }

    fun postShowOverlay(settings: List<OverlayButtonSetting>) = mainHandler.postSafety {
        logger.d("showOverlay, current showing: $showingOverlays, settings: $settings")

        if (showingOverlays.any {
                it.settings == settings
            }) {
            logger.d("showOverlay skip since has showing for same settings.")
            return@postSafety
        }

        val posX = store.read(Settings.IntSettings.OverlayButtonPosX)
        val posY = store.read(Settings.IntSettings.OverlayButtonPosY)

        @Suppress("UNCHECKED_CAST") val overlayIns =
            overlayClassLoader.newComposeOverlayObject(
                initialPos = android.util.Pair.create(posX, posY),
                onPosChange = { pos ->
                    handler.postSafety {
                        logger.d("onPosChange, write settings: $pos")
                        store.write(Settings.IntSettings.OverlayButtonPosX, pos.first)
                        store.write(Settings.IntSettings.OverlayButtonPosY, pos.second)
                    }
                },

                settings = settings.map { it.icon }
            ) {
                shortXContext.rule.executeActions(
                    "Overlay Button #$it click",
                    settings[it].actionsList
                )
            } as java.util.function.Function<String, Boolean>


        showingOverlays.add(ShowingOverlay(overlayIns, settings).apply {
            token.show()
        })
    }

    fun postHideAllOverlay() = mainHandler.postSafety {
        hideAllOverlay()
    }

    private fun hideAllOverlay() {
        logger.d("hideAllOverlay")
        showingOverlays.forEach {
            it.token.dismiss()
        }
        showingOverlays.clear()
    }
}