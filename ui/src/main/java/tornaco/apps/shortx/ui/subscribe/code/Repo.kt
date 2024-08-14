package tornaco.apps.shortx.ui.subscribe.code

import android.content.Context
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.CommonApiResWrapper
import tornaco.apps.shortx.core.isSuccess
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import java.io.IOException

const val MIN_LENGTH = 12

sealed class VerifyResult {
    data class Success(val data: CommonApiResWrapper) : VerifyResult()
    data object Fail : VerifyResult()
    data class Error(val msg: String) : VerifyResult()
    data object NetworkError : VerifyResult()
}

private var cachedSubscriptionConfig: SubscriptionConfig2? = null


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
                cachedSubscriptionConfig ?: CodeApi.create(context).getSubscriptionConfig2().apply {
                    cachedSubscriptionConfig = this
                }
            )
        }.getOrElse {
            logger.e(it, "getSubscriptionConfig")
            tornaco.apps.shortx.ui.base.Result.Error(it)
        }
}