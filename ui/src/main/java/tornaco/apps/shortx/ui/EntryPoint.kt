package tornaco.apps.shortx.ui

import android.content.Context
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        I18NCache.init(context)
        HiddenApiBypass.addHiddenApiExemptions("")
    }
}