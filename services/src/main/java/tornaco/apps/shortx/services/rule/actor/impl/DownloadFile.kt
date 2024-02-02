package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.DownloadHelper


object DownloadFile : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.DownloadFile::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val t = action unpack_ tornaco.apps.shortx.core.proto.action.DownloadFile::class.java
        DownloadHelper.downloadFile(androidContext, t.subject)
    }
}