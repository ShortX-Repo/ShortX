package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.core.i18n.I18N_R

class SetWallpaperState(
    val onSelected: (Action.SetWallpaper) -> Unit
) : CommonDialogState() {

    var currentValue by mutableStateOf("")
    var crop by mutableStateOf(false)

    fun show(initialValue: Action.SetWallpaper? = null) {
        this.currentValue = initialValue?.url ?: ""
        this.crop = initialValue?.crop ?: false
        show()
    }

}

@Composable
fun rememberSetWallpaperState(
    onSelected: (Action.SetWallpaper) -> Unit
): SetWallpaperState {
    return remember {
        SetWallpaperState(
            onSelected
        )
    }
}

@Composable
fun SetWallpaperSelectorDialog(state: SetWallpaperState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current[I18N_R.UI_ACTION_SET_WALLPAPER])
        }, buttons = {
            ShortXTextButton(
                enabled = state.currentValue.isNotEmpty(),
                onClick = {
                    state.onSelected(
                        Action.SetWallpaper(
                            url = state.currentValue,
                            crop = state.crop
                        )
                    )
                    state.dismiss()
                }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {

                FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_SET_WALLPAPER])
                TipBody(text = LocalI18N.current[I18N_R.UI_ACTION_SET_WALLPAPER_URL])
                ShortXTextField(
                    value = state.currentValue,
                    onValueChange = {
                        state.currentValue = it
                    })

                StandardSpacer()


                FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_SET_WALLPAPER_CROP])
                TipBody(text = LocalI18N.current[I18N_R.UI_ACTION_SET_WALLPAPER_CROP_SUMMARY])
                ShortXSwitch(checked = state.crop, onCheckedChange = {
                    state.crop = it
                })

            }
        })
    }
}
