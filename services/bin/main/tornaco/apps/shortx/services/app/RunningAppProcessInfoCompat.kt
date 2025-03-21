package tornaco.apps.shortx.services.app

import android.app.ActivityManager
import android.content.ComponentName

data class RunningAppProcessInfoCompat(
    val processName: String,
    val pid: Int,
    val uid: Int,
    val pkgList: List<String>,
    val flags: Int,
    val importance: Int,
    val lru: Int,
    val processState: Int,
    val importanceReasonPid: Int,
    val importanceReasonCode: Int,
    val importanceReasonComponent: ComponentName?
)

fun ActivityManager.RunningAppProcessInfo.compat(): RunningAppProcessInfoCompat {
    return RunningAppProcessInfoCompat(
        processName,
        pid,
        uid,
        pkgList?.toList() ?: emptyList(),
        flags,
        importance,
        lru,
        processState,
        importanceReasonPid,
        importanceReasonCode,
        importanceReasonComponent
    )
}
