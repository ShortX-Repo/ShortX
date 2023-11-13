package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.common.RegexMatchOptions

data class RegexMatchData(
    val regex: String,
    val options: RegexMatchOptions
)