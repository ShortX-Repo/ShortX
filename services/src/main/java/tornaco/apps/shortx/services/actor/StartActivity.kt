package tornaco.apps.shortx.services.actor

import android.content.ComponentName
import android.content.Context
import android.content.Intent

class StartActivity(
    private val context: Context,
    private val startActivity: tornaco.apps.shortx.core.action.StartActivity,
) {
    operator fun invoke() {
        context.startActivity(Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setComponent(ComponentName.unflattenFromString(startActivity.shortName)))
    }
}