package tornaco.apps.shortx.core.platform

import tornaco.apps.shortx.core.util.OsUtils

object PackageNames {
    const val android = "android"

    val mediaProvider = if (OsUtils.isROrAbove()) {
        "com.android.providers.media.module"
    } else {
        "com.android.providers.media"
    }
}

object Methods {
    const val start = "start"
    const val systemReady = "systemReady"
    const val filterInputEvent = "filterInputEvent"
    const val reportEvent = "reportEvent"
    const val getService = "getService"
    const val onTransact = "onTransact"
    const val print = "print"
    const val startListening = "startListening"
    const val registerPostedByApp = "registerPostedByApp"
    const val registerUpdatedByApp = "registerUpdatedByApp"
    const val registerRemovedByApp = "registerRemovedByApp"
    const val registerDismissedByUser = "registerDismissedByUser"
    const val cleanUpRemovedTaskLocked = "cleanUpRemovedTaskLocked"
    const val removeLruProcessLocked = "removeLruProcessLocked"
    const val activityResumed = "activityResumed"
    const val activityResumedLocked = "activityResumedLocked"
    const val activityStopped = "activityStopped"
    const val activityStoppedLocked = "activityStoppedLocked"
    const val updateState = "updateState"
    const val onMediaStoreInsert = "onMediaStoreInsert"
    const val onMediaStoreDelete = "onMediaStoreDelete"
    const val interceptKeyBeforeQueueing = "interceptKeyBeforeQueueing"
}

object ClassNames {
    const val AMS = "com.android.server.am.ActivityManagerService"
    const val ATMS = "com.android.server.wm.ActivityTaskManagerService"
    const val INPUT_MANAGER_SERVICE = "com.android.server.inputmethod.InputMethodManagerService"
    const val NOTIFICATION_USAGE_STATS_SERVICE =
        "com.android.server.notification.NotificationUsageStats"
    const val PROCESS_LIST = "com.android.server.am.ProcessList"
    const val PROCESS_RECORD = "com.android.server.am.ProcessRecord"
    const val ACTIVITY_RECORD_WM = "com.android.server.wm.ActivityRecord"
    const val ACTIVITY_RECORD_AM = "com.android.server.am.ActivityRecord"
    const val VPN = "com.android.server.connectivity.Vpn"
    const val MEDIA_DOCUMENTS_PROVIDER = "com.android.providers.media.MediaDocumentsProvider"
    const val PHONE_WINDOW_MANAGER = "com.android.server.policy.PhoneWindowManager"
}

object Fields {
    const val mContext = "mContext"
}

fun String.lifecycleClassName() = "$this\$Lifecycle"