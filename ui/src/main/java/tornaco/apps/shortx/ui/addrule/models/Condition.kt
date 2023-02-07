package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.AppCategory
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed interface Condition {
    data class CurrentForegroundApp(val apps: List<App>) : Condition
    data class MVEL(val expression: String) : Condition
    object TRUE : Condition
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkgList.newBuilder().addAllPkgs(
                apps.map { app ->
                    AppPkg.newBuilder().setPkgName(app.pkg.pkgName).setUserId(app.pkg.userId)
                        .build()
                }
            ).build()
        }
        is Condition.MVEL -> {
            MatchMVEL.newBuilder().setExpression(expression).build()
        }
        Condition.TRUE -> {
            True.newBuilder().build()
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
        else -> null
    }
}