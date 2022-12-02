package tornaco.apps.shortx.ui

import android.content.Context
import tornaco.apps.shortx.ui.base.I18NCache

object EntryPoint {
    fun onCreate(context: Context) {
        I18NCache.init(context)
    }
}