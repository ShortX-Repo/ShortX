package tornaco.apps.shortx.ui.addrule.fact

import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.defaultNewFactId
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.base.WithState
import tornaco.apps.shortx.ui.base.WithStateImpl
import tornaco.apps.shortx.ui.base.i18NIns

data class FactContentState(
    val facts: List<Fact> = emptyList(),
    val isChanged: Boolean = false,
)

fun FactContentStateHandler.asFactHandler(): FactHandler {
    return FactHandler(
        addFact = this::addFact,
        removeFact = this::removeFact,
        copy = { factId ->
            UiClipboard.copy(UiClipboard.ClipboardContent.FactContent(
                facts.first { it.id == factId }
            ))
        },
        insertAbove = { fact, anchorId ->
            insertFactAbove(fact, anchorId)
        },
        insertBelow = { fact, anchorId ->
            insertFactBelow(fact, anchorId)
        }
    )
}

class FactContentStateHandler(
    private val withState: WithStateImpl<FactContentState> = WithStateImpl { FactContentState() }
) : WithState<FactContentState> by withState {
    private val shortXManager = tornaco.apps.shortx.core.shortXManager
    private val logger = Logger("FactContentStateHandler")
    private val i18N = i18NIns

    val facts get() = state.value.facts

    fun addFact(fact: Fact) {
        logger.d("addFact: $fact")
        if (!state.value.facts.contains(fact)) {
            updateState {
                copy(
                    facts = state.value.facts.toMutableList().apply {
                        val existingIndex = indexOfFirst { it.id == fact.id }
                        if (existingIndex >= 0) {
                            set(existingIndex, fact)
                        } else {
                            add(fact)
                        }
                    },
                    isChanged = true
                )
            }
        }
    }

    fun removeFact(fact: Fact) {
        if (state.value.facts.contains(fact)) {
            updateState {
                copy(
                    facts = state.value.facts.toMutableList().apply {
                        remove(fact)
                    },
                    isChanged = true
                )
            }
        }
    }

    fun insertFactAbove(fact: Fact, anchorId: String) {
        val refinedFact = requireNotNull(
            fact.toProtoFact(overrideId = defaultNewFactId()).pack_()
                .toFact(shortXManager)
        ) {
            "refinedFact is null"
        }
        updateState {
            copy(facts = facts.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex, refinedFact)
                } else {
                    add(refinedFact)
                }
            }, isChanged = true)
        }
    }

    fun insertFactBelow(fact: Fact, anchorId: String) {
        val refinedFact = requireNotNull(
            fact.toProtoFact(overrideId = defaultNewFactId()).pack_()
                .toFact(shortXManager)
        ) {
            "refinedFact is null"
        }
        updateState {
            copy(facts = facts.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex + 1, refinedFact)
                } else {
                    add(refinedFact)
                }
            }, isChanged = true)
        }
    }

    fun init(
        facts: List<Fact>,
    ) {
        updateState { copy(facts = facts) }
    }
}