package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.Settings
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

class SystemSettingsTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("SystemSettingsTrigger")

    private val registerUrls = mutableSetOf<String>()

    private val contentObs =
        object : ContentObserver(newHandlerOfNewThread("SystemSettingsTrigger")) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                super.onChange(selfChange, uri)
                logger.d("onChange: $uri")

                shortXContext.rule.publishFact(
                    "ContentObserver changed",
                    SystemSettingsChanged.newBuilder()
                        .addAllUrl(listOf(uri.toString()))
                        .build()
                )
            }
        }

    private fun registerForRules(rules: List<Rule>) {
        val urls = mutableSetOf<String>()

        fun handleFacts(factsList: List<ProtoAny>) {
            factsList.forEach { proto ->
                if (proto is_ SystemSettingsChanged::class.java) {
                    val settings = proto unpack_ SystemSettingsChanged::class.java
                    settings.urlList.forEach {
                        if (it.startsWith(GLOBAL_VAR_REF_PREFIX)) {
                            val globalVar = shortXContext.rule.getGlobalVarByName(
                                it.replace(
                                    GLOBAL_VAR_REF_PREFIX, ""
                                )
                            )
                            logger.p("registerForRules, globalVar: $globalVar")
                            if (globalVar != null) {
                                if (globalVar.isList()) {
                                    urls.addAll(globalVar.valueList(noValueReturn = { emptyList() }))
                                }
                            } else {
                                urls.add(it)
                            }
                        } else {
                            urls.add(it)
                        }
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
                    Settings.System.getUriFor(it),
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

    override fun unRegister() {
        context.contentResolver.unregisterContentObserver(contentObs)
    }
}