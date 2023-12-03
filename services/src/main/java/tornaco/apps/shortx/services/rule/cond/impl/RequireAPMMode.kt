package tornaco.apps.shortx.services.rule.cond.impl

import android.content.Context
import android.provider.Settings
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireAPMMode : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireAPMMode::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireAPMMode::class.java
            val res = data.isAPMEnable == context.isAPMModeEnabled()
            Cond.Result(
                if (data.isInvert) {
                    !res
                } else {
                    res
                }
            )
        }
    }
}

fun Context.isAPMModeEnabled(): Boolean {
    return Settings.Global.getInt(
        contentResolver,
        Settings.Global.AIRPLANE_MODE_ON,
        0
    ) == 1
}