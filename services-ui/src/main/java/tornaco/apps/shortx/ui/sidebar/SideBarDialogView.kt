package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.dialog.DialogView

private const val BAR_W = 68
private const val BAR_H = 320

@SuppressLint("ViewConstructor")
class SideBarDialogView(
    private val action: ShowSideBar,
    private val contextData: Map<String, String>,
    private val localVars: LocalVars,
    private val parameters: List<FuncParameterInput>,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings = DialogUiStyleSettings.getDefaultInstance(),
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        Bar()
    }
}


@Composable
private fun Bar() {
    LazyColumn(
        modifier = Modifier
            .width(BAR_W.dp)
            .height(BAR_H.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(20) {
            ColoredIcon {
                RemixIcon(Remix.System.settings_fill)
            }
            StandardSpacer()
        }
    }
}