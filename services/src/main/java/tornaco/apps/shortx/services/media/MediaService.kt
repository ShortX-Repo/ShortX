package tornaco.apps.shortx.services.media

import android.net.Uri
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService


class MediaService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    override fun systemReady() {
        super.systemReady()
    }

    fun onMediaStoreInsert(uri: Uri) {
        kotlin.runCatching {
            logger.p("onMediaStoreInsert: $uri")
        }.onFailure {
            logger.e(it)
        }
    }

    fun onMediaStoreDelete(uri: Uri) {
        kotlin.runCatching {
            logger.p("onMediaStoreDelete: $uri")
        }.onFailure {
            logger.e(it)
        }
    }
}
