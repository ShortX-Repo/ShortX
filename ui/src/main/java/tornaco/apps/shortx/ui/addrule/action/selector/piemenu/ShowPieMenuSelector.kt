package tornaco.apps.shortx.ui.addrule.action.selector.piemenu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer

@AndroidEntryPoint
class ShowPieMenuSelectorActivity : BaseSelectorActivity<Action.ShowPieMenu>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowPieMenu>.SelectorContent(currentData: Action.ShowPieMenu?) {
        ShowPieMenuSelector(currentData)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BaseSelectorActivity<Action.ShowPieMenu>.ShowPieMenuSelector(currentAction: Action.ShowPieMenu?) {
    val vm = hiltViewModel<ShowPieMenuSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) vm.resetState() else vm.initFrom(currentAction)
    }
    val state by vm.state.collectAsState()
    val i18N = LocalI18N.current

    ShortXAppBarScaffold(
        title = i18N.get(key = "ui.action.show.pie.menu", fallback = "PIE Menu"),
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                if (vm.validate()) setResultAndFinish(vm.state.value.toAction())
            }) { RemixIcon(remixName = Remix.System.check_line) }
        },
    ) {
        Column(Modifier.padding(16.dp)) {
            Content(vm = vm)
        }
    }
}

@Composable
private fun Content(vm: ShowPieMenuSelectorViewModel) {
    val state by vm.state.collectAsState()
    val i18N = LocalI18N.current

    state.validationMessage?.let {
        Text(text = i18N.get(key = it, fallback = it))
        StandardSpacer()
    }

    TipRow(text = i18N.get(
        key = "ui.action.show.pie.menu.tip",
        fallback = "Configure menu items with icons, labels and actions. When swiping from the edge, a radial arc menu appears at the touch point — drag to select, release to execute. Supports up to 12 items. Arc direction adapts automatically to the swipe edge."
    ))
    StandardSpacer()

    PieStyleEditor(
        pieColor = state.pieColorArgb,
        iconColor = state.iconColorArgb,
        scalePercent = state.scalePercent,
        arcSpanDegrees = state.arcSpanDegrees,
        onPieColorChange = vm::updatePieColor,
        onIconColorChange = vm::updateIconColor,
        onScaleChange = vm::updateScale,
        onArcSpanChange = vm::updateArcSpan,
    )
    StandardSpacer()

    Button(onClick = { vm.addItem() }) {
        Text(i18N.get(key = "ui.action.show.pie.menu.add.item", fallback = "Add item"))
    }
    state.items.forEachIndexed { index, item ->
        PieMenuItemEditor(
            index = index,
            item = item,
            evaluatingActions = state.evaluatingActions,
            updateIcon = vm::updateIcon,
            updateLabel = vm::updateLabel,
            removeItem = vm::removeItem,
        )
    }
}

@Composable
private fun PieStyleEditor(
    pieColor: Int,
    iconColor: Int,
    scalePercent: Int,
    arcSpanDegrees: Int,
    onPieColorChange: (Int) -> Unit,
    onIconColorChange: (Int) -> Unit,
    onScaleChange: (Int) -> Unit,
    onArcSpanChange: (Int) -> Unit,
) {
    val i18N = LocalI18N.current
    ShortXCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            MediumTitle(text = i18N.get(key = "ui.action.show.pie.menu.style", fallback = "Style"))
            StandardSpacer()

            Text(text = "${i18N.get(key = "ui.action.show.pie.menu.scale", fallback = "Scale")}: $scalePercent%")
            Slider(
                value = scalePercent.toFloat(),
                valueRange = 75f..200f,
                onValueChange = { onScaleChange(it.toInt()) },
            )
            MediumSpacer()

            Text(text = "${i18N.get(key = "ui.action.show.pie.menu.arc.span", fallback = "Arc span")}: $arcSpanDegrees°")
            Slider(
                value = arcSpanDegrees.toFloat(),
                valueRange = 60f..300f,
                onValueChange = { onArcSpanChange(it.toInt()) },
            )
            StandardSpacer()

            ColorStyleButton(
                label = i18N.get(key = "ui.action.show.pie.menu.pie.color", fallback = "Pie color"),
                color = pieColor,
                updateColor = onPieColorChange,
            )
            ColorStyleButton(
                label = i18N.get(key = "ui.action.show.pie.menu.icon.color", fallback = "Icon color"),
                color = iconColor,
                updateColor = onIconColorChange,
            )
        }
    }
}

@Composable
private fun ColorStyleButton(
    label: String,
    color: Int,
    updateColor: (Int) -> Unit,
) {
    val colorPickerState = remember(color) {
        ColorPickerDialogState(colorInt = color, onColorChanged = updateColor)
    }
    ColorPickerDialog(state = colorPickerState)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(modifier = Modifier.weight(1f), text = label)
        ShortXFilledTonalButton(
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(color)),
            liquidGlassTint = Color(color),
            onClick = { colorPickerState.show() },
        ) {}
    }
}

@Composable
private fun PieMenuItemEditor(
    index: Int,
    item: PieMenuItemEditorState,
    evaluatingActions: List<String>,
    updateIcon: (Int, String) -> Unit,
    updateLabel: (Int, String) -> Unit,
    removeItem: (Int) -> Unit,
) {
    val iconLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.popupSelectorResult<MDIcon>()?.let { updateIcon(index, it.iconNameOrBase64) }
    }
    val context = LocalContext.current
    val i18N = LocalI18N.current

    ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                MediumTitle(
                    text = i18N.get(
                        key = "ui.action.show.pie.menu.item.of",
                        fallback = "Item ${index + 1}",
                        args = mapOf("index" to "${index + 1}")
                    )
                )
                IconButton(onClick = { removeItem(index) }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }
            StandardSpacer()
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmallTitle(text = i18N.get(key = "ui.action.show.pie.menu.item.icon", fallback = "Icon"))
                MediumSpacer()
                IconButton(onClick = {
                    iconLauncher.launch(
                        BaseSelectorActivity.newIntent(context, null, MDIconSelectorActivity::class.java)
                    )
                }) {
                    if (item.icon.isBlank()) {
                        RemixIcon(Remix.Design.pencil_line, Modifier.size(24.dp))
                    } else {
                        RemixIcon(item.icon, Modifier.size(24.dp))
                    }
                }
            }
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = item.label,
                onValueChange = { updateLabel(index, it) },
                label = { Text(i18N.get(key = "ui.action.show.pie.menu.item.label", fallback = "Label")) },
            )
            StandardSpacer()
            val actionHandle = remember(item) { item.actionHandle }
            val actionState by actionHandle.state.collectAsState()
            ActionContent(
                header = {
                    SmallTitle(
                        Modifier.padding(vertical = 12.dp),
                        i18N.get(key = "ui.action.show.pie.menu.item.action", fallback = "Action")
                    )
                },
                actions = actionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = actionState.actionWarningMessages,
                handler = actionHandle.asActionHandler(),
            )
        }
    }
}
