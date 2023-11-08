package tornaco.apps.shortx.services.rule.cond.impl

import android.annotation.SuppressLint
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireMobileDataEnabled : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled::class.java

    @SuppressLint("MissingPermission")
    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled::class.java
            val res = if (data.slotId < 0) {
                context.telephonyManager.isDataEnabled
            } else {
                val targetSubInfo =
                    SubscriptionManager.from(context).activeSubscriptionInfoList.firstOrNull {
                        it.simSlotIndex == data.slotId
                    }
                requireNotNull(targetSubInfo) {
                    "Sub not found for slot: ${data.slotId}"
                }
                logger.d("targetSubInfo.subId: ${targetSubInfo.subscriptionId}")

                context.telephonyManager.getDataEnabled(targetSubInfo.subscriptionId)
            }

            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}