package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
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
import tornaco.apps.shortx.core.proto.common.WindowInsetType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SwitchRow

@AndroidEntryPoint
class ShowHideInsetsSelectorActivity : BaseSelectorActivity<Action.ShowHideInsets>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowHideInsets>.SelectorContent(currentData: Action.ShowHideInsets?) {
        Selector(currentData)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.ShowHideInsets>.Selector(currentAction: Action.ShowHideInsets?) {
    val vm = hiltViewModel<ShowHideInsetsViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.show.hide.insets"],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(
                    Action.ShowHideInsets(
                        isHide = state.isHide,
                        types = state.types
                    )
                )
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    )
    {
        Column(Modifier.padding(horizontal = 16.dp)) {
            SwitchRow(
                text = if (state.isHide) {
                    LocalI18N.current["ui.action.show.hide.insets.hide"]
                } else {
                    LocalI18N.current["ui.action.show.hide.insets.show"]
                },
                isChecked = state.isHide,
                onCheckChange = {
                    vm.setIsHide(!state.isHide)
                }
            )

            LargeSpacer()

            listOf(
                WindowInsetType.WindowInsetType_StatusBar,
                WindowInsetType.WindowInsetType_NavBar,
                WindowInsetType.WindowInsetType_Ime,
                WindowInsetType.WindowInsetType_displayCutout,
            ).forEach { item ->
                ListItem(
                    titleLines = listOf(LocalI18N.current[item.labelKey()]),
                    action1 = {
                        Checkbox(checked = state.types.contains(item), onCheckedChange = {
                            vm.addOrRemoveType(item, !state.types.contains(item))
                        })
                    })
            }
        }
    }
}