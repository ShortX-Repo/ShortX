package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.ParcelFileDescriptor
import com.google.protobuf.ByteString
import com.google.protobuf.StringValue
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ByteArrayList
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections
import java.io.FileDescriptor


class TessOCRImpl(private val context: Context) : IOCR.Stub() {
    private val logger = Logger("OCRImpl")
    private val apiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ext.api.ocr.ShortXTessApi"
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun findAllContinuousTextPositions(
        engine: Int,
        bitmap: Bitmap,
        text: String
    ): List<ByteArrayWrapper> {
        logger.d("findAllContinuousTextPositions apiIns: $apiIns")
        val result = (Reflections.callMethod(
            apiIns,
            "findAllContinuousTextPositions",
            bitmap,
            text
        ) as List<ByteArray>).map {
            ByteArrayWrapper(it)
        }
        logger.d("findAllContinuousTextPositions result: $result")
        return result
    }

    override fun recognizeText(
        engine: Int,
        bitmap: Bitmap,
    ): String {
        logger.d("recognizeText apiIns: $apiIns")
        val result = (Reflections.callMethod(
            apiIns,
            "recognizeText",
            bitmap
        ) as String?)
        logger.d("recognizeText result: $result")
        return result.orEmpty()
    }

    override fun findAllContinuousTextPositionsPFD(
        engine: Int,
        pfd: ParcelFileDescriptor,
        text: String,
        receiver: SynchronousResultReceiver
    ) {
        val res: List<ByteArray> = kotlin.runCatching {
            val fd: FileDescriptor = pfd.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fd)
            logger.d("findAllContinuousTextPositionsPFD, bitmap: $bitmap")
            val result = kotlin.runCatching {
                findAllContinuousTextPositions(engine, bitmap, text)
            }.getOrElse {
                logger.e(it, "findAllContinuousTextPositionsPFD")
                emptyList()
            }.map {
                it.byteData
            }
            bitmap.recycle()
            result
        }.getOrElse {
            logger.e(it, "findAllContinuousTextPositionsPFD")
            emptyList()
        }
        receiver.send(
            ByteArrayWrapper(
                ByteArrayList.newBuilder()
                    .addAllData(res.map { ByteString.copyFrom(it) })
                    .build().toByteArray()
            )
        )
    }

    override fun recognizeTextPFD(
        engine: Int,
        pfd: ParcelFileDescriptor,
        receiver: SynchronousResultReceiver
    ) {
        var bitmap: Bitmap? = null
        val result: String = kotlin.runCatching {
            val fd: FileDescriptor = pfd.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fd)
            logger.d("recognizeTextPFD, bitmap: $bitmap")
            recognizeText(engine, requireNotNull(bitmap) { "bitmap is null." })
        }.getOrElse {
            logger.e(it, "recognizeTextPFD")
            runCatching { bitmap?.recycle() }
            throw it
        }
        runCatching { bitmap?.recycle() }
        receiver.send(
            ByteArrayWrapper(
                StringValue.newBuilder().setValue(result).build().toByteArray()
            )
        )
    }
}