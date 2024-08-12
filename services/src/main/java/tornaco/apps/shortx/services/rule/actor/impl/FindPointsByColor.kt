package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Point
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object FindPointsByColor : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        androidContext.requireExt(3)
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
        val points = handleAction(d.config, params)

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
    }

    suspend fun handleAction(
        config: tornaco.apps.shortx.core.proto.common.FindPointsByColorConfig,
        params: HandleParams,
    ): List<Point> {
        with(params) {
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
            val points = cv.findPointsByColor(
                image,
                config.color,
                config.threshold,
                config.rect.takeIf { it != Rect.getDefaultInstance() }?.toAndroidRect()
            )
            logger.d("points: $points")
            return points
        }
    }
}