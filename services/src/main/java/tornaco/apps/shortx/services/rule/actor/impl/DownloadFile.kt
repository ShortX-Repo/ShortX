package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.context.ContextDataMapping
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
        val id = DownloadHelper.downloadFile(androidContext, t.subject)

        while (true) {
            delay(1000)
            val status = DownloadHelper.getDownloadStatus(androidContext, id)
            logger.d("status: $status")
            when (status) {
                is DownloadHelper.DownloadStatus.Success -> {
                    contextData.add(
                        ContextDataMapping.DownloadFile.isDownloadSuccess.name,
                        true.toString()
                    )
                    contextData.add(
                        ContextDataMapping.DownloadFile.downloadFileUri.name,
                        status.localUri
                    )
                    contextData.add(
                        ContextDataMapping.DownloadFile.downloadFilePath.name,
                        status.path
                    )
                    break
                }

                is DownloadHelper.DownloadStatus.Fail -> {
                    contextData.add(
                        ContextDataMapping.DownloadFile.isDownloadSuccess.name,
                        false.toString()
                    )
                    break
                }

                else -> {
                    // Noop
                }
            }
        }
        logger.d("Download complete.")
    }
}