package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class FromDASelectorState(
    val data: Action.FromDA
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FromDADialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<FromDASelectorState>(
        context, {
            FromDASelectorState(
                data = Action.FromDA(daId = "", title = null, description = null),
            )
        }
    ) {

    fun initFrom(settings: Action.FromDA) {
        logger.d("initFrom: $settings")
        selectDA(settings.daId)
    }

    fun selectDA(daId: String) {
        logger.d("selectDA: $daId")
        val da = shortXManager.getDirectActionById(daId)
        updateState {
            copy(
                data = data.copy(daId = daId, title = da?.title, description = da?.description)
            )
        }
    }
}