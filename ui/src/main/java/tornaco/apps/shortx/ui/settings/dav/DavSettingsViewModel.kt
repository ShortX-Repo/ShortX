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

package tornaco.apps.shortx.ui.settings.dav

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import javax.inject.Inject

data class DavSettingsState(val profiles: List<WebDavProfile> = emptyList())

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DavSettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    StateViewModel<DavSettingsState, Unit>(
        { DavSettingsState() }
    ) {
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState {
                    copy(profiles = shortXManager.getAllWebDavProfiles())
                }
            }
        }
    }

    fun addProfile(profile: WebDavProfile) {
        shortXManager.addWebDavProfile(profile)
        loadData()
    }

    fun removeProfile(profileId: String) {
        shortXManager.removeWebDavProfile(profileId)
        loadData()
    }
}