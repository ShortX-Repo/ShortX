package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Bitmap
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.proto.common.ImageSourceBase64
import tornaco.apps.shortx.core.proto.common.ImageSourcePath
import tornaco.apps.shortx.core.proto.common.ImageSourceScreenshot
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.OcrDetect.getBitmapFromPath
import java.io.File
import java.io.FileOutputStream

object FindImagePosition : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindImagePosition::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        androidContext.requireExt()

        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindImagePosition::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindImagePosition when device is not interactive"
        }

        val image =
            d.image.getBitmapFromImageSrcProto(androidContext, evaluateContext, shortXContext)
        logger.d("image: $image")
        saveScreenBitmapToFile(image, Dirs.baseServerCacheDir(), "image")

        val template =
            d.template.getBitmapFromImageSrcProto(androidContext, evaluateContext, shortXContext)
        logger.d("template: $template")
        saveScreenBitmapToFile(template, Dirs.baseServerCacheDir(), "template")

        val cv = shortXContext.cv.requireCV()
        logger.d("cv: $cv")
        val point = cv.findImage(image, template)
        logger.d("point: $point")

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.FindImagePosition.isImageFound.name)
                .fallbackOnEmpty { ContextDataMapping.FindImagePosition.isImageFound.name },
            point != null
        )
        point?.let {
            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.FindImagePosition.pointX.name)
                    .fallbackOnEmpty { ContextDataMapping.FindImagePosition.pointX.name },
                point.x.toString()
            )
            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.FindImagePosition.pointY.name)
                    .fallbackOnEmpty { ContextDataMapping.FindImagePosition.pointY.name },
                point.y.toString()
            )
        }
        Actor.Res.EMPTY
    }
}

suspend fun ProtoAny.getBitmapFromImageSrcProto(
    androidContext: Context,
    evaluateContext: EvaluateContext,
    shortXContext: ShortXContext
): Bitmap {
    return if (this is_ ImageSourceBase64::class.java) {
        val d = this unpack_ ImageSourceBase64::class.java
        requireNotNull(BitmapUtils.base64ToBitmap(d.base64)) {
            "Fail to decode bitmap from base64."
        }
    } else if (this is_ ImageSourcePath::class.java) {
        val d = this unpack_ ImageSourcePath::class.java
        val bitmap = getBitmapFromPath(evaluateContext, shortXContext, d.path)
        bitmap
    } else if (this is_ ImageSourceScreenshot::class.java) {
        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.waitUntilDestroyed()
        }
        screen
    } else {
        throw Error("Unknown: ${this.typeUrl}")
    }
}

fun saveScreenBitmapToFile(imageData: Bitmap, dir: File, prefix: String = "screenshot"): File {
    val fileName = "$prefix-${System.currentTimeMillis()}.png"
    val filePath = File(dir, fileName).apply {
        parentFile?.mkdirs()
    }
    FileOutputStream(filePath).use {
        imageData.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return filePath
}
