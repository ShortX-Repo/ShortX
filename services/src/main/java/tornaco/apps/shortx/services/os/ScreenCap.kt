@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.services.os

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.IMediaProjection
import android.media.projection.IMediaProjectionManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjection.Callback
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.ServiceManager
import android.view.Surface
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.data.Dirs
import java.io.File
import java.io.FileOutputStream


class ScreenCap(private val context: Context) {
    private val handler by lazy { newHandlerOfNewThread("ShortXScreenCap") }
    private val logger = Logger("ShortXScreenCap")

    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var mediaProjection: MediaProjection? = null

    suspend fun capAndScanQR(): String {
        val bitmap = cap()
        onDebugBuild { saveBitmapToFile(bitmap) }
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

    suspend fun cap(): Bitmap {
        logger.d("cap")
        //Setup media projection
        val b = ServiceManager.getService(Context.MEDIA_PROJECTION_SERVICE)
        val mediaService = IMediaProjectionManager.Stub.asInterface(b)
        val proj = mediaService.createProjection(
            Binder.getCallingUid(),
            context.packageName,
            MediaProjectionManager.TYPE_SCREEN_CAPTURE,
            false
        )
        val projection = IMediaProjection.Stub.asInterface(proj.asBinder())
        mediaProjection = MediaProjection(context, projection)
        val mp = mediaProjection
        requireNotNull(mp) { "WTF mediaProjection is null" }

        mp.registerCallback(callback, handler)

        return runCatching {
            val bitmap = cap(mp)
            logger.d("cap complete.")
            mp.stop()
            bitmap
        }.getOrElse {
            logger.e(it, "cap")
            mp.stop()
            throw Error(it)
        }


    }

    private suspend fun cap(
        mediaProjection: MediaProjection,
    ): Bitmap {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels
        logger.d("screenWidth: $screenWidth")
        logger.d("screenHeight: $screenHeight")

        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        val surface: Surface = requireNotNull(imageReader) { "WTF imageReader is null" }.surface

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ShortX-ScreenCap",
            screenWidth,
            screenHeight,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            handler
        )
        logger.d("virtualDisplay: $virtualDisplay")

        return requireNotNull(imageReader) { "WTF imageReader is null" }.awaitImageAvailable(
            screenWidth,
            screenHeight
        )
    }

    private suspend fun ImageReader.awaitImageAvailable(
        screenWidth: Int,
        screenHeight: Int
    ): Bitmap =
        suspendCancellableCoroutine { cont ->
            var hasResumed = false
            setOnImageAvailableListener({ imageReader ->
                logger.d("ImageAvailable.")

                if (hasResumed) {
                    logger.w("Already resumed.")
                    return@setOnImageAvailableListener
                }

                val image = imageReader.acquireLatestImage()
                    ?: throw Error("get screen image result failed")

                val planes = image.planes
                val buffer = planes[0].buffer

                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding: Int = rowStride - pixelStride * screenWidth
                var bitmap =
                    Bitmap.createBitmap(
                        screenWidth + rowPadding / pixelStride,
                        screenHeight,
                        Bitmap.Config.ARGB_8888
                    )
                bitmap.copyPixelsFromBuffer(buffer)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
                image.close()

                if (!bitmap.isEmptyBitmap()) {
                    logger.d("image not empty")
                    setOnImageAvailableListener(null, null)
                    hasResumed = true
                    cont.resume(bitmap) {}
                } else {
                    logger.w("image empty")
                }
            }, handler)

            cont.invokeOnCancellation { setOnImageAvailableListener(null, null) }
        }

    fun Bitmap.isEmptyBitmap(): Boolean {
        val emptyBitmap = Bitmap.createBitmap(width, height, config)
        return this.sameAs(emptyBitmap)
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


    private val callback = object : Callback() {
        override fun onStop() {
            logger.w("stopping projection.")
            handler.post {
                virtualDisplay?.release()
                imageReader?.setOnImageAvailableListener(null, null)
                mediaProjection?.unregisterCallback(this)
            }
        }
    }
}