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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
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
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TipRow
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val EDGE_GESTURE_SEQUENCE_STEP_TIMEOUT_MS_MIN = 300
private const val EDGE_GESTURE_SEQUENCE_STEP_TIMEOUT_MS_MAX = 2000
private const val EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MIN = 100
private const val EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MAX = 1200
private const val EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MIN = 8
private const val EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MAX = 96
private const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MIN = 24
private const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MAX = 200
private const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MIN = 10
private const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MAX = 60

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
                    cornerGestureAreaRatioPercent = shortXManager.getCornerGestureRegionRadiusRatioPercent(),
                    cornerGestureMinSwipeDistanceDp = shortXManager.getCornerGestureMinSwipeDistanceDp(),
                    edgeGestureSequenceHoverDurationMs = shortXManager.getEdgeGestureSequenceHoverDurationMs(),
                    edgeGestureSequenceHoverRadiusDp = shortXManager.getEdgeGestureSequenceHoverRadiusDp(),
                    edgeGestureSequenceChildSwipeMinDistanceDp = shortXManager.getEdgeGestureSequenceChildSwipeMinDistanceDp(),
                    edgeGestureSequenceChildSwipeMaxAngleDeviationDeg = shortXManager.getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(),
                    edgeGestureSequenceChildSwipeTriggerOnRelease = shortXManager.isEdgeGestureSequenceChildSwipeTriggerOnRelease(),
                    isEdgeGestureEnabled = shortXManager.isAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number),
                    isKeyEventEnabled = shortXManager.isAppFeatureEnabled(AppFeature.AppFeature_KeyEvent.number),
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

    fun setCornerGestureRegionRadiusRatioPercent(percent: Int) {
        viewModelScope.launch {
            shortXManager.setCornerGestureRegionRadiusRatioPercent(percent)
            shortXManager.showCornerHint(
                ScreenCorner.entries.filter { it != ScreenCorner.UNRECOGNIZED }
            )
            loadState()
        }
    }

    fun setCornerGestureMinSwipeDistanceDp(dp: Int) {
        viewModelScope.launch {
            shortXManager.setCornerGestureMinSwipeDistanceDp(dp)
            loadState()
        }
    }

    fun setEdgeGestureSequenceHoverDurationMs(durationMs: Int) {
        viewModelScope.launch {
            shortXManager.setEdgeGestureSequenceHoverDurationMs(durationMs)
            loadState()
        }
    }

    fun setEdgeGestureSequenceHoverRadiusDp(radiusDp: Int) {
        viewModelScope.launch {
            shortXManager.setEdgeGestureSequenceHoverRadiusDp(radiusDp)
            loadState()
        }
    }

    fun setEdgeGestureSequenceChildSwipeMinDistanceDp(distanceDp: Int) {
        viewModelScope.launch {
            shortXManager.setEdgeGestureSequenceChildSwipeMinDistanceDp(distanceDp)
            loadState()
        }
    }

    fun setEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(angleDeg: Int) {
        viewModelScope.launch {
            shortXManager.setEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(angleDeg)
            loadState()
        }
    }

    fun setEdgeGestureSequenceChildSwipeTriggerOnRelease(enabled: Boolean) {
        viewModelScope.launch {
            shortXManager.setEdgeGestureSequenceChildSwipeTriggerOnRelease(enabled)
            loadState()
        }
    }

    fun setGestureFeatureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            shortXManager.setAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number, enabled)
            loadState()
        }
    }

    fun setKeyEventFeatureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            shortXManager.setAppFeatureEnabled(AppFeature.AppFeature_KeyEvent.number, enabled)
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
    val cornerGestureAreaRatioPercent: Int = 18,
    val cornerGestureMinSwipeDistanceDp: Int = 72,
    val edgeGestureSequenceHoverDurationMs: Int = 300,
    val edgeGestureSequenceHoverRadiusDp: Int = 24,
    val edgeGestureSequenceChildSwipeMinDistanceDp: Int = 72,
    val edgeGestureSequenceChildSwipeMaxAngleDeviationDeg: Int = 35,
    val edgeGestureSequenceChildSwipeTriggerOnRelease: Boolean = false,
    val isEdgeGestureEnabled: Boolean = false,
    val isKeyEventEnabled: Boolean = false,
    // Key
    val keyDetectorLongPressTimeoutMS: Long = 0,
)

@Composable
fun KeyAndGestureSettingsActivity.EdgeGestureSettingsSelector() {

    val vm = hiltViewModel<EdgeGestureSettingsViewModel>()
    val state by vm.state.collectAsState()
    LaunchedEffect(vm) {
        vm.loadState()
    }

    Scaffold(
        modifier = Modifier,
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
            SwitchBar(
                isChecked = state.isEdgeGestureEnabled,
                text = LocalI18N.current["ui.settings.touch.gesture"],
                onCheckChange = {
                    vm.setGestureFeatureEnabled(it)
                })
            MediumSpacer()
            TipRow(LocalI18N.current["ui.settings.touch.gesture.disable"])
            StandardSpacer()

            AnimatedVisibility(state.isEdgeGestureEnabled) {
                Column {
                    LargeTitle(text = LocalI18N.current["ui.settings.touch.gesture"])
                    MediumSpacer()
                    TipRow(LocalI18N.current["ui.settings.touch.gesture.summary"])
                    LargeSpacer()

                    GestureSettingCard(
                        title = LocalI18N.current["ui.settings.edge.gesture.group.title"],
                        summary = LocalI18N.current["ui.settings.edge.gesture.group.summary"]
                    ) {
                        SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.size"])
                        MediumSpacer()
                        Text(text = state.gestureAreaSize.toString())
                        Slider(
                            valueRange = 1f..150f,
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
                    }

                    LargeSpacer()

                    GestureSettingCard(
                        title = LocalI18N.current["ui.settings.edge.gesture.sub.group.title"],
                        summary = LocalI18N.current["ui.settings.edge.gesture.sub.group.summary"]
                    ) {
                        SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.sub.hover.duration"])
                        MediumSpacer()
                        Text(text = "${state.edgeGestureSequenceHoverDurationMs} ms")
                        TipRow(LocalI18N.current["ui.settings.edge.gesture.sub.hover.duration.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MIN.toFloat()..EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MAX.toFloat(),
                            value = state.edgeGestureSequenceHoverDurationMs.toFloat(),
                            onValueChange = {
                                vm.setEdgeGestureSequenceHoverDurationMs(it.roundToInt())
                            })
                        LargeSpacer()

                        SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.sub.hover.radius"])
                        MediumSpacer()
                        Text(text = "${state.edgeGestureSequenceHoverRadiusDp} dp")
                        TipRow(LocalI18N.current["ui.settings.edge.gesture.sub.hover.radius.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MIN.toFloat()..EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MAX.toFloat(),
                            value = state.edgeGestureSequenceHoverRadiusDp.toFloat(),
                            onValueChange = {
                                vm.setEdgeGestureSequenceHoverRadiusDp(it.roundToInt())
                            })
                        LargeSpacer()

                        SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.distance"])
                        MediumSpacer()
                        Text(text = "${state.edgeGestureSequenceChildSwipeMinDistanceDp} dp")
                        TipRow(LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.distance.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MIN.toFloat()..EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MAX.toFloat(),
                            value = state.edgeGestureSequenceChildSwipeMinDistanceDp.toFloat(),
                            onValueChange = {
                                vm.setEdgeGestureSequenceChildSwipeMinDistanceDp(it.roundToInt())
                            })
                        LargeSpacer()

                        SmallTitle(text = LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.angle"])
                        MediumSpacer()
                        Text(text = "${state.edgeGestureSequenceChildSwipeMaxAngleDeviationDeg}°")
                        TipRow(LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.angle.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MIN.toFloat()..EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MAX.toFloat(),
                            value = state.edgeGestureSequenceChildSwipeMaxAngleDeviationDeg.toFloat(),
                            onValueChange = {
                                vm.setEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(it.roundToInt())
                            })
                        LargeSpacer()

                        SwitchBar(
                            isChecked = state.edgeGestureSequenceChildSwipeTriggerOnRelease,
                            text = LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.release.trigger"],
                            onCheckChange = {
                                vm.setEdgeGestureSequenceChildSwipeTriggerOnRelease(it)
                            }
                        )
                        TipRow(LocalI18N.current["ui.settings.edge.gesture.sub.child.swipe.release.trigger.description"])
                    }

                    LargeSpacer()

                    GestureSettingCard(
                        title = LocalI18N.current["ui.settings.corner.gesture.group.title"],
                        summary = LocalI18N.current["ui.settings.corner.gesture.group.summary"]
                    ) {
                        SmallTitle(text = LocalI18N.current["ui.settings.corner.gesture.size"])
                        MediumSpacer()
                        Text(text = "${state.cornerGestureAreaRatioPercent}%")
                        TipRow(LocalI18N.current["ui.settings.corner.gesture.size.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = 6f..40f,
                            value = state.cornerGestureAreaRatioPercent.toFloat(),
                            onValueChange = {
                                vm.setCornerGestureRegionRadiusRatioPercent(it.roundToInt())
                            })
                        LargeSpacer()

                        SmallTitle(text = LocalI18N.current["ui.settings.corner.gesture.swipe.distance"])
                        MediumSpacer()
                        Text(text = "${state.cornerGestureMinSwipeDistanceDp} dp")
                        TipRow(LocalI18N.current["ui.settings.corner.gesture.swipe.distance.description"])
                        StandardSpacer()
                        Slider(
                            valueRange = 24f..200f,
                            value = state.cornerGestureMinSwipeDistanceDp.toFloat(),
                            onValueChange = {
                                vm.setCornerGestureMinSwipeDistanceDp(it.roundToInt())
                            })
                    }

                    SectionSpacer()
                    SectionSpacer()
                }
            }

            // Keys
            SwitchBar(
                isChecked = state.isKeyEventEnabled,
                text = LocalI18N.current["ui.settings.keyevent"],
                onCheckChange = {
                    vm.setKeyEventFeatureEnabled(it)
                })
            MediumSpacer()
            TipRow(LocalI18N.current["ui.settings.keyevent.disable"])
            StandardSpacer()
            AnimatedVisibility(state.isKeyEventEnabled) {
                Column {
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
                }
            }

            repeat(4) {
                SectionSpacer()
            }
        }
    }
}

val Edge.isAll
    get() = listOf(Edge.BottomAll, Edge.TopAll, Edge.LeftAll, Edge.RightAll).contains(
        this
    )

@Composable
private fun GestureSettingCard(
    title: String,
    summary: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShortXCardRoundedCornerShapeXL,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SmallTitle(text = title)
            MediumSpacer()
            TipRow(summary)
            LargeSpacer()
            content()
        }
    }
}