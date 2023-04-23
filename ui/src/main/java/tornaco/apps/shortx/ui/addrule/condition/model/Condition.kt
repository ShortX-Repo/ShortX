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
    @Parcelize
    data class CurrentForegroundApp(val apps: List<App>, val pkgSets: List<PkgSet>) : Condition

    @Parcelize
    data class MVEL(val expression: String) : Condition

    @Parcelize
    object TRUE : Condition

    @Parcelize
    data class EvaluateGlobalVar(
        val varId: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        val note: String = "",
        val id: String = defaultNewConditionId()
    ) : Condition
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkgList.newBuilder()
                .addAllPkgs(
                    apps.map { app ->
                        AppPkg.newBuilder()
                            .setPkgName(app.pkg.pkgName)
                            .setUserId(app.pkg.userId)
                            .build()
                    }
                )
                .addAllPkgSets(pkgSets.map {
                    it.id
                })
                .build()
        }
        is Condition.MVEL -> {
            MatchMVEL.newBuilder().setExpression(expression).build()
        }
        Condition.TRUE -> {
            True.newBuilder().build()
        }
        is Condition.EvaluateGlobalVar -> {
            EvaluateGlobalVar.newBuilder()
                .setId(id)
                .setNote(note)
                .setVarId(varId)
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
                apps = pkgs.map { pkg ->
                    val appLabel = shortXManager.getAppLabel(pkg)
                    App(
                        pkg = pkg, label = appLabel, category = AppCategory.User
                    )
                },
                pkgSets = data.pkgSetsList.mapNotNull {
                    shortXManager.getPkgSetById(it)
                }
            )
        }
        this is_ MatchMVEL::class.java -> {
            val data = this unpack_ MatchMVEL::class.java
            Condition.MVEL(data.expression)
        }
        this is_ True::class.java -> {
            Condition.TRUE
        }
        this is_ EvaluateGlobalVar::class.java -> {
            val data = this unpack_ EvaluateGlobalVar::class.java
            Condition.EvaluateGlobalVar(
                id = data.id,
                note = data.note,
                varId = data.varId,
                op = data.op,
                payload = data.payload
            )
        }
        else -> null
    }
}