package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.AudioFocusChanged
import tornaco.apps.shortx.core.proto.fact.AudioFocusGain
import tornaco.apps.shortx.core.proto.fact.AudioFocusLost
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.audio.FocusEvent
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class AudioFocusTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("AudioFocusTrigger")

    override fun register() {
        shortXContext.audio.systemServiceScope.launchSafety {
            shortXContext.audio.focusEvent.collect {
                logger.d("focusEvent: ${it.javaClass.simpleName} ${it.target}")

                when (it) {
                    is FocusEvent.Gain -> {
                        shortXContext.rule.publishFact(
                            reason = "FocusEvent.Gain",
                            fact = AudioFocusGain.newBuilder()
                                .addAllApps(listOf(it.target))
                                .build()
                        )
                    }

                    is FocusEvent.Lost -> {
                        shortXContext.rule.publishFact(
                            reason = "FocusEvent.Lost",
                            fact = AudioFocusLost.newBuilder()
                                .addAllApps(listOf(it.target))
                                .build()
                        )
                    }
                }

                shortXContext.rule.publishFact(
                    reason = "FocusEvent",
                    fact = AudioFocusChanged.newBuilder().build(),
                    mapOf(
                        ContextDataMapping.AudioFocusChanged.userId.name to it.target.userId,
                        ContextDataMapping.AudioFocusChanged.pkgName.name to it.target.pkgName,
                        ContextDataMapping.AudioFocusChanged.isGain.name to (it is FocusEvent.Gain),
                        ContextDataMapping.AudioFocusChanged.isLost.name to (it is FocusEvent.Lost)
                    )
                )
            }
        }

    }

    override fun unRegister() {
    }
}