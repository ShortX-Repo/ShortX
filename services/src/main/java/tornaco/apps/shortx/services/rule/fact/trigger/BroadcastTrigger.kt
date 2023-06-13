package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.Broadcast
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class BroadcastTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("BroadcastTrigger")
    private val receivers = mutableMapOf<String, SafeBroadcastReceiver>()

    override fun register() {
        shortXContext.rule.getAllRules()
            .filter { it.isEnabled }
            .filter { it.fact.isBroadcast() }
            .forEach {
                registerForBroadcast(it, "Trigger#register")
            }
    }

    private fun registerForBroadcast(rule: Rule, reason: String) {
        val fact = rule.fact unpack_ Broadcast::class.java
        val actions = fact.actionsList
        val dataSchema = fact.dataSchema
        logger.i("register broadcast for rule: ${rule.title} $actions $dataSchema, $reason")

        val filter = IntentFilter()
        actions.forEach { filter.addAction(it) }
        val receiver = object : SafeBroadcastReceiver(shortXContext) {
            override fun handleReceive(context: Context, intent: Intent) {
                shortXContext.rule.publishFact(
                    reason = "Received broadcast",
                    fact = Broadcast
                        .newBuilder()
                        .setId(fact.id)
                        .addAllActions(listOf(intent.action))
                        .build()
                )
            }
        }

        receivers[fact.id] = receiver

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun unRegisterReceiverForBroadcast(broadcast: Broadcast, reason: () -> String) {
        receivers.remove(broadcast.id)?.let {
            logger.i("unRegisterReceiverForBroadcast: ${reason()}")
            context.unregisterReceiver(it)
        }
    }

    override fun unRegister() {
        receivers.values.forEach { context.unregisterReceiver(it) }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        if (rule.fact.isBroadcast() && rule.isEnabled) {
            registerForBroadcast(rule, "onAddRule")
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        // FAST: If both old or new is not brd, skip.
        if (!oldRule.fact.isBroadcast() && !newRule.fact.isBroadcast()) {
            logger.d("Both old or new rule is not brd.")
            return
        }

        // FAST: If brd not changed, skip.
        if (oldRule.fact.isBroadcast() && newRule.fact.isBroadcast()) {
            val oldBrd = oldRule.fact unpack_ Broadcast::class.java
            val newBrd = newRule.fact unpack_ Broadcast::class.java
            if (oldBrd == newBrd) {
                logger.d("Broadcast not change for this rule(new label): ${newRule.title}")
                return
            }
        }

        if (oldRule.fact.isBroadcast()) {
            val oldBrd = oldRule.fact unpack_ Broadcast::class.java
            unRegisterReceiverForBroadcast(oldBrd) { "unregister for old rule: ${oldRule.title}" }
        }

        if (newRule.isEnabled && newRule.fact.isBroadcast()) {
            val newBrd = newRule.fact unpack_ Broadcast::class.java
            unRegisterReceiverForBroadcast(newBrd) { "unregister for new rule: ${newRule.title}" }
            registerForBroadcast(newRule, "onUpdateRule")
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        if (rule.fact.isBroadcast()) {
            val brd = rule.fact unpack_ Broadcast::class.java
            unRegisterReceiverForBroadcast(brd) { "unregister for deleted rule: ${rule.title}" }
        }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (rule.fact.isBroadcast() && rule.isEnabled) {
                registerForBroadcast(rule, "onRuleEnabled")
            }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (rule.fact.isBroadcast()) {
                val brd = rule.fact unpack_ Broadcast::class.java
                unRegisterReceiverForBroadcast(brd) { "unregister for disabled rule: ${rule.title}" }
            }
        }
    }

    private fun ProtoAny.isBroadcast() =
        this is_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java
}