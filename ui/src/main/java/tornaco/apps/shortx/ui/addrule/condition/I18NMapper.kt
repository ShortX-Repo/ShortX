package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
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
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "CurrentForegroundApp"
            )
        }
        Condition.MVEL::class -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Condition.TRUE::class -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle, contentDescription = "True"
            )
        }
    }
}


@Composable
fun IconForCondition(condition: Condition) {
    when (condition) {
        is Condition.CurrentForegroundApp -> {
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "CurrentForegroundApp"
            )
        }
        is Condition.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Condition.TRUE -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle, contentDescription = "True"
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
            Icon(
                imageVector = Icons.Filled.SelectAll, contentDescription = "All"
            )
        }
        Op.Any -> {
            Icon(
                imageVector = Icons.Filled.Analytics, contentDescription = "Any"
            )
        }

        Op.None -> {
            Icon(
                imageVector = Icons.Filled.InsertInvitation, contentDescription = "None"
            )
        }

        is Op.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
    }
}