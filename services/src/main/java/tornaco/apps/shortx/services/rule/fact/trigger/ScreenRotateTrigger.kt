package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.proto.fact.ScreenRotateDegree
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
                val rotation: Int = context.windowManager.defaultDisplay.rotation
                val degree = when (rotation) {
                    Surface.ROTATION_0 -> {
                        ScreenRotateDegree.ScreenRotateDegree_0
                    }

                    Surface.ROTATION_90 -> {
                        ScreenRotateDegree.ScreenRotateDegree_90
                    }

                    Surface.ROTATION_180 -> {
                        ScreenRotateDegree.ScreenRotateDegree_180
                    }

                    Surface.ROTATION_270 -> {
                        ScreenRotateDegree.ScreenRotateDegree_270
                    }

                    else -> {
                        ScreenRotateDegree.UNRECOGNIZED
                    }
                }
                logger.d("onOrientationChanged: $orientation $rotation $degree")

                shortXContext.rule.publishFact(
                    reason = "onOrientationChanged",
                    fact = ScreenRotate.newBuilder().setDegree(degree).build()
                ) { emptyMap() }
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