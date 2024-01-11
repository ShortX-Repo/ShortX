package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.rule.cond.Cond

object BatteryPercent : Cond {
    override fun condClass(): Class<out Message> = BatteryPercent::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val state =
                requireNotNull(BatteryImpl(context).batteryState) { "Fail get battery state" }

            val batteryPercent = condition unpack_ BatteryPercent::class.java
            val value = batteryPercent.value
            val res = matchIntOp(
                op = batteryPercent.op ?: IntOp.UNRECOGNIZED,
                currentValue = state.batteryLevel,
                expectedValue = value
            )

            if (batteryPercent.isInvert) {
                !res
            } else {
                res
            }.toResult("Current value: ${state.batteryLevel}")
        }
    }
}