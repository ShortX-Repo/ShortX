package tornaco.apps.shortx.ui.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.Compression
import id.zelory.compressor.constraint.default
import java.io.File

object Compressor {
    suspend fun compressAndToBase64(
        context: Context, uri: Uri,
        compressionPatch: Compression.() -> Unit = { default() }
    ): String {
        val base64 = context.contentResolver.openInputStream(uri)?.use {
            val actualImageFile = File.createTempFile("image", ".png")
            it.copyTo(actualImageFile.outputStream())
            val compressedImageFile =
                Compressor.compress(
                    context = context,
                    imageFile = actualImageFile,
                    compressionPatch = compressionPatch
                )
            val byteArray = compressedImageFile.readBytes()
            Base64.encodeToString(byteArray, 0)
        }
        return requireNotNull(base64) {
            "Fail to decode uri: $uri"
        }
    }
}