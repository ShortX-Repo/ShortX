package tornaco.apps.shortx.ui.addrule.action.selector.cv

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.FindPointsByColorConfig
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.util.toComposeColor

@AndroidEntryPoint
class FindColorSelectorActivity : BaseSelectorActivity<FindPointsByColorConfig>() {
    @Composable
    override fun BaseSelectorActivity<FindPointsByColorConfig>.SelectorContent(currentData: FindPointsByColorConfig?) {
        Selector(currentAction = currentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<FindPointsByColorConfig>.Selector(currentAction: FindPointsByColorConfig?) {
    val vm = hiltViewModel<FindColorSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.find.color"],
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
            val i18N = LocalI18N.current

            FieldTitle(text = LocalI18N.current["ui.action.find.color.image"])
            SmallSpacer()
            ImageSourceSelector(state.image, i18N["ui.action.find.color.image"], true) {
                vm.setImage(it)
            }

            SectionSpacer()

            FieldTitle(text = LocalI18N.current["ui.action.find.color.color"])
            SmallSpacer()
            val colorPickerState = remember(state) {
                ColorPickerDialogState(
                    colorInt = state.color,
                    onColorChanged = {
                        vm.setColor(it)
                    })
            }
            ColorPickerDialog(state = colorPickerState)
            ShortXCard(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(64.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(20.dp)
                    ),
                backgroundColor = state.color.toComposeColor(),
                onClick = {
                    colorPickerState.show()
                }
            ) {

            }
        }
    }
}