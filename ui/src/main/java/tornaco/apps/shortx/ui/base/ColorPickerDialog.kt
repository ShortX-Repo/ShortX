package tornaco.apps.shortx.ui.base


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.toColorInt
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun ColorPickerDialog(state: ColorPickerDialogState) {
    val context = LocalContext.current
    val errorMsg =
        LocalI18N.current[I18N_R.UI_COMMON_MANUALLY_INPUT_COLOR_FORMAT_ERROR]
    val showColorFormatError = {
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
    }

    val inputDialogState = rememberTextInputState(
        title = LocalI18N.current[I18N_R.UI_COMMON_MANUALLY_INPUT_COLOR],
        message = LocalI18N.current[I18N_R.UI_COMMON_MANUALLY_INPUT_COLOR_MESSAGE]
    ) {
        kotlin.runCatching {
            val colorInt = android.graphics.Color.parseColor(it)
            state.onColorChanged(colorInt)
            state.dismiss()
            composeLogger.d("$it $colorInt")
        }.onFailure {
            showColorFormatError()
        }
    }
    TextInputDialog(state = inputDialogState)

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
                        })
                    StandardSpacer()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.withAuto) {
                            ShortXTextButton(
                                modifier = Modifier,
                                onClick = {
                                    state.dismiss()
                                    state.onColorChanged.invoke(DANMU_COLOR_AUTO)
                                }) {
                                Text(
                                    text = "Auto",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        state.onAutoClicked?.let { onAutoClicked ->
                            ShortXTextButton(
                                modifier = Modifier,
                                onClick = {
                                    state.dismiss()
                                    onAutoClicked()
                                }) {
                                Text(
                                    text = "Monet",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        ShortXTextButton(
                            modifier = Modifier,
                            onClick = {
                                state.dismiss()
                                inputDialogState.show("#ff4018")
                            }) {
                            Text(
                                text = LocalI18N.current[I18N_R.UI_COMMON_MANUALLY_INPUT],
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        ShortXTextButton(
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
    val withAuto: Boolean = false,
    val onColorChanged: (Int) -> Unit,
    val onAutoClicked: (() -> Unit)? = null,
) : CommonDialogState()
