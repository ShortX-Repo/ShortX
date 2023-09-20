package tornaco.apps.shortx.ui.base.timepicker


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.ColorDefaults
import java.time.LocalTime


@Composable
@Deprecated("Use TimePickerDialog")
fun BottomTimePicker(
    currentTime: LocalTime? = null,
    is24TimeFormat: Boolean,
    onTimeChanged: (LocalTime) -> Unit
) {

    var time by remember { mutableStateOf(currentTime ?: LocalTime.now()) }

    PickerContainer(
        modifier = Modifier.padding(18.dp),
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        cornerRadius = 16.dp,
        fadingEdgeLength = 60.dp
    ) {
        TimePicker(
            modifier = Modifier.height(130.dp),
            itemHeight = 40.dp,
            is24TimeFormat = is24TimeFormat,
            itemStyles = ItemStyles(
                defaultTextStyle = TextStyle(
                    MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                selectedTextStyle = TextStyle(
                    MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            ),
            onTimeChanged = {
                time = it
                onTimeChanged(it)
            },
            currentTime = time
        )
    }
}