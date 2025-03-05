package tornaco.apps.shortx.services.media

import android.net.Uri
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.fact.MediaStoreDelete
import tornaco.apps.shortx.core.proto.fact.MediaStoreInsert
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.watchdog.postSafety


class MediaService(shortXContext: ShortXContext) : SystemService(shortXContext, "MediaService") {
    override fun systemReady() {
        super.systemReady()
    }

    fun onMediaStoreInsert(uri: Uri, path: String) {
        kotlin.runCatching {
            logger.logOnDebug("onMediaStoreInsert: $uri -- $path")
            handler.postSafety("onMediaStoreInsert") {
                shortXContext.rule.publishFactChecked(
                    MediaStoreInsert.getDefaultInstance()
                ) {
                    FactPublishData(
                        reason = "onMediaStoreInsert",
                        fact = MediaStoreInsert.newBuilder()
                            .setPath(path)
                            .setUriString(uri.toString())
                            .setFilterPath(path)
                            .build()
                    ) {
                        mapOf(
                            ContextDataMapping.MediaStoreInsert.mediaUri.name to uri.toString(),
                            ContextDataMapping.MediaStoreInsert.mediaFilePath.name to path
                        )
                    }
                }
            }

        }.onFailure {
            logger.e(it)
        }
    }

    fun onMediaStoreDelete(uri: Uri, path: String?) {
        kotlin.runCatching {
            logger.logOnDebug("onMediaStoreDelete: $uri -- $path")

            handler.postSafety("onMediaStoreDelete") {
                shortXContext.rule.publishFactChecked(
                    MediaStoreDelete.getDefaultInstance()
                ) {
                    FactPublishData(
                        reason = "onMediaStoreDelete",
                        fact = MediaStoreDelete.newBuilder()
                            .setPath(path)
                            .setUriString(uri.toString())
                            .setFilterPath(path)
                            .build()
                    ) {
                        mapOf(
                            ContextDataMapping.MediaStoreDelete.mediaUri.name to uri.toString(),
                            ContextDataMapping.MediaStoreDelete.mediaFilePath.name to path.orEmpty()
                        )
                    }
                }
            }
        }.onFailure {
            logger.e(it)
        }
    }
}
