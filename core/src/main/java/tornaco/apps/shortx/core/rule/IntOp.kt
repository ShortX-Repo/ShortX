package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.common.IntOp

fun IntOp.toDisplayName(): String {
    return when (this) {
        IntOp.IntGreaterThan -> ">"
        IntOp.IntGreaterThanOrEQ -> ">="
        IntOp.IntLessThan -> "<"
        IntOp.IntLessThanOrEQ -> "<="
        IntOp.IntEqualTo -> "=="
        IntOp.UNRECOGNIZED -> "N/A"
    }
}