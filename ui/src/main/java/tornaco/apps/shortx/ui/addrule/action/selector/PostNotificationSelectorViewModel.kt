package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class PostNotificationSelectorState(
    val data: Action.PostNotification
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class PostNotificationSelectorViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<PostNotificationSelectorState>(context) {

    override fun initState(): PostNotificationSelectorState {
        return PostNotificationSelectorState(
            data = Action.PostNotification(
                tag = "tag-${System.currentTimeMillis()}",
                title = "",
                message = "",
                largeIcon = "",
                smallIcon = Remix.Communication.chat_2_line,
                isImportant = false,
                vibrate = false,
                sound = false
            )
        )
    }

    fun initFrom(settings: Action.PostNotification) {
        logger.d("initFrom: $settings")
        updateState {
            copy(data = settings)
        }
    }

    fun updateTag(it: String) {
        updateState { copy(data = data.copy(tag = it)) }
    }

    fun updateTitle(it: String) {
        updateState { copy(data = data.copy(title = it)) }
    }

    fun updateMessage(it: String) {
        updateState { copy(data = data.copy(message = it)) }
    }

    fun setImportant(it: Boolean) {
        updateState { copy(data = data.copy(isImportant = it)) }
    }

    fun setVibrate(it: Boolean) {
        updateState { copy(data = data.copy(vibrate = it)) }
    }

    fun setSound(it: Boolean) {
        updateState { copy(data = data.copy(sound = it)) }
    }

    fun updateSmallIcon(it: String) {
        updateState { copy(data = data.copy(smallIcon = it)) }
    }

    fun updateLargeIcon(it: String) {
        updateState { copy(data = data.copy(largeIcon = it)) }
    }
}