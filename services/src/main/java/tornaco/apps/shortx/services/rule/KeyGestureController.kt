package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import android.view.KeyEvent
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.policy.KeyCombinationTrigger
import tornaco.apps.shortx.services.policy.SingleKeyGestureDetector
import tornaco.apps.shortx.services.policy.SingleKeyRuleTrigger

class KeyGestureController(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val handler: Handler
) {
    private val logger = Logger("KeyGestureController")

    private val singleKeyGestureDetector: SingleKeyGestureDetector by lazy {
        SingleKeyGestureDetector.get().also {
            SingleKeyGestureDetector.sDefaultLongPressTimeout =
                shortXContext.gesture.getKeyDetectorLongPressTimeoutMS().toLong().apply {
                    logger.i("sDefaultLongPressTimeout: $this")
                }
        }
    }

    val singleKeyRuleTrigger by lazy {
        SingleKeyRuleTrigger(
            context,
            shortXContext,
            singleKeyGestureDetector
        )
    }

    val keyCombinationTrigger by lazy {
        KeyCombinationTrigger(
            context,
            shortXContext
        )
    }

    fun consumeKeyEvent(event: KeyEvent, interactive: Boolean): Boolean {
        val interceptKeyRes = keyCombinationTrigger.interceptKey(event, interactive)
        logger.logOnDebug("keyCombinationTrigger interceptKeyRes: $interceptKeyRes")
        if (interceptKeyRes) {
            singleKeyGestureDetector.reset()
            logger.w("keyCombinationTrigger interceptKey-consumed: $event")
            return true
        }

        // Check ADV rules.
        if (singleKeyRuleTrigger.hasAny()) {
            singleKeyGestureDetector.interceptKey(event, interactive)

            if (singleKeyRuleTrigger.hasAnyWithInterceptMode(
                    event.keyCode
                )
                && singleKeyGestureDetector.isKeyIntercepted(
                    event.keyCode
                )
            ) {
                logger.w("singleKeyRuleTrigger-consumed: $event")
                return true
            }
        }

        return false
    }
}