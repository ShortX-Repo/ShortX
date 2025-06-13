@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.dialog.ShadowDialogView

private const val COLOR_ICON_SIZE = 50
private const val COLOR_ICON_PADDING = 8
private const val BAR_W = 100
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
) : ShadowDialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        Bar()
    }


    @Composable
    private fun Bar() {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(BAR_H.dp)
        ) {
            action.buttonSettingsList.forEach { setting ->
                ColoredIcon(
                    size = COLOR_ICON_SIZE.dp,
                    modifier = Modifier.padding(COLOR_ICON_PADDING.dp),
                    onClick = {
                    }
                ) {
                    RemixIcon(remixName = setting.icon)
                }
            }
        }
    }
}
