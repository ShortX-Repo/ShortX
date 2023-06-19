package tornaco.apps.shortx.core

import tornaco.apps.shortx.core.proto.common.ProcessName

fun String.toProcessName(userId: Int) =
    ProcessName.newBuilder().setProcessName(this).setUserId(userId).build()