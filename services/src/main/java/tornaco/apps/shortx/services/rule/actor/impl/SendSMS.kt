package tornaco.apps.shortx.services.rule.actor.impl

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.PhoneUtils
import java.util.concurrent.atomic.AtomicInteger


object SendSMS : Actor {
    private const val SMS_SENT_ACTION = "shortx.action.SMS_SENT"
    private val requestCode = AtomicInteger(8888)

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        ContextCompat.registerReceiver(
            androidContext,
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    logger.d("onReceive: $intent")
                    if (intent == null || intent.action == null) return
                    val action = intent.action
                    if (action == SMS_SENT_ACTION) {
                        logger.i("Received SMS_SENT_ACTION")
                    }
                }
            },
            IntentFilter(SMS_SENT_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SendSMS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val t = action unpack_ tornaco.apps.shortx.core.proto.action.SendSMS::class.java
        val subId = PhoneUtils.slotIdToSubIdOrThrow(androidContext, t.slotId)
        val smsManager = androidContext.getSystemService(Context.SMS_SERVICE) as SmsManager
        val smsForSub = if (OsUtils.isSOrAbove()) {
            smsManager.createForSubscriptionId(subId)
        } else {
            SmsManager.getSmsManagerForContextAndSubscriptionId(androidContext, subId)
        }
        logger.d("smsForSub: $smsForSub")

        // 设置短信内容
        val message = t.message
        // 设置目标手机号
        val phoneNumber = t.to
        // 设置 PendingIntent，用于接收发送短信的状态
        val sentIntent = PendingIntent.getBroadcast(
            androidContext,
            requestCode.getAndIncrement(),
            Intent(SMS_SENT_ACTION),
            PendingIntent.FLAG_IMMUTABLE
        )

        smsForSub.sendTextMessage(phoneNumber, null, message, sentIntent, null)
    }
}