package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireScreenRotate : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireScreenRotate::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireScreenRotate::class.java)
        val currentDegree = param.shortXContext.wm.screenRotateDegree
        logger.d("degree: $currentDegree")

        val res =
            currentDegree == data.degree || data.degree == ScreenRotateDegree.ScreenRotateDegree_Any

        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }
}