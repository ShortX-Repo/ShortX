package tornaco.apps.shortx.services.app

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import com.elvishew.xlog.XLog
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.activityManager

object TaskMapping {
    private val logger = Logger("TaskMapping")

    private const val TASK_QUERY_SIZE = 200

    fun getLastTaskId(context: Context): Int {
        val am = context.activityManager
        // Assume calling pkg has this permission.
        val tasks =
            am.getRecentTasks(TASK_QUERY_SIZE, ActivityManager.RECENT_WITH_EXCLUDED)
                .filter { it.baseIntent != null }
                .filter {
                    val pkgOfThisTask: String = PkgUtils.packageNameOf(it.baseIntent)
                    "com.android.systemui" != pkgOfThisTask
                }
        tasks.forEach {
            logger.d("${it.baseIntent.component}")
        }
        return if (tasks.size < 2) {
            -1
        } else {
            getTaskId(tasks[1])
        }
    }

    fun getPackageNameForTaskId(context: Context, taskId: Int): String? {
        val am = context.activityManager
        // Assume calling pkg has this permission.
        val tasks = am.getRecentTasks(TASK_QUERY_SIZE, ActivityManager.RECENT_WITH_EXCLUDED)
        logger.d("getPackageNameForTaskId, RecentTaskInfo tasks: $tasks")
        if (tasks != null) {
            for (rc in tasks) {
                // persistentId: Deprecated
                // As of Build.VERSION_CODES.Q, use taskId.
                logger.d("getPackageNameForTaskId, RecentTaskInfo rc: $rc, persistentId: ${rc?.persistentId}")
                if (rc != null && rc.persistentId == taskId) {
                    val pkgOfThisTask: String = PkgUtils.packageNameOf(rc.baseIntent)
                    logger.d("getPackageNameForTaskId, RecentTaskInfo pkgOfThisTask: $pkgOfThisTask")
                    if (!TextUtils.isEmpty(pkgOfThisTask)) {
                        return pkgOfThisTask
                    }
                    break
                }
            }
        }
        return null
    }

    fun getTasksIdForPackage(
        context: Context,
        pkgList: List<AppPkg>,
        ignoreNotRunning: Boolean
    ): List<Int> {
        val res: MutableList<Int> = ArrayList()
        val am = context.activityManager
        // Assume calling pkg has this permission.
        val flags = if (ignoreNotRunning) {
            ActivityManager.RECENT_IGNORE_UNAVAILABLE
        } else {
            ActivityManager.RECENT_WITH_EXCLUDED
        }
        val tasks = am.getRecentTasks(TASK_QUERY_SIZE, flags)
        logger.d("getTasksIdForPackage, RecentTaskInfo tasks: $tasks")
        if (tasks != null) {
            for (rc in tasks) {
                logger.d(
                    "RecentTaskInfo rc: $rc, persistentId: ${if (rc == null) "" else getTaskId(rc)}"
                )
                if (ignoreNotRunning && !isRunning(rc)) {
                    continue
                }
                if (rc?.baseIntent != null) {
                    val pkgOfThisTask: String = PkgUtils.packageNameOf(rc.baseIntent)
                    val userIdOfThisTask = rc.userId
                    logger.d("RecentTaskInfo this task is running, pkgOfThisTask: $pkgOfThisTask, userIdOfThisTask: $userIdOfThisTask")
                    val taskPkg =
                        AppPkg.newBuilder().setPkgName(pkgOfThisTask).setUserId(userIdOfThisTask)
                            .build()
                    if (pkgList.contains(taskPkg)) {
                        res.add(getTaskId(rc))
                    }
                }
            }
        }
        return res
    }

    fun hasRecentTaskForPkg(
        context: Context,
        pkg: AppPkg,
        ignoreNotRunning: Boolean
    ): Boolean {
        return getTasksIdForPackage(
            context,
            listOf(pkg),
            ignoreNotRunning
        ).isNotEmpty()
    }

    private fun getTaskId(rc: ActivityManager.RecentTaskInfo): Int {
        return try {
            if (OsUtils.isQOrAbove()) rc.taskId else rc.persistentId
        } catch (e: Throwable) {
            XLog.e("RecentTaskInfoCompat getTaskId error", e)
            0
        }
    }

    private fun isRunning(rc: ActivityManager.RecentTaskInfo): Boolean {
        return try {
            // isRunning is introduced from Q
            !OsUtils.isQOrAbove() || rc.isRunning
        } catch (e: Throwable) {
            XLog.e("RecentTaskInfoCompat isRunning error", e)
            true
        }
    }
}

