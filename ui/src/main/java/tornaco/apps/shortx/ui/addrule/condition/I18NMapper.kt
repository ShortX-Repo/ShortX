package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionColorPalettes
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.allConditionClasses
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass

@Composable
fun labelForConditionSelector(condition: KClass<out Condition>): List<String> {
    return labelForConditionSelector(LocalI18N.current, condition)
}

fun labelForConditionSelector(i18N: I18N, condition: KClass<out Condition>): List<String> {
    return when (condition) {
        Condition.CurrentForegroundApp::class -> {
            listOf(i18N["ui.dialog.title.current.app"])
        }

        Condition.MVEL::class -> {
            listOf(i18N["ui.title.mvel"])
        }

        Condition.TRUE::class -> {
            listOf(
                i18N["ui.conditions.true"]
            )
        }

        Condition.EvaluateGlobalVar::class -> {
            listOf(
                i18N["ui.conditions.eva.global.var"]
            )
        }

        else -> {
            emptyList()
        }
    }
}

@Composable
fun labelForCondition(condition: Condition): List<String> {
    return labelForCondition(LocalI18N.current, condition)
}

fun labelForCondition(i18N: I18N, condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            mutableListOf(i18N["ui.dialog.title.current.app"])
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.MVEL -> {
            listOf("MVEL expression", condition.expression)
        }

        is Condition.TRUE -> {
            listOf(
                i18N["ui.conditions.true"],
                i18N["ui.conditions.true.desc"]
            )
        }

        is Condition.EvaluateGlobalVar -> listOf(
            i18N["ui.conditions.eva.global.var"]
        )
    }
}

fun iconForCondition(condition: KClass<out Condition>): String {
    return when (condition) {
        Condition.CurrentForegroundApp::class -> {
            Remix.Logos.android_line
        }

        Condition.MVEL::class -> {
            Remix.Development.code_line
        }

        Condition.TRUE::class -> {
            Remix.System.check_line
        }

        Condition.EvaluateGlobalVar::class -> {
            Remix.Document.file_list_line
        }

        else -> {
            Remix.Document.file_unknow_line
        }
    }
}

@Composable
fun opTitle(op: Op): String {
    return when (op) {
        Op.All -> {
            "ALL"
        }

        Op.Any -> {
            "ANY"
        }

        Op.None -> {
            "NONE"
        }

        is Op.MVEL -> {
            "MVEL"
        }
    }
}


@Composable
fun opDescription(op: Op): List<String> {
    return when (op) {
        Op.All -> {
            listOf(LocalI18N.current["ui.condition.op.all.desc"])
        }

        Op.Any -> {
            listOf(LocalI18N.current["ui.condition.op.any.desc"])
        }

        Op.None -> {
            listOf(LocalI18N.current["ui.condition.op.none.desc"])
        }

        is Op.MVEL -> {
            listOf(LocalI18N.current["ui.condition.op.mvel.desc"], op.expression)
        }
    }
}

fun iconForOp(op: Op): String {
    return when (op) {
        Op.All -> {
            Remix.System.check_double_line
        }

        Op.Any -> {
            Remix.Editor.asterisk
        }

        Op.None -> {
            Remix.System.checkbox_blank_circle_line
        }

        is Op.MVEL -> {
            Remix.Development.code_line
        }
    }
}

fun colorForCondition(condition: KClass<out Condition>): Color {
    val index = allConditionClasses.indexOf(condition) % ConditionColorPalettes.size
    return when (condition) {
        Condition.CurrentForegroundApp::class -> {
            ConditionColorPalettes[index]
        }

        Condition.MVEL::class -> {
            ConditionColorPalettes[index]
        }

        Condition.TRUE::class -> {
            ConditionColorPalettes[index]
        }

        Condition.EvaluateGlobalVar::class -> {
            ConditionColorPalettes[index]
        }

        else -> {
            ConditionColorPalettes[index]
        }
    }
}

val ConditionColorPalettes = ActionColorPalettes
