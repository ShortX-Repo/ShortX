package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireNotificationPanelExpanded : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireNotificationPanelExpanded::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireNotificationPanelExpanded::class.java
            val res = false
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