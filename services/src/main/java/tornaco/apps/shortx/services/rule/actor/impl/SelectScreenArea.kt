package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.graphics.Rect
import com.google.protobuf.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object SelectScreenArea : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java

        val rect: Rect? = startSelectScreenArea(shortXContext, d.themeMode)?.getRect(androidContext)

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

        Actor.Res(rect.flattenToString())
    }

    suspend fun startSelectScreenArea(shortXContext: ShortXContext, themeMode: tornaco.apps.shortx.core.proto.common.UiThemeMode = tornaco.apps.shortx.core.proto.common.UiThemeMode.UiThemeMode_System): AreaSelection? {
        val rect: AreaSelection? = suspendCancellableCoroutine<AreaSelection?> { con ->
            shortXContext.wm.enableAreaSelector(
                themeMode = themeMode,
                onSelect = {
                    con.resume(AreaSelection.SelectRect(it)) {}
                },
                onSelectFullScreen = {
                    con.resume(AreaSelection.FullScreen) {}
                },
                onDetach = {
                    con.resume(null) {}
                }
            )

            con.invokeOnCancellation {
                shortXContext.wm.disableAreaSelector()
            }
        }
        return rect
    }
}

sealed interface AreaSelection {
    fun getRect(context: Context): Rect

    data object FullScreen : AreaSelection {
        override fun getRect(context: Context): Rect {
            val screen = context.screenSize()
            return Rect(
                0,
                0,
                screen.x,
                screen.y
            )
        }
    }

    data class SelectRect(val rect: Rect) : AreaSelection {
        override fun getRect(context: Context): Rect {
            return rect
        }
    }
}
