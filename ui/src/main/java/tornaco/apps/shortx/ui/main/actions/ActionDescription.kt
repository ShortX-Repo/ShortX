package tornaco.apps.shortx.ui.main.actions

import tornaco.apps.shortx.core.action.Action
import tornaco.apps.shortx.core.action.ShellCommand
import tornaco.apps.shortx.core.action.StartActivity

fun Action.getExtraDescription(): List<String> {
    return when (this) {
        is StartActivity -> listOf(
            this.shortName
        )
        is ShellCommand -> listOf(
            this.command
        )
        else -> emptyList()
    }
}