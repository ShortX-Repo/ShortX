package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SwitchMobileDataSlot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot::class.java
        val targetSubInfo =
            SubscriptionManager.from(context).activeSubscriptionInfoList.firstOrNull {
                it.simSlotIndex == data.slotId
            }
        requireNotNull(targetSubInfo) {
            "Sub not found for slot: ${data.slotId}"
        }
        logger.i("setDataEnabled: ${targetSubInfo.subscriptionId}")
        SubscriptionManager.from(context).setDefaultDataSubId(targetSubInfo.subscriptionId)
    }
}