package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.AppIsNotRunning
import tornaco.apps.shortx.core.proto.condition.AppIsRunning
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.ChargeState
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.PlugState
import tornaco.apps.shortx.core.proto.condition.RequireBTConnected
import tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.condition.RequireWifiConnected
import tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected
import tornaco.apps.shortx.core.proto.condition.ScreenIsOn
import tornaco.apps.shortx.core.proto.condition.ServiceIsRunning
import tornaco.apps.shortx.core.proto.condition.TimeInRange
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.condition.VPNIsConnected
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
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
                op = data.op.toOp(null),
                services = data.servicesList
            )
        }

        this is_ AppHasAudioFocus::class.java -> {
            val data = this unpack_ AppHasAudioFocus::class.java
            val models = (data.pkgsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Condition.AppHasAudioFocus(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
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
                expression = data.expression
            )
        }

        this is_ True::class.java -> {
            val data = this unpack_ True::class.java
            Condition.TRUE(id = data.id, note = data.note, isInvert = data.isInvert)
        }

        this is_ ScreenIsOn::class.java -> {
            val data = this unpack_ ScreenIsOn::class.java
            Condition.ScreenIsOn(id = data.id, note = data.note, isInvert = data.isInvert)
        }

        this is_ VPNIsConnected::class.java -> {
            val data = this unpack_ VPNIsConnected::class.java
            Condition.VPNIsConnected(id = data.id, note = data.note, isInvert = data.isInvert)
        }

        this is_ EvaluateGlobalVar::class.java -> {
            val data = this unpack_ EvaluateGlobalVar::class.java
            Condition.EvaluateGlobalVar(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
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
                op = data.op,
                value = data.value
            )
        }

        this is_ ChargeState::class.java -> {
            val data = this unpack_ ChargeState::class.java
            Condition.ChargeState(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                requireIsCharge = data.requireIsCharge
            )
        }

        this is_ PlugState::class.java -> {
            val data = this unpack_ PlugState::class.java
            Condition.PlugState(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                requireIsPlug = data.requireIsPlug
            )
        }

        this is_ TimeInRange::class.java -> {
            val data = this unpack_ TimeInRange::class.java
            Condition.TimeInRange(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                range = data.range
            )
        }

        this is_ RequireWifiConnected::class.java -> {
            val data = this unpack_ RequireWifiConnected::class.java
            Condition.RequireWifiConnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                requiredSSID = data.requiredSSID
            )
        }

        this is_ RequireWifiDisconnected::class.java -> {
            val data = this unpack_ RequireWifiDisconnected::class.java
            Condition.RequireWifiDisconnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
            )
        }

        this is_ RequireBTConnected::class.java -> {
            val data = this unpack_ RequireBTConnected::class.java
            Condition.RequireBTConnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                requiredDevice = data.device
            )
        }

        this is_ RequireBTDisconnected::class.java -> {
            val data = this unpack_ RequireBTDisconnected::class.java
            Condition.RequireBTDisconnected(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
            )
        }

        this is_ RequireMobileDataEnabled::class.java -> {
            val data = this unpack_ RequireMobileDataEnabled::class.java
            Condition.RequireMobileDataEnabled(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                slot = data.slotId
            )
        }

        else -> null
    }
}