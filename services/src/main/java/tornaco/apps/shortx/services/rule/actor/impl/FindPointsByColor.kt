package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Point
import com.google.protobuf.Message
import com.google.protobuf.StringValue
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.FindPointsByColorOutputType
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.OcrDetect.useSharedPFD
import java.time.Duration

object FindPointsByColor : Actor {
    private const val FIND_COLOR_JSON_TIMEOUT_S = 30L

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        androidContext.requireExt()
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
        val cfg = d.config

        when (cfg.outputType) {
            FindPointsByColorOutputType.FindPointsByColorOutputType_Json -> {
                val image = cfg.image.getBitmapFromImageSrcProto(
                    androidContext,
                    evaluateContext,
                    shortXContext
                )
                logger.d("image: $image")
                val cv = shortXContext.cv.requireCV()
                val receiver = SynchronousResultReceiver.get()
                val json: String = image.useSharedPFD { imagePFD ->
                    cv.findPointsByColorJsonPFD(
                        imagePFD,
                        cfg.color,
                        cfg.threshold,
                        cfg.rect.takeIf { it != Rect.getDefaultInstance() }?.toAndroidRect(),
                        receiver
                    )
                    val receiverRes = receiver.awaitResultNoInterrupt(
                        Duration.ofSeconds(FIND_COLOR_JSON_TIMEOUT_S)
                    )
                    val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
                    requireNotNull(byteRes) { "findPointsByColorJsonPFD empty response." }
                    StringValue.parseFrom(byteRes.byteData).value
                }
                logger.d("findPointsByColorJson: $json")

                params.contextData.add(
                    d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.isColorFound.name)
                        .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.isColorFound.name },
                    json.isNotEmpty()
                )
                params.contextData.add(
                    d.customContextDataKey.forKey(
                        ContextDataMapping.FindPointsByColor.findPointsByColorJson.name
                    )
                        .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.findPointsByColorJson.name },
                    json
                )
                params.contextData.add(
                    d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.pointX.name)
                        .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.pointX.name },
                    ""
                )
                params.contextData.add(
                    d.customContextDataKey.forKey(ContextDataMapping.FindPointsByColor.pointY.name)
                        .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.pointY.name },
                    ""
                )
            }

            FindPointsByColorOutputType.FindPointsByColorOutputType_Text,
            FindPointsByColorOutputType.UNRECOGNIZED -> {
                val points = handleAction(
                    "FindPointsByColor",
                    cfg,
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
                params.contextData.add(
                    d.customContextDataKey.forKey(
                        ContextDataMapping.FindPointsByColor.findPointsByColorJson.name
                    )
                        .fallbackOnEmpty { ContextDataMapping.FindPointsByColor.findPointsByColorJson.name },
                    ""
                )
            }
        }
        Actor.Res.EMPTY
    }

    /**
     * 始终走点查找CV路径（Text/Unrecognized分支），忽略 outputType。
     */
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

        logger.w("$log points: $points")
        return points
    }
}
