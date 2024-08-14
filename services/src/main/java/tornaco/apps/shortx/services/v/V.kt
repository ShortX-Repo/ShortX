package tornaco.apps.shortx.services.v

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.CommonApiRes
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.util.StringMD5
import java.security.MessageDigest

val logger = Logger("ShortV")

@SuppressLint("PackageManagerGetSignatures")
fun s(context: Context): Array<Signature> {
    return if (OsUtils.isPOrAbove()) {
        context.packageManager.getPackageInfo(
            BuildPropInternal.APP_PACKAGE_NAME,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo.apkContentsSigners
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(
            BuildPropInternal.APP_PACKAGE_NAME,
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
            val md5 = StringMD5.md5(String(hexChars))
            logger.d("s_index_$index: $md5")
            if (BuildPropInternal.SIGNATURES_MD5.split("|")[index] != md5
                && BuildPropInternal.SIGNATURES2_MD5.split("|")[index] != md5
            ) {
                match = false
            }
        }
    } catch (e: Throwable) {
        logger.e(e)
        match = false
    }

    return if (match) {
        logger.d("Match")
        run()
    } else {
        logger.d("Not match")
        null
    }
}

suspend fun vb(
    code: String,
    deviceId: String,
    onSuccess: (CommonApiRes) -> Unit,
    onError: (Throwable) -> Unit
) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        logger.d("vb")
    }

    if (errorTimes >= 24) {
        onSuccess(CommonApiRes.newBuilder().setResult(-1).setMsg("Too many failures.").build())
        return
    }

    withContext(Dispatchers.IO) {
        runCatching {
            api.verify(code, deviceId).let { call ->
                val res = call.execute().body()
                logger.d("vb success: $res")
                res?.let { wrapper ->
                    onSuccess(CommonApiRes.newBuilder().setResult(wrapper.result).apply {
                        wrapper.msg?.let { msg = it }
                        wrapper.k?.let { k = it }
                        wrapper.i?.let { i = it }
                        wrapper.j?.let { j = it }
                        wrapper.l?.let { l = it }
                        wrapper.m?.let { m = it }
                    }.build())

                }
            }
        }.onFailure {
            onError(it)
            errorTimes += 1
            if (BuildPropInternal.APP_IS_DEBUG) logger.e(it, "vb error")
        }
    }
}

var errorTimes = 0
val api: Api by lazy { Api.Factory.create() }