package tornaco.apps.shortx.services.util

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

object MediaScannerUtil {
    suspend fun scanFile(context: Context, filePath: String): Pair<String, Uri> {
        return withTimeout(3000) {
            suspendCancellableCoroutine<Pair<String, Uri>> { continuation ->
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(filePath),
                    null
                ) { path: String, uri: Uri ->
                    continuation.resume(path to uri) { _, _, _ -> }
                }
                continuation.invokeOnCancellation {}
            }
        }
    }
}