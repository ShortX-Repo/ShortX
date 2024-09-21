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

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.settings.danmu.ItemCard
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class StatusBarClipUISettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, StatusBarClipUISettingsActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        require(LVLStateHolder.collectAsState().value.isSubscribed)
        StatusBarClipUISettingsScreen {
            finish()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusBarClipUISettingsScreen(back: () -> Unit) {
    val viewModel = hiltViewModel<StatusBarClipUISettingsViewModel>()
    val state by viewModel.state.collectAsState()

    ShortXTheme {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                ShortXMediumTopAppBar(
                    title = {
                        Text(
                            text = LocalI18N.current["ui.settings.statusbar.chip"]
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            back()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                ItemCard("Padding Start") {
                    Slider(value = state.settings.paddingStart.toFloat(),
                        valueRange = 0f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setPaddingStart(it.toInt())
                                    .build(),
                                previewText = "Hello"
                            )
                        })
                }
                StandardSpacer()

                ItemCard("Padding End") {
                    Slider(value = state.settings.paddingEnd.toFloat(),
                        valueRange = 0f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setPaddingEnd(it.toInt())
                                    .build(),
                                previewText = "Hello"
                            )
                        })
                }
                StandardSpacer()

                ItemCard("Padding Top") {
                    Slider(value = state.settings.paddingTop.toFloat(),
                        valueRange = 0f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setPaddingTop(it.toInt())
                                    .build(),
                                previewText = "Hello"
                            )
                        })
                }
                StandardSpacer()

                ItemCard("Padding Bottom") {
                    Slider(value = state.settings.paddingBottom.toFloat(),
                        valueRange = 0f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setPaddingBottom(it.toInt())
                                    .build(),
                                previewText = "Hello"
                            )
                        })
                }
                StandardSpacer()

                ItemCard("Text size") {
                    Slider(value = state.settings.fontSize.toFloat(),
                        valueRange = 3f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setFontSize(it.toInt())
                                    .build(),
                                previewText = "Hello"
                            )
                        })
                }
                StandardSpacer()

                ItemCard("Icon size") {
                    Slider(value = state.settings.iconSize.toFloat(),
                        valueRange = 3f..30f,
                        onValueChange = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setIconSize(it.toInt())
                                    .build(),
                                previewIcon = "app://${BuildPropInternal.APP_PACKAGE_NAME}"
                            )
                        })
                }
                StandardSpacer()

                val bgColorPickerState = remember(state) {
                    ColorPickerDialogState(
                        colorInt = state.settings.backgroundColor,
                        withAuto = true,
                        onColorChanged = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setBackgroundColor(it)
                                    .build(),
                                previewText = "Text"
                            )
                        })
                }
                ColorPickerDialog(state = bgColorPickerState)
                ItemCard(itemLabel = "Background color") {
                    FilledTonalButton(onClick = { bgColorPickerState.show() }) {}
                }
                StandardSpacer()

                val textColorPickerState = remember(state) {
                    ColorPickerDialogState(
                        colorInt = state.settings.textColor,
                        withAuto = true,
                        onColorChanged = {
                            viewModel.updateSettings(
                                settings = StatusBarClipUiSettings.newBuilder(state.settings)
                                    .setTextColor(it)
                                    .build(),
                                previewText = "Text"
                            )
                        })
                }
                ColorPickerDialog(state = textColorPickerState)
                ItemCard(itemLabel = "Text color") {
                    FilledTonalButton(onClick = { textColorPickerState.show() }) {}
                }
                StandardSpacer()
            }
        }
    }
}