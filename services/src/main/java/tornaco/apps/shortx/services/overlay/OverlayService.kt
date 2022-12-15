package tornaco.apps.shortx.services.overlay

import android.os.Handler
import android.os.Looper
import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener
import tornaco.apps.shortx.core.proto.fact.OverlayButtonClick
import tornaco.apps.shortx.core.proto.fact.OverlayButtonLongClick
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.postSafety

class OverlayService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val overlayButtonSettingsChangeListenerCallbacks by lazy {
        RemoteCallbackList<IOverlayButtonSettingsChangeListener>()
    }

    private val overlayClassLoader by lazy {
        OverlayClassLoader(context)
    }
    private var overlay: Any? = null

    private val actionBoundButtonIndexes = mutableSetOf<Int>()

    override fun systemReady() {
        super.systemReady()
        shortXContext.appState.onPackageChanged {
            hideOverlay(dispose = true)
            logger.d("Dispose overlay object since apk updated")
        }

        shortXContext.rule.addRuleChangeListener {
            logger.d("Rule changed.")
            shortXContext.rule.getAllRules().filter {
                it.isEnabled
            }.forEach { rule ->
                if (rule.fact is_ OverlayButtonClick::class.java) {
                    val click = rule.fact unpack_ OverlayButtonClick::class.java
                    actionBoundButtonIndexes.add(click.index)
                    logger.d("OverlayButtonClick on index: ${click.index}")
                } else if (rule.fact is_ OverlayButtonLongClick::class.java) {
                    val click = rule.fact unpack_ OverlayButtonLongClick::class.java
                    actionBoundButtonIndexes.add(click.index)
                    logger.d("OverlayButtonLongClick on index: ${click.index}")
                }
            }
        }
    }

    fun showOverlay() = mainHandler.postSafety {
        logger.d("showOverlay")
        if (overlay == null) {
            overlay = overlayClassLoader.getShortXObject()
        }
        overlay?.show()
    }

    fun hideOverlay(dispose: Boolean = false) = mainHandler.postSafety {
        logger.d("hideOverlay")
        overlay?.dismiss()
        if (dispose) {
            logger.d("dispose overlay.")
            overlay = null
        }
    }

    fun getOverlayButtonIconName(index: Int): String {
        return store.read(Settings.StringSettings.OverlayButtonIconForIndex(index))
    }

    fun setOverlayButtonIconName(index: Int, name: String) {
        store.write(Settings.StringSettings.OverlayButtonIconForIndex(index), name)
        overlayButtonIconChanged(index)
    }

    fun registerOverlayButtonSettingsChangeListener(listener: IOverlayButtonSettingsChangeListener) {
        overlayButtonSettingsChangeListenerCallbacks.register(listener)
    }

    fun unRegisterOverlayButtonSettingsChangeListener(listener: IOverlayButtonSettingsChangeListener) {
        overlayButtonSettingsChangeListenerCallbacks.unregister(listener)
    }

    fun hasBoundActionOnOverlayButton(index: Int): Boolean {
        return actionBoundButtonIndexes.contains(index)
    }

    private fun overlayButtonIconChanged(index: Int) = handler.postSafety {
        overlayButtonSettingsChangeListenerCallbacks.broadcast {
            kotlin.runCatching { it.overlayButtonSettingsChanged(index) }.onFailure {
                logger.e(it, "Error call overlayButtonSettingsChanged")
            }
        }
    }
}