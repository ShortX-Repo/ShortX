package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.content.ContextWrapper
import com.google.protobuf.Message
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File

object PluginAction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PluginAction::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        Actor.Res.EMPTY
    }
}

class PluginAndroidContextWrapper(context: Context, private val pluginId: String) :
    ContextWrapper(context) {
    override fun getCacheDir(): File {
        return File(Dirs.pluginCacheDir(), pluginId)
    }
}
