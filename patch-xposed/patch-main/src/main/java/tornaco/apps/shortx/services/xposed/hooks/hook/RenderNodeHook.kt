package tornaco.apps.shortx.services.xposed.hooks.hook

import android.graphics.RenderNode
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

object RenderNodeHook {
    private val logger = Logger("ShortXHook.RenderNodeHook")

    fun onPackageLoaded() {
        logger.d("hook")
        if (OsUtils.isQOrAbove()) {
            afterMethod(
                RenderNode::class.java,
                "addAnimator"
            ) { param ->
                param.logParamsOnDebug(logger)
                if (param.throwable != null) {
                    logger.w("Fix render node crash: ${param.throwable}")
                    param.throwable = null
                    param.result = null
                }
            }
        }
    }
}