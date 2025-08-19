package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.cond.Cond

object PluginCond : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.PluginCondition::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return Cond.Result(false)
    }
}