package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

private data class HasFoundNodeOnScreenBrief(val id: String, val packageName: String)

class AccessibilityNodeOnScreenTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("AccessibilityNodeOnScreenTrigger")
    private val nodeMatchers = mutableMapOf<HasFoundNodeOnScreenBrief, MatcherList>()

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsList.any { it is_ HasFoundNodeOnScreen::class.java }) {
            loadTriggerState()
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsList.any { it is_ HasFoundNodeOnScreen::class.java }) {
            loadTriggerState()
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        if (oldRule.factsList.any { it is_ HasFoundNodeOnScreen::class.java }
            || newRule.factsList.any { it is_ HasFoundNodeOnScreen::class.java }) {
            loadTriggerState()
        }
    }

    private fun loadTriggerState() {
        logger.i("loadTriggerState")
        register()
    }

    override fun register() {
        nodeMatchers.clear()

        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            rule.factsList.forEach { factProtoAny ->
                if (factProtoAny is_ HasFoundNodeOnScreen::class.java) {
                    val fact = factProtoAny unpack_ HasFoundNodeOnScreen::class.java
                    fact.extractMatchers().let {
                        nodeMatchers[HasFoundNodeOnScreenBrief(fact.id, fact.packageName)] = it
                    }
                }
            }
            rule.quit.factsList.forEach { factProtoAny ->
                if (factProtoAny is_ HasFoundNodeOnScreen::class.java) {
                    val fact = factProtoAny unpack_ HasFoundNodeOnScreen::class.java
                    fact.extractMatchers().let {
                        nodeMatchers[HasFoundNodeOnScreenBrief(fact.id, fact.packageName)] = it
                    }
                }
            }
        }

        logger.p("nodeMatchers: $nodeMatchers")

        if (nodeMatchers.isEmpty()) {
            shortXContext.wm.setOnAccessibilityEvent(null)
        } else {
            runBlocking {
                shortXContext.wm.withUiAutomation("Has node matcher.") {
                }
            }

            shortXContext.wm.setOnAccessibilityEvent { event ->
                if (event.eventType == TYPE_WINDOW_CONTENT_CHANGED) {
                    val rootPkgName = event.packageName
                    val matcherForThisPackage =
                        nodeMatchers.filterKeys { it.packageName == rootPkgName || it.packageName.isEmpty() || it.packageName == "*" }
                    val shouldCheckEvent = matcherForThisPackage.isNotEmpty()
                    if (shouldCheckEvent) {
                        val allNodes = shortXContext.wm.getAllNodesFromScreen()
                        onDebugBuild { logger.d("allNodes: $allNodes") }

                        matcherForThisPackage.forEach { (brief, matcherList) ->
                            var matchViewIdContextData = ""
                            var matchTextContextData = ""
                            var matchIsRegexContextData = false
                            val matchedOnThisFact = matcherList.matchers.any { matcherAny ->
                                when (matcherAny) {
                                    is NodeMatcherViewId -> {
                                        val isMatch =
                                            allNodes.any { it.viewId == matcherAny.id || it.shortViewId == matcherAny.id }
                                        if (isMatch) {
                                            logger.d("Match viewId: ${matcherAny.id}")
                                            matchViewIdContextData = matcherAny.id
                                        }
                                        isMatch
                                    }

                                    is NodeMatcherText -> {
                                        val isMatch =
                                            allNodes.any { matchTextOrRegex(it.text, matcherAny) }
                                        if (isMatch) {
                                            logger.d("Match text: ${matcherAny.text}")
                                            matchTextContextData = matcherAny.text
                                            matchIsRegexContextData = matcherAny.isRegex
                                        }
                                        isMatch
                                    }

                                    else -> false
                                }
                            }

                            if (matchedOnThisFact) {
                                shortXContext.rule.publishFact(
                                    reason = "Match node",
                                    fact = HasFoundNodeOnScreen.newBuilder().setId(brief.id)
                                        .build(),
                                    initialContextData = mapOf(
                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewId.name to matchViewIdContextData,
                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewText.name to matchTextContextData,
                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewTextIsRegex.name to matchIsRegexContextData,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun unRegister() {

    }
}

private fun matchTextOrRegex(text: CharSequence?, matcher: NodeMatcherText): Boolean {
    if (text == null) return false
    if (matcher.isRegex && matcher.text.toRegex().matches(text.toString())) return true
    return !matcher.isRegex && matcher.text == text
}

private fun HasFoundNodeOnScreen.extractMatchers(): MatcherList {
    val matchers = mutableListOf<Any>()
    matchersList.forEach {
        val realMatcher = if (it is_ NodeMatcherText::class.java) {
            it unpack_ NodeMatcherText::class.java
        } else if (it is_ NodeMatcherViewId::class.java) {
            it unpack_ NodeMatcherViewId::class.java
        } else null
        realMatcher?.let {
            matchers.add(realMatcher)
        }
    }
    return MatcherList(matchers)
}

private data class MatcherList(val matchers: List<Any>)