package tornaco.apps.shortx.core

import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.proto.common.CommonApiRes

private const val SUCCESS = 0

val CommonApiRes.isSuccess get() = result == SUCCESS
val CommonApiResWrapper.isSuccess get() = result == SUCCESS


@DoNotStrip
data class CommonApiResWrapper(
    val result: Int,
    val msg: String?,
    val k: String?,
    val i: String?,
    // Dummy
    val j: String?,
    val l: String?,
    val m: String?,
)