package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.OrientationEventListener
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.windowManager
import tornaco.apps.shortx.services.ShortXContext

class ScreenRotateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ScreenRotateTrigger")
    private val listener by lazy {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = context.windowManager.defaultDisplay.rotation
                logger.d("onOrientationChanged: $orientation $rotation")
            }
        }
    }

    override fun register() {
        listener.enable()
    }

    override fun unRegister() {
        listener.disable()
    }

}