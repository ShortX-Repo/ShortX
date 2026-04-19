package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.ocr

import android.content.Context
import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.OCREngine
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper

class OCRImpl(private val context: Context) : IOCR.Stub() {
    private val tess by lazy { TessOCRImpl(context) }
    private val paddle by lazy { PaddleOCRImpl(context) }

    private fun getEngine(engine: Int): IOCR {
        val ocrEngine: OCREngine = OCREngine.forNumber(engine)
        return when (ocrEngine) {
            OCREngine.OCREngine_Paddle -> {
                paddle
            }

            OCREngine.OCREngine_Tesseract, OCREngine.UNRECOGNIZED -> {
                tess
            }
        }
    }

    override fun findAllContinuousTextPositions(
        engine: Int,
        bitmap: Bitmap,
        text: String
    ): List<ByteArrayWrapper> {
        return getEngine(engine).findAllContinuousTextPositions(engine, bitmap, text)
    }

    override fun recognizeText(
        engine: Int,
        bitmap: Bitmap,
    ): String {
        return getEngine(engine).recognizeText(engine, bitmap)
    }

    override fun findAllContinuousTextPositionsPFD(
        engine: Int,
        pfd: ParcelFileDescriptor,
        text: String,
        receiver: SynchronousResultReceiver
    ) {
        getEngine(engine).findAllContinuousTextPositionsPFD(engine, pfd, text, receiver)
    }

    override fun recognizeTextPFD(
        engine: Int,
        pfd: ParcelFileDescriptor,
        receiver: SynchronousResultReceiver
    ) {
        getEngine(engine).recognizeTextPFD(engine, pfd, receiver)
    }

    override fun recognizeTextJson(
        engine: Int,
        image: Bitmap?
    ): String? {
        return getEngine(engine).recognizeTextJson(engine, image)
    }

    override fun recognizeTextJsonPFD(
        engine: Int,
        pfd: ParcelFileDescriptor?,
        receiver: SynchronousResultReceiver?
    ) {
        getEngine(engine).recognizeTextJsonPFD(engine, pfd, receiver)
    }
}