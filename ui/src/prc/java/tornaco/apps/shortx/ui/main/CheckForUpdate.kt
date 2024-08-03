package tornaco.apps.shortx.ui.main

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import tornaco.apps.shortx.core.util.Logger

class CheckForUpdate(
    private val context: Context,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>
) {

    private val logger = Logger("CheckForUpdate")

    operator fun invoke() {
    }
}