package tornaco.apps.shortx.services.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import tornaco.apps.shortx.services.ShortXContext

abstract class SafeBroadcastReceiver(val shortXContext: ShortXContext) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        kotlin.runCatching { handleReceive(context, intent) }.onFailure {
            shortXContext.watchDog.onFatalError(it, "handleReceive error - $intent")
        }
    }

    abstract fun handleReceive(context: Context, intent: Intent)
}