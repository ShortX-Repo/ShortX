package tornaco.apps.shortx.services.rule.cond.impl

import android.annotation.SuppressLint
import android.service.quicksettings.Tile
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.QSTileState
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireTileState : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireTileState::class.java

    @SuppressLint("NewApi", "MissingPermission")
    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireTileState::class.java)
        val stateInt = param.shortXContext.ui.getTileState(data.tileSpec)
        val tileState = stateInt.intToQSTileState()
        logger.d("spec: ${data.tileSpec}, state: $tileState")

        val res = tileState == data.state
        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }

    private fun Int.intToQSTileState(): QSTileState {
        return if (this == Tile.STATE_ACTIVE) QSTileState.QSTileState_Active
        else if (this == Tile.STATE_INACTIVE) QSTileState.QSTileState_InActive
        else if (this == Tile.STATE_UNAVAILABLE) QSTileState.QSTileState_Unavailable
        else QSTileState.UNRECOGNIZED
    }
}
