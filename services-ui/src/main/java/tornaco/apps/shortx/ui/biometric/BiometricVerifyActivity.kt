package tornaco.apps.shortx.ui.biometric

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.os.bundleOf
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.BiometricVerify
import tornaco.apps.shortx.core.proto.common.BiometricVerifyResultCase
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.ConfirmDeviceCredentialUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty

class BiometricVerifyActivity : AppCompatActivity() {
    private val logger = Logger("BiometricVerifyActivity")

    private var requestCode = 0
    private var title = "ShortX"
    private var subTitle = ""

    private var biometricPrompt: BiometricPrompt? = null

    companion object {
        const val EXTRA_REQUEST_CODE = "extra.request.code"
        const val EXTRA_TITLE = "extra.title"
        const val EXTRA_SUBTITLE = "extra.subtitle"
        const val RESULT_CASE = "result.case"

        @JvmStatic
        fun start(context: Context, requestCode: Int, action: BiometricVerify) {
            val starter = Intent("")
                .setPackage(BuildProp.APP_PACKAGE_NAME)
                .setClassName(
                    BuildProp.APP_PACKAGE_NAME,
                    "tornaco.apps.shortx.ui.biometric.BiometricVerifyActivity"
                )
                .putExtra(EXTRA_REQUEST_CODE, requestCode)
                .putExtra(EXTRA_TITLE, action.title)
                .putExtra(EXTRA_SUBTITLE, action.subTitle)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveIntent()
        startVerifyWithBiometrics()
    }


    private fun resolveIntent() {
        requestCode =
            intent.getIntExtra(EXTRA_REQUEST_CODE, Int.MIN_VALUE)
        require(requestCode >= 0) {
            "Bad requestCode: $requestCode"
        }
        title = intent.getStringExtra(EXTRA_TITLE).fallbackOnEmpty("ShortX")
        subTitle = intent.getStringExtra(EXTRA_SUBTITLE).fallbackOnEmpty("")
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