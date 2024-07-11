package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.ui.cv.api.Images
import tornaco.apps.shortx.ui.cv.api.ScreenMetrics
import tornaco.apps.shortx.ui.cv.api.ShortXImages.findImageX
import tornaco.apps.shortx.ui.cv.image.ImageWrapper
import tornaco.apps.shortx.ui.cv.opencv.OpenCVHelper


@OptIn(ExperimentalCoroutinesApi::class)
object FindImagePositionFromScreen : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindImagePositionFromScreen when device is not interactive"
        }

        val target = requireNotNull(BitmapUtils.base64ToBitmap(d.imageBase64)) {
            "Fail to decode bitmap from base64."
        }
        logger.d("target: $target")

        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.destroy()
        }
        logger.d("screen: $screen")

        suspendCancellableCoroutine { con ->
            OpenCVHelper.initIfNeeded(androidContext) {
                logger.d("Init complete.")
                con.resume(Unit) {}

                con.invokeOnCancellation {
                }
            }
        }

        val images = Images(ScreenMetrics())
        val point = images.findImageX(
            requireNotNull(ImageWrapper.ofBitmap(screen)) { "Screen is null" },
            requireNotNull(ImageWrapper.ofBitmap(target)) { "Target is null" })
        val pointStatus = point.toString()

        logger.d("pointStatus: $pointStatus")
    }
}