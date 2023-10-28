package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.os.ServiceManager
import android.view.IRotationWatcher
import android.view.IWindowManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class ScreenRotateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ScreenRotateTrigger")
    private val wm by lazy { IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)) }

    private val watcher by lazy {
        object : IRotationWatcher.Stub() {
            override fun onRotationChanged(rotation: Int) {
                logger.d("onRotationChanged: $rotation")
                runCatching {

                }.onFailure {
                    shortXContext.watchDog.onFatalError(it, "onRotationChanged")
                }
            }
        }
    }

    override fun register() {
        wm.watchRotation(watcher, context.displayId)
    }

    override fun unRegister() {
        wm.removeRotationWatcher(watcher)
    }

}

