@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.services.os

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.data.Dirs
import java.io.File
import java.io.FileOutputStream


class ScreenScan(private val context: Context) {
    private val logger = Logger("ScreenScan")

    suspend fun capAndScanQR(): String {
        val bitmap = ScreenCap(context).cap()
        logger.logOnDebug {
            saveBitmapToFile(bitmap)
            "DEBUG - saveBitmapToFile"
        }
        logger.i("About to scan: $bitmap")
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val binaryBitmap =
            BinaryBitmap(HybridBinarizer(RGBLuminanceSource(width, height, pixels)))
        val reader: Reader = MultiFormatReader()
        val result = reader.decode(binaryBitmap)
        val qrCodeText: String = result.text
        return qrCodeText
    }

    fun saveBitmapToFile(imageData: Bitmap) {
        logger.d("saveBitmapToFile: $imageData")
        val fileName = "screenshot.png"
        val filePath = File(Dirs.baseServerCacheDir(), fileName).apply {
            parentFile.mkdirs()
        }
        FileOutputStream(filePath).use {
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}