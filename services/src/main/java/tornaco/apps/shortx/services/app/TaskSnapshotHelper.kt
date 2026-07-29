package tornaco.apps.shortx.services.app

import android.app.ActivityManager
import android.app.ActivityTaskManager
import android.graphics.Bitmap
import android.hardware.HardwareBuffer
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import java.lang.reflect.Method

object TaskSnapshotHelper {
    private val logger = Logger("TaskSnapshotHelper")

    fun getTaskSnapshot(taskId: Int): Bitmap? {
        return try {
            val service = if (OsUtils.isUOrAbove()) {
                ActivityTaskManager.getService()
            } else {
                ActivityManager.getService()
            }
            val snapshot = invokeGetTaskSnapshot(service, taskId) ?: return null
            snapshotToBitmap(snapshot)
        } catch (e: Throwable) {
            logger.e(e, "getTaskSnapshot failed for taskId=$taskId")
            null
        }
    }

    private fun invokeGetTaskSnapshot(service: Any, taskId: Int): Any? {
        val clazz = service.javaClass
        val method = findSnapshotMethod(clazz) ?: return null
        method.isAccessible = true
        return when (method.parameterTypes.size) {
            2 -> method.invoke(service, taskId, false)
                ?: method.invoke(service, taskId, true)
            3 -> method.invoke(service, taskId, false, true)
                ?: method.invoke(service, taskId, true, true)
            else -> null
        }
    }

    private fun findSnapshotMethod(clazz: Class<*>): Method? {
        // Try (int, boolean) — most common
        try {
            return clazz.getMethod(
                "getTaskSnapshot",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
            )
        } catch (_: NoSuchMethodException) {}

        // Try (int, boolean, boolean) — some Android 15+ builds
        try {
            return clazz.getMethod(
                "getTaskSnapshot",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
            )
        } catch (_: NoSuchMethodException) {}

        logger.w("No getTaskSnapshot method found on ${clazz.name}")
        return null
    }

    private fun snapshotToBitmap(snapshot: Any): Bitmap? {
        val buffer = extractHardwareBuffer(snapshot) ?: return null
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

    private fun extractHardwareBuffer(snapshot: Any): HardwareBuffer? {
        val snapshotClass = snapshot.javaClass

        // Try getHardwareBuffer() — Android 12+
        try {
            val m = snapshotClass.getMethod("getHardwareBuffer")
            m.isAccessible = true
            val result = m.invoke(snapshot)
            if (result is HardwareBuffer) return result
        } catch (_: NoSuchMethodException) {}

        // Try getSnapshot() — older Android versions return GraphicBuffer
        try {
            val m = snapshotClass.getMethod("getSnapshot")
            m.isAccessible = true
            val result = m.invoke(snapshot) ?: return null
            if (result is HardwareBuffer) return result
            // GraphicBuffer → HardwareBuffer via nativeGetHardwareBuffer
            try {
                val convertMethod = result.javaClass.getMethod("toHardwareBuffer")
                convertMethod.isAccessible = true
                return convertMethod.invoke(result) as? HardwareBuffer
            } catch (_: NoSuchMethodException) {}
        } catch (_: NoSuchMethodException) {}

        logger.w("No buffer extraction method found on ${snapshotClass.name}")
        return null
    }
}
