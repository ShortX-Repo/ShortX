package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * https://gist.github.com/TheMelody/5681affaf39600b1f974f4db214a7b0a
 */

class FabState(
    val onCollapsed: () -> Unit = {},
    val onExpanded: () -> Unit = {},
    val onFabItemClicked: (item: MultiFabItem) -> Unit,
) {
    val currentState = mutableStateOf(MultiFabState.Collapsed)

    fun collapse() {
        currentState.value = MultiFabState.Collapsed
        onCollapsed()
    }

    fun toggle() {
        currentState.value =
            if (currentState.value == MultiFabState.Collapsed) MultiFabState.Expanded else MultiFabState.Collapsed
        if (currentState.value == MultiFabState.Collapsed) {
            onCollapsed()
        } else {
            onExpanded()
        }
    }
}

@Composable
fun rememberFabState(
    onCollapsed: () -> Unit = {},
    onExpanded: () -> Unit = {},
    onFabItemClicked: (item: MultiFabItem) -> Unit
): FabState {
    return remember {
        FabState(onCollapsed, onExpanded, onFabItemClicked)
    }
}

@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    state: FabState,
    srcIcon: ImageVector,
    showLabels: Boolean = true,
    items: List<MultiFabItem>,
) {
    val transition = updateTransition(targetState = state.currentState, label = "")
    val rotateAnim: Float by transition.animateFloat(
        transitionSpec = {
            if (targetState.value == MultiFabState.Expanded) {
                tween(durationMillis = 200)
            } else {
                tween(durationMillis = 330)
            }
        }, label = ""
    ) { state ->
        if (state.value == MultiFabState.Collapsed) 0F else -45F
    }
    val alphaAnim: Float by transition.animateFloat(transitionSpec = {
        tween(durationMillis = 200)
    }, label = "") { state ->
        if (state.value == MultiFabState.Expanded) 1F else 0F
    }
    val shrinkListAnim: MutableList<Float> = mutableListOf()
    items.forEachIndexed { index, _ ->
        val shrinkAnim by transition.animateFloat(targetValueByState = { state ->
            when (state.value) {
                MultiFabState.Collapsed -> 5F
                MultiFabState.Expanded -> (index + 1) * 60F + if (index == 0) 5F else 0F
            }
        }, label = "", transitionSpec = {
            if (targetState.value == MultiFabState.Expanded) {
                tween(durationMillis = 200)
            } else {
                tween(durationMillis = 300)
            }
        })
        shrinkListAnim.add(index, shrinkAnim)
    }
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        bottom = shrinkListAnim[index].dp,
                        top = 6.dp,
                        end = 6.dp
                    )
                    .alpha(animateFloatAsState(alphaAnim, label = "Alpha").value)
            ) {
                if (showLabels) {
                    Text(
                        item.label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clip(ShortXCardRoundedCornerShape)
                            .background(color = MaterialTheme.colorScheme.secondary)
                            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                FloatingActionButton(
                    modifier = Modifier.size(46.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        state.onFabItemClicked(item)
                        state.onCollapsed()
                        state.collapse()
                    }
                ) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = item.icon,
                    )
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier,
            onClick = {
                state.toggle()
            }) {
            Icon(
                imageVector = srcIcon,
                modifier = Modifier.rotate(rotateAnim),
                contentDescription = null
            )
        }
    }
}

class MultiFabItem(
    val id: String,
    val icon: String,
    val label: String,
)

enum class MultiFabState {
    Collapsed,
    Expanded
}
