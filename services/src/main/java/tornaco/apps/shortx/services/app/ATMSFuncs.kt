package tornaco.apps.shortx.services.app

import android.os.IBinder
import android.os.UserHandle
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object ATMSFuncs {
    private val logger = Logger("ATMSFuncs")

    fun hasResumedActivity(atmsInt: Any, uid: Int): Boolean {
        return Reflections.callMethod(atmsInt, "hasResumedActivity", uid) as Boolean
    }

    fun findActivityRecord(
        atmsInt: Any,
        componentName: android.content.ComponentName,
        userId: Int,
    ): Any? {
        return getTopVisibleActivityRecords(atmsInt).firstOrNull { activityRecord ->
            val uid = ActivityRecordUtils.getUid(activityRecord)
            val intent = ActivityRecordUtils.getIntent(activityRecord)
            intent?.component == componentName && UserHandle.getUserId(uid) == userId
        }
    }

    fun findActivityRecord(atmsInt: Any, appPkg: AppPkg): Any? {
        return getTopVisibleActivityRecords(atmsInt).firstOrNull { activityRecord ->
            val uid = ActivityRecordUtils.getUid(activityRecord)
            val intent = ActivityRecordUtils.getIntent(activityRecord)
            intent?.component?.packageName == appPkg.pkgName && UserHandle.getUserId(uid) == appPkg.userId
        }
    }

    fun getTopVisibleActivities(atmsInt: Any): List<InDisplay<AppComponent>> {
        return getTopVisibleActivityRecords(atmsInt).mapNotNull { ar ->
            val uid = ActivityRecordUtils.getUid(ar)
            val intent = ActivityRecordUtils.getIntent(ar)
            val displayId = ActivityRecordUtils.getDisplayId(ar)
            logger.d("getTopVisibleActivities, uid: $uid, displayId: $displayId, intent: $intent")

            if (uid < 0 || intent == null) {
                logger.e("getTopVisibleActivities, uid or intent is null.")
                null
            } else {
                intent.component?.toAppComponent(UserHandle.getUserId(uid))?.inDisplay(displayId)
            }
        }
    }

    private fun getTopVisibleActivityRecords(atmsInt: Any): List<Any> {
        @Suppress("UNCHECKED_CAST")
        val top = Reflections.callMethod(atmsInt, "getTopVisibleActivities") as List<Any>
        // ActivityAssistInfo or IBinder

        return top.mapNotNull { arOrActivityAssistInfo ->
            if (arOrActivityAssistInfo is IBinder) {
                ActivityRecordUtils.forTokenLocked(
                    arOrActivityAssistInfo,
                    atmsInt.javaClass.classLoader
                )
            } else {
                val ib = kotlin.runCatching {
                    Reflections.callMethod(arOrActivityAssistInfo, "getActivityToken") as IBinder
                }.getOrElse {
                    // One UI 7 has no getter method
                    // public final class ActivityAssistInfo {
                    //    public final IBinder mActivityToken;
                    //    public final IBinder mAssistToken;
                    //    public final ComponentName mComponentName;
                    //    public final int mTaskId;
                    //    public final int mUserId;
                    //
                    //    public ActivityAssistInfo(ActivityRecord activityRecord) {
                    //        this.mActivityToken = activityRecord.token;
                    //        this.mAssistToken = activityRecord.assistToken;
                    //        this.mTaskId = activityRecord.task.mTaskId;
                    //        this.mComponentName = activityRecord.mActivityComponent;
                    //        this.mUserId = activityRecord.mUserId;
                    //    }
                    //}
                    Reflections.getObjectField(arOrActivityAssistInfo, "mActivityToken") as IBinder
                }
                ActivityRecordUtils.forTokenLocked(ib, atmsInt.javaClass.classLoader)
            }
        }
    }
}
