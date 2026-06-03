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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.settings.log.http

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.Md3ExpPullRefreshIndicator
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.settings.log.LogList
import tornaco.apps.shortx.ui.settings.log.MenuActions
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class HttpLogActivity : BaseActivity() {
    object Starter {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, HttpLogActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val viewModel = hiltViewModel<HttpLogViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.refresh()
        }

        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        ShortXTheme {
            Surface {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = {  },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                MenuActions(
                                    logs = state.logs,
                                    clearLogs = { viewModel.clearLogs() },
                                    scrollToTop = {
                                        scope.launch {
                                            listState.scrollToItem(0)
                                        }
                                    },
                                    scrollToBottom = {
                                        scope.launch {
                                            listState.scrollToItem(state.logs.size - 1)
                                        }
                                    },
                                    toggleHorizontalScroll = {
                                        viewModel.setHorizontalScrollEnabled(!state.horizontalScrollEnabled)
                                    })
                            }
                        )
                    }) { paddingValues ->
                    val refreshing = state.isLoading
                    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
                        viewModel.refresh()
                    })
                    Box(
                        Modifier
                            .fillMaxSize()
                            .pullRefresh(refreshState)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            SwitchBar(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp),
                                isChecked = state.isLogEnabled
                            ) {
                                viewModel.enableLog(it)
                            }

                            LogList(
                                horizontalScrollEnabled = state.horizontalScrollEnabled,
                                listState = listState,
                                state = state
                            )
                        }

                        Md3ExpPullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier.align(Alignment.TopCenter),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

    }

}

