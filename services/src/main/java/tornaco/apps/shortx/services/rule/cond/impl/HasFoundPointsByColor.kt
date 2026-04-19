package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.FindPointsByColor
import tornaco.apps.shortx.services.rule.cond.Cond

object HasFoundPointsByColor : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.HasFoundPointsByColor::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.HasFoundPointsByColor::class.java)
        logger.d("evaluate")

        val points = with(param) {
            FindPointsByColor.handleAction(
                "HasFoundPointsByColor",
                data.config,
                context,
                evaluateContext,
                shortXContext,
            )
        }
        logger.d("evaluate complete: ${points.size}")

        val res = points.isNotEmpty()
        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }

}
