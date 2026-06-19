package tornaco.apps.shortx.ui.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun EvaRecordSetting(id: String) {
    val scope = rememberCoroutineScope()
    var currentEnabled by remember(id) {
        mutableStateOf(shortXManager.isEvaluateRecordEnabled(id))
    }
    SwitchBar(
        text = LocalI18N.current[I18N_R.UI_SETTINGS_EVA_RECORD_ENABLED],
        isChecked = currentEnabled,
        onCheckChange = {
            scope.launch {
                shortXManager.setEvaluateRecordEnabled(id, it)
                currentEnabled = shortXManager.isEvaluateRecordEnabled(id)
            }
        }
    )
}