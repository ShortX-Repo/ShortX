package tornaco.apps.shortx.ui.util

import com.amdelamar.jotp.OTP
import com.amdelamar.jotp.type.Type
import org.apache.commons.codec.binary.Base32
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.composeLogger


inline fun verifyOtp(
    code: String,
    otp: String,
    crossinline onRes: () -> Unit,
    crossinline onError: () -> Unit
) {
    val hexTime = OTP.timeInHex(System.currentTimeMillis(), 60)
    OTP.verify(base32(code.reversed()), hexTime, otp, 6, Type.TOTP)
        .select(t = {
            composeLogger.p("verifyOtp success: $code $otp")
            onRes()
        }, f = {
            composeLogger.p("verifyOtp fail: $code $otp")
            onError()
        })
}

fun base32(str: String): String {
    val encodedBytes: ByteArray = Base32().encode(str.toByteArray())
    return String(encodedBytes)
}