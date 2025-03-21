package tornaco.apps.shortx.services.rule.fact.alarm

import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.powerManager

fun Context.acquireWakeLockForPublishFactAndNextAlarmSchedule(tag: String): PowerManager.WakeLock? {
    return powerManager.newWakeLock(
        PARTIAL_WAKE_LOCK,
        BuildPropInternal.APP_PACKAGE_NAME + "$tag:wakelock"
    ).apply {
        setReferenceCounted(false)
        acquire(15000)
    }
}