package tornaco.apps.shortx.core.action

import android.os.Bundle

class TakeScreenshot : Action(NAME) {
    companion object {
        const val NAME = "TakeScreenshot"

        fun readFromBundle(bundle: Bundle): Action {
            return TakeScreenshot()
        }
    }

    override fun writeToBundle(bundle: Bundle) {
    }
}