package tornaco.apps.shortx.services.app

import android.app.ActivityManager
import android.app.ActivityTaskManager
import android.graphics.Bitmap
import android.hardware.HardwareBuffer
import android.os.SystemClock
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections

object TaskSnapshotHelper {
    private val logger = Logger("TaskSnapshotHelper")

    fun getTaskSnapshot(taskId: Int): Bitmap? {
        return try {
            if (OsUtils.isUOrAbove()) {
                getTaskSnapshotApi34(taskId)
            } else {
                getTaskSnapshotLegacy(taskId)
            }
        } catch (e: Throwable) {
            logger.e(e, "getTaskSnapshot failed for taskId=$taskId")
            null
        }
    }

    private fun getTaskSnapshotApi34(taskId: Int): Bitmap? {
        val atms = ActivityTaskManager.getService()
        val snapshot = Reflections.callMethod(atms, "getTaskSnapshot", taskId, false)
            ?: return null
        return snapshotToBitmap(snapshot)
    }

    private fun getTaskSnapshotLegacy(taskId: Int): Bitmap? {
        val am = ActivityManager.getService()
        val snapshot = Reflections.callMethod(am, "getTaskSnapshot", taskId, false)
            ?: return null
        return snapshotToBitmap(snapshot)
    }

    private fun snapshotToBitmap(snapshot: Any): Bitmap? {
        val buffer = Reflections.callMethod(snapshot, "getHardwareBuffer") as? HardwareBuffer
            ?: return null
        return try {
            val bitmap = Bitmap.wrapHardwareBuffer(buffer, null) ?: return null
            val sw = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            bitmap.recycle()
            buffer.close()
            sw
        } catch (e: Throwable) {
            logger.e(e, "snapshotToBitmap failed")
            buffer.close()
            null
        }
    }
}
