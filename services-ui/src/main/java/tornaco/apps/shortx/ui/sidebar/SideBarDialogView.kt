@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.RemixIcon
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
    Box(
        modifier = Modifier
            .width(BAR_W.dp)
            .height(BAR_H.dp)
    ) {
        val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        VerticalFloatingToolbar(
            // Always expanded as the toolbar is bottom-centered. We will use a
            // FloatingToolbarScrollBehavior to hide both the toolbar and its FAB on scroll.
            expanded = true,
            floatingActionButton = {
                // Match the FAB to the vibrantColors. See also StandardFloatingActionButton.
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = { /* doSomething() */ },
                ) {
                    RemixIcon(Remix.System.add_line)
                }
            },
            colors = vibrantColors,
            content = {
                IconButton(onClick = { /* doSomething() */ }) {
                    RemixIcon(Remix.System.check_line)
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    RemixIcon(Remix.System.check_line)
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    RemixIcon(Remix.System.check_line)
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    RemixIcon(Remix.System.check_line)
                }
            },
        )
    }
}