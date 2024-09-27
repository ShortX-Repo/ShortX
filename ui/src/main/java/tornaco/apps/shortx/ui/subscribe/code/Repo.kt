package tornaco.apps.shortx.ui.subscribe.code

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.android.sec.net.S
import tornaco.apps.shortx.core.CommonApiResWrapper
import tornaco.apps.shortx.core.isSuccess
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import tornaco.apps.shortx.ui.subscribe.util.setLVLToken
import java.io.IOException

// 133 4455 3344
const val MIN_LENGTH = 6

sealed class VerifyResult {
    data class Success(val data: CommonApiResWrapper) : VerifyResult()
    data object Fail : VerifyResult()
    data class Error(val msg: String) : VerifyResult()
    data object NetworkError : VerifyResult()
}

private var cachedSubscriptionConfig: SubscriptionConfig2? = null
private var cacheCodeRemaining: CodeRemaining? = null
private val gson by lazy { Gson() }

class Repo(private val context: Context) {
    private val logger = Logger("Repo")

    suspend fun verify(
        code: String,
    ): VerifyResult = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            if (code.length < MIN_LENGTH || code.isDigitsOnly()) {
                delay(4567)
                return@runCatching VerifyResult.Fail
            }

            val binding = DeviceCodeBinding.from(code, DeviceId.requireDeviceId())
            val body = binding.toRequestBody()
            val res = CodeApi.create(context).verify(body)
            if (res.isSuccess) {
                VerifyResult.Success(res)
            } else {
                VerifyResult.Fail
            }
        }.getOrElse {
            if (it is IOException) {
                VerifyResult.NetworkError
            } else {
                VerifyResult.Error(it.message ?: it::class.java.name)
            }
        }
    }

    suspend fun getSubscriptionConfig2(): tornaco.apps.shortx.ui.base.Result<SubscriptionConfig2> =
        kotlin.runCatching {
            tornaco.apps.shortx.ui.base.Result.Success(
                cachedSubscriptionConfig ?: CodeApi.create(context).getSubscriptionConfig2().let {
                    val config: SubscriptionConfig2 =
                        gson.fromJson(it.msg, SubscriptionConfig2::class.java)
                    cachedSubscriptionConfig = config
                    config
                }
            )
        }.getOrElse {
            logger.e(it, "getSubscriptionConfig2")
            tornaco.apps.shortx.ui.base.Result.Error(it)
        }

    suspend fun getCodeRemaining(code: String): tornaco.apps.shortx.ui.base.Result<CodeRemaining> =
        kotlin.runCatching {
            tornaco.apps.shortx.ui.base.Result.Success(
                cacheCodeRemaining ?: CodeApi.create(context).verifyActivationCode(code).let {
                    if (it.isSuccess) {
                        S.c(code, it.k)
                    } else {
                        logger.logOnDebug("getCodeRemaining verify fail, uncheck")
                        setLVLToken(null)
                    }
                    require(it.isSuccess) { "verify fail" }

                    val config: CodeRemaining =
                        gson.fromJson(it.msg, CodeRemaining::class.java)
                    cacheCodeRemaining = config
                    config
                }
            )
        }.getOrElse {
            logger.e(it, "getCodeRemaining")
            tornaco.apps.shortx.ui.base.Result.Error(it)
        }
}