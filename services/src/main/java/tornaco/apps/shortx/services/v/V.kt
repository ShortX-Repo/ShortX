package tornaco.apps.shortx.services.v

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import java.security.MessageDigest

val logger = Logger("ShortV")

@SuppressLint("PackageManagerGetSignatures")
fun s(context: Context): Array<Signature> {
    return if (OsUtils.isPOrAbove()) {
        context.packageManager.getPackageInfo(
            BuildProp.APP_PACKAGE_NAME,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo.apkContentsSigners
    } else {
        context.packageManager.getPackageInfo(
            BuildProp.APP_PACKAGE_NAME,
            PackageManager.GET_SIGNATURES
        ).signatures
    }
}

inline fun <T> v(context: Context, run: () -> T): T? {
    var match = true
    try {
        //note sample just checks the first signature
        s(context).forEachIndexed { index, signature ->
            // SHA1 the signature
            val digest = MessageDigest.getInstance("SHA1")
            digest.update(signature.toByteArray())
            val bytes = digest.digest()
            val hexArray = charArrayOf(
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                'A',
                'B',
                'C',
                'D',
                'E',
                'F'
            )
            val hexChars = CharArray(bytes.size * 2)
            var v: Int
            for (j in bytes.indices) {
                v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            val sha1 = String(hexChars)
            // logger.d("s_index_$index: $sha1")
            if (BuildProp.SIGNATURES.split("|")[index] != sha1) {
                match = false
            }
        }
    } catch (e: Throwable) {
        logger.e(e)
        match = false
    }

    return if (match) {
        run()
    } else {
        if (BuildProp.APP_IS_DEBUG) {
            logger.e("Not match")
        }
        null
    }
}
