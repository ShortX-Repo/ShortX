package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class OCRSelectorState(
    val id: String = defaultNewActionId(),
    val src: Action.OcrDetect.RectSrc = Action.OcrDetect.RectSrc.SelectScreenArea
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class OCRSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<OCRSelectorState>(
        context, {
            OCRSelectorState()
        }
    ) {


    fun initFrom(settings: Action.OcrDetect?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    src = it.src
                )
            }
        }
    }

    fun setSrc(src: Action.OcrDetect.RectSrc) {
        updateState {
            copy(src = src)
        }
    }

    fun getAction(): Action.OcrDetect {
        return Action.OcrDetect(src = state.value.src, id = state.value.id)
    }
}