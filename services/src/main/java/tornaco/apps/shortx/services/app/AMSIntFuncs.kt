package tornaco.apps.shortx.services.app

import android.os.IBinder
import android.os.UserHandle
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object AMSIntFuncs {
    private val logger = Logger("AMSIntFuncs")

    fun getTopVisibleActivities(atmsInt: Any): List<AppComponent> {
        @Suppress("UNCHECKED_CAST")
        val top = Reflections.callMethod(atmsInt, "getTopVisibleActivities") as List<Any>
        // ActivityAssistInfo or IBinder

        return top.mapNotNull {
            // ActivityRecord
            val ar =
                ActivityRecordUtils.forTokenLocked(it as IBinder, atmsInt.javaClass.classLoader)
            val uid = ActivityRecordUtils.getUid(ar)
            val intent = ActivityRecordUtils.getIntent(ar)

            if (uid < 0 || intent == null) {
                logger.e("getTopVisibleActivities, uid or intent is null.")
                null
            } else {
                intent.component.toAppComponent(UserHandle.getUserId(uid))
            }
        }
    }
}

