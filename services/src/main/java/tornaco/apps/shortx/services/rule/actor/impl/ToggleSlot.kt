package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.systemServiceScope


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

        logger.d(
            "TEST.... getSubscriptionId: ${
                SubscriptionManager.getSubId(slot)?.toList()?.map { "SubId: $it" }
            }"
        )

        val isCurrentlyActive =
            SubscriptionManager.from(androidContext).activeSubscriptionInfoList.any {
                it.simSlotIndex == slot
            }
        logger.i("ToggleSlotForSlot, isCurrentlyActive: $isCurrentlyActive")

        val subId =
            shortXContext.phone.store.read(Settings.IntSettings.SIMSlotSubIdMapping(slot)).takeIf {
                logger.d("SIMSlotSubIdMapping.... for slot: $slot ---> subId: $it")
                it >= 0
            } ?: getSubId(androidContext, slot)

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

        logger.i("ToggleSlotForSlot, isToEnable: $isToEnable, subId: $subId")
        SubscriptionManager.from(androidContext)
            .setUiccApplicationsEnabled(subId, isToEnable)

        if (!isToEnable) {
            // Save subId mapping.
            shortXContext.phone.systemServiceScope.launch {
                shortXContext.phone.store.write(
                    Settings.IntSettings.SIMSlotSubIdMapping(slot),
                    subId
                )
            }
        }

        Actor.Res.EMPTY
    }

    @SuppressLint("MissingPermission")
    private fun getSubId(context: Context, slotIndex: Int): Int {
        return if (OsUtils.isUOrAbove()) {
            SubscriptionManager.from(context).allSubscriptionInfoList.apply {
                logger.logOnDebug {
                    "allSubscriptionInfoList. ${
                        this.map {
                            "simSlotIndex-${it.simSlotIndex} subscriptionId-${it.subscriptionId} displayName-${it.displayName?.toString()}"
                        }
                    }"
                }
            }.firstOrNull {
                it.simSlotIndex == slotIndex
            }?.subscriptionId ?: (slotIndex + 1)
        } else {
            slotIndex + 1
        }
    }
}