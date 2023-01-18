package tornaco.apps.shortx.ui.base.timepicker


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.ColorDefaults
import java.time.LocalTime


@Composable
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
                    Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                selectedTextStyle = TextStyle(
                    Color.Black,
                    fontSize = 18.sp,
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