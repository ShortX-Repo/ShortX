package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.common.StringPair

fun Pair<String, String>.toStringPair() = StringPair.newBuilder()
    .setFirst(first)
    .setSecond(second)
    .build()