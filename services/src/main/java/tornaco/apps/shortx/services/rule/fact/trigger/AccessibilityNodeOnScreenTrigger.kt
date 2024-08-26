package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.wm.NodeMatcher
import tornaco.apps.shortx.services.wm.NodeMatcher.extractMatchers
import tornaco.apps.shortx.services.wm.NodeMatcherRes


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
    private val nodeMatchers = mutableMapOf<HasFoundNodeOnScreenBrief, NodeMatcher.MatcherList>()
    private var currentClassName: String? = null
    private var currentClassTimeMillis: Long = 0L

    private val scope by lazy { CoroutineScope(SupervisorJob()) }

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
        scope.launchSafety {
            doRegister()
        }
    }

    private suspend fun doRegister() {
        logger.i("doRegister")
        nodeMatchers.clear()

        shortXContext.rule.forEachEnabledFacts { _, factProtoAny ->
            if (factProtoAny is_ HasFoundNodeOnScreen::class.java) {
                val fact = factProtoAny unpack_ HasFoundNodeOnScreen::class.java
                if (!fact.isDisabled) {
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
            shortXContext.wm.withUiAutomation("Has node matcher.") { wms ->
                wms.setOnAccessibilityEvent { event ->
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
                        onDebugBuild { logger.d("TYPE_WINDOW_CONTENT_CHANGED currentClassName: $currentClassName timePassed: $timePassed") }

                        // Ignore some packages.
                        if (rootPkgName != "android") {
                            val matcherForThisPackage =
                                nodeMatchers
                                    .filterKeys { it.packageName == rootPkgName || it.packageName.isEmpty() || it.packageName == "*" }
                                    .filterKeys { it.componentName == null || it.componentName.className == currentClassName }
                                    .filterKeys { it.timeout < 0 || it.timeout >= timePassed }

                            val shouldCheckEvent = matcherForThisPackage.isNotEmpty()
                            if (shouldCheckEvent) {
                                matcherForThisPackage.forEach { (brief, matcherList) ->
                                    // Handle every Fact
                                    matcherList.matchers.forEach FE@{ matcherAny ->
                                        val hasFoundNode =
                                            shortXContext.wm.hasFoundNodeOnScreen { viewNode ->
                                                val matchRes =
                                                    NodeMatcher.matchNode(matcherAny, viewNode)
                                                when (matchRes) {
                                                    is NodeMatcherRes.MatchById -> {
                                                        shortXContext.rule.publishFactChecked(
                                                            HasFoundNodeOnScreen.getDefaultInstance()
                                                        ) {
                                                            FactPublishData(
                                                                reason = "Match MatchById",
                                                                fact = HasFoundNodeOnScreen.newBuilder()
                                                                    .setId(brief.id)
                                                                    .build(),
                                                                initialContextDataLazy = {
                                                                    mapOf(
                                                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewId.name to matchRes.matchViewIdContextData,
                                                                        ContextDataMapping.HasFoundNodeOnScreen.sourceNodeId.name to matchRes.sourceNodeId,
                                                                        ContextDataMapping.HasFoundNodeOnScreen.windowId.name to matchRes.windowId,
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    }

                                                    is NodeMatcherRes.MatchByText -> {
                                                        shortXContext.rule.publishFactChecked(
                                                            HasFoundNodeOnScreen.getDefaultInstance()
                                                        ) {
                                                            FactPublishData(
                                                                reason = "Match MatchByText",
                                                                fact = HasFoundNodeOnScreen.newBuilder()
                                                                    .setId(brief.id)
                                                                    .build(),
                                                                initialContextDataLazy = {
                                                                    mapOf(
                                                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewText.name to matchRes.matchTextContextData,
                                                                        ContextDataMapping.HasFoundNodeOnScreen.matchedViewTextIsRegex.name to matchRes.matchIsRegexContextData,
                                                                        ContextDataMapping.HasFoundNodeOnScreen.sourceNodeId.name to matchRes.sourceNodeId,
                                                                        ContextDataMapping.HasFoundNodeOnScreen.windowId.name to matchRes.windowId,
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    }

                                                    NodeMatcherRes.NotMatch -> {

                                                    }
                                                }
                                                matchRes.isMatched
                                            }
                                        logger.d("brief: $brief $hasFoundNode")
                                        if (hasFoundNode) {
                                            return@FE
                                        }
                                    }
                                }
                            } else {
                                logger.p("No need to check, shouldCheckEvent is false.")
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

private fun HasFoundNodeOnScreen.extractMatchers(): NodeMatcher.MatcherList {
    return matchersList.extractMatchers()
}

private fun tryGetActivity(context: Context, componentName: ComponentName): ActivityInfo? {
    return try {
        context.packageManager.getActivityInfo(componentName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}