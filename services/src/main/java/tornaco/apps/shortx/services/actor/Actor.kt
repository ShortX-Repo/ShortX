package tornaco.apps.shortx.services.actor

import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.action.*
import tornaco.apps.shortx.core.action.ExpandNotification
import tornaco.apps.shortx.core.action.ShellCommand
import tornaco.apps.shortx.core.action.StartActivity
import tornaco.apps.shortx.core.action.TakeScreenshot
import tornaco.apps.shortx.core.util.Logger

interface Actor {
    fun execute(action: Action)
}

class Supervisor(
    private val context: Context,
    private val handler: Handler,
    private val beforeExecute: (Action) -> Unit = {},
) : Actor {
    private val logger = Logger("Actor.Supervisor")

    override fun execute(action: Action) {
        logger.i("execute: $action")
        kotlin.runCatching { execute0(action) }.onFailure {
            logger.e(it, "execute action error")
        }
    }

    private fun execute0(action: Action) {
        beforeExecute(action)
        when (action) {
            is TakeScreenshot -> {
                TakeScreenshot(context, handler).invoke()
            }
            is StartActivity -> {
                StartActivity(context, action).invoke()
            }
            is ExpandNotification -> {
                ExpandNotification(context).invoke()
            }
            is ShellCommand -> {
                ShellCommand(context, action).invoke()
            }
            else -> {
                logger.w("UnSupported action: $action")
            }
        }
    }
}