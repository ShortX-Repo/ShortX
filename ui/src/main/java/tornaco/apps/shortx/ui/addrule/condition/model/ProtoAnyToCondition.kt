package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.AppHasNotification
import tornaco.apps.shortx.core.proto.condition.AppHasTask
import tornaco.apps.shortx.core.proto.condition.AppHasWindowFocus
import tornaco.apps.shortx.core.proto.condition.AppIsNotRunning
import tornaco.apps.shortx.core.proto.condition.AppIsRunning
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.ChargeState
import tornaco.apps.shortx.core.proto.condition.ConnectedWifiSignal
import tornaco.apps.shortx.core.proto.condition.CurrentActivity
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.CurrentPkgListByPkg
import tornaco.apps.shortx.core.proto.condition.EvaluateContextVar
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.EvaluateLocalVar
import tornaco.apps.shortx.core.proto.condition.HasNodeOnScreen
import tornaco.apps.shortx.core.proto.condition.IsHeadsetPlug
import tornaco.apps.shortx.core.proto.condition.IsInCall
import tornaco.apps.shortx.core.proto.condition.IsRinging
import tornaco.apps.shortx.core.proto.condition.IsRuleEnabled
import tornaco.apps.shortx.core.proto.condition.KeyguardIsLocked
import tornaco.apps.shortx.core.proto.condition.MatchJS
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.PlugState
import tornaco.apps.shortx.core.proto.condition.RequireAPMMode
import tornaco.apps.shortx.core.proto.condition.RequireBTConnected
import tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound
import tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireDelay
import tornaco.apps.shortx.core.proto.condition.RequireFactTag
import tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.condition.RequireScreenRotate
import tornaco.apps.shortx.core.proto.condition.RequireSensorOff
import tornaco.apps.shortx.core.proto.condition.RequireTileState
import tornaco.apps.shortx.core.proto.condition.RequireWifiConnected
import tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireWindowRotation
import tornaco.apps.shortx.core.proto.condition.ScreenIsOn
import tornaco.apps.shortx.core.proto.condition.ScreenOrientationIsPort
import tornaco.apps.shortx.core.proto.condition.ServiceIsRunning
import tornaco.apps.shortx.core.proto.condition.TheXXTimeToday
import tornaco.apps.shortx.core.proto.condition.TimeInRange
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.condition.VPNIsConnected
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addrule.fact.model.NodeMatcher
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels


fun ProtoAny.toCondition(shortXManager: ShortXManager): Condition? {
    return when {
        this is_ CurrentPkgList::class.java -> {
            val data = this unpack_ CurrentPkgList::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.CurrentForegroundApp(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ CurrentPkgListByPkg::class.java -> {
            val data = this unpack_ CurrentPkgListByPkg::class.java
            Condition.CurrentForegroundAppByPkg(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                stringPairs = data.pkgAndUsersList,
                op = data.op.toOp(null)
            )
        }

        this is_ AppHasNotification::class.java -> {
            val data = this unpack_ AppHasNotification::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppHasNotification(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ AppHasTask::class.java -> {
            val data = this unpack_ AppHasTask::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppHasTask(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ AppIsRunning::class.java -> {
            val data = this unpack_ AppIsRunning::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppIsRunning(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ AppIsNotRunning::class.java -> {
            val data = this unpack_ AppIsNotRunning::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppIsNotRunning(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ ServiceIsRunning::class.java -> {
            val data = this unpack_ ServiceIsRunning::class.java
            Condition.ServiceIsRunning(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                op = data.op.toOp(null),
                services = data.servicesList
            )
        }

        this is_ CurrentActivity::class.java -> {
            val data = this unpack_ CurrentActivity::class.java
            Condition.CurrentActivity(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                op = data.op.toOp(null),
                activities = data.activitiesList
            )
        }

        this is_ AppHasAudioFocus::class.java -> {
            val data = this unpack_ AppHasAudioFocus::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppHasAudioFocus(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ AppHasWindowFocus::class.java -> {
            val data = this unpack_ AppHasWindowFocus::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppHasWindowFocus(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                apps = models.first,
                pkgSets = models.second,
                op = data.op.toOp(null)
            )
        }

        this is_ MatchMVEL::class.java -> {
            val data = this unpack_ MatchMVEL::class.java
            Condition.MVEL(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                expression = data.expression
            )
        }

        this is_ MatchJS::class.java -> {
            val data = this unpack_ MatchJS::class.java
            Condition.JS(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                expression = data.expression
            )
        }

        this is_ True::class.java -> {
            val data = this unpack_ True::class.java
            Condition.TRUE(
                id = data.id,
                note = data.note,
                isDisabled = data.isDisabled,
                isInvert = data.isInvert
            )
        }

        this is_ ScreenIsOn::class.java -> {
            val data = this unpack_ ScreenIsOn::class.java
            Condition.ScreenIsOn(
                id = data.id,
                note = data.note,
                isDisabled = data.isDisabled,
                isInvert = data.isInvert
            )
        }

        this is_ VPNIsConnected::class.java -> {
            val data = this unpack_ VPNIsConnected::class.java
            Condition.VPNIsConnected(
                id = data.id,
                note = data.note,
                isDisabled = data.isDisabled,
                isInvert = data.isInvert
            )
        }

        this is_ EvaluateGlobalVar::class.java -> {
            val data = this unpack_ EvaluateGlobalVar::class.java
            Condition.EvaluateGlobalVar(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                varName = data.varName,
                op = data.op,
                payload = data.payload
            )
        }

        this is_ EvaluateContextVar::class.java -> {
            val data = this unpack_ EvaluateContextVar::class.java
            Condition.EvaluateContextVar(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                varName = data.varName,
                op = data.op,
                payload = data.payload
            )
        }

        this is_ EvaluateLocalVar::class.java -> {
            val data = this unpack_ EvaluateLocalVar::class.java
            Condition.EvaluateLocalVar(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                varName = data.varName,
                op = data.op,
                payload = data.payload
            )
        }

        this is_ BatteryPercent::class.java -> {
            val data = this unpack_ BatteryPercent::class.java
            Condition.BatteryPercent(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                op = data.op,
                value = data.value
            )
        }

        this is_ ConnectedWifiSignal::class.java -> {
            val data = this unpack_ ConnectedWifiSignal::class.java
            Condition.ConnectedWifiSignal(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                op = data.op,
                level = data.level
            )
        }

        this is_ ChargeState::class.java -> {
            val data = this unpack_ ChargeState::class.java
            Condition.ChargeState(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                requireIsCharge = data.requireIsCharge
            )
        }

        this is_ PlugState::class.java -> {
            val data = this unpack_ PlugState::class.java
            Condition.PlugState(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                type = data.type
            )
        }

        this is_ TimeInRange::class.java -> {
            val data = this unpack_ TimeInRange::class.java
            Condition.TimeInRange(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                range = data.range
            )
        }

        this is_ RequireWifiConnected::class.java -> {
            val data = this unpack_ RequireWifiConnected::class.java
            Condition.RequireWifiConnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                requiredSSID = data.requiredSSID
            )
        }

        this is_ RequireWifiDisconnected::class.java -> {
            val data = this unpack_ RequireWifiDisconnected::class.java
            Condition.RequireWifiDisconnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ RequireBTConnected::class.java -> {
            val data = this unpack_ RequireBTConnected::class.java
            Condition.RequireBTConnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                requiredDevice = data.device
            )
        }

        this is_ RequireBTDisconnected::class.java -> {
            val data = this unpack_ RequireBTDisconnected::class.java
            Condition.RequireBTDisconnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ RequireBTDeviceFound::class.java -> {
            val data = this unpack_ RequireBTDeviceFound::class.java
            Condition.RequireBTDeviceFound(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                device = data.device,
                address = data.address,
                timeout = data.timeout
            )
        }

        this is_ RequireMobileDataEnabled::class.java -> {
            val data = this unpack_ RequireMobileDataEnabled::class.java
            Condition.RequireMobileDataEnabled(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                slot = data.slotId
            )
        }

        this is_ KeyguardIsLocked::class.java -> {
            val data = this unpack_ KeyguardIsLocked::class.java
            Condition.KeyguardIsLocked(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ ScreenOrientationIsPort::class.java -> {
            val data = this unpack_ ScreenOrientationIsPort::class.java
            Condition.ScreenOrientationIsPort(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ IsInCall::class.java -> {
            val data = this unpack_ IsInCall::class.java
            Condition.IsInCall(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ IsRinging::class.java -> {
            val data = this unpack_ IsRinging::class.java
            Condition.IsRinging(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey
            )
        }

        this is_ HasNodeOnScreen::class.java -> {
            val data = this unpack_ HasNodeOnScreen::class.java
            Condition.HasNodeOnScreen(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                packageName = data.packageName,
                componentName = data.componentName,
                detectTimeout = data.detectTimeout,
                matchers = data.matchersList.mapNotNull {
                    if (it is_ NodeMatcherText::class.java) {
                        val m = it unpack_ NodeMatcherText::class.java
                        NodeMatcher.Text(m.text, m.isRegex, m.regexOptions)
                    } else if (it is_ NodeMatcherViewId::class.java) {
                        val m = it unpack_ NodeMatcherViewId::class.java
                        NodeMatcher.ViewId(m.id)
                    } else null
                }
            )
        }

        this is_ IsRuleEnabled::class.java -> {
            val data = this unpack_ IsRuleEnabled::class.java
            Condition.IsRuleEnabled(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                ruleId = data.ruleId,
                isEnabled = data.isEnabled,
                ruleLabel = shortXManager.getRuleById(data.ruleId)?.title ?: "N/A"
            )
        }

        this is_ IsHeadsetPlug::class.java -> {
            val data = this unpack_ IsHeadsetPlug::class.java
            Condition.IsHeadsetPlug(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                isPlug = data.isPlug
            )
        }

        this is_ RequireScreenRotate::class.java -> {
            val data = this unpack_ RequireScreenRotate::class.java
            Condition.RequireScreenRotate(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                degree = data.degree
            )
        }

        this is_ RequireWindowRotation::class.java -> {
            val data = this unpack_ RequireWindowRotation::class.java
            Condition.RequireWindowRotation(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                degree = data.degree
            )
        }

        this is_ RequireDelay::class.java -> {
            val data = this unpack_ RequireDelay::class.java
            Condition.RequireDelay(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                timeString = data.timeString,
                isAlarm = data.useAlarm
            )
        }

        this is_ RequireSensorOff::class.java -> {
            val data = this unpack_ RequireSensorOff::class.java
            Condition.RequireSensorOff(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                isRequireOn = data.isRequireOn
            )
        }

        this is_ RequireTileState::class.java -> {
            val data = this unpack_ RequireTileState::class.java
            Condition.RequireTileState(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                state = data.state,
                spec = data.tileSpec
            )
        }

        this is_ RequireFactTag::class.java -> {
            val data = this unpack_ RequireFactTag::class.java
            Condition.RequireFactTag(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                tag = data.tag,
            )
        }

        this is_ RequireAPMMode::class.java -> {
            val data = this unpack_ RequireAPMMode::class.java
            Condition.RequireAPMMode(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                isEnableAPM = data.isAPMEnable
            )
        }

        this is_ RequireIMEVisibility::class.java -> {
            val data = this unpack_ RequireIMEVisibility::class.java
            Condition.RequireIMEVisibility(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                isShown = data.isShown
            )
        }

        this is_ TheXXTimeToday::class.java -> {
            val data = this unpack_ TheXXTimeToday::class.java
            Condition.TheXXTimeToday(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                isDisabled = data.isDisabled,
                customContextDataKey = data.customContextDataKey,
                what = data.what,
                scope = data.scope,
                time = data.time,
                op = data.op
            )
        }

        else -> null
    }
}