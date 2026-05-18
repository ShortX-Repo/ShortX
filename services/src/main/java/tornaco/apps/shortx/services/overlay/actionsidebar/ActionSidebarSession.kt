package tornaco.apps.shortx.services.overlay.actionsidebar

import tornaco.apps.shortx.core.proto.action.ActionSidebarButton

enum class ActionSidebarSessionState {
    Visible,
    Dismissing,
    Dismissed,
    Failed,
}

data class ActionSidebarSession(
    val sourceActionId: String,
    val visibleButtons: List<ActionSidebarButton>,
    val state: ActionSidebarSessionState,
    val failureMessage: String? = null,
)
