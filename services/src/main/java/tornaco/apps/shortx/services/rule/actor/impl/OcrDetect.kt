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
                "ocr_tmp_${path.hashCode()}.png"
            )
            if (!tmpFile.exists()) {
                // Unable to decode stream: java.io.FileNotFoundException:
                // /data/system/shortx_zMQSbizboGjCfJdF/cache/rule/DA9906629e-530d-4db7-9942-13d9f86f17d5
                // /RectSourcePath_tmp_A-8c84842c-2f1c-437e-90ed-b1a53e514ed5_-677193684.png:
                // open failed: EACCES (Permission denied)
                CopyFile.copyFile(shortXContext, File(path), tmpFile, false, "1000.1000")
                logger.d("RectSourcePath new tmpFile: $tmpFile")
            } else {
                logger.d("RectSourcePath tmpFile exists: $tmpFile")
            }

            val bitmap = requireNotNull(BitmapFactory.decodeFile(tmpFile.absolutePath)) {
                "Failed to decode $tmpFile （读取文件失败）"
            }
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

