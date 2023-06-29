package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireWifiConnected : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireWifiConnected::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireWifiConnected::class.java
            logger.d("current connectedWifiSSID: ${shortXContext.network.connectedWifiSSID}")
            val requiredSSID = data.requiredSSID
            val currentConnectedOrNull = shortXContext.network.connectedWifiSSID
            val res =
                (currentConnectedOrNull != null && requiredSSID == "*") || currentConnectedOrNull == requiredSSID
            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}