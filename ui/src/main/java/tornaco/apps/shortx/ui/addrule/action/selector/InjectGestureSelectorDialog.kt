@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.math.BigDecimal
import java.math.RoundingMode


class InjectGestureRecordDialogState(
    val onSelected: (Action.InjectGestureRecording) -> Unit
) : CommonDialogState() {
    var selectedGesture: GestureRecord? by mutableStateOf(null)
    var speed by mutableStateOf(1f)
    var showGesturePathView by mutableStateOf(false)

    fun show(current: Action.InjectGestureRecording? = null) {
        this.selectedGesture = current?.gestureRecord
        this.speed = current?.speed ?: 1f
        this.showGesturePathView = current?.showGesturePathView ?: false
        show()
    }
}

@Composable
fun rememberInjectGestureRecordDialogState(
    onSelected: (Action.InjectGestureRecording) -> Unit
): InjectGestureRecordDialogState {
    return remember {
        InjectGestureRecordDialogState(onSelected)
    }
}

@Composable
fun InjectGestureRecordSelectorDialog(state: InjectGestureRecordDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            InjectGestureRecordSelector(state)
        }
    }
}

@Composable
private fun InjectGestureRecordSelector(dialogState: InjectGestureRecordDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.gesture.record.inject"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.selectedGesture != null) {
                                    dialogState.onSelected(
                                        Action.InjectGestureRecording(
                                            gestureRecord = dialogState.selectedGesture!!,
                                            speed = dialogState.speed,
                                            showGesturePathView = dialogState.showGesturePathView
                                        )
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        },
                    )
                }) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val gestureSelectorState = rememberGestureRecordDialogState(onSelected = {
                        dialogState.selectedGesture = it
                    })
                    GestureRecordSelectorDialog(state = gestureSelectorState)

                    FieldTitle(text = LocalI18N.current["ui.action.gesture.record.select"])
                    MediumSpacer()
                    FilledTonalButton(onClick = {
                        gestureSelectorState.show()
                    }) {
                        Text(
                            text = dialogState.selectedGesture?.label
                                ?: LocalI18N.current["ui.common.unselected"]
                        )
                    }

                    StandardSpacer()

                    FieldTitle(text = LocalI18N.current["ui.action.gesture.record.inject.speed"])
                    MediumSpacer()
                    Text(text = "${dialogState.speed} x")
                    Slider(
                        valueRange = 0.1f..10f,
                        value = dialogState.speed,
                        onValueChange = {
                            dialogState.speed = BigDecimal(it.toString())
                                .setScale(1, RoundingMode.HALF_DOWN)
                                .toFloat()
                        })

                    StandardSpacer()
                    SwitchRow(
                        text = LocalI18N.current["ui.action.gesture.record.inject.show.path"],
                        isChecked = dialogState.showGesturePathView,
                        onCheckChange = {
                            dialogState.showGesturePathView = it
                        })
                }
            }
        }
    }

}