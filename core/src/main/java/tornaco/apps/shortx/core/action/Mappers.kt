package tornaco.apps.shortx.core.action

import tornaco.apps.shortx.core.proto.NoAction

fun ActionBytes.toAction(): Any {
    return NoAction.newBuilder().build()
}