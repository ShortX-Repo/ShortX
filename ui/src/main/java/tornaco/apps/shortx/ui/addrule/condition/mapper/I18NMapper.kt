package tornaco.apps.shortx.ui.addrule.condition.mapper

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.common.timeOfADayRangeToString
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppComponents
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
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

        Condition.ChargeState::class -> {
            listOf(
                i18N["ui.conditions.charge.state"]
            )
        }

        Condition.PlugState::class -> {
            listOf(
                i18N["ui.conditions.plug.state"]
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

        Condition.TimeInRange::class -> {
            listOf(
                i18N["ui.conditions.time.in.range"]
            )
        }

        Condition.RequireWifiConnected::class -> {
            listOf(
                i18N["ui.conditions.wifi.connected"]
            )
        }

        Condition.RequireWifiDisconnected::class -> {
            listOf(
                i18N["ui.conditions.wifi.not.connected"]
            )
        }

        Condition.RequireBTConnected::class -> {
            listOf(
                i18N["ui.conditions.bt.connected"]
            )
        }

        Condition.RequireBTDisconnected::class -> {
            listOf(
                i18N["ui.conditions.bt.not.connected"]
            )
        }

        Condition.RequireMobileDataEnabled::class -> {
            listOf(
                i18N["ui.conditions.mobile.data.enabled"]
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
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppIsRunning -> {
            mutableListOf(i18N["ui.conditions.app.running"])
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppIsNotRunning -> {
            mutableListOf(i18N["ui.conditions.app.not.running"])
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppHasAudioFocus -> {
            mutableListOf(i18N["ui.conditions.has.audio.focus"])
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.ServiceIsRunning -> {
            mutableListOf(i18N["ui.conditions.service.running"]).apply {
                if (condition.services.size > 1) {
                    add(opDescriptionForComponent(op = condition.op, i18N = i18N))
                }
            }.labelLinesFromAppComponents(
                condition.services
            )
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

        is Condition.ChargeState -> {
            listOf(
                i18N["ui.conditions.charge.state"],
                condition.requireIsCharge.select(
                    i18N["ui.conditions.charge.state.charge"],
                    i18N["ui.conditions.charge.state.not.charge"]
                )
            )
        }

        is Condition.PlugState -> {
            listOf(
                i18N["ui.conditions.plug.state"],
                condition.requireIsPlug.select(
                    i18N["ui.conditions.plug.state.plug"],
                    i18N["ui.conditions.plug.state.not.plug"]
                )
            )
        }

        is Condition.TimeInRange -> {
            listOf(
                i18N["ui.conditions.time.in.range"],
                condition.range.timeOfADayRangeToString()
            )
        }

        is Condition.RequireWifiConnected -> {
            listOf(
                i18N["ui.conditions.wifi.connected"],
                condition.requiredSSID
            )
        }

        is Condition.RequireWifiDisconnected -> {
            listOf(
                i18N["ui.conditions.wifi.not.connected"],
            )
        }

        is Condition.RequireBTConnected -> {
            listOf(
                i18N["ui.conditions.bt.connected"],
                condition.requiredDevice
            )
        }

        is Condition.RequireBTDisconnected -> {
            listOf(
                i18N["ui.conditions.bt.not.connected"],
            )
        }

        is Condition.RequireMobileDataEnabled -> {
            listOf(
                i18N["ui.conditions.mobile.data.enabled"],
                (condition.slot > 0).select(
                    i18N["ui.common.mobile.data.enabled.slot", mapOf(
                        "slotId" to condition.slot.toString(),
                        "label" to (shortXManager.getSlotLabel(condition.slot) ?: "")
                    )],
                    i18N["ui.conditions.mobile.data.enabled.any"]
                )
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
    return opDescription(op, LocalI18N.current)
}

fun opDescription(op: Op, i18N: I18N): List<String> {
    return when (op) {
        Op.All -> {
            listOf(i18N["ui.condition.op.all.desc"])
        }

        Op.Any -> {
            listOf(i18N["ui.condition.op.any.desc"])
        }

        Op.None -> {
            listOf(i18N["ui.condition.op.none.desc"])
        }

        is Op.MVEL -> {
            listOf(i18N["ui.condition.op.mvel.desc"], op.expression)
        }
    }
}

private fun MutableList<String>.appendConditionOp(
    apps: List<App> = emptyList(),
    pkgSets: List<PkgSet> = emptyList(),
    op: Op,
    i18N: I18N
): MutableList<String> {
    val showOp = pkgSets.isNotEmpty() || apps.size > 1
    return apply {
        if (showOp) {
            add(opDescriptionForApps(op, i18N))
        }
    }
}

fun opDescriptionForApps(op: Op, i18N: I18N): String {
    return when (op) {
        Op.All -> {
            (i18N["ui.condition.selector.app.op.all.desc"])
        }

        Op.Any -> {
            (i18N["ui.condition.selector.app.op.any.desc"])
        }

        else -> {
            op.toString()
        }
    }
}

fun opDescriptionForComponent(op: Op, i18N: I18N): String {
    return when (op) {
        Op.All -> {
            (i18N["ui.condition.selector.component.op.all.desc"])
        }

        Op.Any -> {
            (i18N["ui.condition.selector.component.op.any.desc"])
        }

        else -> {
            op.toString()
        }
    }
}