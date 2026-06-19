package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.ImageScaleType
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.ActionThemeModeSelector
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import javax.inject.Inject
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class DisplayImageSelectorActivity : BaseSelectorActivity<Action.DisplayImage>() {
    @Composable
    override fun BaseSelectorActivity<Action.DisplayImage>.SelectorContent(currentData: Action.DisplayImage?) {
        Selector(currentAction = currentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.DisplayImage>.Selector(currentAction: Action.DisplayImage?) {
    val vm = hiltViewModel<DisplayImageSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()
    val i18N = LocalI18N.current

    ShortXAppBarScaffold(
        title = LocalI18N.current[I18N_R.UI_ACTION_DISPLAY_IMAGE],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(vm.toActionOrNull())
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    )
    {
        Column(Modifier.padding(horizontal = 16.dp)) {
            FieldTitle(
                modifier = Modifier.fillMaxWidth(),
                text = LocalI18N.current[I18N_R.UI_ACTION_DISPLAY_IMAGE_URL]
            )
            TinySpacer()
            ShortXTextField(value = state.url,
                onValueChange = {
                    vm.setUrl(it)
                })

            SectionSpacer()

            val srcMenuState = rememberMenuDialogState<ImageScaleType>(
                title = i18N[I18N_R.UI_ACTION_DISPLAY_IMAGE_SCALE_TYPE],
                menuItems = ImageScaleType.entries.filterNot { it == ImageScaleType.UNRECOGNIZED }
                    .map {
                        MenuDialogItem(
                            id = it.name,
                            title = it.displayName
                        )
                    },
                onItemSelected = { _, id ->
                    val type = ImageScaleType.valueOf(id)
                    vm.setScaleType(type)
                }
            )
            MenuDialog(state = srcMenuState)

            FieldTitle(
                modifier = Modifier.fillMaxWidth(),
                text = LocalI18N.current[I18N_R.UI_ACTION_DISPLAY_IMAGE_SCALE_TYPE]
            )
            TinySpacer()
            FilledTonalButton(onClick = {
                srcMenuState.show()
            }) {
                Text(text = state.scaleType.displayName)
            }

            SectionSpacer()

            ActionThemeModeSelector(themeMode = state.themeMode) {
                vm.setThemeMode(it)
            }
        }

    }
}

val ImageScaleType.displayName get() = this.name.replace("IMAGE_SCALE_TYPE_", "").replace("_", " ")

data class DisplayImageSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val scaleType: ImageScaleType = ImageScaleType.IMAGE_SCALE_TYPE_FIT_CENTER,
    val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DisplayImageSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<DisplayImageSelectorState>(
        context, {
            DisplayImageSelectorState()
        }
    ) {

    fun initFrom(settings: Action.DisplayImage?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    url = it.imageUrl,
                    scaleType = it.scaleType,
                    themeMode = it.themeMode,
                )
            }
        }
    }

    fun toActionOrNull(): Action.DisplayImage {
        return Action.DisplayImage(
            id = state.value.id,
            imageUrl = state.value.url,
            scaleType = state.value.scaleType,
            themeMode = state.value.themeMode,
        )
    }

    fun setUrl(it: String) {
        updateState {
            copy(url = it)
        }
    }

    fun setScaleType(it: ImageScaleType) {
        updateState {
            copy(scaleType = it)
        }
    }

    fun setThemeMode(it: UiThemeMode) {
        updateState { copy(themeMode = it) }
    }
}
