package tornaco.apps.shortx.services.policy

import android.content.Context
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_POWER
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_UP
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.policy.KeyCombinationManager.TwoKeysCombinationRule
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger


class KeyCombinationTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("KeyCombinationTrigger")
    private val keyCombinationManager by lazy {
        KeyCombinationManager(shortXContext.rule.handler)
    }

    override fun register() {
        keyCombinationManager.addRule(
            object : TwoKeysCombinationRule(KEYCODE_VOLUME_UP, KEYCODE_POWER) {
                public override fun execute() {
                    logger.d("KEYCODE_VOLUME_UP+KEYCODE_POWER")
                }

                public override fun cancel() {
                }
            }
        )
    }

    override fun unRegister() {
    }

    fun interceptKey(keyEvent: KeyEvent): Boolean {
        return keyCombinationManager.interceptKey(keyEvent, true)
    }

    fun isKeyConsumed(keyEvent: KeyEvent): Boolean {
        return keyCombinationManager.isKeyConsumed(keyEvent)
    }
}