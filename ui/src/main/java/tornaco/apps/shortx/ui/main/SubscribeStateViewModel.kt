package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.subscribe.util.isLVLChecked
import java.util.*
import javax.inject.Inject


data class SubscribeState(
    val isSubscribed: Boolean = false,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SubscribeStateViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<SubscribeState>(context, { SubscribeState() }) {
    init {
        updateState {
            copy(
                isSubscribed = isLVLChecked(context),
            )
        }
    }

    fun refreshStates() {
        updateState {
            copy(
                isSubscribed = isLVLChecked(context)
            )
        }
    }
}