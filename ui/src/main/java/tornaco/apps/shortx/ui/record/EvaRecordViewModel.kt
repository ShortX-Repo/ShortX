package tornaco.apps.shortx.ui.record

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.context.isFromDA
import tornaco.apps.shortx.core.context.isFromRule
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.LoadState

sealed interface EvaRecord {
    val formattedTimeStr: String
    val timeMillis: Long

    val canViewSrc: Boolean

    data class FactPublishRecordUM(
        val factIns: Fact,
        override val formattedTimeStr: String,
        val record: FactPublishRecord
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp
        override val canViewSrc: Boolean
            get() = false
    }

    data class ConditionEvaluateRecordUM(
        val actionIns: Condition,
        override val formattedTimeStr: String,
        val record: ConditionEvaluateRecord,
        override val canViewSrc: Boolean
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp
    }

    data class ActionEvaluateRecordUM(
        val actionIns: Action,
        override val formattedTimeStr: String,
        val record: ActionEvaluateRecord,
        override val canViewSrc: Boolean
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp
    }
}

data class EvaRecordState(
    val loadState: LoadState<List<EvaRecord>> = LoadState.Loading,
    val factSetting: ViewRecordType.Fact = ViewRecordType.Fact(show = true),
    val conditionSetting: ViewRecordType.Condition = ViewRecordType.Condition(
        showMatch = true,
        showNotMatch = true,
        showError = true
    ),
    val actionSetting: ViewRecordType.Action = ViewRecordType.Action(
        showSuccess = true,
        showError = true
    ),
    val isRecordEnabled: Boolean = false,
)

sealed interface ViewRecordType {
    data class Fact(val show: Boolean) : ViewRecordType

    data class Condition(
        val showMatch: Boolean,
        val showNotMatch: Boolean,
        val showError: Boolean
    ) : ViewRecordType

    data class Action(
        val showSuccess: Boolean,
        val showError: Boolean,
    ) : ViewRecordType
}

class EvaRecordViewModel : ViewModel(), ContainerHost<EvaRecordState, Nothing> {
    private val logger = Logger("EvaRecordViewModel")

    override val container: Container<EvaRecordState, Nothing> = container(
        initialState = EvaRecordState()
    )

    fun loadSwitch() = intent {
        reduce {
            state.copy(isRecordEnabled = shortXManager.isEvaluateRecordEnabled())
        }
    }

    fun toggleRecordEnable(enable: Boolean) = intent {
        shortXManager.setEvaluateRecordEnabled(enable)
        loadSwitch()
    }

    fun showFact(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(factSetting = state.factSetting.copy(show = show))
        }
    }

    fun showConditionMatch(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(conditionSetting = state.conditionSetting.copy(showMatch = show))
        }
    }

    fun showConditionNotMatch(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(conditionSetting = state.conditionSetting.copy(showNotMatch = show))
        }
    }

    fun showConditionError(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(conditionSetting = state.conditionSetting.copy(showError = show))
        }
    }

    fun showActionSuccess(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(actionSetting = state.actionSetting.copy(showSuccess = show))
        }
    }

    fun showActionError(show: Boolean) = intent {
        loadRecords()
        reduce {
            state.copy(actionSetting = state.actionSetting.copy(showError = show))
        }
    }

    fun clearRecords() = intent {
        shortXManager.clearEvaluateRecords()
        loadRecords()
    }

    fun loadRecords() =
        intent {
            reduce {
                state.copy(loadState = LoadState.Loading)
            }

            val facts = withContext(Dispatchers.IO) {
                if (state.factSetting.show) {
                    shortXManager.getFactPublishRecords()
                        .reversed()
                        .mapNotNull { record ->
                            val factIns = kotlin.runCatching {
                                record.fact.toFact(shortXManager)
                            }.getOrElse {
                                logger.e(it, "Map Fact record error")
                                null
                            }
                            factIns?.let {
                                EvaRecord.FactPublishRecordUM(
                                    factIns = factIns,
                                    formattedTimeStr = DateUtils.formatLongForMessageTime(record.timeStamp),
                                    record = record
                                )
                            }
                        }
                } else {
                    emptyList()
                }
            }

            val conditions = withContext(Dispatchers.IO) {
                shortXManager.getConditionEvaluateRecords()
                    .filter {
                        (state.conditionSetting.showMatch && it.isMatch)
                                || (state.conditionSetting.showNotMatch && !it.isMatch)
                                || (state.conditionSetting.showError && !it.isSuccess)
                    }
                    .reversed()
                    .mapNotNull { record ->
                        val actionIns = kotlin.runCatching {
                            record.condition.toCondition(shortXManager)
                        }.getOrElse {
                            logger.e(it, "Map Condition record error")
                            null
                        }
                        actionIns?.let {
                            EvaRecord.ConditionEvaluateRecordUM(
                                actionIns = actionIns,
                                formattedTimeStr = DateUtils.formatLongForMessageTime(record.timeStamp),
                                record = record,
                                canViewSrc = record.context.srcEvent.isFromRule || record.context.srcEvent.isFromDA
                            )
                        }
                    }
            }

            val actions = withContext(Dispatchers.IO) {
                shortXManager.getActionEvaluateRecords()
                    .filter {
                        (state.actionSetting.showSuccess && it.isSuccess)
                                || (state.actionSetting.showError && !it.isSuccess)
                    }
                    .reversed()
                    .apply {
                        logger.d("${this.map { "${it.context.srcTitle} - ${it.context.srcId} - ${it.message}" }}")
                    }
                    .mapNotNull { record ->
                        val actionIns = kotlin.runCatching {
                            record.action.toAction(shortXManager)
                        }.getOrElse {
                            logger.e(it, "Map Action record error")
                            null
                        }
                        actionIns?.let {
                            EvaRecord.ActionEvaluateRecordUM(
                                actionIns = actionIns,
                                formattedTimeStr = DateUtils.formatLongForMessageTime(record.timeStamp),
                                record = record,
                                canViewSrc = record.context.srcEvent.isFromRule || record.context.srcEvent.isFromDA
                            )
                        }
                    }
            }

            val finalRecords = withContext(Dispatchers.IO) {
                (facts + conditions + actions)
                    .sortedByDescending {
                        it.timeMillis
                    }
            }

            reduce {
                state.copy(
                    loadState = LoadState.Success(data = finalRecords),
                )
            }
        }

}