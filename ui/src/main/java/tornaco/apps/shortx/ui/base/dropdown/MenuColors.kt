package tornaco.apps.shortx.ui.base.dropdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.ui.base.ColorDefaults

/*[DropDownMenuColors]  contains color information to be used for background color and content color. */
data class DropDownMenuColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

/*[dropDownMenuColors]  composable function to set background color and content color. */
@Composable
fun dropDownMenuColors(
    backgroundColor: Color = ColorDefaults.backgroundSurfaceColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface
): DropDownMenuColors {
    return DropDownMenuColors(backgroundColor, contentColor)
}
