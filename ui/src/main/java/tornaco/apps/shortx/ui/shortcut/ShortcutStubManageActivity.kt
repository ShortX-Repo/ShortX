@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.toColorInt
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIconSelectorDialog
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberMDIconSelectorState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ShortcutStubManageActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ShortcutStubManageActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ShortXTheme {
            val snackbarHostState = remember { SnackbarHostState() }

            val createApkDialogState = rememberCreateShortcutStubDialogState()
            CreateShortcutStubDialog(state = createApkDialogState)

            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                val i18N = LocalI18N.current
                Scaffold(modifier = Modifier,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            createApkDialogState.show()
                        }) {
                            RemixIcon(remixName = Remix.System.add_line)
                        }
                    },
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = i18N["ui.settings.audio.record"]) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {

                            }
                        )
                    }) { paddingValues ->

                }
            }
        }
    }
}

@Composable
fun rememberCreateShortcutStubDialogState(
): CreateShortcutStubDialogState {
    return remember {
        CreateShortcutStubDialogState()
    }
}

class CreateShortcutStubDialogState : CommonDialogState() {
    var appLabel by mutableStateOf("")
    var appPkgName by mutableStateOf("")
    var appIcon by mutableStateOf("")
    var appIconTintColor by mutableIntStateOf(-50331649)
    var appIconBgColor by mutableIntStateOf(-66238482)

    fun toShortcutStubInfo(): ShortcutStubInfo {
        return ShortcutStubInfo(
            appLabel = appLabel,
            appPkgName = appPkgName,
            appIcon = appIcon,
            appIconTintColor = appIconTintColor,
            appIconBgColor = appIconBgColor
        )
    }
}


@Composable
fun CreateShortcutStubDialog(
    state: CreateShortcutStubDialogState,
) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            CreateShortcutStubContent(state)
        }
    }
}

@Composable
fun CreateShortcutStubContent(
    state: CreateShortcutStubDialogState,
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val mdIconSelectorState = rememberMDIconSelectorState(onSelected = {
                        state.appIcon = it
                    })
                    MDIconSelectorDialog(state = mdIconSelectorState)

                    val appIconColorPickerState = remember(state) {
                        ColorPickerDialogState(
                            colorInt = state.appIconTintColor,
                            onColorChanged = {
                                state.appIconTintColor = it
                            })
                    }
                    ColorPickerDialog(state = appIconColorPickerState)

                    val appIconBgColorPickerState = remember(state) {
                        ColorPickerDialogState(
                            colorInt = state.appIconBgColor,
                            onColorChanged = {
                                state.appIconBgColor = it
                            })
                    }
                    ColorPickerDialog(state = appIconBgColorPickerState)


                    ColoredIcon(color = Color(state.appIconBgColor)) {
                        RemixIcon(remixName = state.appIcon, tint = Color(state.appIconTintColor))
                    }
                    SectionSpacer()

                    FieldTitle(text = "Label")
                    MediumSpacer()
                    ShortXTextField(value = state.appLabel, onValueChange = { state.appLabel = it })
                    StandardSpacer()

                    FieldTitle(text = "Icon")
                    MediumSpacer()
                    Button(onClick = { mdIconSelectorState.show() }) {
                        Text(text = "Pick icon")
                    }
                    StandardSpacer()

                    FieldTitle(text = "Icon tint color")
                    MediumSpacer()
                    Button(onClick = { appIconColorPickerState.show() }) {
                        Text(text = "Pick color")
                    }
                    StandardSpacer()

                    FieldTitle(text = "Icon background color")
                    MediumSpacer()
                    Button(onClick = { appIconBgColorPickerState.show() }) {
                        Text(text = "Pick color")
                    }
                    StandardSpacer()
                }
            }
        }
    }
}


@Composable
private fun ColorPickerDialog(state: ColorPickerDialogState) {
    if (state.isShowing) {
        var colorValue by remember {
            mutableIntStateOf(state.colorInt)
        }
        Dialog(properties = DialogProperties(dismissOnClickOutside = false), onDismissRequest = {
            state.dismiss()
        }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClassicColorPicker(modifier = Modifier.height(180.dp),
                        color = Color(state.colorInt),
                        showAlphaBar = true,
                        onColorChanged = { color: HsvColor ->
                            colorValue = color.toColorInt()
                            composeLogger.d("onColorChanged: $colorValue")
                        })
                    StandardSpacer()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            modifier = Modifier,
                            onClick = {
                                state.dismiss()
                                state.onColorChanged.invoke(colorValue)
                            }) {
                            Text(
                                text = stringResource(id = android.R.string.ok),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

        }
    }
}

class ColorPickerDialogState(
    val colorInt: Int,
    val onColorChanged: (Int) -> Unit,
) : CommonDialogState()