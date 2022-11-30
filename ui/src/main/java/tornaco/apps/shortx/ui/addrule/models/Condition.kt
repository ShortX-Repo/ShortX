package tornaco.apps.shortx.ui.addrule.models

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed class Condition(val label: (Context) -> String, val icon: ImageVector) {
    data class CurrentPkg(val pkg: Pkg) : Condition({ "$pkg" }, Icons.Filled.Key)
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentPkg -> {
            CurrentPkg.newBuilder().setPkgName(this.pkg.pkgName).build()
        }
    }
}

fun ProtoAny.toCondition(): Condition? {
    return if (this is_ CurrentPkg::class.java) {
        val data = this unpack_ CurrentPkg::class.java
        Condition.CurrentPkg(pkg = Pkg(data.pkgName, userId = 0))
    } else null
}