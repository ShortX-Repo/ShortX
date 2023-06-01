package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.rule.cond.Cond

object BatteryPercent : Cond {
    override fun condClass(): Class<out Message> = BatteryPercent::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val state = BatteryImpl(context).batteryState ?: return false
            val condition = param.condition unpack_ BatteryPercent::class.java
            val value = condition.value
            when (condition.op ?: IntOp.UNRECOGNIZED) {
                IntOp.IntGreaterThan -> {
                    state.batteryLevel > value
                }

                IntOp.IntGreaterThanOrEQ -> {
                    state.batteryLevel >= value
                }

                IntOp.IntLessThan -> {
                    state.batteryLevel < value
                }

                IntOp.IntLessThanOrEQ -> {
                    state.batteryLevel <= value
                }

                IntOp.IntEqualTo -> {
                    state.batteryLevel == value
                }

                IntOp.UNRECOGNIZED -> {
                    false
                }
            }
        }
    }
}