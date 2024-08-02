package tornaco.apps.shortx.ui.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase


class RowAnalytics : Analytics {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    init {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
    }

    override fun logEvent(event: Event) {
        kotlin.runCatching {
            firebaseAnalytics?.logEvent(event.eventName) {
                event.params.forEach { (t, u) ->
                    param(t, u)
                }
            }
        }
    }
}