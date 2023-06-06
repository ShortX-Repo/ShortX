package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_


fun ProtoAny.toCondition(shortXManager: ShortXManager): Condition? {
    return when {
        this is_ CurrentPkgList::class.java -> {
            val data = this unpack_ CurrentPkgList::class.java
            val pkgs = data.pkgsList.map { pkg ->
                Pkg(pkg.pkgName, userId = pkg.userId)
            }
            Condition.CurrentForegroundApp(
                id = data.id,
                note = data.note,
                isInvert = data.isInvert,
                apps = pkgs.map { pkg ->
                    val appLabel = shortXManager.getAppLabel(pkg)
                    App(
                        pkg = pkg,
                        label = appLabel,
                        category = tornaco.apps.shortx.core.proto.common.AppCategory.User
                    )
                },
                pkgSets = data.pkgSetsList.mapNotNull {
                    shortXManager.getPkgSetByLabel(it)
                }
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