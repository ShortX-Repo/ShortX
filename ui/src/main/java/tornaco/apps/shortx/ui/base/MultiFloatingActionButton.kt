package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    srcIcon: ImageVector,
    showLabels: Boolean = true,
    items: List<MultiFabItem>,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val currentState = remember { mutableStateOf(MultiFabState.Collapsed) }
    val transition = updateTransition(targetState = currentState, label = "")
    val rotateAnim: Float by transition.animateFloat(
        transitionSpec = {
            if (targetState.value == MultiFabState.Expanded) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
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
                spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.58F)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
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
                    .alpha(animateFloatAsState(alphaAnim).value)
            ) {
                if (showLabels) {
                    Text(
                        item.label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clip(ShortXCardRoundedCornerShape)
                            .background(color = MaterialTheme.colorScheme.secondary)
                            .alpha(animateFloatAsState(alphaAnim).value)
                            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                FloatingActionButton(
                    modifier = Modifier.size(46.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        currentState.value = MultiFabState.Collapsed
                        onFabItemClicked(item)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier,
            onClick = {
                currentState.value =
                    if (currentState.value == MultiFabState.Collapsed) MultiFabState.Expanded else MultiFabState.Collapsed
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
    val icon: ImageVector,
    val label: String,
)

enum class MultiFabState {
    Collapsed,
    Expanded
}
