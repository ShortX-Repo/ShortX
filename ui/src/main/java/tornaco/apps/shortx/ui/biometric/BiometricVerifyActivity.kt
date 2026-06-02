package tornaco.apps.shortx.ui.biometric

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import tornaco.apps.shortx.core.proto.common.BiometricVerifyResultCase
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.ConfirmDeviceCredentialUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_REQUEST_CODE
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_SUBTITLE
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_THEME_MODE
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_TITLE
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.RESULT_CASE

class BiometricVerifyActivity : BaseActivity() {
    private val logger = Logger("BiometricVerifyActivity")

    private var requestCode = 0
    private var title = "ShortX"
    private var subTitle = ""
    private var themeMode = UiThemeMode.UiThemeMode_System

    private var biometricPrompt: BiometricPrompt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveIntent()
        delegate.localNightMode = themeMode.toNightMode()
        super.onCreate(savedInstanceState)
        startVerifyWithBiometrics()
    }

    @Composable
    override fun Content() {
    }

    private fun resolveIntent() {
        requestCode =
            intent.getIntExtra(EXTRA_REQUEST_CODE, Int.MIN_VALUE)
        require(requestCode >= 0) {
            "Bad requestCode: $requestCode"
        }
        title = intent.getStringExtra(EXTRA_TITLE).fallbackOnEmpty("ShortX")
        subTitle = intent.getStringExtra(EXTRA_SUBTITLE).fallbackOnEmpty("")
        themeMode = runCatching {
            UiThemeMode.valueOf(intent.getStringExtra(EXTRA_THEME_MODE).fallbackOnEmpty(UiThemeMode.UiThemeMode_System.name))
        }.getOrDefault(UiThemeMode.UiThemeMode_System)
    }

    private fun UiThemeMode.toNightMode(): Int {
        return when (this) {
            UiThemeMode.UiThemeMode_Light -> AppCompatDelegate.MODE_NIGHT_NO
            UiThemeMode.UiThemeMode_Dark -> AppCompatDelegate.MODE_NIGHT_YES
            UiThemeMode.UNRECOGNIZED, UiThemeMode.UiThemeMode_System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    private fun startVerifyWithBiometrics() {
        if (!isBiometricReady(this)) {
            shortXManager.setStartActivityResult(
                requestCode,
                Activity.RESULT_OK,
                bundleOf(RESULT_CASE to BiometricVerifyResultCase.BiometricVerifyResult_Allow_BiometricNotReady.name)
            )
            setResult(RESULT_OK)
            finish()
            return
        }

        biometricPrompt = authenticateWithBiometric(
            Prompt(
                title,
                subTitle
            )
        ) { success: Boolean, message: String ->
            logger.i("authenticateWithBiometric result: $success $message")
            if (success) {
                logger.d("setVerifyResult ALLOW")
                shortXManager.setStartActivityResult(
                    requestCode,
                    RESULT_OK,
                    bundleOf(RESULT_CASE to BiometricVerifyResultCase.BiometricVerifyResult_Allow_Verified.name)
                )
                ConfirmDeviceCredentialUtils.checkForPendingIntent(this@BiometricVerifyActivity)
                setResult(RESULT_OK)
                finish()
            } else {
                logger.d("setVerifyResult DENY")
                shortXManager.setStartActivityResult(
                    requestCode,
                    RESULT_CANCELED,
                    bundleOf(RESULT_CASE to BiometricVerifyResultCase.BiometricVerifyResult_Deny_VerifyFail.name)
                )
                cancelVerifyAndFinish()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        shortXManager.setStartActivityResult(
            requestCode,
            RESULT_CANCELED,
            bundleOf(RESULT_CASE to BiometricVerifyResultCase.BiometricVerifyResult_Deny_UserCanceled)
        )
        cancelVerifyAndFinish()
    }

    private fun cancelVerifyAndFinish() {
        logger.w("cancelVerifyAndFinish...")
        kotlin.runCatching { biometricPrompt?.cancelAuthentication() }
        setResult(RESULT_CANCELED)
        finish()
    }
}
