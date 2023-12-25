package tornaco.apps.shortx.ui.demo

import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.services.audio.RingtoneLoader
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.composeLogger


class PickRingtoneActivity : BaseActivity() {
    @Composable
    override fun Content() {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            RingtoneLoader(context).load(RingtoneManager.TYPE_ALARM).forEach {
                composeLogger.d("TYPE_ALARM: $it")
            }
            RingtoneLoader(context).load(RingtoneManager.TYPE_NOTIFICATION).forEach {
                composeLogger.d("TYPE_NOTIFICATION: $it")
            }
            RingtoneLoader(context).load(RingtoneManager.TYPE_RINGTONE).forEach {
                composeLogger.d("TYPE_RINGTONE: $it")
            }

        }
    }
}