package tornaco.apps.shortx.services.media

import android.net.Uri
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.MediaStoreInsert
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.postSafety


class MediaService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    override fun systemReady() {
        super.systemReady()
    }

    fun onMediaStoreInsert(uri: Uri, path: String) {
        kotlin.runCatching {
            logger.p("onMediaStoreInsert: $uri -- $path")
            handler.postSafety {
                shortXContext.rule.publishFact(
                    "onMediaStoreInsert",
                    MediaStoreInsert.newBuilder()
                        .setPath(path)
                        .setUriString(uri.toString())
                        .setFilterPath(path)
                        .build(),
                    mapOf(
                        ContextDataMapping.MediaStoreInsert.mediaUri.name to uri.toString(),
                        ContextDataMapping.MediaStoreInsert.mediaFilePath.name to path
                    )
                )
            }

        }.onFailure {
            logger.e(it)
        }
    }

    fun onMediaStoreDelete(uri: Uri, path: String) {
        kotlin.runCatching {
            logger.p("onMediaStoreDelete: $uri -- $path")
        }.onFailure {
            logger.e(it)
        }
    }
}
