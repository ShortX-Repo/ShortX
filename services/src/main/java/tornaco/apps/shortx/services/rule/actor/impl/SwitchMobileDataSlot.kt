package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import util.Reflections


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
            SubscriptionManager.from(androidContext).activeSubscriptionInfoList.firstOrNull {
                it.simSlotIndex == data.slotId
            }
        requireNotNull(targetSubInfo) {
            "Sub not found for slot: ${data.slotId}"
        }
        logger.i("setDataEnabled: ${targetSubInfo.subscriptionId} isMIUI? ${OsUtils.isMIUI()}")

        if (!setDefaultDataSlotIdOnMIUI(data.slotId)) {
            SubscriptionManager.from(androidContext)
                .setDefaultDataSubId(targetSubInfo.subscriptionId)
        }
    }

    // MIUI 14
    // import miui.telephony.SubscriptionManager;
    // SubscriptionManager.getDefault().setDefaultDataSlotId(i);
    private fun setDefaultDataSlotIdOnMIUI(slotId: Int): Boolean {
        return runCatching {
            if (!OsUtils.isMIUI()) return false
            val clazz = Class.forName("miui.telephony.SubscriptionManager")
            logger.d("SubscriptionManagerClass: $clazz")
            val subManager = Reflections.callStaticMethod(clazz, "getDefault")
            Reflections.callMethod(subManager, "setDefaultDataSlotId", slotId)
            logger.d("setDefaultDataSlotIdOnMIUI: $slotId OK.")
            true
        }.getOrElse {
            logger.e(it, "setDefaultDataSlotIdOnMIUI")
            false
        }
    }
}

