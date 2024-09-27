package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.os.ServiceManager
import android.view.IRotationWatcher
import android.view.IWindowManager
import android.view.Surface
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.fact.WindowRotationChange
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.displayIdCompat
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData

class ScreenRotateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ScreenRotateTrigger")
    private val wm by lazy { IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)) }

    private val watcher by lazy {
        @DoNotStrip
        object : IRotationWatcher.Stub() {
            override fun onRotationChanged(rotation: Int) {
                runCatching {
                    val degree = rotation.rotationToDegree()
                    logger.d("onRotationChanged: $rotation $degree")
                    if (degree != ScreenRotateDegree.UNRECOGNIZED) {
                        shortXContext.rule.publishFactChecked(WindowRotationChange.getDefaultInstance()) {
                            FactPublishData(
                                "onRotationChanged",
                                WindowRotationChange.newBuilder().setDegree(degree).build()
                            )
                        }
                    }
                }.onFailure {
                    shortXContext.watchDog.onFatalError(it, "onRotationChanged")
                }
            }
        }
    }

    override fun register() {
        wm.watchRotation(watcher, context.displayIdCompat.also {
            logger.i("watchRotation for display: $it")
        })
    }

    override fun unRegister() {
        wm.removeRotationWatcher(watcher)
    }

}

fun Int.rotationToDegree(): ScreenRotateDegree {
    return when (this) {
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
}