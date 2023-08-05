package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.Settings
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.fact.SystemSettingsChanged
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.ShortXContext
import util.Reflections

class SystemSettingsTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("SystemSettingsTrigger")

    private val registerUrls = mutableSetOf<String>()

    private val contentObs =
        object : ContentObserver(newHandlerOfNewThread("SystemSettingsTrigger")) {
            @SuppressLint("MissingPermission")
            override fun onChange(selfChange: Boolean, uri: Uri) {
                super.onChange(selfChange, uri)

                // content://settings/system/display_color_mode
                // segments: [system, display_color_mode]
                logger.d("onChange: $uri, segments: ${uri.pathSegments}")

                runCatching {
                    val scope = uri.pathSegments[0]
                    val name = uri.pathSegments[1]
                    val value: String = when (scope) {
                        "system" -> Settings.System.getString(context.contentResolver, name)
                        "global" -> Settings.Global.getString(context.contentResolver, name)
                        "secure" -> Settings.Secure.getString(context.contentResolver, name)
                        // Settings.Config.getString(context.contentResolver, name)
                        "config" -> Reflections.callStaticMethod(
                            Settings.Config::class.java,
                            "getString",
                            context.contentResolver,
                            name
                        ).toString()

                        else -> "Unsupported Settings: $scope"
                    }
                    logger.d("scope: $scope, name: $name, value: $value")

                    shortXContext.rule.publishFact(
                        reason = "ContentObserver changed",
                        fact = SystemSettingsChanged.newBuilder()
                            .setUrlAndExpectedValueRegex(
                                StringPair.newBuilder().setFirst(uri.toString()).setSecond(value)
                                    .build()
                            )
                            .build(),
                        initialContextData = mapOf(
                            ContextDataMapping.SystemSettingsChanged.settingsUrl.name to uri.toString(),
                            ContextDataMapping.SystemSettingsChanged.settingsValue.name to value
                        )
                    )
                }.onFailure {
                    logger.e(it, "onChange")
                }
            }
        }

    private fun registerForRules(rules: List<Rule>) {
        logger.d("registerForRules: ${rules.size}")
        val urls = mutableSetOf<String>()

        fun handleFacts(factsList: List<ProtoAny>) {
            factsList.forEach { proto ->
                if (proto is_ SystemSettingsChanged::class.java) {
                    val settings = proto unpack_ SystemSettingsChanged::class.java
                    val first = settings.urlAndExpectedValueRegex.first
                    if (first.startsWith(GLOBAL_VAR_REF_PREFIX)) {
                        val globalVar = shortXContext.rule.getGlobalVarByName(
                            first.replace(
                                GLOBAL_VAR_REF_PREFIX, ""
                            )
                        )
                        logger.p("registerForRules, globalVar: $globalVar")
                        if (globalVar != null) {
                            if (globalVar.isList()) {
                                urls.addAll(globalVar.valueList(noValueReturn = { emptyList() }))
                            }
                        } else {
                            urls.add(first)
                        }
                    } else {
                        urls.add(first)
                    }
                }
            }
        }

        rules.forEach { rule ->
            handleFacts(rule.factsList)
            handleFacts(rule.quit.factsList)
        }

        // Remove already registered.
        urls.removeIf { registerUrls.contains(it) }

        logger.d("register for new urls: $urls")
        if (urls.isNotEmpty()) {
            urls.forEach {
                context.contentResolver.registerContentObserver(
                    Uri.parse(it),
                    true,
                    contentObs
                )
            }
        }

        this.registerUrls.addAll(urls)
    }

    override fun register() {
        registerForRules(shortXContext.rule.getAllEnabledRules())
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.isEnabled) {
            registerForRules(listOf(rule))
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        registerForRules(listOf(newRule))
    }

    override fun unRegister() {
        context.contentResolver.unregisterContentObserver(contentObs)
    }
}