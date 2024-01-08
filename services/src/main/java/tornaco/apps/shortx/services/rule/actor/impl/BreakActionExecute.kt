package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.BreakActionException
import tornaco.apps.shortx.services.rule.actor.HandleParams


object BreakActionExecute : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java
    }

    override suspend fun handle(params: HandleParams) {
        val d =
            params.action.unpack(tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java)
        throw BreakActionException("BreakActionExecute action", scope = d.scope)
    }
}

inline fun BreakActionExecuteScope.rethrowOrReturnLoop(returnLoop: () -> Unit) {
    when (this) {
        BreakActionExecuteScope.BreakActionExecuteScope_Current -> {
            returnLoop()
        }

        BreakActionExecuteScope.BreakActionExecuteScope_Parent -> {
            throw BreakActionException(
                "BreakActionExecute action",
                scope = BreakActionExecuteScope.BreakActionExecuteScope_Current
            )
        }

        BreakActionExecuteScope.BreakActionExecuteScope_Root -> {
            throw BreakActionException(
                "BreakActionExecute root action",
                scope = BreakActionExecuteScope.BreakActionExecuteScope_Root
            )
        }

        else -> {
            returnLoop()
        }
    }
}