package tornaco.apps.shortx.ui.actionsidebar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon

@Composable
fun ActionSidebarPanel(
    buttons: List<ActionSidebarButtonUiModel>,
    callbacks: ActionSidebarCallbacks,
    position: ActionSidebarPosition,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = position.enterTransition(),
        exit = position.exitTransition(),
    ) {
        Column(
            modifier = modifier
                .width(108.dp)
                .heightIn(max = 520.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = onDismissRequest) {
                RemixIcon(remixName = Remix.System.close_line, modifier = Modifier.size(18.dp))
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                buttons.sortedBy { it.order }.forEach { button ->
                    ActionSidebarButtonItem(button, callbacks)
                }
            }
        }
    }
}

@Composable
private fun ActionSidebarButtonItem(
    button: ActionSidebarButtonUiModel,
    callbacks: ActionSidebarCallbacks,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(
                onClick = { callbacks.onButtonTap(button) },
                onLongClick = { callbacks.onButtonLongPress(button) },
            )
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ColoredIcon(size = 46.dp, color = MaterialTheme.colorScheme.surfaceVariant) {
            if (button.icon.isNotBlank()) {
                RemixIcon(remixName = button.icon, modifier = Modifier.size(24.dp))
            } else {
                RemixIcon(remixName = Remix.System.apps_line, modifier = Modifier.size(24.dp))
            }
        }
        if (button.text.isNotBlank()) {
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = button.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

private fun ActionSidebarPosition.enterTransition() = when (this) {
    ActionSidebarPosition.ActionSidebarPosition_Right -> slideInHorizontally(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        initialOffsetX = { it },
    )

    ActionSidebarPosition.ActionSidebarPosition_Top -> slideInVertically(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        initialOffsetY = { -it },
    )

    ActionSidebarPosition.ActionSidebarPosition_Bottom -> slideInVertically(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        initialOffsetY = { it },
    )

    else -> slideInHorizontally(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        initialOffsetX = { -it },
    )
}

private fun ActionSidebarPosition.exitTransition() = when (this) {
    ActionSidebarPosition.ActionSidebarPosition_Right -> slideOutHorizontally(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        targetOffsetX = { it },
    )

    ActionSidebarPosition.ActionSidebarPosition_Top -> slideOutVertically(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        targetOffsetY = { -it },
    )

    ActionSidebarPosition.ActionSidebarPosition_Bottom -> slideOutVertically(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        targetOffsetY = { it },
    )

    else -> slideOutHorizontally(
        animationSpec = tween(ACTION_SIDEBAR_ANIMATION_DURATION_MS),
        targetOffsetX = { -it },
    )
}
