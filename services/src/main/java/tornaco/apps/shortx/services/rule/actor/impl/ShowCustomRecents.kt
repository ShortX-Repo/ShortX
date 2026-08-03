package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowCustomRecents : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowCustomRecents::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.ShowCustomRecents::class.java
        shortXContext.wm.showRecentTasks(
            layoutStyle = data.layoutStyle,
            gridColumns = data.gridColumns.takeIf { it > 0 } ?: 3,
            backgroundColor = data.backgroundColor.takeIf { it != 0 } ?: 0x99000000.toInt(),
            backgroundBlurRadius = data.backgroundBlurRadius,
            backgroundImagePath = data.backgroundImagePath,
            miniMode = data.miniMode,
            showAppName = data.showAppName,
            arcArrangement = data.arcArrangement,
            centerActiveApp = data.centerActiveApp,
        )
        Actor.Res.EMPTY
    }
}
