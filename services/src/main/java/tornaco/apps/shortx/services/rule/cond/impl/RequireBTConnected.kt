package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper

object RequireBTConnected : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireBTConnected::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireBTConnected::class.java

            val currentConnectedDevices =
                BTHelper.getBTBondedDevices().filter { it.isConnected }.map { it.alias }
            logger.d("currentConnectedDevices: $currentConnectedDevices")

            val requiredDevice = data.device
            val res =
                (currentConnectedDevices.isNotEmpty() && requiredDevice == "*") || currentConnectedDevices.contains(
                    requiredDevice
                )
            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}