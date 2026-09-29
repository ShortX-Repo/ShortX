package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK
import android.os.PowerManager.WakeLock
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StayAwake : Actor {
    private var wakeLock: WakeLock? = null

    fun isStayAwake(): Boolean = wakeLock?.isHeld ?: false

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StayAwake::class.java
    }

    @SuppressLint("WakelockTimeout", "Wakelock")
    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.StayAwake::class.java
        logger.w("StayAwake stay? ${d.isStay}")
        val stay: OnOffToggle = d.isStay
        val isToStay = when (stay) {
            OnOffToggle.OnOffToggle_On -> true
            OnOffToggle.OnOffToggle_Off -> false
            OnOffToggle.OnOffToggle_Toggle -> {
                !isStayAwake()
            }

            OnOffToggle.UNRECOGNIZED -> false
        }
        logger.w("StayAwake isToStay?: $isToStay")

        if (isToStay) {
            wakeLock?.release()
            wakeLock = null
            wakeLock = androidContext.powerManager.newWakeLock(
                SCREEN_BRIGHT_WAKE_LOCK,
                BuildPropInternal.APP_PACKAGE_NAME + ":wakelock"
            ).apply {
                setReferenceCounted(false)
                acquire()
            }
            logger.w("StayAwake stay, wakeLock: $wakeLock")
        } else {
            logger.w("StayAwake release, wakeLock: $wakeLock")
            wakeLock?.release()
            wakeLock = null
        }
        Actor.Res.EMPTY
    }
}
