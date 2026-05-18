package tornaco.apps.shortx.ui.addrule.action.selector.actionsidebar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState

@AndroidEntryPoint
class ShowActionSidebarSelectorActivity : BaseSelectorActivity<Action.ShowActionSidebar>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowActionSidebar>.SelectorContent(currentData: Action.ShowActionSidebar?) {
        ShowActionSidebarSelector(currentData)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BaseSelectorActivity<Action.ShowActionSidebar>.ShowActionSidebarSelector(currentAction: Action.ShowActionSidebar?) {
    val vm = hiltViewModel<ShowActionSidebarSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) vm.resetState() else vm.initFrom(currentAction)
    }
    val state by vm.state.collectAsState()
    val sortState = rememberActionSidebarButtonSortState(buttons = { state.buttons }) { vm.applySort(it) }
    ActionSidebarButtonSortDialog(sortState)

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.show.action.sidebar"],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                if (vm.validate()) setResultAndFinish(vm.state.value.toAction())
            }) { RemixIcon(remixName = Remix.System.check_line) }
        },
    ) {
        Column(Modifier.padding(16.dp)) {
            Content(vm = vm, showSort = { sortState.show() })
        }
    }
}

@Composable
private fun ColumnScope.Content(vm: ShowActionSidebarSelectorViewModel, showSort: () -> Unit) {
    val state by vm.state.collectAsState()
    state.validationMessage?.let {
        Text(text = LocalI18N.current[it])
        StandardSpacer()
    }
    ActionSidebarPositionSelector(
        position = state.position,
        updatePosition = vm::updatePosition,
    )
    StandardSpacer()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { vm.addButton() }) { Text(LocalI18N.current["ui.action.show.add.button"]) }
        TextButton(onClick = showSort, enabled = state.buttons.size > 1) { Text(LocalI18N.current["ui.common.long.press.sort"]) }
    }
    state.buttons.forEachIndexed { index, button ->
        ButtonListItem(
            index = index,
            button = button,
            evaluatingActions = state.evaluatingActions,
            updateIcon = vm::updateIcon,
            updateText = vm::updateText,
            removeButton = vm::removeButton,
        )
    }
    SectionSpacer()
}

@Composable
private fun ActionSidebarPositionSelector(
    position: ActionSidebarPosition,
    updatePosition: (ActionSidebarPosition) -> Unit,
) {
    val positionDropdownState = rememberDropdownSelectorState()
    SmallTitle(Modifier.padding(vertical = 12.dp), LocalI18N.current["ui.action.show.action.sidebar.position"])
    Box(modifier = Modifier.fillMaxWidth()) {
        ShortXFilledTonalButton(onClick = { positionDropdownState.open() }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = LocalI18N.current[position.labelKey()])
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
        DropdownSelector(
            state = positionDropdownState,
            items = listOf(
                ActionSidebarPosition.ActionSidebarPosition_Left,
                ActionSidebarPosition.ActionSidebarPosition_Right,
                ActionSidebarPosition.ActionSidebarPosition_Top,
                ActionSidebarPosition.ActionSidebarPosition_Bottom,
            ).map {
                DropdownItem(labelLines = listOf(LocalI18N.current[it.labelKey()]), data = it)
            },
            onSelect = { updatePosition(it.data) }
        )
    }
}

@Composable
private fun ButtonListItem(
    index: Int,
    button: ActionSidebarButtonEditorState,
    evaluatingActions: List<String>,
    updateIcon: (Int, String) -> Unit,
    updateText: (Int, String) -> Unit,
    removeButton: (Int) -> Unit,
) {
    val iconLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.popupSelectorResult<MDIcon>()?.let { updateIcon(index, it.iconNameOrBase64) }
    }
    val context = LocalContext.current

    ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                MediumTitle(text = LocalI18N.current["ui.action.show.overlay.button.of", mapOf("index" to "${index + 1}")])
                IconButton(onClick = { removeButton(index) }) { RemixIcon(remixName = Remix.System.close_line) }
            }
            StandardSpacer()
            TextButton(onClick = {
                iconLauncher.launch(BaseSelectorActivity.newIntent(context, null, MDIconSelectorActivity::class.java))
            }) {
                if (button.icon.isBlank()) Text("No icon") else RemixIcon(button.icon, Modifier.size(24.dp))
            }
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = button.text,
                onValueChange = { updateText(index, it) },
                label = { Text(LocalI18N.current["ui.action.show.action.sidebar.button.label"]) },
            )
            val tapHandle = remember(button) { button.tapActionHandle }
            val tapState by tapHandle.state.collectAsState()
            ActionContent(
                header = { SmallTitle(Modifier.padding(vertical = 12.dp), LocalI18N.current["ui.actions.single.click.title"]) },
                actions = tapState.actions,
                evaluatingActions = evaluatingActions,
                warnings = tapState.actionWarningMessages,
                handler = tapHandle.asActionHandler(),
            )
            val longPressHandle = remember(button) { button.longPressActionHandle }
            val longPressState by longPressHandle.state.collectAsState()
            ActionContent(
                header = { SmallTitle(Modifier.padding(vertical = 12.dp), LocalI18N.current["ui.actions.long.click.title"]) },
                actions = longPressState.actions,
                evaluatingActions = evaluatingActions,
                warnings = longPressState.actionWarningMessages,
                handler = longPressHandle.asActionHandler(),
            )
        }
    }
}

private fun ActionSidebarPosition.labelKey(): String = when (this) {
    ActionSidebarPosition.ActionSidebarPosition_Right -> "ui.action.show.action.sidebar.position.right"
    ActionSidebarPosition.ActionSidebarPosition_Top -> "ui.action.show.action.sidebar.position.top"
    ActionSidebarPosition.ActionSidebarPosition_Bottom -> "ui.action.show.action.sidebar.position.bottom"
    else -> "ui.action.show.action.sidebar.position.left"
}
