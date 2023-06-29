package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireWifiDisconnected : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected::class.java
            logger.d("current connectedWifiSSID: ${shortXContext.network.connectedWifiSSID}")
            val res = shortXContext.network.connectedWifiSSID == null
            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}