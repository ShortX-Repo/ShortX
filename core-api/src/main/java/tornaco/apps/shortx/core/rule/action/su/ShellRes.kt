package tornaco.apps.shortx.core.rule.action.su

import tornaco.apps.shortx.core.proto.common.ShellRes

fun ShellRes.shellResToString(): String {
    return "${this.outList.joinToString()}${System.lineSeparator()}${System.lineSeparator()}${System.lineSeparator()}${this.errList.joinToString()}"
}