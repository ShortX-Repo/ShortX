package tornaco.apps.shortx.core.action

import android.os.Bundle

data class ShellCommand(val command: String) : Action(NAME) {
    companion object {
        const val KEY_SHELL_COMMAND = "shell.command"
        const val NAME = "ShellCommand"

        fun readFromBundle(bundle: Bundle): Action {
            return ShellCommand(requireNotNull(bundle.getString(KEY_SHELL_COMMAND)) {
                "Missing command on Bundle."
            })
        }
    }

    override fun writeToBundle(bundle: Bundle) {
        bundle.putString(KEY_SHELL_COMMAND, command)
    }
}