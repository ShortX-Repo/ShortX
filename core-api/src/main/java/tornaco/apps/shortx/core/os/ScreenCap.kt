@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.core.os

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.IMediaProjection
import android.media.projection.IMediaProjectionManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjection.Callback
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.ServiceManager
import android.view.Display
import android.view.Surface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.defaultDisplay
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.core.util.screenSize
import util.Reflections
import java.util.concurrent.atomic.AtomicBoolean

class ScreenCap(private val context: Context) {
    private val handler by lazy { newHandlerOfNewThread("ShortX-ScreenCap") }
    private val logger = Logger("ScreenCap")

    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var mediaProjection: MediaProjection? = null

    private val isDestroyed = AtomicBoolean(false)

    fun version() = 1

    suspend fun waitUntilDestroyed() {
        logger.d("waitUntilDestroyed in")
        // Noop. stop callback will do this.
        while (!isDestroyed.get()) delay(30)
        logger.d("waitUntilDestroyed out")
    }

    @SuppressLint("MissingPermission")
    suspend fun cap(): Bitmap {
        logger.d("cap")
        //Setup media projection
        val b = ServiceManager.getService(Context.MEDIA_PROJECTION_SERVICE)
        val mediaService = IMediaProjectionManager.Stub.asInterface(b)
        val proj = runCatching {
            mediaService.createProjection(
                Binder.getCallingUid(),
                context.packageName,
                MediaProjectionManager.TYPE_SCREEN_CAPTURE,
                false
            )
        }.getOrElse {
            // Android 16
            //    IMediaProjection createProjection(int processUid, String packageName, int type,
            //            boolean permanentGrant, int displayId);
            Reflections.callMethod(
                mediaService, "createProjection",
                Binder.getCallingUid(),
                context.packageName,
                MediaProjectionManager.TYPE_SCREEN_CAPTURE,
                false,
                Display.DEFAULT_DISPLAY
            ) as IMediaProjection
        }

        logger.d("cap proj: $proj")
        val projection = IMediaProjection.Stub.asInterface(proj.asBinder())
        mediaProjection = MediaProjection(context, projection)
        logger.d("cap mediaProjection: $mediaProjection")
        val mp = mediaProjection
        requireNotNull(mp) { "WTF mediaProjection is null" }

        mp.registerCallback(callback, handler)
        logger.d("cap registerCallback.")
        return runCatching {
            val bitmap = cap(mp)
            logger.d("cap complete.")
            mp.stop()
            bitmap
        }.getOrElse {
            logger.e(it, "cap err")
            mp.stop()
            throw Error(it)
        }
    }

    private suspend fun cap(
        mediaProjection: MediaProjection,
    ): Bitmap {
        val screenSize = context.screenSize()
        logger.d("screenSize: $screenSize")

        val rotation = context.defaultDisplay().rotation
        logger.d("rotation: $rotation")

        val screenWidth = screenSize.x
        val screenHeight = screenSize.y

        // 根据屏幕旋转调整宽高
        val adjustedWidth =
            if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) screenHeight else screenWidth
        val adjustedHeight =
            if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) screenWidth else screenHeight

        logger.d("adjustedWidth: $adjustedWidth")
        logger.d("adjustedHeight: $adjustedHeight")

        imageReader =
            ImageReader.newInstance(adjustedWidth, adjustedHeight, PixelFormat.RGBA_8888, 2)
        val surface: Surface = requireNotNull(imageReader) { "WTF imageReader is null" }.surface

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ShortX-ScreenCap",
            adjustedWidth,
            adjustedHeight,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            handler
        )
        logger.d("virtualDisplay: $virtualDisplay")

        var image = requireNotNull(imageReader).capture()
        //在缓存图像均不可用的情况下等待2秒取得截图，否则抛出错误
        val newImage = image ?: runCatching {
            withTimeout(2000) {
                while (image == null) {
                    delay(200)
                    image = requireNotNull(imageReader).capture()
                }
                yield()
                return@withTimeout image!!
            }
        }.getOrElse {
            logger.e(it, "Capture waited.")
            throw Exception("ScreenCap timeout")
        }
        val bitmap = toBitmap(newImage)
        return bitmap
    }

    private fun ImageReader.capture(): Image? = synchronized(this) {
        if (isDestroyed.get()) throw Exception("ScreenCap is not available")
        val newImage = acquireLatestImage()
        return newImage
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
                val bitmap = toBitmap(image)

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

    private fun Bitmap.isEmptyBitmap(): Boolean {
        val emptyBitmap = Bitmap.createBitmap(width, height, config)
        return this.sameAs(emptyBitmap)
    }

    private val callback = object : Callback() {
        override fun onStop() {
            logger.w("stopping projection.")
            handler.post {
                virtualDisplay?.release()
                imageReader?.setOnImageAvailableListener(null, null)
                mediaProjection?.unregisterCallback(this)
                isDestroyed.set(true)
            }
        }
    }

    private fun toBitmap(image: Image): Bitmap {
        val plane = image.planes[0]
        val buffer = plane.buffer
        buffer.position(0)
        val pixelStride = plane.pixelStride
        val rowPadding = plane.rowStride - pixelStride * image.width
        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        if (rowPadding == 0) {
            return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
    }
}

// Change log:
// java.util.ConcurrentModificationException
//	at android.util.ArrayMap.binarySearchHashes(ArrayMap.java:133)
//	at android.util.ArrayMap.indexOf(ArrayMap.java:149)
//	at android.util.ArrayMap.indexOfKey(ArrayMap.java:442)
//	at android.util.ArrayMap.remove(ArrayMap.java:733)
//	at android.media.projection.MediaProjection.unregisterCallback(MediaProjection.java:124)
//	at tornaco.apps.shortx.core.os.ScreenCap$callback$1.onStop$lambda$0(ScreenCap.kt:162)
//	at tornaco.apps.shortx.core.os.ScreenCap$callback$1.$r8$lambda$Fm-KS7CUW2iDvFZaODIsuzBLyU8(Unknown Source:0)
//	at tornaco.apps.shortx.core.os.ScreenCap$callback$1$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
//	at android.os.Handler.handleCallback(Handler.java:958)
//	at android.os.Handler.dispatchMessage(Handler.java:99)
//	at android.os.Looper.loopOnce(Looper.java:255)
//	at android.os.Looper.loop(Looper.java:364)
//	at android.os.HandlerThread.run(HandlerThread.java:67)