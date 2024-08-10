package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO

@Composable
fun ColorPickerDialog(state: ColorPickerDialogState) {
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

                        TextButton(
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
) {
    private var _isShowing: Boolean by mutableStateOf(false)
    val isShowing get() = _isShowing

    fun show() {
        _isShowing = true
    }

    fun dismiss() {
        _isShowing = false
    }
}