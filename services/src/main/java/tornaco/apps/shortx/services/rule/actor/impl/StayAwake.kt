package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK
import android.os.PowerManager.WakeLock
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StayAwake : Actor {
    private var wakeLock: WakeLock? = null

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StayAwake::class.java
    }

    @SuppressLint("WakelockTimeout")
    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.StayAwake::class.java
        logger.d("stay? ${d.isStay}")
        val stay: OnOffToggle = d.isStay
        val isToStay = when (stay) {
            OnOffToggle.OnOffToggle_On -> true
            OnOffToggle.OnOffToggle_Off -> false
            OnOffToggle.OnOffToggle_Toggle -> {
                val currentHold = wakeLock?.isHeld ?: false
                !currentHold
            }

            OnOffToggle.UNRECOGNIZED -> false
        }
        logger.d("isToStay?: $isToStay")

        if (isToStay) {
            wakeLock?.release()
            wakeLock = androidContext.powerManager.newWakeLock(
                SCREEN_BRIGHT_WAKE_LOCK,
                BuildProp.APP_PACKAGE_NAME + ":wakelock"
            ).apply {
                setReferenceCounted(false)
                acquire()
            }
            logger.d("stay, wakeLock: $wakeLock")
        } else {
            logger.d("release, wakeLock: $wakeLock")
            wakeLock?.release()
            wakeLock = null
        }
    }
}