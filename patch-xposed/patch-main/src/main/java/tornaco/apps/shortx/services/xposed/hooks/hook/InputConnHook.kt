package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.util.Logger
import util.Reflections
import java.util.concurrent.atomic.AtomicInteger

object InputConnHook {
    private val logger = Logger("ShortXHook.InputConnHook")

    fun onPackageLoaded() {
        logger.d("hook")
        afterMethod(
            Reflections.findClass("android.view.inputmethod.RemoteInputConnectionImpl", null),
            "commitText"
        ) { param ->
            param.logParamsOnDebug(logger)
            val mCurrentSessionId =
                Reflections.getObjectField(param.thisObject, "mCurrentSessionId") as AtomicInteger
            logger.d("commitText, mCurrentSessionId: ${mCurrentSessionId.get()}")
        }
    }
}