package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.runtime.Composable
import github.tornaco.shortx.android.icon.Remix
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
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
        else -> {
            emptyList()
        }
    }
}

@Composable
fun labelForCondition(condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            mutableListOf(LocalI18N.current["ui.dialog.title.current.app"])
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }
        is Condition.MVEL -> {
            listOf("MVEL expression", condition.expression)
        }
        Condition.TRUE -> {
            listOf(
                LocalI18N.current["ui.conditions.true"],
                LocalI18N.current["ui.conditions.true.desc"]
            )
        }
    }
}

@Composable
fun IconForConditionSelector(condition: KClass<out Condition>) {
    when (condition) {
        Condition.CurrentForegroundApp::class -> {
            RemixIcon(
                remixName = Remix.Logos.android_line
            )
        }
        Condition.MVEL::class -> {
            RemixIcon(
                remixName = Remix.Development.code_line
            )
        }
        Condition.TRUE::class -> {
            RemixIcon(
                remixName = Remix.System.check_line
            )
        }
    }
}


@Composable
fun IconForCondition(condition: Condition) {
    when (condition) {
        is Condition.CurrentForegroundApp -> {
            RemixIcon(
                remixName = Remix.Logos.android_line
            )
        }
        is Condition.MVEL -> {
            RemixIcon(
                remixName = Remix.Development.code_line
            )
        }
        Condition.TRUE -> {
            RemixIcon(
                remixName = Remix.System.check_line
            )
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

@Composable
fun IconForOp(op: Op) {
    when (op) {
        Op.All -> {
            RemixIcon(
                remixName = Remix.System.check_double_line
            )
        }
        Op.Any -> {
            RemixIcon(
                remixName = Remix.Editor.asterisk
            )
        }

        Op.None -> {
            RemixIcon(
                remixName = Remix.System.checkbox_blank_circle_line
            )
        }

        is Op.MVEL -> {
            RemixIcon(
                remixName = Remix.Development.code_line
            )
        }
    }
}