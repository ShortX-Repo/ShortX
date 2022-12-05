package tornaco.apps.shortx.ui.addrule.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.AppCategory
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.base.I18N

sealed class Condition(
    val labelWithValue: (I18N) -> String,
    val labelForSelector: (I18N) -> String,
    val icon: ImageVector
) {
    data class CurrentForegroundApp(val app: App) : Condition({
        it["ui.dialog.title.current.app.with.app.label", mapOf("app" to app.label)]
    }, {
        it["ui.dialog.title.current.app"]
    }, Icons.Filled.Key)
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentForegroundApp -> {
            CurrentPkg.newBuilder().setPkgName(this.app.pkg.pkgName).build()
        }
    }
}

fun ProtoAny.toCondition(shortXManager: ShortXManager): Condition? {
    return if (this is_ CurrentPkg::class.java) {
        val data = this unpack_ CurrentPkg::class.java
        val pkg = Pkg(data.pkgName, userId = 0)
        val appLabel = shortXManager.getAppLabel(pkg)
        Condition.CurrentForegroundApp(
            app = App(
                pkg = pkg, label = appLabel, category = AppCategory.User
            )
        )
    } else null
}