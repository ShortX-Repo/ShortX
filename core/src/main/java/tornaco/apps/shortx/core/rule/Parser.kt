package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleList
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("Parser")

fun parseShareContentDA(content: String): DirectAction? {
    return runCatching {
        DirectAction.newBuilder(
            DirectAction.parseFrom(
                DirectAction.newBuilder().toProto(content).toByteArray()
            )?.apply {
                requireNotNull(this.id) { "Id is null. invalid da?" }
                requireNotNull(this.title) { "Title is null. invalid da?" }
            }).build()
    }.getOrElse {
        logger.e(it, "parseShareContentDA")
        null
    }
}


fun parseShareContentRule(content: String): Rule? {
    return runCatching {
        Rule.newBuilder(
            Rule.parseFrom(Rule.newBuilder().toProto(content).toByteArray())?.apply {
                requireNotNull(this.id) { "Id is null. invalid rule?" }
                requireNotNull(this.title) { "Title is null. invalid rule?" }
            })
            .setIsEnabled(false).build()
    }.getOrElse {
        logger.e(it, "parseShareContentRule")
        null
    }
}

fun parseShareContentRuleSet(content: String): RuleSet? {
    return runCatching {
        val set = RuleSet.newBuilder(
            RuleSet.parseFrom(RuleSet.newBuilder().toProto(content).toByteArray())
        ).build()

        val allRules = set.ruleList.rulesList.map {
            Rule.newBuilder(it).setIsEnabled(false).build()
        }

        RuleSet.newBuilder(set)
            .clearRuleList()
            .setRuleList(RuleList.newBuilder().addAllRules(allRules).build()).build()
    }.getOrElse {
        logger.e(it, "parseShareContentRuleSet")
        null
    }
}

fun parseShareContentDASet(content: String): DirectActionSet? {
    return runCatching {
        DirectActionSet.newBuilder(
            DirectActionSet.parseFrom(DirectActionSet.newBuilder().toProto(content).toByteArray())
        ).build()
    }.getOrElse {
        logger.e(it, "parseShareContentDASet")
        null
    }
}