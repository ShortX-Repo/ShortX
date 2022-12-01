package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import android.widget.Toast
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.ActionEvaluator
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.postSafety

class ActionEvaluatorImpl(private val context: Context, private val handler: Handler) :
    ActionEvaluator {
    override suspend fun evaluate(action: ProtoAny) {
        if (action is_ StartActivity::class.java) {
            val startActivity = action unpack_ StartActivity::class.java
            tornaco.apps.shortx.services.rule.actor.StartActivity(
                context,
                startActivity.componentNameAsString
            ).invoke()
        } else if (action is_ TakeScreenshot::class.java) {
            tornaco.apps.shortx.services.rule.actor.TakeScreenshot(context, handler).invoke()
        } else if (action is_ ShellCommand::class.java) {
            val sc = action unpack_ ShellCommand::class.java
            tornaco.apps.shortx.services.rule.actor.ShellCommand(context, sc.command).invoke()
        } else if (action is_ ShowToast::class.java) {
            val t = action unpack_ ShowToast::class.java
            handler.postSafety {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        } else if (action is_ Delay::class.java) {
            val d = action unpack_ Delay::class.java
            delay(maxOf(0, d.time))
        }
    }
}