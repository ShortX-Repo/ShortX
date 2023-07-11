package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.rule.cond.Cond

object ChargeState : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.ChargeState::class.java

    override suspend fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.ChargeState::class.java
            val state =
                requireNotNull(BatteryImpl(context).batteryState) { "Fail get battery state" }
            val res = state.isCharging == data.requireIsCharge
            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}