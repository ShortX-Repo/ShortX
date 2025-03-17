package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Point
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object FindPointsByColor : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        androidContext.requireExt()
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
        val points = handleAction(
            "FindPointsByColor",
            d.config,
            androidContext,
            evaluateContext,
            shortXContext
        )

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.isColorFound.name)
                .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.isColorFound.name },
            points.isNotEmpty()
        )

        val firstPoint = points.firstOrNull()
        firstPoint?.let {
            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.pointX.name)
                    .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.pointX.name },
                firstPoint.x.toString()
            )
            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.pointY.name)
                    .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.pointY.name },
                firstPoint.y.toString()
            )
        }
        Actor.Res.EMPTY
    }

    suspend fun handleAction(
        log: String,
        config: tornaco.apps.shortx.core.proto.common.FindPointsByColorConfig,
        androidContext: Context,
        evaluateContext: EvaluateContext,
        shortXContext: ShortXContext
    ): List<Point> {
        val image =
            config.image.getBitmapFromImageSrcProto(
                androidContext,
                evaluateContext,
                shortXContext
            )
        logger.d("image: $image")
        saveScreenBitmapToFile(image, Dirs.baseServerCacheDir(), "image")

        val cv = shortXContext.cv.requireCV()
        logger.d("cv: $cv")

        val points: List<Point> = with(OcrDetect) {
            image.useSharedPFD { imagePFD ->
                cv.findPointsByColorPFD(
                    imagePFD,
                    config.color,
                    config.threshold,
                    config.rect.takeIf { it != Rect.getDefaultInstance() }?.toAndroidRect()
                )
            }
        }

        logger.d("$log points: $points")
        return points
    }
}