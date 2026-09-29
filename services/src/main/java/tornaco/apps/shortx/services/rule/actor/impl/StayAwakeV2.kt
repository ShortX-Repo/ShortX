package tornaco.apps.shortx.services.rule.actor.impl

import android.provider.Settings
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

// 保持屏幕常亮方案2：通过延长系统"屏幕超时"设置实现，避免依赖 SCREEN_BRIGHT_WAKE_LOCK
// 该废弃唤醒锁在部分厂商（如三星 One UI）设备上会被系统电源策略静默忽略。
object StayAwakeV2 : Actor {
    private const val KEEP_AWAKE_SCREEN_OFF_TIMEOUT = Int.MAX_VALUE
    private var savedScreenOffTimeout: Int? = null
    private var isStaying = false

    fun isStayAwake(): Boolean = isStaying

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StayAwakeV2::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.StayAwakeV2::class.java
        logger.w("StayAwakeV2 stay? ${d.isStay}")
        val stay: OnOffToggle = d.isStay
        val isToStay = when (stay) {
            OnOffToggle.OnOffToggle_On -> true
            OnOffToggle.OnOffToggle_Off -> false
            OnOffToggle.OnOffToggle_Toggle -> !isStaying
            OnOffToggle.UNRECOGNIZED -> false
        }
        logger.w("StayAwakeV2 isToStay?: $isToStay")

        val resolver = androidContext.contentResolver
        if (isToStay) {
            if (savedScreenOffTimeout == null) {
                savedScreenOffTimeout = runCatching {
                    Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT)
                }.getOrNull()
            }
            Settings.System.putInt(
                resolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                KEEP_AWAKE_SCREEN_OFF_TIMEOUT
            )
            isStaying = true
            logger.w("StayAwakeV2 stay, savedScreenOffTimeout: $savedScreenOffTimeout")
        } else {
            logger.w("StayAwakeV2 release, savedScreenOffTimeout: $savedScreenOffTimeout")
            savedScreenOffTimeout?.let {
                Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, it)
            }
            savedScreenOffTimeout = null
            isStaying = false
        }
        Actor.Res.EMPTY
    }
}
