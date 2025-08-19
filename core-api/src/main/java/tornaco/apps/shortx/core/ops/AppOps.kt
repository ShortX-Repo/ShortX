package tornaco.apps.shortx.core.ops

import tornaco.apps.shortx.core.compat.AppOpsManagerCompat


fun Int.opsLabelKey(): String {
    return when (this) {
        AppOpsManagerCompat.OP_GPS -> "ui.fact.op.start.location"
        AppOpsManagerCompat.OP_CAMERA -> "ui.fact.op.start.camera"
        AppOpsManagerCompat.OP_RECORD_AUDIO -> "ui.fact.op.start.audio"
        else -> ""
    }
}