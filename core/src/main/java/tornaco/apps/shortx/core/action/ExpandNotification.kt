package tornaco.apps.shortx.core.action

import android.os.Bundle

class ExpandNotification : Action(NAME) {
    companion object {
        const val NAME = "ExpandNotification"

        fun readFromBundle(bundle: Bundle): Action {
            return ExpandNotification()
        }
    }

    override fun writeToBundle(bundle: Bundle) {
    }
}