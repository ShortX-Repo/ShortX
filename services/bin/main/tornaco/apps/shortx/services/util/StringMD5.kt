package tornaco.apps.shortx.services.util

import java.math.BigInteger
import java.security.MessageDigest

object StringMD5 {
    fun md5(input: String): String {
        val md = MessageDigest.getInstance(String(byteArrayOf(77, 68, 53)))
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}