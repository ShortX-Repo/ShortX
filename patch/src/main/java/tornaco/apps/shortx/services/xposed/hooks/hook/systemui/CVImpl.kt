package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.ParcelFileDescriptor
import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections
import java.io.FileDescriptor

// TODO Report error.
// java.lang.NoSuchMethodError:
// tornaco.apps.shortx.ext.api.cv.ShortXCVApi#findPointsByColor(android.graphics.Bitmap,java.lang.Integer,java.lang.Integer,android.graphics.Rect)#bestmatch
//	at twbpy.nhwhfj.hho.U22.f(Unknown Source:191)
//	at twbpy.nhwhfj.hho.U22.b(Unknown Source:26)
//	at tornaco.apps.shortx.services.xposed.hooks.hook.systemui.CVImpl.findPointsByColor(Unknown Source:77)
//	at tornaco.apps.shortx.core.cv.ICV$Stub.onTransact(Unknown Source:65)
//	at android.os.Binder.execTransactInternal(Binder.java:1512)
//	at android.os.Binder.execTransact(Binder.java:1451)
class CVImpl(private val context: Context) : ICV.Stub() {
    private val logger = Logger("CVImpl")
    private val cvApiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ext.api.cv.ShortXCVApi"
        )
    }

    override fun findImage(image: Bitmap, template: Bitmap): Point? {
        return runCatching {
            logger.d("findImage cvApiIns: $cvApiIns")
            val point: Point? =
                Reflections.callMethod(cvApiIns, "findImage", image, template) as Point?
            logger.d("findImage point: $point")
            point
        }.getOrElse {
            logger.e(it, "findImage error")
            null
        }
    }

    override fun findPointsByColor(
        image: Bitmap,
        color: Int,
        threshold: Int,
        rect: Rect?
    ): List<Point> {
        return runCatching {
            logger.d("findPointsByColor cvApiIns: $cvApiIns")
            val res =
                Reflections.callMethod(cvApiIns, "findPointsByColor", image, color, threshold, rect)

            @Suppress("UNCHECKED_CAST")
            val points = (res as List<Point>)
            // Log too large will cause TransactionTooLarge err.
            logger.d("findPointsByColor points count: ${res.size}")
            points.take(1)
        }.getOrElse {
            logger.e(it, "findPointsByColor")
            emptyList()
        }
    }

    override fun findImagePFD(
        imagePFD: ParcelFileDescriptor,
        templatePFD: ParcelFileDescriptor
    ): Point? {
        val image = BitmapFactory.decodeFileDescriptor(imagePFD.fileDescriptor)
        val template = BitmapFactory.decodeFileDescriptor(templatePFD.fileDescriptor)
        logger.d("findImagePFD, image: $image, template: $template")
        val res = findImage(image, template)
        runCatching { image.recycle() }
        runCatching { template.recycle() }
        return res
    }

    override fun findPointsByColorPFD(
        pfd: ParcelFileDescriptor,
        color: Int,
        threshold: Int,
        rect: Rect?
    ): List<Point> {
        val fd: FileDescriptor = pfd.fileDescriptor
        val bitmap = BitmapFactory.decodeFileDescriptor(fd)
        logger.d("findPointsByColorPFD, bitmap: $bitmap")
        val res = findPointsByColor(bitmap, color, threshold, rect)
        runCatching { bitmap.recycle() }
        return res
    }
}