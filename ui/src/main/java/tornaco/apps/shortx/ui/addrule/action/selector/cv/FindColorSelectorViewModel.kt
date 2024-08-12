package tornaco.apps.shortx.ui.addrule.action.selector.cv

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.ui.addrule.action.model.FindPointsByColorConfig
import tornaco.apps.shortx.ui.addrule.action.model.ImageSource
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FindColorSelectorState(
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

    fun initFrom(settings: FindPointsByColorConfig?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
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

    fun toActionOrNull(): FindPointsByColorConfig {
        return FindPointsByColorConfig(
            image = state.value.image,
            color = state.value.color,
            threshold = state.value.threshold,
            rect = state.value.rect,
        )
    }
}