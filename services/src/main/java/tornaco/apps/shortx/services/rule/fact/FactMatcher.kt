package tornaco.apps.shortx.services.rule.fact

import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage

interface FactMatcher {
    sealed interface MatcherResult {
        data class Match(val factTag: String) : MatcherResult
        data object NotMatch : MatcherResult
    }

    fun match(actualFact: ProtoMessage, ruleFacts: List<ProtoAny>): MatcherResult
}

val FactMatcher.MatcherResult.isMatch get() = this is FactMatcher.MatcherResult.Match
