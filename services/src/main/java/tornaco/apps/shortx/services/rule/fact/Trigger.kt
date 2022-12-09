package tornaco.apps.shortx.services.rule.fact

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext

interface Trigger {
    val context: Context
    val shortXContext: ShortXContext

    fun register()
    fun unRegister()
}