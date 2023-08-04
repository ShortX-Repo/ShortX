package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent

val ContextSrcEvent.isFromRule
    get() = this == ContextSrcEvent.ContextSrc_RuleHook
            || this == ContextSrcEvent.ContextSrc_Rule
            || this == ContextSrcEvent.ContextSrc_RuleQuit
            || this == ContextSrcEvent.ContextSrc_ExecuteRuleActions

val ContextSrcEvent.isFromDA
    get() = this == ContextSrcEvent.ContextSrc_DirectAction
