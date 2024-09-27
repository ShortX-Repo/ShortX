package tornaco.apps.shortx.ui.main

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.ui.config.config

class CheckForUpdate(
    private val context: Context,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>
) {

    private val logger = Logger("CheckForUpdate")

    operator fun invoke() {
        kotlin.runCatching {
            logger.i("checkForUpdate...")
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                logger.i("appUpdateInfo.updateAvailability: ${appUpdateInfo.updateAvailability()}")
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    )
                ) {
                    val forceUpgradeVersion = config.forceUpgradeVersion
                    logger.i("forceUpgradeVersion: $forceUpgradeVersion")
                    if (BuildPropInternal.APP_VERSION_CODE < forceUpgradeVersion) {
                        logger.i("Launch force upgrade.")
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            launcher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        )
                    }
                }
            }
        }.onFailure {
            logger.e(it, "ERROR")
        }
    }
}