package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.selector.ScreenFlashSelectorViewModel.Companion.FALLBACK_COLOR
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.util.toComposeColor
import javax.inject.Inject

@AndroidEntryPoint
class ScreenFlashSelectorActivity : BaseSelectorActivity<Action.ScreenFlash>() {
    @Composable
    override fun BaseSelectorActivity<Action.ScreenFlash>.SelectorContent(currentData: Action.ScreenFlash?) {
        Selector(currentAction = currentData)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.ScreenFlash>.Selector(currentAction: Action.ScreenFlash?) {
    val vm = hiltViewModel<ScreenFlashSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.screen.flash"],
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

            FieldTitle(text = LocalI18N.current["ui.action.screen.flash.color"])
            SmallSpacer()
            val colorPickerState = remember(state) {
                ColorPickerDialogState(
                    colorInt = state.color.toIntOrNull() ?: FALLBACK_COLOR,
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
                backgroundColor = (state.color.toIntOrNull() ?: FALLBACK_COLOR).toComposeColor(),
                onClick = {
                    colorPickerState.show()
                }
            ) {

            }

            SectionSpacer()

            FieldTitle(text = LocalI18N.current["ui.action.screen.flash.times"])
            SmallSpacer()
            ShortXTextField(value = state.times,
                onValueChange = {
                    vm.setTimes(it)
                })
        }
    }
}


data class ScreenFlashSelectorState(
    val id: String = defaultNewActionId(),
    val color: String = "$FALLBACK_COLOR",
    val times: String = "2"
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ScreenFlashSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ScreenFlashSelectorState>(
        context, {
            ScreenFlashSelectorState()
        }
    ) {
    companion object {
        const val FALLBACK_COLOR = 0x55FF4018
    }

    fun initFrom(settings: Action.ScreenFlash?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    color = it.colorInt,
                    times = it.times
                )
            }
        }
    }

    fun setColor(color: Int) {
        updateState {
            copy(color = color.toString())
        }
    }

    fun setTimes(times: String) {
        updateState {
            copy(times = times)
        }
    }

    fun toActionOrNull(): Action.ScreenFlash {
        return Action.ScreenFlash(
            id = state.value.id,
            colorInt = state.value.color,
            times = state.value.times
        )
    }
}