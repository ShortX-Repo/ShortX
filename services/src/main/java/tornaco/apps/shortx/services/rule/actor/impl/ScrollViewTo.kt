package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.ServiceManager
import android.view.IWindowManager
import android.view.Surface
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ScrollViewTo : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java
        val location = d.location
        logger.d("location = $location")
    }
}