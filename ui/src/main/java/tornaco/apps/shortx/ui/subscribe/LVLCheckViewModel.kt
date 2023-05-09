package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.google.android.vending.licensing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.EntryPoint
import tornaco.apps.shortx.ui.subscribe.util.hasAChanceToSkipLicenseCheck
import tornaco.apps.shortx.ui.subscribe.util.setLVLChecked
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

data class UiState(val checkState: CheckState)

class LVLCheckViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(CheckState.CHECKING))
    val uiState: StateFlow<UiState> = _uiState

    fun check(context: Context) {
        _uiState.value = _uiState.value.copy(checkState = CheckState.CHECKING)

        val checkerCallback = object : LicenseCheckerCallback {
            override fun allow(reason: Int) {
                XLog.v("LicenseCheckerCallback.allow: $reason")
                setLVLChecked(context, true)
                _uiState.value = _uiState.value.copy(checkState = CheckState.SUCCESS)
            }

            override fun dontAllow(policyReason: Int) {
                XLog.v("LicenseCheckerCallback do not allow: $policyReason")
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
                setLVLChecked(context, false)
            }

            override fun applicationError(errorCode: Int) {
                XLog.v("LicenseCheckerCallback.applicationError: $errorCode")
                _uiState.value = _uiState.value.copy(checkState = CheckState.FAIL)
            }
        }


        // Try to use more data here. ANDROID_ID is a single point of attack.
        val deviceId = EntryPoint.getDeviceId(context)
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
        return hasAChanceToSkipLicenseCheck(context)
    }

    fun onSkip(context: Context) {
        return skipLicenseCheckOnce(context)
    }
}