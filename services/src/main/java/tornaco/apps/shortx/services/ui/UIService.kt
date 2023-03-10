package tornaco.apps.shortx.services.ui

import tornaco.apps.shortx.core.doIfDebug
import tornaco.apps.shortx.core.settings.defaultDanmuUISettings
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class UIService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private var liveDanmuView: LiveDanmuView? = null

    override fun systemReady() {
        super.systemReady()
    }

    fun showDanmu(icon: String?, text: String) = systemServiceScope.launchSafety {
        val viewAlive = liveDanmuView?.isAttached() ?: false
        if (!viewAlive) {
            liveDanmuView = LiveDanmuView(context, defaultDanmuUISettings).apply { attach() }
        }
        doIfDebug {
            logger.d("showDanmu: $liveDanmuView $icon $text")
        }
        liveDanmuView?.addDanmu(Danmu(icon, text))
    }
}