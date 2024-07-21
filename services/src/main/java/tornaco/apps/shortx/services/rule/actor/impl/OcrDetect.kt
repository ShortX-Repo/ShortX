package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.proto.common.RectSourcePath
import tornaco.apps.shortx.core.proto.common.RectSourceRect
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File

object OcrDetect : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.OcrDetect::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        androidContext.requireExt()

        val data =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.OcrDetect::class.java

        val src = data.rectSrc

        val bitmapToRecycle: MutableList<Bitmap> = mutableListOf()

        val bitmap = if (src is_ RectSourceRect::class.java) {
            val inputRect = (src unpack_ RectSourceRect::class.java).rect
            val screenCap = ScreenCap(androidContext)
            val screen: Bitmap = try {
                screenCap.cap()
            } finally {
                screenCap.destroy()
            }
            logger.d("RectSourceRect screen: $screen")
            BitmapUtils.cropBitmap(screen, inputRect.toAndroidRect()).also {
                bitmapToRecycle.add(it)
            }
        } else if (src is_ RectSourcePath::class.java) {
            val path = (src unpack_ RectSourcePath::class.java).path
            val tmpFile = File(
                Dirs.ruleCacheDir(evaluateContext),
                "RectSourcePath_tmp_${data.id}_${path.hashCode()}.png"
            )
            if (!tmpFile.exists()) {
                CopyFile.copyFile(shortXContext, File(path), tmpFile, false)
                logger.d("RectSourcePath new tmpFile: $tmpFile")
            } else {
                logger.d("RectSourcePath tmpFile exists: $tmpFile")
            }
            val bitmap = BitmapFactory.decodeFile(tmpFile.absolutePath)
            logger.d("RectSourcePath bitmap: $bitmap")
            bitmap
        } else {
            val area = SelectScreenArea.startSelectScreenArea(shortXContext)
            val rect: android.graphics.Rect? = area?.getRect(androidContext)

            requireNotNull(rect) {
                "No Screen Rect selected."
            }
            delay(100)
            val screenCap = ScreenCap(androidContext)
            val screen: Bitmap = try {
                screenCap.cap()
            } finally {
                screenCap.destroy()
            }
            logger.d("RectSourceSelectScreenArea screen: $screen")
            val cropped =
                if (area is AreaSelection.FullScreen) screen else BitmapUtils.cropBitmap(
                    screen,
                    rect
                )
            logger.d("RectSourceSelectScreenArea cropped: $cropped")

            bitmapToRecycle.add(screen)
            bitmapToRecycle.add(cropped)

            cropped
        }

        logger.d("ORC Detect bitmap: $bitmap")

        val ocrResult = shortXContext.ocr.requireOCR().recognizeText(
            bitmap,
            4,
            false
        )

        bitmapToRecycle.forEach { it.recycle() }

        val ocrResultString = ocrResult.joinToString(data.separator)

        params.contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.OcrDetect.ocrResult.name)
                .fallbackOnEmpty { ContextDataMapping.OcrDetect.ocrResult.name },
            ocrResultString
        )

        logger.d("ocrResult: $ocrResultString")
    }
}

