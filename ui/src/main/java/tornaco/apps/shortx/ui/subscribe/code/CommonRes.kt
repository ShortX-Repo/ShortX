package tornaco.apps.shortx.ui.subscribe.code

import com.google.errorprone.annotations.Keep

@Keep
data class CommonRes(
    var result: Int,
    val msg: String,
    val k: String?,
)

private const val SUCCESS = 0

val CommonRes.isSuccess get() = result == SUCCESS
