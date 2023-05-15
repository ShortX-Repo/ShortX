package tornaco.apps.shortx.ui.subscribe.code

import android.content.Context
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.EntryPoint
import java.io.IOException

const val MIN_LENGTH = 12

sealed class VerifyResult {
    data class Success(val data: CommonRes) : VerifyResult()
    object Fail : VerifyResult()
    data class Error(val msg: String) : VerifyResult()
    object NetworkError : VerifyResult()
}

private var cachedSubscriptionConfig: SubscriptionConfig? = null


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

            val binding = DeviceCodeBinding.from(code, EntryPoint.getDeviceId(context))
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

    suspend fun getSubscriptionConfig(): tornaco.apps.shortx.ui.base.Result<SubscriptionConfig> =
        kotlin.runCatching {
            tornaco.apps.shortx.ui.base.Result.Success(
                cachedSubscriptionConfig ?: CodeApi.create(context).getSubscriptionConfig().apply {
                    cachedSubscriptionConfig = this
                }
            )
        }.getOrElse {
            logger.e(it, "getSubscriptionConfig")
            tornaco.apps.shortx.ui.base.Result.Error(it)
        }
}