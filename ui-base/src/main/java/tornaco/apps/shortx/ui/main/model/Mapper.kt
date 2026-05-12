package tornaco.apps.shortx.ui.main.model

import com.github.promeg.pinyinhelper.Pinyin
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.util.Logger
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


fun ContextViewModel<*, *>.daToDirectActionUM(
    directAction: DirectAction,
    globalVars: List<GlobalVar>,
    pinnedDAIds: Set<String> = emptySet()
): DirectActionUM {
    val firstAction: Action? = directAction.actionsList?.let { list ->
        if (list.isEmpty()) {
            null
        } else {
            kotlin.runCatching {
                list[0].toAction(shortXManager)
            }.getOrElse {
                Logger.nameless.e(it, "toAction error.")
                null
            }
        }
    }

    return DirectActionUM(
        id = directAction.id,
        title = directAction.title,
        titlePinyin = Pinyin.toPinyin(directAction.title, "").lowercase(),
        description = translateDA(directAction, globalVars),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.createTime)
        ),
        icon = directAction.icon.takeIf { it.isNotBlank() } ?: firstAction?.let {
            imageVectorForAction(it)
        } ?: Remix.Animals.dog,
        iconColor = directAction.iconColor.instructionIconColorOrNull() ?: firstAction?.let {
            colorForAction(it::class).value
        } ?: DefaultInstructionIconColor,
        directAction = directAction,
        versionCode = directAction.versionCode,
        hasAnyParameter = directAction.parametersList.isNotEmpty(),
        isPinned = pinnedDAIds.contains(directAction.id)
    )
}

private fun ContextViewModel<*, *>.translateDA(
    da: DirectAction,
    globalVars: List<GlobalVar>
) =
    da.description
        .replaceGlobalVarValues { globalVars }
        .fallbackOnEmpty(da.actionsList.mapNotNull { mod ->
            runCatching {
                mod.toAction(shortXManager)
            }.getOrElse {
                Logger.nameless.e(it, "translateDA.toAction")
                null
            }
        }.joinToString {
            labelAndDescriptionForActionSelector(i18N, it::class).first
        })

fun ContextViewModel<*, *>.ruleToRuleUM(
    rule: Rule,
    globalVars: List<GlobalVar>,
    pinnedRuleIds: Set<String> = emptySet()
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
        titlePinyin = Pinyin.toPinyin(rule.title, "").lowercase(),
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
        icon = rule.icon.takeIf { it.isNotBlank() } ?: firstFact?.let {
            imageVectorForFact(
                it::class
            )
        } ?: Remix.Animals.dog,
        iconColor = rule.iconColor.instructionIconColorOrNull() ?: firstFact?.let {
            colorForFact(it::class).value
        } ?: DefaultInstructionIconColor,
        rule = rule,
        versionCode = rule.versionCode,
        hasAnyParameter = rule.parametersList.isNotEmpty(),
        isPinned = pinnedRuleIds.contains(rule.id)
    )
}

fun ContextViewModel<*, *>.translateRule(rule: Rule): String {
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
        runCatching {
            it.toAction(shortXManager)
        }.getOrElse {
            Logger.nameless.e(it, "translateRule.toAction")
            null
        }
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
