package tornaco.apps.shortx.services.overlay

import android.content.Context
import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.postSafety

internal data class ShowingOverlay(
    val token: java.util.function.Function<String, Boolean>,
    val settings: List<OverlayButtonSetting>,
    val tag: String
)

class OverlayService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private lateinit var overlayClassLoader: OverlayClassLoader

    private var showingOverlays: MutableList<ShowingOverlay> = mutableListOf()

    override fun start(context: Context) {
        super.start(context)
        overlayClassLoader = OverlayClassLoader(context)
    }

    override fun systemReady() {
        super.systemReady()
        shortXContext.appState.onPackageUpdated {
            if (it.pkg.pkgName == BuildProp.APP_PACKAGE_NAME) {
                logger.d("Dispose overlay object since ShortX apk updated")
                postHideAllOverlay()
                // Reset class loader.
                overlayClassLoader = OverlayClassLoader(context)
            }
        }
    }

    fun postShowOverlay(
        autoCollapse: Boolean,
        settings: List<OverlayButtonSetting>,
        tag: String
    ) = mainHandler.postSafety {
        logger.d("showOverlay: $tag, current showing: $showingOverlays, settings: $settings")

        if (showingOverlays.any {
                it.tag == tag
            }) {
            logger.d("showOverlay skip since has showing for same owner.")
            return@postSafety
        }

        val posX = store.read(Settings.IntSettings.OverlayButtonPosX)
        val posY = store.read(Settings.IntSettings.OverlayButtonPosY)

        @Suppress("UNCHECKED_CAST") val overlayIns =
            overlayClassLoader.newComposeOverlayObject(
                autoCollapse = autoCollapse,
                initialPos = android.util.Pair.create(posX, posY),
                onPosChange = { pos ->
                    handler.postSafety {
                        logger.d("onPosChange, write settings: $pos")
                        store.write(Settings.IntSettings.OverlayButtonPosX, pos.first)
                        store.write(Settings.IntSettings.OverlayButtonPosY, pos.second)
                    }
                },

                icons = settings.map { it.icon },
                labels = settings.map { it.label ?: "Label" }
            ) {
                shortXContext.rule.executeActions(
                    "Overlay//NO_ID",
                    "Overlay Button #$it click",
                    settings[it].actionsList
                )
            } as java.util.function.Function<String, Boolean>


        showingOverlays.add(
            ShowingOverlay(
                token = overlayIns,
                settings = settings,
                tag = tag
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