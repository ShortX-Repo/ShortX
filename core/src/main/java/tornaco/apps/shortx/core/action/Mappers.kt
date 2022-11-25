package tornaco.apps.shortx.core.action

import tornaco.apps.shortx.core.proto.NoAction

fun ByteArrayWrapper.toAction(): Any {
    return NoAction.newBuilder().build()
}