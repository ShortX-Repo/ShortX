package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.plugin.toParamsData

object PluginCond : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.PluginCondition::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val d =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.PluginCondition::class.java
            logger.i("execute plugin condition: ${d.pluginId}")
            val paramsData = d.params.toParamsData()

            val isMatch = false
            val reason = ""

            if (d.isInvert) {
                !isMatch
            } else {
                isMatch
            }.toResult(reason)
        }
    }
}