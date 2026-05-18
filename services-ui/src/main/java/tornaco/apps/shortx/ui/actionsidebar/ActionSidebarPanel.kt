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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.proto.action.ActionSidebarStyleSettings
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon

private const val DEFAULT_PANEL_BG = 0xFF1F1F23.toInt()
private const val DEFAULT_ICON_BG = 0xFF3A3A40.toInt()
private const val DEFAULT_ICON_COLOR = 0xFFFFFFFF.toInt()
private const val DEFAULT_TEXT_COLOR = 0xFFFFFFFF.toInt()

@Composable
fun ActionSidebarPanel(
    buttons: List<ActionSidebarButtonUiModel>,
    callbacks: ActionSidebarCallbacks,
    position: ActionSidebarPosition,
    style: ActionSidebarStyleSettings,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = position.enterTransition(),
        exit = position.exitTransition(),
    ) {
        val resolvedStyle = style.withDefaults()
        Surface(
            modifier = modifier
                .width(resolvedStyle.panelWidthDp.dp)
                .heightIn(max = resolvedStyle.maxHeightDp.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(resolvedStyle.panelCornerRadiusDp.dp),
                    clip = false,
                ),
            color = Color(resolvedStyle.backgroundColor).copy(
                alpha = resolvedStyle.backgroundAlphaPercent.coerceIn(0, 100) / 100f,
            ),
            shape = RoundedCornerShape(resolvedStyle.panelCornerRadiusDp.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = resolvedStyle.contentPaddingHorizontalDp.dp,
                        vertical = resolvedStyle.contentPaddingVerticalDp.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(onClick = onDismissRequest) {
                    RemixIcon(
                        remixName = Remix.System.close_line,
                        modifier = Modifier.size(resolvedStyle.iconSizeDp.dp),
                    )
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = resolvedStyle.contentPaddingHorizontalDp.dp),
                    verticalArrangement = Arrangement.spacedBy(resolvedStyle.itemSpacingDp.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    buttons.sortedBy { it.order }.forEach { button ->
                        ActionSidebarButtonItem(button, callbacks, resolvedStyle)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionSidebarButtonItem(
    button: ActionSidebarButtonUiModel,
    callbacks: ActionSidebarCallbacks,
    style: ActionSidebarStyleSettings,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(style.buttonCornerRadiusDp.dp))
            .combinedClickable(
                onClick = { callbacks.onButtonTap(button) },
                onLongClick = { callbacks.onButtonLongPress(button) },
            )
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ColoredIcon(size = style.iconBackgroundSizeDp.dp, color = Color(style.iconBackgroundColor)) {
            if (button.icon.isNotBlank()) {
                RemixIcon(remixName = button.icon, modifier = Modifier.size(style.iconSizeDp.dp), tint = Color(style.iconColor))
            } else {
                RemixIcon(remixName = Remix.System.apps_line, modifier = Modifier.size(style.iconSizeDp.dp), tint = Color(style.iconColor))
            }
        }
        if (button.text.isNotBlank()) {
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = button.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(style.textColor),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = style.textSizeSp.sp),
            )
        }
    }
}

fun ActionSidebarStyleSettings.withDefaults(): ActionSidebarStyleSettings {
    return ActionSidebarStyleSettings.newBuilder(this)
        .setBackgroundColor(if (backgroundColor == 0) DEFAULT_PANEL_BG else backgroundColor)
        .setBackgroundAlphaPercent(if (backgroundAlphaPercent == 0) 92 else backgroundAlphaPercent)
        .setPanelCornerRadiusDp(if (panelCornerRadiusDp == 0) 24 else panelCornerRadiusDp)
        .setButtonCornerRadiusDp(if (buttonCornerRadiusDp == 0) 18 else buttonCornerRadiusDp)
        .setPanelWidthDp(if (panelWidthDp == 0) 108 else panelWidthDp)
        .setMaxHeightDp(if (maxHeightDp == 0) 520 else maxHeightDp)
        .setIconSizeDp(if (iconSizeDp == 0) 24 else iconSizeDp)
        .setIconBackgroundSizeDp(if (iconBackgroundSizeDp == 0) 46 else iconBackgroundSizeDp)
        .setIconColor(if (iconColor == 0) DEFAULT_ICON_COLOR else iconColor)
        .setIconBackgroundColor(if (iconBackgroundColor == 0) DEFAULT_ICON_BG else iconBackgroundColor)
        .setTextColor(if (textColor == 0) DEFAULT_TEXT_COLOR else textColor)
        .setTextSizeSp(if (textSizeSp == 0) 11 else textSizeSp)
        .setContentPaddingHorizontalDp(if (contentPaddingHorizontalDp == 0) 8 else contentPaddingHorizontalDp)
        .setContentPaddingVerticalDp(if (contentPaddingVerticalDp == 0) 8 else contentPaddingVerticalDp)
        .setItemSpacingDp(if (itemSpacingDp == 0) 10 else itemSpacingDp)
        .build()
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
