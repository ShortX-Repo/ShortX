package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import com.google.protobuf.Message
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.io.FileOutputStream

object FindImagePositionFromScreen : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        androidContext.requireExt()

        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindImagePositionFromScreen::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindImagePositionFromScreen when device is not interactive"
        }

        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.destroy()
        }
        logger.d("screen: $screen")
        saveScreenBitmapToFile(screen, Dirs.baseServerCacheDir())

        val target =
            Bitmap.createScaledBitmap(requireNotNull(BitmapUtils.base64ToBitmap(d.imageBase64)) {
                "Fail to decode bitmap from base64."
            }, screen.width, screen.height, false)
        logger.d("target: $target")
        saveScreenBitmapToFile(target, Dirs.baseServerCacheDir())

        val cv = shortXContext.cv.requireCV()
        logger.d("cv: $cv")
        val point = cv.findImage(screen, target)
        logger.d("point: $point")
    }
}

fun saveScreenBitmapToFile(imageData: Bitmap, dir: File): File {
    val fileName = "screenshot-${System.currentTimeMillis()}.png"
    val filePath = File(dir, fileName).apply {
        parentFile?.mkdirs()
    }
    FileOutputStream(filePath).use {
        imageData.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return filePath
}
