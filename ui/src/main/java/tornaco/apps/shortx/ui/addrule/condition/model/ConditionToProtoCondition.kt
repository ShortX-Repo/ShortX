package tornaco.apps.shortx.ui.addrule.condition.model

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.BatteryPercent
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoMessage


fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkgList.newBuilder()
                .setId(id)
                .setNote(note)
                .setIsInvert(isInvert)
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.label
                })
                .build()
        }

        is Condition.MVEL -> {
            MatchMVEL.newBuilder()
                .setId(id)
                .setNote(note)
                .setIsInvert(isInvert).setExpression(expression).build()
        }

        is Condition.TRUE -> {
            True.newBuilder()
                .setId(id)
                .setNote(note)
                .setIsInvert(isInvert)
                .build()
        }

        is Condition.EvaluateGlobalVar -> {
            EvaluateGlobalVar.newBuilder()
                .setId(id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setVarName(varName)
                .setOp(op)
                .setPayload(payload)
                .build()
        }

        is Condition.BatteryPercent -> {
            BatteryPercent.newBuilder()
                .setId(id)
                .setNote(note)
                .setIsInvert(isInvert)
                .setValue(value)
                .setOp(op)
                .build()
        }
    }
}