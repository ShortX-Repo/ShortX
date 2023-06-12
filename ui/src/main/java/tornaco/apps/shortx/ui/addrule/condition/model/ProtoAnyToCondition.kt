package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.AppIsRunning
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
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
                pkgSets = models.second
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
                pkgSets = models.second
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

        else -> null
    }
}