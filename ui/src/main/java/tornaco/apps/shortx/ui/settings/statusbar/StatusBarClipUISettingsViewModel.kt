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

package tornaco.apps.shortx.ui.settings.statusbar

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.action.ShowStatusBarChip
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import javax.inject.Inject

data class UISettingsState(val settings: StatusBarClipUiSettings = StatusBarClipUiSettings.getDefaultInstance())

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class StatusBarClipUISettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    StateViewModel<UISettingsState, Unit>(
        {
            UISettingsState(
                shortXManager.getStatusbarClipSettings()
            )
        }
    ) {
    private val evaluateContext = EvaluateContext.newBuilder()
        .setSrcId("Test")
        .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
        .setSrcTitle("Testing")
        .build()

    private var previewJob: Job? = null
    private val logger = tornaco.apps.shortx.core.util.Logger("StatusBarClipUISettingsViewModel")

    fun updateSettings(
        settings: StatusBarClipUiSettings,
        previewText: String = "",
        previewIcon: String = ""
    ) {
        logger.d("updateUISettings $settings")
        previewJob?.cancel()
        updateState {
            copy(settings = settings)
        }
        shortXManager.setStatusbarClipSettings(settings)
        previewJob = viewModelScope.launch {
            shortXManager.executeAction(
                evaluateContext,
                ShowStatusBarChip.newBuilder()
                    .setId("test")
                    .setText(previewText)
                    .setIcon(previewIcon)
                    .build().pack_()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.executeAction(
            evaluateContext,
            ShowStatusBarChip.newBuilder()
                .build().pack_()
        )
    }
}