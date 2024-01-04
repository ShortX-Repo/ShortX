package tornaco.apps.shortx.core.platform

import android.content.Intent
import android.os.IBinder
import android.view.Display
import tornaco.apps.shortx.core.platform.Classes.activityRecordClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections


object ActivityRecordUtils {
    private val logger = Logger("ActivityRecordUtils")

    // final ActivityRecord r = ActivityRecord.forTokenLocked(token);
    fun forTokenLocked(token: IBinder?, classLoader: ClassLoader): Any? {
        return if (token == null) null else try {
            val clazz = classLoader.activityRecordClass()
            Reflections.callStaticMethod(
                clazz,
                "forTokenLocked",
                token
            )
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#forTokenLocked error")
            null
        }
    }

    fun getIntent(activityRecord: Any?): Intent? {
        return if (activityRecord == null) {
            null
        } else try {
            Reflections.getObjectField(activityRecord, "intent") as Intent
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#getIntent error")
            null
        }
    }

    fun getUid(activityRecord: Any?): Int {
        return if (activityRecord == null) {
            -1
        } else try {
            Reflections.callMethod(activityRecord, "getUid") as Int
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#getUid error")
            -1
        }
    }

    fun getDisplayId(activityRecord: Any?): Int {
        return if (activityRecord == null) {
            Display.INVALID_DISPLAY
        } else try {
            Reflections.callMethod(activityRecord, "getDisplayId") as Int
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#getDisplayId error")
            Display.INVALID_DISPLAY
        }
    }


    // This method is useless, can not be used as check if Activity is shown.
    fun isVisible(activityRecord: Any?): Boolean {
        return if (OsUtils.isROrAbove()) {
            isVisibleAndroid11Plus(activityRecord)
        } else {
            nowVisibleAndroid10(activityRecord)
        }
    }

    // Since Android11
    private fun isVisibleAndroid11Plus(activityRecord: Any?): Boolean {
        return if (activityRecord == null) {
            false
        } else try {
            Reflections.callMethod(activityRecord, "isVisible") as Boolean
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#isVisible error")
            false
        }
    }

    // Android 10
    private fun nowVisibleAndroid10(activityRecord: Any?): Boolean {
        return if (activityRecord == null) {
            false
        } else try {
            Reflections.getBooleanField(activityRecord, "nowVisible")
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#nowVisible error")
            false
        }
    }
}