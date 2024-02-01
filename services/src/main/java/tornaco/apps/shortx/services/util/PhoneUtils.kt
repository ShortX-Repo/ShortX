package tornaco.apps.shortx.services.util

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager

object PhoneUtils {
    @SuppressLint("MissingPermission")
    fun slotIdToSubIdOrThrow(context: Context, slotId: Int): Int {
        val targetSubInfo =
            SubscriptionManager.from(context).activeSubscriptionInfoList.firstOrNull {
                it.simSlotIndex == slotId
            }
        requireNotNull(targetSubInfo) {
            "Sub not found for slot: $slotId"
        }
        return targetSubInfo.subscriptionId
    }
}