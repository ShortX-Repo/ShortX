package tornaco.apps.shortx.services.policy

import android.content.Context
import android.view.KeyEvent
import tornaco.apps.shortx.core.proto.fact.CombineKeyEvent
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
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
        updateKeyCombineManagerRules()
    }

    override fun unRegister() {
        keyCombinationManager.clearRules()
    }

    override fun onAnyChanged() {
        super.onAnyChanged()
        updateKeyCombineManagerRules()
    }

    private fun updateKeyCombineManagerRules() {
        keyCombinationManager.clearRules()

        val cks = mutableListOf<Pair<Int, Int>>()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            rule.factsAndQuitFacts.forEach {
                if (it is_ CombineKeyEvent::class.java) {
                    val ck = it unpack_ CombineKeyEvent::class.java
                    cks.add(ck.keyCode1 to ck.keyCode2)
                }
            }
        }

        val disCks = cks.distinct()
        logger.d("disCks: $disCks")

        disCks.forEach {
            keyCombinationManager
                .addRule(object : KeyCombinationManager.TwoKeysCombinationRule(
                    it.first,
                    it.second
                ) {
                    override fun execute() {
                        super.execute()
                        shortXContext.rule.publishFactChecked(
                            CombineKeyEvent.getDefaultInstance(),
                        ) {
                            FactPublishData(
                                reason = "Combine key",
                                fact = CombineKeyEvent.newBuilder()
                                    .setKeyCode1(it.first)
                                    .setKeyCode2(it.second)
                                    .build()
                            )
                        }
                    }
                })
        }
    }

    fun interceptKey(keyEvent: KeyEvent): Boolean {
        return keyCombinationManager.interceptKey(keyEvent, true)
    }

    fun isKeyConsumed(keyEvent: KeyEvent): Boolean {
        return keyCombinationManager.isKeyConsumed(keyEvent)
    }

    fun reset() {
        keyCombinationManager.reset()
    }
}