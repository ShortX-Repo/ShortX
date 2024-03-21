package tornaco.apps.shortx.ui.addrule.condition.mapper

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.common.timeOfADayRangeToString
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.QSTileState
import tornaco.apps.shortx.core.proto.condition.TheXXTimeTodayScope
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppComponents
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass

fun labelForConditionSelector(i18N: I18N, condition: KClass<out Condition>): List<String> {
    return when (condition) {
        Condition.CurrentForegroundApp::class -> {
            listOf(i18N["ui.dialog.title.current.app"])
        }

        Condition.CurrentForegroundAppByPkg::class -> {
            listOf(i18N["ui.dialog.title.current.app.by.pkg"])
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

        Condition.CurrentActivity::class -> {
            listOf(i18N["ui.conditions.current.activity"])
        }

        Condition.AppHasAudioFocus::class -> {
            listOf(i18N["ui.conditions.has.audio.focus"])
        }

        Condition.AppHasWindowFocus::class -> {
            listOf(i18N["ui.condition.app.has.window.focus"])
        }

        Condition.MVEL::class -> {
            listOf(i18N["ui.title.mvel"])
        }

        Condition.JS::class -> {
            listOf(i18N["ui.title.js"])
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

        Condition.EvaluateContextVar::class -> {
            listOf(
                i18N["ui.conditions.eva.context.var"]
            )
        }

        Condition.EvaluateLocalVar::class -> {
            listOf(
                i18N["ui.conditions.eva.local.var"]
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

        Condition.ConnectedWifiSignal::class -> {
            listOf(
                i18N["ui.conditions.wifi.signal"]
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

        Condition.RequireBTDeviceFound::class -> {
            listOf(
                i18N["ui.conditions.bt.device.found"]
            )
        }

        Condition.RequireMobileDataEnabled::class -> {
            listOf(
                i18N["ui.conditions.mobile.data.enabled"]
            )
        }

        Condition.AppHasNotification::class -> {
            listOf(
                i18N["ui.conditions.app.has.notification"]
            )
        }

        Condition.AppHasTask::class -> {
            listOf(
                i18N["ui.conditions.app.has.task"]
            )
        }

        Condition.KeyguardIsLocked::class -> {
            listOf(
                i18N["ui.conditions.screen.locked"]
            )
        }

        Condition.ScreenOrientationIsPort::class -> {
            listOf(
                i18N["ui.conditions.screen.orientation.p"]
            )
        }

        Condition.IsInCall::class -> {
            listOf(i18N["ui.condition.is.incall"])
        }

        Condition.IsRinging::class -> {
            listOf(i18N["ui.condition.is.ringing"])
        }

        Condition.HasNodeOnScreen::class -> {
            listOf(i18N["ui.condition.is.has.node.on.screen"])
        }

        Condition.IsRuleEnabled::class -> {
            listOf(i18N["ui.condition.is.rule.enabled"])
        }

        Condition.IsHeadsetPlug::class -> {
            listOf(
                i18N["ui.fact.headset.plug"],
            )
        }

        Condition.RequireScreenRotate::class -> {
            listOf(
                i18N["ui.fact.screen.rotate"],
            )
        }

        Condition.RequireWindowRotation::class -> {
            listOf(
                i18N["ui.fact.window.rotate"],
            )
        }

        Condition.RequireDelay::class -> {
            listOf(
                i18N["ui.action.delay"],
            )
        }

        Condition.RequireSensorOff::class -> {
            listOf(
                i18N["ui.action.sensors.off"],
            )
        }

        Condition.RequireTileState::class -> {
            listOf(
                i18N["ui.conditions.tile.state"],
            )
        }

        Condition.RequireFactTag::class -> {
            listOf(
                i18N["ui.condition.fact.tag"],
            )
        }

        Condition.RequireAPMMode::class -> {
            listOf(
                i18N["ui.condition.apm.mode"],
            )
        }

        Condition.RequireIMEVisibility::class -> {
            listOf(
                i18N["ui.condition.ime.visibility"],
            )
        }

        Condition.TheXXTimeToday::class -> {
            listOf(
                i18N["ui.conditions.the.xx.time.within"],
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

        is Condition.CurrentForegroundAppByPkg -> {
            listOf(i18N["ui.dialog.title.current.app.by.pkg"])
        }

        is Condition.AppIsRunning -> {
            mutableListOf(i18N["ui.conditions.app.running"])
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppHasNotification -> {
            mutableListOf(i18N["ui.conditions.app.has.notification"])
                .appendConditionOp(condition.apps, condition.pkgSets, condition.op, i18N)
                .labelLinesFromAppsAndPkgSets(condition.apps, condition.pkgSets)
        }

        is Condition.AppHasTask -> {
            mutableListOf(i18N["ui.conditions.app.has.task"])
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

        is Condition.AppHasWindowFocus -> {
            mutableListOf(i18N["ui.condition.app.has.window.focus"])
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

        is Condition.CurrentActivity -> {
            mutableListOf(i18N["ui.conditions.current.activity"]).apply {
                if (condition.activities.size > 1) {
                    add(opDescriptionForComponent(op = condition.op, i18N = i18N))
                }
            }.labelLinesFromAppComponents(
                condition.activities
            )
        }

        is Condition.MVEL -> {
            listOf(i18N["ui.title.mvel"], condition.expression)
        }

        is Condition.JS -> {
            listOf(i18N["ui.title.js"], condition.expression)
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

        is Condition.KeyguardIsLocked -> {
            listOf(
                i18N["ui.conditions.screen.locked"],
            )
        }

        is Condition.VPNIsConnected -> {
            listOf(
                i18N["ui.conditions.vpn.is.connected"],
            )
        }

        is Condition.EvaluateGlobalVar -> listOf(
            i18N["ui.conditions.eva.global.var"],
            condition.varName
        )

        is Condition.EvaluateContextVar -> listOf(
            i18N["ui.conditions.eva.context.var"],
            condition.varName
        )

        is Condition.EvaluateLocalVar -> listOf(
            i18N["ui.conditions.eva.local.var"],
            condition.varName
        )

        is Condition.BatteryPercent -> {
            listOf(
                i18N["ui.conditions.battery.percent"],
                "${condition.op.toDisplayName()} ${condition.value}%"
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
                i18N["ui.conditions.plug.type.${condition.type.name.lowercase()}"]
            )
        }

        is Condition.TimeInRange -> {
            listOf(
                i18N["ui.conditions.time.in.range"],
                condition.range.timeOfADayRangeToString()
            )
        }

        is Condition.ConnectedWifiSignal -> {
            listOf(
                i18N["ui.conditions.wifi.signal"],
                "${condition.op.toDisplayName()} ${condition.level}"
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

        is Condition.RequireBTDeviceFound -> {
            listOf(
                i18N["ui.conditions.bt.device.found"],
                condition.device
            )
        }

        is Condition.ScreenOrientationIsPort -> {
            listOf(
                i18N["ui.conditions.screen.orientation.p"],
            )
        }

        is Condition.RequireMobileDataEnabled -> {
            listOf(
                i18N["ui.conditions.mobile.data.enabled"],
                (condition.slot >= 0).select(
                    i18N["ui.common.mobile.data.enabled.slot", mapOf(
                        "slotId" to condition.slot.toString(),
                        "label" to (shortXManager.getSlotLabel(condition.slot) ?: "")
                    )],
                    i18N["ui.conditions.mobile.data.enabled.any"]
                )
            )
        }

        is Condition.IsInCall -> {
            listOf(
                i18N["ui.condition.is.incall"]
            )
        }

        is Condition.IsRinging -> {
            listOf(
                i18N["ui.condition.is.ringing"]
            )
        }

        is Condition.HasNodeOnScreen -> {
            listOf(
                i18N["ui.condition.is.has.node.on.screen"]
            )
        }

        is Condition.IsRuleEnabled -> {
            listOf(
                i18N["ui.condition.is.rule.enabled"],
                condition.ruleLabel
            )
        }

        is Condition.IsHeadsetPlug -> {
            listOf(
                i18N["ui.fact.headset.plug"],
                condition.isPlug.select(
                    i18N["ui.condition.headset.plugged"],
                    i18N["ui.condition.headset.unplugged"]
                )
            )
        }

        is Condition.RequireScreenRotate -> {
            listOf(
                i18N["ui.fact.screen.rotate"],
                i18N[condition.degree.factLabelKey()]
            )
        }

        is Condition.RequireWindowRotation -> {
            listOf(
                i18N["ui.fact.window.rotate"],
                i18N[condition.degree.factLabelKey()]
            )
        }

        is Condition.RequireDelay -> {
            listOf(
                i18N["ui.action.delay"],
                i18N[condition.timeString]
            )
        }

        is Condition.RequireSensorOff -> {
            listOf(
                i18N["ui.action.sensors.off"],
                condition.isRequireOn.select(
                    i18N["ui.common.on"],
                    i18N["ui.common.off"]
                )
            )
        }

        is Condition.RequireTileState -> {
            listOf(
                i18N["ui.conditions.tile.state"],
                i18N[condition.state.labelKey()]
            )
        }

        is Condition.RequireFactTag -> {
            listOf(
                i18N["ui.condition.fact.tag"],
                condition.tag
            )
        }

        is Condition.RequireAPMMode -> {
            listOf(
                i18N["ui.condition.apm.mode"],
                condition.isEnableAPM.select(
                    i18N["ui.common.on"],
                    i18N["ui.common.off"],
                )
            )
        }

        is Condition.RequireIMEVisibility -> {
            listOf(
                i18N["ui.condition.ime.visibility"],
                condition.isShown.select(
                    i18N["ui.fact.ime.visibility.show"],
                    i18N["ui.fact.ime.visibility.hide"],
                )
            )
        }

        is Condition.TheXXTimeToday -> {
            listOf(
                i18N["ui.conditions.the.xx.time.within"],
                "${i18N[condition.scope.labelKey()]} ${condition.what} ${condition.op.toDisplayName()} ${condition.time}"
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

fun QSTileState.labelKey(): String {
    return "ui.conditions.tile.state.${this.name.lowercase()}"
}

fun TheXXTimeTodayScope.labelKey(): String {
    return when (this) {
        TheXXTimeTodayScope.Today -> "ui.conditions.the.xx.time.within.today"
        TheXXTimeTodayScope.TodayAM -> "ui.conditions.the.xx.time.within.today.am"
        TheXXTimeTodayScope.TodayPM -> "ui.conditions.the.xx.time.within.today.pm"
        TheXXTimeTodayScope.ThisWeek -> "ui.conditions.the.xx.time.within.this.week"
        TheXXTimeTodayScope.ThisMonth -> "ui.conditions.the.xx.time.within.this.month"
        TheXXTimeTodayScope.UNRECOGNIZED -> "UNKNOWN"
    }
}