package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RuleScreen(
    state: MainState,
    onDelete: (String) -> Unit,
    onRuleClick: (Rule) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = state.rules,
            key = { listItem: Rule -> listItem.id + listItem.updateTime }) { rule ->
            var deleted by remember { mutableStateOf(false) }
            val dismissState = rememberDismissState(
                confirmStateChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToStart) {
                        deleted = true
                        return@rememberDismissState true
                    }
                    return@rememberDismissState false
                }
            )
            val scope = rememberCoroutineScope()
            if (deleted) {
                scope.launch {
                    delay(500)
                    onDelete(rule.id)
                }
            }

            AnimatedVisibility(
                visible = !deleted,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp),
                    directions = setOf(
                        DismissDirection.EndToStart
                    ),
                    dismissThresholds = {
                        FractionalThreshold(0.75f)
                    },
                    background = {
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.0f
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Filled.DeleteOutline,
                                contentDescription = "Delete",
                                modifier = Modifier.scale(scale)
                            )
                        }
                    },
                    dismissContent = {
                        Card(
                            backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = animateDpAsState(
                                if (dismissState.dismissDirection != null) 1.dp else 0.dp
                            ).value
                        ) {
                            RuleItem(rule = rule, onClick = {
                                onRuleClick(rule)
                            }, setRuleEnabled = setRuleEnabled)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: Rule,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    ListItem(
        title = rule.title,
        text1 = rule.description,
        text2 = rule.updateTime,
        checked = rule.isEnabled,
        onClick = onClick,
        onCheckedChange = { setRuleEnabled(rule.id, it) }
    )
}