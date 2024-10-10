package tornaco.apps.shortx.services.dev

import android.media.AudioManager
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.phone.sms.MarkAsRead
import tornaco.apps.shortx.services.phone.sms.Sms
import tornaco.apps.shortx.services.phone.sms.SmsListener
import tornaco.apps.shortx.services.phone.sms.SmsRadar
import tornaco.apps.shortx.services.phone.sms.SmsRadarService
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety


// For test purpose.
class DevService(shortXContext: ShortXContext) : SystemService(shortXContext, "DevService") {
    override fun systemReady() {
        super.systemReady()

        logger.d(
            "audio STREAM_MUSIC min volume: ${
                shortXContext.audio.getStreamMinVolume(
                    AudioManager.STREAM_MUSIC
                )
            }"
        )
        logger.d(
            "audio STREAM_MUSIC max volume: ${
                shortXContext.audio.getStreamMaxVolume(
                    AudioManager.STREAM_MUSIC
                )
            }"
        )

        SmsRadarService(context, handler, store).apply {
            onStart()
            SmsRadar.smsListener = object : SmsListener {
                override fun onSmsSent(sms: Sms) {
                    logger.logOnDebug {
                        "onSmsSent: $sms"
                    }
                }

                override fun onSmsReceived(sms: Sms) {
                    logger.logOnDebug {
                        "onSmsReceived: $sms"
                    }
                    systemServiceScope.launchSafety {
                        delay(3000)
                        val markRes = MarkAsRead.markSmsAsRead(context, sms.id)
                        logger.d("markRes: $markRes")
                    }
                }
            }
        }
    }
}