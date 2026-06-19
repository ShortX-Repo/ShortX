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

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class DanmuUISettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, DanmuUISettingsActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        require(LVLStateHolder.collectAsState().value.isSubscribed)
        DanmuUISettingsScreen {
            finish()
        }
    }
}


@Composable
fun DanmuUISettingsScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DanmuUISettingsScreen(back = { state.dismiss() })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanmuUISettingsScreen(back: () -> Unit) {
    val viewModel = hiltViewModel<DanmuUISettingsViewModel>()
    val state by viewModel.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current[I18N_R.UI_SETTINGS_DANMU]
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

                    ItemCard("Background alpha") {
                        Text(text = "${state.settings.alpha}")
                        Slider(value = state.settings.alpha, onValueChange = {
                            viewModel.updateUISettings(state.settings.copy(alpha = it))
                        })
                    }
                    StandardSpacer()


                    val bgColorPickerState = remember(state) {
                        ColorPickerDialogState(
                            colorInt = state.settings.backgroundColor,
                            withAuto = true,
                            onColorChanged = {
                                viewModel.updateUISettings(state.settings.copy(backgroundColor = it))
                            })
                    }
                    ColorPickerDialog(state = bgColorPickerState)
                    ItemCard(itemLabel = "Background color") {
                        ShortXFilledTonalButton(colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(
                                state.settings.backgroundColor
                            )
                        ), liquidGlassTint = Color(state.settings.backgroundColor), onClick = { bgColorPickerState.show() }) {}
                    }
                    StandardSpacer()

                    ItemCard("Text size") {
                        Text(text = "${state.settings.textSizeSp}")
                        Slider(value = state.settings.textSizeSp.toFloat(),
                            steps = 32,
                            valueRange = 1f..32f,
                            onValueChange = {
                                viewModel.updateUISettings(state.settings.copy(textSizeSp = it.toInt()))
                            })
                    }
                    StandardSpacer()

                    ItemCard(itemLabel = "Duration") {
                        Text(text = "${state.settings.duration / 1000} seconds")
                        Slider(value = (state.settings.duration / 1000).toFloat(),
                            steps = 24,
                            valueRange = 1f..24f,
                            onValueChange = {
                                viewModel.updateUISettings(state.settings.copy(duration = it.toInt() * 1000L))
                            })
                    }
                    StandardSpacer()


                    val textColorPickerState = remember(state) {
                        ColorPickerDialogState(
                            colorInt = state.settings.textColor,
                            withAuto = true,
                            onColorChanged = {
                                viewModel.updateUISettings(state.settings.copy(textColor = it))
                            })
                    }
                    ColorPickerDialog(state = textColorPickerState)
                    ItemCard(itemLabel = "Text color") {
                        ShortXFilledTonalButton(colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(
                                state.settings.textColor
                            )
                        ), liquidGlassTint = Color(state.settings.textColor), onClick = { textColorPickerState.show() }) {}
                    }
                    StandardSpacer()
                }
            }
        }
    }
}

@Composable
fun ItemCard(itemLabel: String, content: @Composable () -> Unit) {
    ShortXCard(
        modifier = Modifier,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = itemLabel,
                style = MaterialTheme.typography.titleMedium
            )
            StandardSpacer()
            content()
        }
    }
}