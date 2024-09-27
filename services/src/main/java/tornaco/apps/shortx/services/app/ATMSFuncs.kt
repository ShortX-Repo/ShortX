package tornaco.apps.shortx.services.app

import android.os.IBinder
import android.os.UserHandle
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object ATMSFuncs {
    private val logger = Logger("ATMSFuncs")

    fun hasResumedActivity(atmsInt: Any, uid: Int): Boolean {
        return Reflections.callMethod(atmsInt, "hasResumedActivity", uid) as Boolean
    }

    fun getTopVisibleActivities(atmsInt: Any): List<InDisplay<AppComponent>> {
        @Suppress("UNCHECKED_CAST")
        val top = Reflections.callMethod(atmsInt, "getTopVisibleActivities") as List<Any>
        // ActivityAssistInfo or IBinder

        return top.mapNotNull {
            // ActivityRecord
            val ar = if (it is IBinder) {
                ActivityRecordUtils.forTokenLocked(it, atmsInt.javaClass.classLoader)
            } else {
                val ib = Reflections.callMethod(it, "getActivityToken") as IBinder
                ActivityRecordUtils.forTokenLocked(ib, atmsInt.javaClass.classLoader)
            }
            val uid = ActivityRecordUtils.getUid(ar)
            val intent = ActivityRecordUtils.getIntent(ar)
            val displayId = ActivityRecordUtils.getDisplayId(ar)
            logger.d("getTopVisibleActivities, uid: $uid, displayId: $displayId, intent: $intent")

            if (uid < 0 || intent == null) {
                logger.e("getTopVisibleActivities, uid or intent is null.")
                null
            } else {
                intent.component.toAppComponent(UserHandle.getUserId(uid)).inDisplay(displayId)
            }
        }
    }
}

