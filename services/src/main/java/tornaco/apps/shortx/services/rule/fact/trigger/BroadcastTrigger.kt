package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.Broadcast
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.quitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class BroadcastTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("BroadcastTrigger")
    private val receivers = mutableMapOf<String, SafeBroadcastReceiver>()

    override fun register() {
        shortXContext.rule.getAllEnabledRules()
            .filter { rule -> rule.factsList.any { it.isBroadcast() } }
            .forEach {
                checkAndRegisterForRule(it, "Trigger#register")
            }

        shortXContext.rule.getAllDirectAction().forEach {
            checkAndRegisterForDA(it, "Trigger#register")
        }
    }

    private fun checkAndRegisterForRule(rule: Rule, reason: String) {
        if (rule.isEnabled) {
            val broadcasts =
                rule.factsAndQuitFacts.filter { it.isBroadcast() }
                    .map { it unpack_ Broadcast::class.java }
            if (broadcasts.isNotEmpty()) {
                broadcasts.forEach { broadcast ->
                    registerBroadcast(broadcast, rule.title, reason)
                }
            }
        }
    }

    private fun checkAndRegisterForDA(da: DirectAction, reason: String) {
        val broadcasts =
            da.quitFacts.filter { it.isBroadcast() }
                .map { it unpack_ Broadcast::class.java }
        if (broadcasts.isNotEmpty()) {
            broadcasts.forEach { broadcast ->
                registerBroadcast(broadcast, da.title, reason)
            }
        }
    }

    private fun registerBroadcast(
        broadcast: Broadcast,
        titleForLog: String,
        reason: String
    ) {
        val actions = broadcast.actionsList
        val dataSchema = broadcast.dataSchema
        logger.i("register broadcast for rule: ${titleForLog} $actions $dataSchema, $reason")

        val filter = IntentFilter()
        actions.forEach { filter.addAction(it) }
        val receiver = object : SafeBroadcastReceiver(shortXContext) {
            override fun handleReceive(context: Context, intent: Intent) {
                logger.d("handleReceive- $intent")
                shortXContext.rule.publishFactChecked(
                    Broadcast.getDefaultInstance()
                ) {
                    FactPublishData(
                        reason = "Received broadcast",
                        fact = Broadcast
                            .newBuilder()
                            .setId(broadcast.id)
                            .build(),
                        initialContextDataLazy = {
                            mapOf(
                                ContextDataMapping.Broadcast.intent.name to Intent(
                                    intent
                                )
                            )
                        }
                    )
                }
            }
        }

        receivers[broadcast.id] = receiver

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

    private fun unRegisterReceiverForRule(rule: Rule, reason: () -> String) {
        rule.factsAndQuitFacts.filter { it.isBroadcast() }.map { it unpack_ Broadcast::class.java }
            .forEach {
                unRegisterReceiverForBroadcast(it, reason)
            }
    }

    private fun unRegisterReceiverForDA(rule: DirectAction, reason: () -> String) {
        rule.quitFacts.filter { it.isBroadcast() }.map { it unpack_ Broadcast::class.java }
            .forEach {
                unRegisterReceiverForBroadcast(it, reason)
            }
    }

    override fun unRegister() {
        receivers.values.forEach { context.unregisterReceiver(it) }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndRegisterForRule(rule, "onAddRule: ${rule.title}")
    }

    override fun onAddDA(da: DirectAction) {
        super.onAddDA(da)
        checkAndRegisterForDA(da, "onAddOrUpdateDA: ${da.title}")
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        if (oldRule.factsAndQuitFacts == newRule.factsAndQuitFacts) {
            logger.d("Facts not change for this rule(new label): ${newRule.title}")
            return
        }

        if (oldRule.factsAndQuitFacts.filter { it.isBroadcast() } == newRule.factsAndQuitFacts.filter { it.isBroadcast() }) {
            logger.d("Broadcasts not change for this rule(new label): ${newRule.title}")
            return
        }

        unRegisterReceiverForRule(oldRule) { "onUpdateRule, unRegister for old: ${oldRule.title}" }
        checkAndRegisterForRule(newRule, "onUpdateRule, register for new rule: ${newRule.title}")
    }

    override fun onUpdateDA(oldDA: DirectAction, newDA: DirectAction) {
        super.onUpdateDA(oldDA, newDA)
        if (oldDA.quitFacts == newDA.quitFacts) {
            logger.d("Facts not change for this da(new label): ${newDA.title}")
            return
        }

        if (oldDA.quitFacts.filter { it.isBroadcast() } == newDA.quitFacts.filter { it.isBroadcast() }) {
            logger.d("Broadcasts not change for this da(new label): ${newDA.title}")
            return
        }

        unRegisterReceiverForDA(oldDA) { "onUpdateDA, unRegister for old: ${oldDA.title}" }
        checkAndRegisterForDA(newDA, "onUpdateDA, register for new rule: ${newDA.title}")
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        unRegisterReceiverForRule(rule) { "onDeleteRule, unRegister for old: ${rule.title}" }
    }

    override fun onDeleteDA(da: DirectAction) {
        super.onDeleteDA(da)
        unRegisterReceiverForDA(da) { "onDeleteDA, unRegister for old: ${da.title}" }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            checkAndRegisterForRule(it, "onRuleEnabled: ${it.title}")
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            unRegisterReceiverForRule(rule) { "onRuleDisabled, unRegister for old: ${rule.title}" }
        }
    }

    private fun ProtoAny.isBroadcast() =
        this is_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java
}