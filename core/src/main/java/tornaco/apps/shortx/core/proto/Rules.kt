package tornaco.apps.shortx.core.proto

import com.google.protobuf.Message
import com.google.protobuf.TextFormat
import tornaco.apps.shortx.core.proto.rule.Rule

val Rule.factsAndQuitFacts get() = this.factsList + this.quit.factsList

fun Message.containsTypeUrl(typeUrl: String): Boolean {
    return TextFormat.printer().printToString(this).contains(typeUrl)
}

fun Message.containsAnyTypeUrl(typeUrl: List<String>): Boolean {
    return TextFormat.printer().printToString(this).let { messageString ->
        typeUrl.any { messageString.contains(it) }
    }
}