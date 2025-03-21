package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ToggleSlot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleSlot::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ToggleSlot::class.java
        val state: OnOffToggle = d.state
        val slot = d.slotId
        logger.w("ToggleSlotForSlot: $slot, $state")

        logger.d("getSubscriptionId: ${SubscriptionManager.getSubId(slot)}")

        val isCurrentlyActive =
            SubscriptionManager.from(androidContext).activeSubscriptionInfoList.any {
                it.simSlotIndex == slot
            }
        logger.i("ToggleSlotForSlot, isCurrentlyActive: $isCurrentlyActive")
        val subId = getSubId(androidContext, slot)
        logger.i("ToggleSlotForSlot, subId: $subId")
        val isToEnable = when (state) {
            OnOffToggle.OnOffToggle_On -> {
                true
            }

            OnOffToggle.OnOffToggle_Off -> {
                false
            }

            OnOffToggle.OnOffToggle_Toggle -> {
                !isCurrentlyActive
            }

            OnOffToggle.UNRECOGNIZED -> {
                true
            }
        }
        logger.i("ToggleSlotForSlot, isToEnable: $isToEnable")
        SubscriptionManager.from(androidContext)
            .setUiccApplicationsEnabled(subId, isToEnable)

        Actor.Res.EMPTY
    }

    @SuppressLint("MissingPermission")
    private fun getSubId(context: Context, slotIndex: Int): Int {
        return if (OsUtils.isUOrAbove()) {
            SubscriptionManager.from(context).allSubscriptionInfoList.firstOrNull {
                it.simSlotIndex == slotIndex
            }?.subscriptionId ?: throw Error("Unable to get subId for slot $slotIndex")
        } else {
            slotIndex + 1
        }
    }
}