package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.FindAndClickViewByText
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FindAndClickViewByText : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ FindAndClickViewByText::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindAndClickViewByText when device is not interactive"
        }
        shortXContext.wm.withUiAutomation("FindAndClickViewByText") {
            val cv = RuleContextAndVars(params)
            it.findAndClickViewByText(
                text = d.text.compileContextAndVars(cv),
                isRegex = d.isRegex,
                timeout = maxOf(d.timeout, 1000L)
            )
        }
    }
}