package tornaco.apps.shortx.services.media

import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.media.FileRow
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

    fun onMediaStoreInsert(row: FileRow) {
        with(row) {
            logger.w("onMediaStoreInsert: ${this.id}")
            handler.postSafety("onMediaStoreInsert") {
                logger.w("About to publishFactChecked: MediaStoreInsert.")
                shortXContext.rule.publishFactChecked(
                    MediaStoreInsert.getDefaultInstance()
                ) {
                    FactPublishData(
                        reason = "onMediaStoreInsert",
                        fact = MediaStoreInsert.newBuilder()
                            .setPath(path)
                            .setUriString(mediaUri.orEmpty())
                            .setFilterPath(path)
                            .build()
                    ) {
                        mapOf(
                            ContextDataMapping.MediaStoreInsert.mediaUri.name to mediaUri.orEmpty(),
                            ContextDataMapping.MediaStoreInsert.mediaFilePath.name to path
                        )
                    }
                }
            }
        }
    }

    fun onMediaStoreDelete(row: FileRow) {
        with(row) {
            kotlin.runCatching {
                logger.logOnDebug("onMediaStoreDelete: $this")

                handler.postSafety("onMediaStoreDelete") {
                    shortXContext.rule.publishFactChecked(
                        MediaStoreDelete.getDefaultInstance()
                    ) {
                        FactPublishData(
                            reason = "onMediaStoreDelete",
                            fact = MediaStoreDelete.newBuilder()
                                .setPath(path)
                                .setUriString(mediaUri.orEmpty())
                                .setFilterPath(path)
                                .build()
                        ) {
                            mapOf(
                                ContextDataMapping.MediaStoreDelete.mediaUri.name to mediaUri.orEmpty(),
                                ContextDataMapping.MediaStoreDelete.mediaFilePath.name to path
                            )
                        }
                    }
                }
            }.onFailure {
                logger.e(it)
            }
        }
    }
}
