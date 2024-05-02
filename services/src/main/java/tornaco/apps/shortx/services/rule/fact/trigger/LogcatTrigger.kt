package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.proto.fact.Logcat
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.LogcatManager
import tornaco.apps.shortx.services.watchdog.launchSafety

class LogcatTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    companion object {
        const val LOG_TAG = "LogcatTrigger"
    }

    private val logger = Logger(LOG_TAG)

    private val regexMatchers = mutableListOf<Pair<Logcat, Regex>>()

    override fun register() {
        LogcatManager.setCallback { line ->
            if (!line.contains(LOG_TAG)) {
                onDebugBuild { logger.d("---- catching log: $line") }

                regexMatchers.forEach {
                    val regex = it.second
                    val options = it.first.regexMatchOptions
                    val isMatched = if (options == RegexMatchOptions.RegexMatchOptions_Match) {
                        regex.matches(line)
                    } else {
                        regex.containsMatchIn(line)
                    }
                    if (isMatched) {
                        shortXContext.rule.publishFact(
                            "Logcat",
                            Logcat.newBuilder().setId(it.first.id).build()
                        ) { mapOf(ContextDataMapping.Logcat.line.name to line) }
                    }
                }
            }
        }

        shortXContext.rule.systemServiceScope.launchSafety {
            updateTriggerState()
        }
    }

    override fun unRegister() {
        LogcatManager.pause()
    }

    private fun updateTriggerState() {
        regexMatchers.clear()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            (rule.factsAndQuitFacts).forEach { factProtoAny ->
                if (factProtoAny is_ Logcat::class.java) {
                    val fact = factProtoAny unpack_ Logcat::class.java
                    if (!fact.isDisabled) {
                        runCatching { fact.regex.toRegex() }.getOrElse {
                            logger.e(it, "updateTriggerState, regex error")
                            null
                        }?.let {
                            regexMatchers.add(fact to it)
                        }
                    }
                }
            }
        }

        logger.d("regexMatchers: $regexMatchers")

        if (regexMatchers.isEmpty()) {
            logger.i("Disable logcat manager, no fact.")
            LogcatManager.pause()
        } else {
            if (!LogcatManager.isStarted()) {
                logger.i("Start.")
                LogcatManager.start()
            } else {
                logger.i("Resume.")
                LogcatManager.resume()
            }
        }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsAndQuitFacts.any { it is_ Logcat::class.java }) {
            updateTriggerState()
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsAndQuitFacts.any { it is_ Logcat::class.java }) {
            updateTriggerState()
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        if (oldRule.factsAndQuitFacts.any { it is_ Logcat::class.java }
            || newRule.factsAndQuitFacts.any { it is_ Logcat::class.java }) {
            updateTriggerState()
        }
    }
}