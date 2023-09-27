package tornaco.apps.shortx.ui.main.model

import androidx.compose.ui.graphics.Color
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.Date


fun <T> ContextViewModel<T>.daToDirectActionUM(
    directAction: DirectAction,
    activeJobs: List<String>,
    globalVars: List<GlobalVar>
): DirectActionUM {
    val firstAction: Action? = directAction.actionsList?.let {
        if (it.isEmpty()) {
            null
        } else {
            it[0].toAction(shortXManager)
        }
    }

    return DirectActionUM(
        id = directAction.id,
        title = directAction.title,
        description = translateDA(directAction, globalVars),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.createTime)
        ),
        icon = firstAction?.let {
            imageVectorForAction(it)
        } ?: Remix.Weather.sparkling_line,
        iconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        runningInsCount = activeJobs.count {
            it == directAction.id
        },
        directAction = directAction,
        versionCode = directAction.versionCode
    )
}

private fun <T> ContextViewModel<T>.translateDA(da: DirectAction, globalVars: List<GlobalVar>) =
    da.description
        .replaceGlobalVarValues { globalVars }
        .fallbackOnEmpty(da.actionsList.mapNotNull {
            it.toAction(shortXManager)
        }.joinToString {
            labelAndDescriptionForActionSelector(i18N, it::class).first
        })

fun <T> ContextViewModel<T>.ruleToRuleUM(
    rule: Rule,
    activeJobs: List<String>,
    globalVars: List<GlobalVar>
): RuleUM {
    val firstFact: Fact? =
        if (rule.factsList.isEmpty()) null else rule.factsList[0]?.toFact(shortXManager)
    val firstAction: Action? = rule.actionsList?.let {
        if (it.isEmpty()) {
            null
        } else {
            it[0].toAction(shortXManager)
        }
    }
    return RuleUM(
        id = rule.id,
        title = rule.title,
        description = rule.description.replaceGlobalVarValues { globalVars }.fallbackOnEmpty(
            translateRule(rule)
        ),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(rule.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(rule.createTime)
        ),
        isEnabled = rule.isEnabled,
        factIcon = firstFact?.let {
            imageVectorForFact(
                it::class
            )
        } ?: Remix.Weather.sparkling_line,
        factIconColor = firstFact?.let {
            colorForFact(it::class).value
        } ?: Color.Unspecified.value,
        actionIcon = firstAction?.let {
            imageVectorForAction(it)
        } ?: Remix.Weather.sparkling_line,
        actionIconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        rule = rule,
        runningInsCount = activeJobs.count {
            it == rule.id
        },
        versionCode = rule.versionCode
    )
}

fun <T> ContextViewModel<T>.translateRule(rule: Rule): String {
    val fact: String = rule.factsList
        .mapNotNull { factProto ->
            factProto.toFact(shortXManager)?.let { fact ->
                labelAndDescriptionForFactSelector(i18N, fact::class).first
            }
        }.joinToString(" / ")

    val condition = rule.conditionsList
        .filterNot { it is_ True::class.java }
        .mapNotNull {
            it.toCondition(shortXManager)
        }.joinToString {
            labelForConditionSelector(i18N, it::class).joinToString()
        }

    val action = rule.actionsList.mapNotNull {
        it.toAction(shortXManager)
    }.joinToString {
        labelAndDescriptionForActionSelector(i18N, it::class).first
    }

    return if (condition.isNotBlank()) {
        i18N["ui.rule.description.auto.template", mapOf(
            "fact" to fact,
            "condition" to condition,
            "action" to action
        )]
    } else {
        i18N["ui.rule.description.auto.no.condition.template", mapOf(
            "fact" to fact,
            "condition" to condition,
            "action" to action
        )]
    }
}
