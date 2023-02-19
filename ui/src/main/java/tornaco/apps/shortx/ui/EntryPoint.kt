package tornaco.apps.shortx.ui

import android.content.Context
import android.os.Build
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        I18NCache.init(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }
    }
}