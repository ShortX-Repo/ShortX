package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper

object RequireBTDisconnected : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected::class.java

            val currentConnectedDevices = BTHelper._connectedDevices.value.map { it.alias }
            logger.d("currentConnectedDevices: $currentConnectedDevices")

            val res = currentConnectedDevices.isEmpty()
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}