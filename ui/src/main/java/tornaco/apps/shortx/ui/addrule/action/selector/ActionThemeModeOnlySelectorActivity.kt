package tornaco.apps.shortx.ui.addrule.action.selector

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ActionThemeModeOnlySelectorActivity : BaseSelectorActivity<Action>() {
    @Composable
    override fun BaseSelectorActivity<Action>.SelectorContent(currentData: Action?) {
        Selector(currentData)
    }
}

data class ActionThemeModeOnlyState(
    val action: Action? = null,
    val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
)

@HiltViewModel
class ActionThemeModeOnlySelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ActionThemeModeOnlyState>(context, { ActionThemeModeOnlyState() }) {

    fun initFrom(action: Action?) {
        updateState {
            copy(
                action = action,
                themeMode = action?.extractThemeMode() ?: UiThemeMode.UiThemeMode_System,
            )
        }
    }

    fun updateThemeMode(themeMode: UiThemeMode) {
        updateState { copy(themeMode = themeMode) }
    }

    fun buildAction(): Action {
        val action = requireNotNull(state.value.action) { "Action is required" }
        return action.withThemeMode(state.value.themeMode)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BaseSelectorActivity<Action>.Selector(currentAction: Action?) {
    val vm = hiltViewModel<ActionThemeModeOnlySelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.settings.theme.mode"],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = { setResultAndFinish(vm.buildAction()) }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    ) {
        Column(Modifier.padding(16.dp)) {
            ActionThemeModeSelector(themeMode = state.themeMode) {
                vm.updateThemeMode(it)
            }
            SectionSpacer()
        }
    }
}

private fun Action.extractThemeMode(): UiThemeMode {
    return when (this) {
        is Action.EnableUniversalCopy -> themeMode
        is Action.EnableViewIdViewer -> themeMode
        is Action.ShowDrawBoard -> themeMode
        is Action.ShowClipboardView -> themeMode
        is Action.AreaScreenshot -> themeMode
        is Action.SelectScreenArea -> themeMode
        else -> UiThemeMode.UiThemeMode_System
    }
}

private fun Action.withThemeMode(themeMode: UiThemeMode): Action {
    return when (this) {
        is Action.EnableUniversalCopy -> copy(themeMode = themeMode)
        is Action.EnableViewIdViewer -> copy(themeMode = themeMode)
        is Action.ShowDrawBoard -> copy(themeMode = themeMode)
        is Action.ShowClipboardView -> copy(themeMode = themeMode)
        is Action.AreaScreenshot -> copy(themeMode = themeMode)
        is Action.SelectScreenArea -> copy(themeMode = themeMode)
        else -> this
    }
}
