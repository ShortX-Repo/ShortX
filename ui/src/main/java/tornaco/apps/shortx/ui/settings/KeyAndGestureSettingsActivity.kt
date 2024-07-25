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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
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
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LargeTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@AndroidEntryPoint
class KeyAndGestureSettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, KeyAndGestureSettingsActivity::class.java)
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
    ShortXContextViewModel<KeyAndGestureSettings>(context, { KeyAndGestureSettings() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState {
                copy(
                    gestureAreaSize = shortXManager.getGestureAreaSize(),
                    swipeDistanceThreshold = shortXManager.getSwipeDistanceThresholdDp(),
                    swipeLengthScale = shortXManager.getSwipeLengthScale(),
                    isEdgeGestureDisabled = shortXManager.hasHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK),
                    keyDetectorLongPressTimeoutMS = shortXManager.getKeyDetectorLongPressTimeoutMS()
                )
            }
        }
    }

    fun setAreaSize(roundToInt: Int) {
        viewModelScope.launch {
            shortXManager.setGestureAreaSize(roundToInt)
            shortXManager.showEdgeHint(
                Edge.entries.filterNot { it == Edge.UNRECOGNIZED || it.isAll }
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
        val scaledFloat = BigDecimal(float.toString())
            .setScale(1, RoundingMode.HALF_DOWN)
            .toFloat()
        viewModelScope.launch {
            shortXManager.setSwipeLengthScale(scaledFloat)
            shortXManager.showEdgeHint(
                Edge.entries.filterNot { it == Edge.UNRECOGNIZED || it.isAll }
                // Edge.entries.filter { it.isAll }
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

    fun setKeyDetectorLongPressTimeoutMS(timeout: Long) {
        viewModelScope.launch {
            shortXManager.setKeyDetectorLongPressTimeoutMS(timeout)
            loadState()
        }
    }
}

data class KeyAndGestureSettings(
    val gestureAreaSize: Int = 0,
    val swipeDistanceThreshold: Int = 0,
    val swipeLengthScale: Float = 0f,
    val isEdgeGestureDisabled: Boolean = false,
    // Key
    val keyDetectorLongPressTimeoutMS: Long = 0,
)

@Composable
fun KeyAndGestureSettingsActivity.EdgeGestureSettingsSelector() {
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
                        title = { Text(text = LocalI18N.current["ui.settings.key.and.gesture"]) },
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
                        .verticalScroll(rememberScrollState())
                ) {
                    LargeTitle(text = LocalI18N.current["ui.settings.edge.gesture"])
                    StandardSpacer()

                    SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.size"])
                    MediumSpacer()
                    Text(text = state.gestureAreaSize.toString())
                    Slider(
                        valueRange = 1f..88f,
                        value = state.gestureAreaSize.toFloat(),
                        onValueChange = {
                            vm.setAreaSize(it.roundToInt())
                        })
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(state.gestureAreaSize.dp)
                            .clip(ShortXCardRoundedCornerShapeXL)
                            .background(color = MaterialTheme.colorScheme.primary)
                    )
                    LargeSpacer()

                    SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.area.length"])
                    MediumSpacer()
                    Text(text = state.swipeLengthScale.toString())
                    Slider(
                        valueRange = 0f..1f,
                        value = state.swipeLengthScale,
                        onValueChange = {
                            vm.setSwipeLengthScale(it)
                        })
                    LargeSpacer()

                    SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.swipe.distance"])
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
                                .clip(ShortXCardRoundedCornerShapeXL)
                                .background(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    LargeSpacer()

                    SectionSpacer()
                    SectionSpacer()

                    // Keys
                    LargeTitle(text = LocalI18N.current["ui.settings.key"])
                    SmallTitle(text = LocalI18N.current["ui.settings.key.long.press.timeout"])
                    MediumSpacer()
                    Text(text = "${state.keyDetectorLongPressTimeoutMS} ms")
                    Slider(
                        valueRange = 200f..5000f,
                        value = state.keyDetectorLongPressTimeoutMS.toFloat(),
                        onValueChange = {
                            vm.setKeyDetectorLongPressTimeoutMS(it.roundToLong())
                        })

                    SectionSpacer()
                    SwitchRow(
                        text = LocalI18N.current["ui.settings.edge.gesture.disable"],
                        isChecked = state.isEdgeGestureDisabled,
                        onCheckChange = {
                            vm.setDisabled(it)
                        }
                    )
                    AnimatedVisibility(visible = state.isEdgeGestureDisabled) {
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

val Edge.isAll
    get() = listOf(Edge.BottomAll, Edge.TopAll, Edge.LeftAll, Edge.RightAll).contains(
        this
    )