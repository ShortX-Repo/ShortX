package tornaco.apps.shortx.core.action

import android.os.Bundle
import tornaco.apps.shortx.core.util.Logger

const val KEY_ACTION_NAME = "action.name"

private val logger = Logger("Action")

abstract class Action(val name: String) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString(KEY_ACTION_NAME, name)
            writeToBundle(this)
        }
    }

    abstract fun writeToBundle(bundle: Bundle)
}

fun Bundle.toActionOrThrow(): Action {
    if (isEmpty) return NoAction()
    return when (val name =
        requireNotNull(getString(KEY_ACTION_NAME)) { "Missing name on this bundle." }) {
        StartActivity.NAME -> {
            StartActivity.readFromBundle(this)
        }
        TakeScreenshot.NAME -> {
            TakeScreenshot.readFromBundle(this)
        }
        ExpandNotification.NAME -> {
            ExpandNotification.readFromBundle(this)
        }
        ShellCommand.NAME -> {
            ShellCommand.readFromBundle(this)
        }
        NoAction.NAME -> {
            NoAction()
        }
        else -> {
            throw IllegalArgumentException("Unknown action name: $name")
        }
    }
}