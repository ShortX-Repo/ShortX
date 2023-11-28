package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.LogcatManager

class LogcatTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("LogcatTrigger")

    override fun register() {
        if (false) {
            onDebugBuild {
                LogcatManager.start()
                LogcatManager.setCallback {
                    if (!it.content.contains("LogcatTrigger")) {
                        logger.d("catched log: $it")
                    }
                }
            }
        }
    }

    override fun unRegister() {
        LogcatManager.destroy()
    }

}