package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.ContextMenuAction
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object PerformContextMenuAction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PerformContextMenuAction::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val ac =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.PerformContextMenuAction::class.java
        require(params.androidContext.powerManager.isInteractive) {
            "Can not PerformContextMenuAction when device is not interactive"
        }
        params.shortXContext.input.performContextMenuAction(ac.action.toAndroidContextMenuActionId())
    }

    private fun ContextMenuAction.toAndroidContextMenuActionId(): Int {
        return when (this) {
            ContextMenuAction.ContextMenuAction_SelectAll -> android.R.id.selectAll
            ContextMenuAction.ContextMenuAction_Cut -> android.R.id.cut
            ContextMenuAction.ContextMenuAction_Copy -> android.R.id.copy
            ContextMenuAction.ContextMenuAction_Paste -> android.R.id.paste
            ContextMenuAction.ContextMenuAction_Undo -> android.R.id.undo
            ContextMenuAction.ContextMenuAction_Redo -> android.R.id.redo
            ContextMenuAction.ContextMenuAction_StartSelectingText -> android.R.id.startSelectingText
            ContextMenuAction.ContextMenuAction_StopSelectingText -> android.R.id.stopSelectingText
            ContextMenuAction.ContextMenuAction_CopyUrl -> android.R.id.copyUrl
            ContextMenuAction.UNRECOGNIZED -> throw Error("UNRECOGNIZED")
        }
    }
}