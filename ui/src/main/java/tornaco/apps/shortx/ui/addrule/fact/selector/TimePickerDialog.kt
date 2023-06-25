@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

class TimePickerDialogState(val onSelected: (LocalTime) -> Unit) : CommonDialogState() {
    internal val logger = Logger("TimePickerDialogState")
}

@Composable
fun rememberTimePickerDialogState(
    onSelected: (LocalTime) -> Unit
): TimePickerDialogState {
    return remember {
        TimePickerDialogState(onSelected)
    }
}

@Composable
fun TimePickerDialog(dialogState: TimePickerDialogState) {
    val pickState = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    if (dialogState.isShowing) {
        DialogContainer(
            onCancel = { dialogState.dismiss() },
            onConfirm = {
                val time = LocalTime.of(pickState.hour, pickState.minute)
                dialogState.logger.i("Entered time: $time")
                dialogState.dismiss()
                dialogState.onSelected(time)
            },
        ) {
            TimePicker(state = pickState)
        }
    }
}

@Composable
private fun DialogContainer(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DialogTitle(text = title)
                LargeSpacer()
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text(stringResource(id = android.R.string.cancel)) }
                    TextButton(
                        onClick = onConfirm
                    ) { Text(stringResource(id = android.R.string.ok)) }
                }
            }
        }
    }
}