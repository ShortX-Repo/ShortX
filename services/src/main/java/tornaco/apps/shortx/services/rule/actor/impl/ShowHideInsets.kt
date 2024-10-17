package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.view.WindowInsets
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.WindowInsetType
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ShowHideInsets : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowHideInsets::class.java
    }

    @SuppressLint("MissingPermission", "NewApi")
    override suspend fun handle(
        params: HandleParams
    ) {
        require(OsUtils.isROrAbove()) {
            "This Action only support Android 11+"
        }
        with(params) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowHideInsets::class.java
            var types = 0
            with(d.typeList) {
                logger.d("typeList: $this")
                if (contains(WindowInsetType.WindowInsetType_StatusBar)) {
                    types = WindowInsets.Type.statusBars()
                }
                if (contains(WindowInsetType.WindowInsetType_NavBar)) {
                    types = types or WindowInsets.Type.navigationBars()
                }
                if (contains(WindowInsetType.WindowInsetType_Ime)) {
                    types = types or WindowInsets.Type.ime()
                }
                if (contains(WindowInsetType.WindowInsetType_displayCutout)) {
                    types = types or WindowInsets.Type.displayCutout()
                }
            }
            shortXContext.statusbar.hideOrShowInsets(types, d.isHide)
        }
    }
}