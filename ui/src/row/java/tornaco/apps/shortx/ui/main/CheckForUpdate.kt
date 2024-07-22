package tornaco.apps.shortx.ui.main

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import tornaco.apps.shortx.core.util.Logger

class CheckForUpdate(private val context: Context) {

    private val logger = Logger("CheckForUpdate")

    operator fun invoke() {
        logger.i("checkForUpdate...")
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            logger.i("appUpdateInfo.updateAvailability: ${appUpdateInfo.updateAvailability()}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                logger.i("Request the update.")
            }
        }
    }
}