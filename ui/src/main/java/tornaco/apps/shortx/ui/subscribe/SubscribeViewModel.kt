package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.base.StateViewModel
import javax.inject.Inject

enum class SubscriptionSource {
    GooglePlay,
    ActivationCode
}

data class SubscriptionState(
    val isSubscribed: Boolean = false,
    val source: SubscriptionSource? = null
)

class SubscribeViewModel @Inject constructor(@ApplicationContext context: Context) :
    StateViewModel<SubscriptionState>(SubscriptionState()) {


}