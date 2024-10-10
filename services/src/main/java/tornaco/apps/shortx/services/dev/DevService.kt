package tornaco.apps.shortx.services.dev

import android.media.AudioManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.phone.sms.SmsRadarService


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
        }
    }
}