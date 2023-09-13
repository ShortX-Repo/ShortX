@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt

data class EdgeGestureSettings(
    val areaSize: Int,
    val swipeDistanceThreshold: Int
)

class EdgeGestureSettingsSelectorState(
    val selected: (EdgeGestureSettings) -> Unit
) : CommonDialogState() {
    var areaSize by mutableIntStateOf(shortXManager.getGestureAreaSize())
    var swipeDistanceThreshold by mutableIntStateOf(shortXManager.getGestureAreaSize())

    fun show(current: EdgeGestureSettings) {
        this.areaSize = current.areaSize
        this.swipeDistanceThreshold = current.swipeDistanceThreshold
        show()
    }
}

@Composable
fun rememberEdgeGestureSettingsSelectorState(
    selected: (EdgeGestureSettings) -> Unit
): EdgeGestureSettingsSelectorState {
    return remember {
        EdgeGestureSettingsSelectorState(selected)
    }
}

@Composable
fun EdgeGestureSettingsSelector(state: EdgeGestureSettingsSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = LocalI18N.current["ui.settings.edge.gesture"]) },
                                actions = {
                                    IconButton(onClick = {
                                        state.selected(
                                            EdgeGestureSettings(
                                                state.areaSize,
                                                state.swipeDistanceThreshold
                                            )
                                        )
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            FieldTitle(text = LocalI18N.current["ui.settings.edge.gesture.size"])
                            MediumSpacer()
                            Text(text = state.areaSize.toString())
                            Slider(
                                valueRange = 1f..88f,
                                value = state.areaSize.toFloat(),
                                onValueChange = {
                                    state.areaSize = it.roundToInt()
                                })
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(state.areaSize.dp)
                                    .background(color = MaterialTheme.colorScheme.primary)
                            )
                            SectionSpacer()

                            FieldTitle(text = LocalI18N.current["ui.settings.edge.gesture.swipe.distance"])
                            MediumSpacer()
                            Text(text = state.swipeDistanceThreshold.toString())
                            Slider(
                                valueRange = 1f..300f,
                                value = state.swipeDistanceThreshold.toFloat(),
                                onValueChange = {
                                    state.swipeDistanceThreshold = it.roundToInt()
                                })

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "\uD83D\uDC46")
                                StandardSpacer()
                                Spacer(
                                    modifier = Modifier
                                        .width(state.swipeDistanceThreshold.dp)
                                        .height(6.dp)
                                        .background(color = MaterialTheme.colorScheme.primary)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}