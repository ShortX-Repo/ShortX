package tornaco.apps.shortx.core.action

import android.os.Bundle

data class StartActivity(val shortName: String) : Action(NAME) {
    companion object {
        const val KEY_SHORT_NAME = "activity.short.name"
        const val NAME = "StartActivity"

        fun readFromBundle(bundle: Bundle): Action {
            return StartActivity(requireNotNull(bundle.getString(KEY_SHORT_NAME)) {
                "Missing shortName on Bundle."
            })
        }
    }

    override fun writeToBundle(bundle: Bundle) {
        bundle.putString(KEY_SHORT_NAME, shortName)
    }
}