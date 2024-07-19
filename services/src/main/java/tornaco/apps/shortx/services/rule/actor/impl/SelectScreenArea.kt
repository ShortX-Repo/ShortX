package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Rect
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

@OptIn(ExperimentalCoroutinesApi::class)
object SelectScreenArea : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java

        val rect: Rect? = suspendCancellableCoroutine<Rect?> { con ->
            shortXContext.wm.enableAreaSelector(
                onSelect = {
                    con.resume(it) {}
                },
                onDetach = {
                    con.resume(null) {}
                }
            )

            con.invokeOnCancellation {
                shortXContext.wm.disableAreaSelector()
            }
        }


        requireNotNull(rect) {
            "No Rect selected."
        }

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.Rect.rectLeft.name)
                .fallbackOnEmpty { ContextDataMapping.Rect.rectLeft.name },
            rect.left.toString(),
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.Rect.rectTop.name)
                .fallbackOnEmpty { ContextDataMapping.Rect.rectTop.name },
            rect.top.toString(),
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.Rect.rectRight.name)
                .fallbackOnEmpty { ContextDataMapping.Rect.rectRight.name },
            rect.right.toString(),
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.Rect.rectBottom.name)
                .fallbackOnEmpty { ContextDataMapping.Rect.rectBottom.name },
            rect.bottom.toString(),
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.Rect.rectFlattenToString.name)
                .fallbackOnEmpty { ContextDataMapping.Rect.rectFlattenToString.name },
            rect.flattenToString(),
        )
    }
}