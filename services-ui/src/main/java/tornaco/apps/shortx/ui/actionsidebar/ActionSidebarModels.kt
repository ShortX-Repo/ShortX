package tornaco.apps.shortx.ui.actionsidebar

import com.google.protobuf.Any

data class ActionSidebarButtonUiModel(
    val id: String,
    val icon: String,
    val text: String,
    val order: Int,
    val tapActions: List<Any> = emptyList(),
    val longPressActions: List<Any> = emptyList(),
)

data class ActionSidebarCallbacks(
    val onDismiss: () -> Unit,
    val onButtonTap: (ActionSidebarButtonUiModel) -> Unit,
    val onButtonLongPress: (ActionSidebarButtonUiModel) -> Unit,
)
