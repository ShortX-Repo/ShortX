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

package tornaco.apps.shortx.ui.settings.log.hook

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.settings.log.LogState
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MethodHookLogViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {
    private val _state =
        MutableStateFlow(
            LogState(
                isLoading = true
            )
        )
    val state = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(500)

            kotlin.runCatching {
                val logPath = shortXManager.getMethodHookLogPath()
                val fd = shortXManager.getMethodHookLogFD()
                val lines = withContext(Dispatchers.IO) {
                    requireNotNull(fd) { "Method hook log file is unavailable." }.use { pfd ->
                        FileInputStream(pfd.fileDescriptor).use { fis ->
                            BufferedReader(InputStreamReader(fis)).use { br ->
                                br.readLines()
                            }
                        }
                    }
                }
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        logs = listOf(
                            ">>>>>>>> START <<<<<<<<",
                            logPath,
                            System.lineSeparator()
                        ) + lines + listOf(">>>>>>>> END <<<<<<<<")
                    )
            }.onFailure {
                _state.value =
                    _state.value.copy(isLoading = false, logs = listOf(Log.getStackTraceString(it)))
            }

        }
    }

    fun clearLogs() {
        shortXManager.clearMethodHookLogs()
        refresh()
    }

    fun setHorizontalScrollEnabled(horizontalScrollEnabled: Boolean) {
        _state.value =
            _state.value.copy(horizontalScrollEnabled = horizontalScrollEnabled)
    }
}
