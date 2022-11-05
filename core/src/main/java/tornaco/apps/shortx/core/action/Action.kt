package tornaco.apps.shortx.core.action

import android.os.Bundle

const val KEY_ACTION_NAME = "action.name"

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
        ExpandNotification.NAME -> {
            ExpandNotification.readFromBundle(this)
        }
        NoAction.NAME -> {
            StartActivity.readFromBundle(this)
        }
        else -> {
            throw IllegalArgumentException("Unknown action name: $name")
        }
    }
}