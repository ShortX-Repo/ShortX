package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.OrientationEventListener
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.util.Logger
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
                shortXContext.wm.currentScreenOrientation = orientation
                val degree = orientation.orientationToDegree()
                logger.d("onOrientationChanged: $orientation $degree")

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

fun Int.orientationToDegree(): ScreenRotateDegree {
    val degree = when (this) {
        0 -> {
            ScreenRotateDegree.ScreenRotateDegree_0
        }

        90 -> {
            ScreenRotateDegree.ScreenRotateDegree_90
        }

        180 -> {
            ScreenRotateDegree.ScreenRotateDegree_180
        }

        270 -> {
            ScreenRotateDegree.ScreenRotateDegree_270
        }

        else -> {
            ScreenRotateDegree.UNRECOGNIZED
        }
    }
    return degree
}