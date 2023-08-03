package tornaco.apps.shortx.ui.record

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
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

    data class FactPublishRecordUM(
        val factIns: Fact,
        override val formattedTimeStr: String,
        val record: FactPublishRecord
    ) : EvaRecord

    data class ConditionEvaluateRecordUM(
        val actionIns: Condition,
        override val formattedTimeStr: String,
        val record: ConditionEvaluateRecord
    ) : EvaRecord

    data class ActionEvaluateRecordUM(
        val actionIns: Action,
        override val formattedTimeStr: String,
        val record: ActionEvaluateRecord
    ) : EvaRecord
}

data class EvaRecordState(
    val loadState: LoadState<List<EvaRecord>> = LoadState.Loading
)

class EvaRecordViewModel : ViewModel(), ContainerHost<EvaRecordState, Nothing> {
    private val logger = Logger("EvaRecordViewModel")

    override val container: Container<EvaRecordState, Nothing> = container(
        initialState = EvaRecordState()
    )

    fun loadRecords() =
        intent {
            reduce {
                state.copy(loadState = LoadState.Loading)
            }

            val facts = withContext(Dispatchers.IO) {
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
            }


            val conditions = withContext(Dispatchers.IO) {
                shortXManager.getConditionEvaluateRecords()
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
                                record = record
                            )
                        }
                    }
            }

            val actions = withContext(Dispatchers.IO) {
                shortXManager.getActionEvaluateRecords()
                    .reversed()
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
                                record = record
                            )
                        }
                    }
            }

            reduce {
                state.copy(
                    loadState = LoadState.Success(data = facts + conditions + actions)
                )
            }
        }

}