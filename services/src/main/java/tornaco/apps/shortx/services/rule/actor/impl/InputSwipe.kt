package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object InputSwipe : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InputSwipe::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java

        require(androidContext.powerManager.isInteractive) {
            "Can not swipe when device is not interactive"
        }

        val cv = RuleContextAndVars(params)

        val startX =
            data.startXS.ifBlank { data.startX.toString() }.compileContextAndVars(cv).trim().toInt()
        val startY =
            data.startYS.ifBlank { data.startY.toString() }.compileContextAndVars(cv).trim().toInt()
        val endX =
            data.endXS.ifBlank { data.endX.toString() }.compileContextAndVars(cv).trim().toInt()
        val endY =
            data.endYS.ifBlank { data.endY.toString() }.compileContextAndVars(cv).trim().toInt()
        val swipeTime =
            data.swipeTimeS.ifBlank { data.swipeTime.toString() }.compileContextAndVars(cv).trim()
                .toLongOrNull() ?: 500


        shortXContext.wm.withUiAutomation("InputSwipe") {
            it.swipe(startX, startY, endX, endY, swipeTime)
        }
    }
}