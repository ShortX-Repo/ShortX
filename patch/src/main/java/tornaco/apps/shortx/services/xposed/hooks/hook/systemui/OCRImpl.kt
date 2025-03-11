package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.ParcelFileDescriptor
import com.google.protobuf.ByteString
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ByteArrayList
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.proto.StringList
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections
import java.io.FileDescriptor


class OCRImpl(private val context: Context) : IOCR.Stub() {
    private val logger = Logger("OCRImpl")
    private val apiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ext.api.ocr.ShortXPaddleApi"
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun detect(
        bitmap: Bitmap,
        numThread: Int,
        useSlim: Boolean
    ): List<ByteArrayWrapper> {
        logger.d("detect apiIns: $apiIns")
        val result = (Reflections.callMethod(
            apiIns,
            "detect",
            bitmap,
            numThread,
            useSlim
        ) as List<ByteArray>).map {
            ByteArrayWrapper(it)
        }
        logger.d("detect result: $result")
        return result
    }

    @Suppress("UNCHECKED_CAST")
    override fun recognizeText(
        bitmap: Bitmap,
        numThread: Int,
        useSlim: Boolean
    ): Array<String> {
        logger.d("recognizeText apiIns: $apiIns")
        val result = (Reflections.callMethod(
            apiIns,
            "recognizeText",
            bitmap,
            numThread,
            useSlim
        ) as Array<String>)
        logger.d("recognizeText result: $result")
        return result
    }

    override fun detectPFD(
        pfd: ParcelFileDescriptor,
        numThread: Int,
        useSlim: Boolean,
        receiver: SynchronousResultReceiver
    ) {
        val res: List<ByteArray> = kotlin.runCatching {
            val fd: FileDescriptor = pfd.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fd)
            logger.d("detectPFD, bitmap: $bitmap")
            val result = kotlin.runCatching {
                detect(bitmap, numThread, useSlim)
            }.getOrElse {
                logger.e(it, "detectPFD")
                emptyList()
            }.map {
                it.byteData
            }
            bitmap.recycle()
            result
        }.getOrElse {
            logger.e(it, "detectPFD")
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
        pfd: ParcelFileDescriptor,
        numThread: Int,
        useSlim: Boolean,
        receiver: SynchronousResultReceiver
    ) {
        var bitmap: Bitmap? = null
        val result: Array<String> = kotlin.runCatching {
            val fd: FileDescriptor = pfd.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fd)
            logger.d("recognizeTextPFD, bitmap: $bitmap")
            recognizeText(requireNotNull(bitmap) { "bitmap is null." }, numThread, useSlim)
        }.getOrElse {
            logger.e(it, "recognizeTextPFD")
            emptyArray()
        }
        runCatching { bitmap?.recycle() }
        receiver.send(
            ByteArrayWrapper(
                StringList.newBuilder().addAllStrings(result.toList()).build().toByteArray()
            )
        )
    }
}