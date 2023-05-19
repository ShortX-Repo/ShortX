package tornaco.apps.shortx.ui.addrule.condition.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.*
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import java.util.*

fun defaultNewConditionId() = "C-" + UUID.randomUUID().toString()

sealed interface Condition : Parcelable {
    val id: String
    val note: String

    @Parcelize
    data class CurrentForegroundApp(
        val apps: List<App>, val pkgSets: List<PkgSet>,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class MVEL(
        val expression: String,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class TRUE(
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class EvaluateGlobalVar(
        val varName: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkgList.newBuilder()
                .setId(id)
                .setNote(note)
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
                .setNote(note).setExpression(expression).build()
        }
        is Condition.TRUE -> {
            True.newBuilder()
                .setId(id)
                .setNote(note)
                .build()
        }
        is Condition.EvaluateGlobalVar -> {
            EvaluateGlobalVar.newBuilder()
                .setId(id)
                .setNote(note)
                .setVarName(varName)
                .setOp(op)
                .setPayload(payload)
                .build()
        }
    }
}

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
                apps = pkgs.map { pkg ->
                    val appLabel = shortXManager.getAppLabel(pkg)
                    App(
                        pkg = pkg, label = appLabel, category = AppCategory.User
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
                expression = data.expression
            )
        }
        this is_ True::class.java -> {
            val data = this unpack_ True::class.java
            Condition.TRUE(id = data.id, note = data.note)
        }
        this is_ EvaluateGlobalVar::class.java -> {
            val data = this unpack_ EvaluateGlobalVar::class.java
            Condition.EvaluateGlobalVar(
                id = data.id,
                note = data.note,
                varName = data.varName,
                op = data.op,
                payload = data.payload
            )
        }
        else -> null
    }
}