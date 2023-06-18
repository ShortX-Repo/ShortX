package tornaco.apps.shortx.core.platform

import android.content.Intent
import android.os.IBinder
import tornaco.apps.shortx.core.platform.Classes.activityRecordClass
import tornaco.apps.shortx.core.util.Logger
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
}