package tornaco.apps.shortx.core.notification

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import java.util.Date

fun handleButtonClick(postN: PostNotification, buttonId: String, shortX: ShortXManager) {
    val button = postN.buttonList.firstOrNull { it.id == buttonId }
    button?.let {
        val evaluateContext = EvaluateContext.newBuilder()
            .setSrcId("Notification button - ${it.id}")
            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
            .setSrcTitle(button.label)
            .build()
        val da = DirectAction.newBuilder()
            .setId("Notification-Button-${Date()}")
            .setLastUpdateTime(System.currentTimeMillis())
            .setTitle("Notification Button ${it.label}")
            .addAllActions(it.actionList)
            .build()
        shortX.executeDirectionAction(
            context = evaluateContext,
            da = da,
            contextAndVars = postN.contextAndVars
        )
    }
}

fun handleNotificationClick(post: PostNotification, shortX: ShortXManager) {
    val evaluateContext = EvaluateContext.newBuilder()
        .setSrcId("Notification - ${post.id}")
        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
        .setSrcTitle(post.title)
        .build()
    val da = DirectAction.newBuilder()
        .setId("Notification-${Date()}")
        .setLastUpdateTime(System.currentTimeMillis())
        .setTitle("Notification ${post.title}")
        .addAllActions(post.clickActionList)
        .build()
    shortX.executeDirectionAction(
        context = evaluateContext,
        da = da,
        contextAndVars = post.contextAndVars
    )
}