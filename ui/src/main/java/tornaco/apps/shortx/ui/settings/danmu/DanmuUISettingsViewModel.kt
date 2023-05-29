/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.ui.settings.danmu

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import javax.inject.Inject

data class UISettingsState(val settings: UISettings)

data class UISettings(
    val alpha: Float = 0f,
    val backgroundColor: Int = 0,
    val textColor: Int = 0,
    val textSizeSp: Int = 0,
    val duration: Long = 0,
)

fun DanmuUISettings.toUISettings() = UISettings(
    alpha = alpha,
    backgroundColor = backgroundColor,
    textColor = textColor,
    textSizeSp = textSizeSp,
    duration = duration,
)

fun UISettings.toDanmuUISettings(): DanmuUISettings =
    DanmuUISettings.newBuilder().setAlpha(alpha).setBackgroundColor(backgroundColor)
        .setTextColor(textColor).setTextSizeSp(textSizeSp).setDuration(duration).build()

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DanmuUISettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    StateViewModel<UISettingsState>(
        {
            UISettingsState(
                shortXManager.getDanmuUISettings().toUISettings()
            )
        }
    ) {

    private var previewJob: Job? = null
    private val logger = tornaco.apps.shortx.core.util.Logger("DanmuUISettingsViewModel")

    fun updateUISettings(settings: UISettings) {
        logger.d("updateUISettings $settings")
        previewJob?.cancel()
        updateState {
            copy(settings = settings)
        }
        shortXManager.setDanmuUISettings(settings.toDanmuUISettings())
        previewJob = viewModelScope.launch {
            delay(300)
            shortXManager.showDanmu("Hello ShortX, Awesome", "app://${BuildProp.APP_PACKAGE_NAME}")
        }
    }
}