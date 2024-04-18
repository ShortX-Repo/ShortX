@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt

@AndroidEntryPoint
class EdgeGestureSettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, EdgeGestureSettingsActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        EdgeGestureSettingsSelector()
    }
}


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class EdgeGestureSettingsViewModel @javax.inject.Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<EdgeGestureSettings>(context, { EdgeGestureSettings() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState {
                copy(
                    areaSize = shortXManager.getGestureAreaSize(),
                    swipeDistanceThreshold = shortXManager.getSwipeDistanceThresholdDp(),
                    swipeLengthScale = shortXManager.getSwipeLengthScale(),
                    isDisabled = shortXManager.hasHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK)
                )
            }
        }
    }

    fun setAreaSize(roundToInt: Int) {
        viewModelScope.launch {
            shortXManager.setGestureAreaSize(roundToInt)
            shortXManager.showEdgeHint(
                listOf(
                    Edge.RightCenter,
                    Edge.BottomCenter,
                    Edge.LeftCenter,
                    Edge.TopCenter
                )
            )
            loadState()
        }
    }

    fun setSwipeDistanceThreshold(roundToInt: Int) {
        viewModelScope.launch {
            shortXManager.setSwipeDistanceThresholdDp(roundToInt)
            loadState()
        }
    }

    fun setSwipeLengthScale(float: Float) {
        viewModelScope.launch {
            shortXManager.setSwipeLengthScale(float)
            shortXManager.showEdgeHint(
                listOf(
                    Edge.RightCenter,
                    Edge.BottomCenter,
                    Edge.LeftCenter,
                    Edge.TopCenter
                )
            )
            loadState()
        }
    }

    fun setDisabled(it: Boolean) {
        viewModelScope.launch {
            if (it) {
                shortXManager.addHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK)
            } else {
                shortXManager.removeHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK)
            }
            loadState()
        }
    }
}

data class EdgeGestureSettings(
    val areaSize: Int = 0,
    val swipeDistanceThreshold: Int = 0,
    val swipeLengthScale: Float = 0f,
    val isDisabled: Boolean = false
)

@Composable
fun EdgeGestureSettingsActivity.EdgeGestureSettingsSelector() {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val vm = hiltViewModel<EdgeGestureSettingsViewModel>()
            val state by vm.state.collectAsState()
            LaunchedEffect(vm) {
                vm.loadState()
            }

            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.settings.edge.gesture"]) },
                        actions = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                finish()
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
                            vm.setAreaSize(it.roundToInt())
                        })
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(state.areaSize.dp)
                            .background(color = MaterialTheme.colorScheme.primary)
                    )
                    SectionSpacer()

                    FieldTitle(text = LocalI18N.current["ui.settings.edge.gesture.area.length"])
                    MediumSpacer()
                    Text(text = state.swipeLengthScale.toString())
                    Slider(
                        valueRange = 0f..1f,
                        value = state.swipeLengthScale,
                        onValueChange = {
                            vm.setSwipeLengthScale(it)
                        })
                    SectionSpacer()

                    FieldTitle(text = LocalI18N.current["ui.settings.edge.gesture.swipe.distance"])
                    MediumSpacer()
                    Text(text = state.swipeDistanceThreshold.toString())
                    Slider(
                        valueRange = 1f..300f,
                        value = state.swipeDistanceThreshold.toFloat(),
                        onValueChange = {
                            vm.setSwipeDistanceThreshold(it.roundToInt())
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

                    SectionSpacer()
                    SectionSpacer()
                    SwitchRow(
                        text = LocalI18N.current["ui.settings.edge.gesture.disable"],
                        isChecked = state.isDisabled,
                        onCheckChange = {
                            vm.setDisabled(it)
                        }
                    )
                    StandardSpacer()
                    AnimatedVisibility(visible = state.isDisabled) {
                        ErrorCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = LocalI18N.current["ui.settings.edge.gesture.disabled.warn"],
                            warnings = emptyList()
                        )
                    }

                }
            }
        }
    }
}