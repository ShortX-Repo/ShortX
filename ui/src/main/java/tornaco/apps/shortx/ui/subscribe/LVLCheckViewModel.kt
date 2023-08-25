package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.google.android.vending.licensing.AESObfuscator
import com.google.android.vending.licensing.LicenseChecker
import com.google.android.vending.licensing.LicenseCheckerCallback
import com.google.android.vending.licensing.Policy
import com.google.android.vending.licensing.ServerManagedPolicy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import tornaco.apps.shortx.ui.TOKEN_GOOGLE_PLAY
import tornaco.apps.shortx.ui.subscribe.util.hasAChanceToSkipLicenseCheck
import tornaco.apps.shortx.ui.subscribe.util.setLVLToken
import tornaco.apps.shortx.ui.subscribe.util.skipLicenseCheckOnce

private val SALT = byteArrayOf(
    -46, 65, 30, -128, -13, -57, 74, -64, 51, 88, -95, -45, 77, -127, -36, -113, -11, 32, -64, 89
)

enum class CheckState {
    CHECKING,
    FAIL,
    FAIL_NOT_LICENSED,
    FAIL_NETWORK,
    SUCCESS
}

data class LVLUiState(val checkState: CheckState)

class LVLCheckViewModel : ViewModel() {
    private val logger = Logger("ShortXApp")
    private val _uiState = MutableStateFlow(LVLUiState(CheckState.CHECKING))
    val uiState: StateFlow<LVLUiState> = _uiState

    fun check(context: Context) {
        _uiState.value = _uiState.value.copy(checkState = CheckState.CHECKING)

        val checkerCallback = object : LicenseCheckerCallback {
            override fun allow(reason: Int) {
                logger.p("LicenseCheckerCallback.allow: $reason")
                setLVLToken(TOKEN_GOOGLE_PLAY)
                _uiState.value = _uiState.value.copy(checkState = CheckState.SUCCESS)
            }

            override fun dontAllow(policyReason: Int) {
                logger.p("LicenseCheckerCallback do not allow: $policyReason")
                when (policyReason) {
                    Policy.RETRY -> {
                        _uiState.value = _uiState.value.copy(checkState = CheckState.FAIL_NETWORK)
                    }

                    Policy.NOT_LICENSED -> {
                        _uiState.value =
                            _uiState.value.copy(checkState = CheckState.FAIL_NOT_LICENSED)
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(checkState = CheckState.FAIL)
                    }
                }
                setLVLToken(null)
            }

            override fun applicationError(errorCode: Int) {
                XLog.v("LicenseCheckerCallback.applicationError: $errorCode")
                _uiState.value = _uiState.value.copy(checkState = CheckState.FAIL)
            }
        }


        // Try to use more data here. ANDROID_ID is a single point of attack.
        val deviceId = DeviceId.requireDeviceId()
        // Library calls this when it's done.
        // Construct the LicenseChecker with a policy.
        val checker = LicenseChecker(
            context,
            ServerManagedPolicy(context, AESObfuscator(SALT, context.packageName, deviceId)),
            BuildProp.PLAY_PUBLIC_KEY
        )

        viewModelScope.launch {
            // Let's see progress.
            delay(2000)
            checker.checkAccess(checkerCallback)
        }
    }

    fun canSkip(context: Context): Boolean {
        return hasAChanceToSkipLicenseCheck()
    }

    fun onSkip(context: Context) {
        return skipLicenseCheckOnce()
    }
}