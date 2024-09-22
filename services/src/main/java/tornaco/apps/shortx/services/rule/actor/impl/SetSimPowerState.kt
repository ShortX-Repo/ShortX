package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.SimCardPowerState
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.concurrent.Executors


object SetSimPowerState : Actor {
    private val executor by lazy { Executors.newSingleThreadExecutor() }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetSimPowerState::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetSimPowerState::class.java
        val telephonyManager = TelephonyManager.from(androidContext)
        val state = when (d.state) {
            SimCardPowerState.SimCardPowerState_Up -> {
                TelephonyManager.CARD_POWER_UP
            }

            SimCardPowerState.SimCardPowerState_Down -> {
                TelephonyManager.CARD_POWER_DOWN
            }

            SimCardPowerState.SimCardPowerState_Up_Pass_through -> {
                TelephonyManager.CARD_POWER_UP_PASS_THROUGH
            }

            else -> {
                TelephonyManager.CARD_POWER_UP
            }
        }
        val slot = d.slotId
        logger.w("setSimPowerStateForSlot: $slot, $state")
        telephonyManager.setSimPowerStateForSlot(
            slot, state, executor
        ) {
            logger.i("setSimPowerStateForSlot, callback: $it")
        }

        SubscriptionManager.from(androidContext)
            .setUiccApplicationsEnabled()
    }
}