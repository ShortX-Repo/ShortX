package tornaco.apps.shortx.services.rule.actor.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FindAndClickMatchedView : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindAndClickMatchedView when device is not interactive"
        }
        val windowId = contextData.get(ContextDataMapping.HasFoundNodeOnScreen.windowId.name) as Int
        val sourceNodeId =
            contextData.get(ContextDataMapping.HasFoundNodeOnScreen.sourceNodeId.name) as Long
        shortXContext.wm.withUiAutomation("FindAndClickMatchedView") {
            it.performAccessibilityAction(
                sourceNodeId,
                windowId,
                AccessibilityNodeInfo.ACTION_CLICK
            )
        }
        Actor.Res.EMPTY
    }
}