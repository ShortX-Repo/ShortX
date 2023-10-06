package tornaco.apps.shortx.ui.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


data class Prompt(val title: String, val subTitle: String)

fun FragmentActivity.authenticateWithBiometric(
    prompt: Prompt,
    onResult: (Boolean, String) -> Unit
): BiometricPrompt? {
    return if (isBiometricReady(this)) {
        val promptInfo = getPromptInfo(prompt)
        val biometricPrompt = getBiometricPrompt(onResult)
        biometricPrompt.authenticate(promptInfo)
        biometricPrompt
    } else {
        onResult(false, "Biometric not ready")
        null
    }
}

fun isBiometricReady(context: Context) =
    hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

private fun hasBiometricCapability(context: Context): Int {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )
}

private fun FragmentActivity.getBiometricPrompt(onResult: (Boolean, String) -> Unit): BiometricPrompt {
    val executor = ContextCompat.getMainExecutor(this)

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onResult(false, "$errorCode - $errString")
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onResult(false, "Unknown reason")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onResult(true, "Success")
        }
    }

    return BiometricPrompt(this, executor, callback)
}

private fun FragmentActivity.getPromptInfo(prompt: Prompt): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder()
        .setTitle(prompt.title)
        .setSubtitle(prompt.subTitle)
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
                    or BiometricManager.Authenticators.BIOMETRIC_WEAK
                    or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()
}