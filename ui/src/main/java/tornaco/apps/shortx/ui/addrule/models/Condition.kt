package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.AppCategory
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed interface Condition {
    data class CurrentForegroundApp(val app: App) : Condition
    data class MVEL(val expression: String) : Condition
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkg.newBuilder().setPkgName(this.app.pkg.pkgName).build()
        }
        is Condition.MVEL -> {
            MatchMVEL.newBuilder().setExpression(expression).build()
        }
    }
}

fun ProtoAny.toCondition(shortXManager: ShortXManager): Condition? {
    return when {
        this is_ CurrentPkg::class.java -> {
            val data = this unpack_ CurrentPkg::class.java
            val pkg = Pkg(data.pkgName, userId = 0)
            val appLabel = shortXManager.getAppLabel(pkg)
            Condition.CurrentForegroundApp(
                app = App(
                    pkg = pkg, label = appLabel, category = AppCategory.User
                )
            )
        }
        this is_ MatchMVEL::class.java -> {
            val data = this unpack_ MatchMVEL::class.java
            Condition.MVEL(data.expression)
        }
        else -> null
    }
}