package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.PlugType
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.rule.cond.Cond

object PlugState : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.PlugState::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.PlugState::class.java
            val requiredType: PlugType = data.type
            val state =
                requireNotNull(BatteryImpl(context).batteryState) { "Fail get battery state" }
            logger.d("requiredType: $requiredType, state: $state")
            val res = when (requiredType) {
                PlugType.PlugType_PlugAny -> {
                    state.isAnyPlug
                }

                PlugType.PlugType_PlugUSB -> {
                    state.isUsbPlug
                }

                PlugType.PlugType_PlugAC -> {
                    state.isAcPlug
                }

                PlugType.PlugType_PlugWireless -> {
                    state.isWirelessPlug
                }

                PlugType.PlugType_IsCharge -> {
                    state.isCharging
                }

                PlugType.UNRECOGNIZED -> {
                    false
                }
            }
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}