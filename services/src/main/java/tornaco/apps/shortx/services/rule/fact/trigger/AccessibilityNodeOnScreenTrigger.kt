package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
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


private data class HasFoundNodeOnScreenBrief(
    val id: String,
    val packageName: String,
    val componentName: ComponentName?,
    val timeout: Long
)

class AccessibilityNodeOnScreenTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("AccessibilityNodeOnScreenTrigger")
    private val nodeMatchers = mutableMapOf<HasFoundNodeOnScreenBrief, MatcherList>()
    private var currentClassName: String? = null
    private var currentClassTimeMillis: Long = 0L

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
            (rule.factsList + rule.quit.factsList).forEach { factProtoAny ->
                if (factProtoAny is_ HasFoundNodeOnScreen::class.java) {
                    val fact = factProtoAny unpack_ HasFoundNodeOnScreen::class.java
                    fact.extractMatchers().let {
                        val brief = HasFoundNodeOnScreenBrief(
                            fact.id,
                            fact.packageName,
                            ComponentName.unflattenFromString(fact.componentName),
                            fact.detectTimeout
                        )
                        logger.d("brief: $brief")
                        nodeMatchers[brief] = it
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
                if (event.eventType == TYPE_WINDOW_STATE_CHANGED) {
                    val eventPackageName = event.packageName?.toString()
                    val eventClassName = event.className?.toString()
                    if (eventPackageName != null && eventClassName != null) {
                        val componentName = ComponentName(eventPackageName, eventClassName)
                        val activityInfo = tryGetActivity(context, componentName)
                        val isActivity = activityInfo != null
                        if (isActivity) {
                            onDebugBuild { logger.d("isActivity, componentName: $componentName") }
                            if (currentClassName != componentName.className) {
                                currentClassName = componentName.className
                                currentClassTimeMillis = System.currentTimeMillis()
                            }
                        }
                    }
                } else if (event.eventType == TYPE_WINDOW_CONTENT_CHANGED) {
                    val timePassed = System.currentTimeMillis() - currentClassTimeMillis

                    val rootPkgName = event.packageName
                    onDebugBuild { logger.d("TYPE_WINDOW_CONTENT_CHANGED currentClassName: $currentClassName") }

                    val matcherForThisPackage =
                        nodeMatchers
                            .filterKeys { it.packageName == rootPkgName || it.packageName.isEmpty() || it.packageName == "*" }
                            .filterKeys { it.componentName == null || it.componentName.className == currentClassName }
                            .filterKeys { it.timeout < 0 || it.timeout >= timePassed }

                    val shouldCheckEvent = matcherForThisPackage.isNotEmpty()
                    if (shouldCheckEvent) {
                        val allNodes = shortXContext.wm.getAllNodesFromScreen()
                        onDebugBuild { logger.d("allNodes: $allNodes") }

                        matcherForThisPackage.forEach { (brief, matcherList) ->
                            var matchViewIdContextData = ""
                            var matchTextContextData = ""
                            var matchIsRegexContextData = false
                            var sourceNodeId = 0L
                            var windowId = 0
                            val matchedOnThisFact = matcherList.matchers.any { matcherAny ->
                                when (matcherAny) {
                                    is NodeMatcherViewId -> {
                                        val isMatch =
                                            allNodes.any { viewNode ->
                                                (viewNode.viewId == matcherAny.id || viewNode.shortViewId == matcherAny.id).also {
                                                    if (it) {
                                                        sourceNodeId = viewNode.sourceNodeId
                                                        windowId = viewNode.windowId
                                                        matchViewIdContextData = viewNode.viewId
                                                    }
                                                }
                                            }
                                        if (isMatch) {
                                            logger.d("Match viewId: $matchViewIdContextData $sourceNodeId")
                                        }
                                        isMatch
                                    }

                                    is NodeMatcherText -> {
                                        val isMatch =
                                            allNodes.any { viewNode ->
                                                matchTextOrRegex(viewNode.text, matcherAny).also {
                                                    matchTextContextData = matcherAny.text
                                                    matchIsRegexContextData = matcherAny.isRegex
                                                    sourceNodeId = viewNode.sourceNodeId
                                                    windowId = viewNode.windowId
                                                }
                                            }
                                        if (isMatch) {
                                            logger.d("Match text: $matchTextContextData $sourceNodeId")
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
                                        ContextDataMapping.HasFoundNodeOnScreen.sourceNodeId.name to sourceNodeId,
                                        ContextDataMapping.HasFoundNodeOnScreen.windowId.name to windowId,
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

private fun tryGetActivity(context: Context, componentName: ComponentName): ActivityInfo? {
    return try {
        context.packageManager.getActivityInfo(componentName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}