package tornaco.apps.shortx.core.action

import android.os.Bundle

class NoAction : Action(NAME) {
    companion object {
        const val NAME = "NoAction"

        fun readFromBundle(bundle: Bundle): Action {
            return NoAction()
        }
    }

    override fun writeToBundle(bundle: Bundle) {
    }
}