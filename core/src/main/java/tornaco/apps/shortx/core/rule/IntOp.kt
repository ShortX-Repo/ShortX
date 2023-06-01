package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.common.IntOp

fun IntOp.toDisplayName(): String {
    return when (this) {
        IntOp.IntGreaterThan -> "${name.removePrefix("Int")} (>)"
        IntOp.IntGreaterThanOrEQ -> "${name.removePrefix("Int")} (>=)"
        IntOp.IntLessThan -> "${name.removePrefix("Int")} (<)"
        IntOp.IntLessThanOrEQ -> "${name.removePrefix("Int")} (<=)"
        IntOp.IntEqualTo -> "${name.removePrefix("Int")} (==)"
        IntOp.UNRECOGNIZED -> "N/A"
    }
}