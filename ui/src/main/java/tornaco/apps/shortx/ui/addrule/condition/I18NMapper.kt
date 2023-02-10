package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON

@Composable
fun labelForConditionSelector(condition: Class<out Condition>): List<String> {
    return when (condition) {
        Condition.CurrentForegroundApp::class.java -> {
            listOf(LocalI18N.current["ui.dialog.title.current.app"])
        }
        Condition.MVEL::class.java -> {
            listOf(LocalI18N.current["ui.title.mvel"])
        }
        Condition.TRUE::class.java -> {
            listOf(
                LocalI18N.current["ui.conditions.true"]
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
            mutableListOf(LocalI18N.current["ui.dialog.title.current.app"]).apply {
                if (condition.pkgSets.isNotEmpty()) {
                    addAll(condition.pkgSets.map {
                        "${it.label}${PREFIX_MD_ICON}Folder"
                    })
                } else {
                    addAll(condition.apps.map {
                        "${it.label}$PREFIX_APP_ICON${it.pkg.pkgName}"
                    })
                }
            }
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
fun IconForConditionSelector(condition: Class<out Condition>) {
    when (condition) {
        Condition.CurrentForegroundApp::class.java -> {
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "CurrentForegroundApp"
            )
        }
        Condition.MVEL::class.java -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Condition.TRUE::class.java -> {
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