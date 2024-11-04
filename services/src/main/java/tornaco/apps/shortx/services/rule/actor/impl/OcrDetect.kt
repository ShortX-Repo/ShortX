package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.proto.common.OcrResult
import tornaco.apps.shortx.core.proto.common.RectSourcePath
import tornaco.apps.shortx.core.proto.common.RectSourceRect
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.io.IOException

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

        val ocrResult = recognizeText(this, src)
        val ocrResultString = ocrResult.joinToString(data.separator)

        params.contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.OcrDetect.ocrResult.name)
                .fallbackOnEmpty { ContextDataMapping.OcrDetect.ocrResult.name },
            ocrResultString
        )

        logger.d("ocrResult: $ocrResultString")
    }

    suspend fun recognizeText(params: HandleParams, src: ProtoAny): Array<out String> {
        return with(params) {
            val ocrResult: Array<String> = withSrcBitmap(
                src,
                androidContext,
                evaluateContext,
                shortXContext
            ) { bitmap ->
                logger.d("recognizeText, bitmap: $bitmap")
                if (BuildPropInternal.APP_IS_DEBUG) {
                    runCatching {
                        saveScreenBitmapToFile(
                            bitmap,
                            Dirs.baseServerCacheDir(),
                            "ocr_recognize"
                        )
                    }
                }
                shortXContext.ocr.requireOCR().recognizeText(
                    bitmap,
                    4,
                    false
                )
            }
            logger.logOnDebug("recognizeText, ocrResult: $ocrResult")
            ocrResult
        }
    }

    suspend fun detect(params: HandleParams, src: ProtoAny): List<OcrResult> {
        return with(params) {
            val ocrResult: List<OcrResult> = withSrcBitmap(
                src,
                androidContext,
                evaluateContext,
                shortXContext
            ) { bitmap ->
                logger.d("detect, bitmap: $bitmap")
                if (BuildPropInternal.APP_IS_DEBUG) {
                    runCatching {
                        saveScreenBitmapToFile(
                            bitmap,
                            Dirs.baseServerCacheDir(),
                            "ocr_detect"
                        )
                    }
                }
                shortXContext.ocr.requireOCR().detect(
                    bitmap,
                    4,
                    false
                )
            }.mapNotNull { OcrResult.parseFrom(it.byteData) }
            logger.logOnDebug("detect, ocrResult: $ocrResult")
            ocrResult
        }
    }

    suspend fun <T> withSrcBitmap(
        src: ProtoAny,
        androidContext: Context,
        evaluateContext: EvaluateContext,
        shortXContext: ShortXContext,
        action: (Bitmap) -> T
    ): T {
        val bitmapToRecycle: MutableList<Bitmap> = mutableListOf()

        val bitmap: Bitmap = if (src is_ RectSourceRect::class.java) {
            val inputRect = (src unpack_ RectSourceRect::class.java).rect
            val screenCap = ScreenCap(androidContext)
            val screen: Bitmap = try {
                screenCap.cap()
            } finally {
                screenCap.waitUntilDestroyed()
            }
            logger.d("detectOCRText RectSourceRect screen: $screen")
            BitmapUtils.cropBitmap(screen, inputRect.toAndroidRect()).also {
                bitmapToRecycle.add(it)
            }
        } else if (src is_ RectSourcePath::class.java) {
            val path = (src unpack_ RectSourcePath::class.java).path
            getBitmapFromPath(evaluateContext, shortXContext, path)
        } else {
            val area = SelectScreenArea.startSelectScreenArea(shortXContext)
            val rect: Rect? = area?.getRect(androidContext)

            requireNotNull(rect) {
                "No Screen Rect selected."
            }
            delay(100)
            val screenCap = ScreenCap(androidContext)
            val screen: Bitmap = try {
                screenCap.cap()
            } finally {
                screenCap.waitUntilDestroyed()
            }
            logger.d("detectOCRText RectSourceSelectScreenArea screen: $screen")
            val cropped =
                if (area is AreaSelection.FullScreen) screen else BitmapUtils.cropBitmap(
                    screen,
                    rect
                )
            logger.d("detectOCRText RectSourceSelectScreenArea cropped: $cropped")

            bitmapToRecycle.add(screen)
            bitmapToRecycle.add(cropped)

            cropped
        }
        return try {
            action(bitmap)
        } finally {
            bitmapToRecycle.forEach { it.recycle() }
            logger.i("Recycling bitmaps.")
        }
    }

    suspend fun getBitmapFromPath(
        evaluateContext: EvaluateContext,
        shortXContext: ShortXContext,
        path: String
    ): Bitmap {
        val tmpFile = File(
            Dirs.ruleCacheDir(evaluateContext),
            "tmp_${path.hashCode()}.png"
        )
        if (!tmpFile.exists()) {
            // Unable to decode stream: java.io.FileNotFoundException:
            // /data/system/shortx_zMQSbizboGjCfJdF/cache/rule/DA9906629e-530d-4db7-9942-13d9f86f17d5
            // /RectSourcePath_tmp_A-8c84842c-2f1c-437e-90ed-b1a53e514ed5_-677193684.png:
            // open failed: EACCES (Permission denied)
            kotlin.runCatching {
                CopyFile.copyFile(
                    shortXContext = shortXContext,
                    srcFile = File(path),
                    destFile = tmpFile,
                    deleteSrcOnSuccess = false,
                    owner = "1000.1000"
                )
            }.onFailure {
                throw IOException("Failed to copy $path to $tmpFile, is this file exists?", it)
            }
            logger.d("getBitmapFromPath new tmpFile: $tmpFile")
        } else {
            logger.d("getBitmapFromPath tmpFile exists: $tmpFile")
        }

        val bitmap = requireNotNull(BitmapFactory.decodeFile(tmpFile.absolutePath)) {
            "Failed to decode $tmpFile （读取文件失败）"
        }
        logger.d("getBitmapFromPath bitmap: $bitmap")
        return bitmap
    }
}

