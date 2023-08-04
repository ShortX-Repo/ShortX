package tornaco.apps.shortx.services.rule.cond.impl

import android.content.res.Configuration
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object ScreenOrientationIsPort : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.ScreenOrientationIsPort::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result  {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.ScreenOrientationIsPort::class.java
            val res =
                context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}