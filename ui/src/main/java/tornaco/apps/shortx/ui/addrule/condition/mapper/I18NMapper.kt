package tornaco.apps.shortx.ui.addrule.condition.mapper

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppComponents
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

        Condition.AppIsRunning::class -> {
            listOf(i18N["ui.conditions.app.running"])
        }

        Condition.AppIsNotRunning::class -> {
            listOf(i18N["ui.conditions.app.not.running"])
        }

        Condition.ServiceIsRunning::class -> {
            listOf(i18N["ui.conditions.service.running"])
        }

        Condition.AppHasAudioFocus::class -> {
            listOf(i18N["ui.conditions.has.audio.focus"])
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

        Condition.BatteryPercent::class -> {
            listOf(
                i18N["ui.conditions.battery.percent"]
            )
        }

        Condition.ScreenIsOn::class -> {
            listOf(
                i18N["ui.conditions.screen.on"]
            )
        }

        Condition.VPNIsConnected::class -> {
            listOf(
                i18N["ui.conditions.vpn.is.connected"]
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

        is Condition.AppIsRunning -> {
            mutableListOf(i18N["ui.conditions.app.running"])
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppIsNotRunning -> {
            mutableListOf(i18N["ui.conditions.app.not.running"])
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.ServiceIsRunning -> {
            mutableListOf(i18N["ui.conditions.service.running"]).labelLinesFromAppComponents(
                condition.services
            )
        }

        is Condition.AppHasAudioFocus -> {
            mutableListOf(i18N["ui.conditions.has.audio.focus"])
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

        is Condition.ScreenIsOn -> {
            listOf(
                i18N["ui.conditions.screen.on"],
            )
        }

        is Condition.VPNIsConnected -> {
            listOf(
                i18N["ui.conditions.vpn.is.connected"],
            )
        }

        is Condition.EvaluateGlobalVar -> listOf(
            i18N["ui.conditions.eva.global.var"]
        )

        is Condition.BatteryPercent -> {
            listOf(
                i18N["ui.conditions.battery.percent"],
                "${condition.op} ${condition.value}%"
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