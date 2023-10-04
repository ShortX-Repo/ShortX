package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetVolume : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetVolume::class.java
    }

    @SuppressLint("NewApi")
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetVolume::class.java

        val min = OsUtils.isPOrAbove().select(
            t = {
                params.androidContext.audioManager.getStreamMinVolume(d.type)
            },
            f = { 0 })
        val max = params.androidContext.audioManager.getStreamMaxVolume(d.type)

        logger.d("min: $min, max: $max")
        require(d.index in min..max) {
            "Invalid volume index: ${d.index}, should in range of: ($min-$max)"
        }

        androidContext.audioManager.setStreamVolume(
            d.type,
            d.index,
            0
        )
    }

}