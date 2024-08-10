package tornaco.apps.shortx.ui.addrule.action.selector.cv

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.ImageSource
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FindColorSelectorState(
    val id: String = defaultNewActionId(),
    val image: ImageSource = ImageSource.Screenshot,
    val color: Int = android.graphics.Color.RED,
    val threshold: Int = 0,
    val rect: Rect = Rect.getDefaultInstance()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FindColorSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FindColorSelectorState>(
        context, {
            FindColorSelectorState()
        }
    ) {

    fun initFrom(settings: Action.FindPointsByColor?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    image = it.image,
                    color = it.color,
                    threshold = it.threshold,
                    rect = it.rect
                )
            }
        }
    }

    fun setImage(src: ImageSource) {
        updateState {
            copy(image = src)
        }
    }

    fun setColor(color: Int) {
        updateState {
            copy(color = color)
        }
    }

    fun toActionOrNull(): Action.FindPointsByColor {
        return Action.FindPointsByColor(
            image = state.value.image,
            color = state.value.color,
            threshold = state.value.threshold,
            rect = state.value.rect,
            id = state.value.id
        )
    }
}