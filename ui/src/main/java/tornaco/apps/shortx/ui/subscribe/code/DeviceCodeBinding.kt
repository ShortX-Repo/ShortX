package tornaco.apps.shortx.ui.subscribe.code

import android.os.Build
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private val gson by lazy { Gson() }

data class DeviceCodeBinding(
    val uuid: String,
    val deviceId: String,
    val deviceModel: String = Build.MODEL,
    val osName: String = Build.ID,
    val osVersion: Int = Build.VERSION.SDK_INT
) {
    companion object {
        fun from(code: String, deviceId: String): DeviceCodeBinding {
            return DeviceCodeBinding(uuid = code, deviceId = deviceId)
        }
    }
}

fun DeviceCodeBinding.toRequestBody() = gson.toJson(this).toRequestBody(
    "application/json; charset=utf-8".toMediaTypeOrNull()
)

