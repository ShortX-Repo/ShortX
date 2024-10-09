package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ClickTile : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ClickTile::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ClickTile::class.java
        val cv = RuleContextAndVars(params)
        if (d.isLongClick) {
            shortXContext.ui.longClickTile(d.tile.tileSpec.compileContextAndVars(cv))
        } else {
            shortXContext.ui.clickTile(d.tile.tileSpec.compileContextAndVars(cv))
        }
    }
}