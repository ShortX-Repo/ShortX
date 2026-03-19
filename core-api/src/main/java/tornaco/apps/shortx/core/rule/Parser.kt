package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleList
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("Parser")

fun parseShareContentDA(content: String): DirectAction? {
    return runCatching {
        parseShareContentDAOrThrow(content)
    }.getOrElse {
        logger.e(it, "parseShareContentDA")
        null
    }
}

fun parseShareContentDAOrThrow(content: String): DirectAction {
    return DirectAction.newBuilder(
        DirectAction.parseFrom(
            DirectAction.newBuilder().toProto(content).toByteArray()
        )?.apply {
            requireNotNull(this.id) { "Id is null. invalid da?" }
            requireNotNull(this.title) { "Title is null. invalid da?" }
        }).build()
}

fun parseShareContentRule(content: String): Rule? {
    return runCatching {
        parseShareContentRuleOrThrow(content)
    }.getOrElse {
        logger.e(it, "parseShareContentRule")
        null
    }
}

fun parseShareContentRuleOrThrow(content: String): Rule {
    return Rule.newBuilder(
        Rule.parseFrom(Rule.newBuilder().toProto(content).toByteArray())?.apply {
            requireNotNull(this.id) { "Id is null. invalid rule?" }
            requireNotNull(this.title) { "Title is null. invalid rule?" }
        })
        .setIsEnabled(false).build()
}

fun parseShareContentToggle(content: String): Toggle? {
    return runCatching {
        Toggle.newBuilder(
            Toggle.parseFrom(Toggle.newBuilder().toProto(content).toByteArray())?.apply {
                requireNotNull(this.id) { "Id is null. invalid Toggle?" }
                requireNotNull(this.title) { "Title is null. invalid Toggle?" }
            }).build()
    }.getOrElse {
        logger.e(it, "parseShareContentToggle")
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

fun parseShareContentCodeLibraryItem(content: String): CodeLibraryItem? {
    return runCatching {
        parseShareContentCodeLibraryItemOrThrow(content)
    }.getOrElse {
        logger.e(it, "parseShareContentCodeLibraryItem")
        null
    }
}

fun parseShareContentCodeLibraryItemOrThrow(content: String): CodeLibraryItem {
    return CodeLibraryItem.newBuilder(
        CodeLibraryItem.parseFrom(
            CodeLibraryItem.newBuilder().toProto(content).toByteArray()
        ).also {
            require(it.name.isNotBlank()) { "Name is blank. invalid code library item?" }
            require(it.content.isNotBlank()) { "Content is blank. invalid code library item?" }
        }
    ).build()
}