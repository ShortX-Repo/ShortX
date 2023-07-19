package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toProto


fun parseShareContentDA(content: String): DirectAction? {
    return runCatching {
        DirectAction.newBuilder(
            DirectAction.parseFrom(
                DirectAction.newBuilder().toProto(content).toByteArray()
            )?.apply {
                requireNotNull(this.id) { "Id is null. invalid da?" }
                requireNotNull(this.title) { "Title is null. invalid da?" }
            })
            .setLastUpdateTime(System.currentTimeMillis()).build()
    }.getOrNull()
}


fun parseShareContentRule(content: String): Rule? {
    return runCatching {
        Rule.newBuilder(
            Rule.parseFrom(Rule.newBuilder().toProto(content).toByteArray())?.apply {
                requireNotNull(this.id) { "Id is null. invalid rule?" }
                requireNotNull(this.title) { "Title is null. invalid rule?" }
            }).build()
    }.getOrNull()
}