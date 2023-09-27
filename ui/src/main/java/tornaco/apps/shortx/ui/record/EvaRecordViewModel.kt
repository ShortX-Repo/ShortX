package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.context.isFromDA
import tornaco.apps.shortx.core.context.isFromRule
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.i18NIns
import javax.inject.Inject

sealed interface EvaRecord {
    val formattedTimeStr: String
    val timeMillis: Long

    val canViewSrc: Boolean

    fun filterSrc(evaluateContext: EvaluateContext): Boolean

    data class FactPublishRecordUM(
        val factIns: Fact,
        override val formattedTimeStr: String,
        val record: FactPublishRecord
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp
        override val canViewSrc: Boolean
            get() = false

        override fun filterSrc(evaluateContext: EvaluateContext): Boolean {
            return true
        }
    }

    data class ConditionEvaluateRecordUM(
        val actionIns: Condition,
        override val formattedTimeStr: String,
        val record: ConditionEvaluateRecord,
        override val canViewSrc: Boolean
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp

        override fun filterSrc(evaluateContext: EvaluateContext): Boolean {
            return evaluateContext.srcId == record.context.srcId
        }
    }

    data class ActionEvaluateRecordUM(
        val actionIns: Action,
        override val formattedTimeStr: String,
        val record: ActionEvaluateRecord,
        override val canViewSrc: Boolean
    ) : EvaRecord {
        override val timeMillis: Long
            get() = record.timeStamp

        override fun filterSrc(evaluateContext: EvaluateContext): Boolean {
            return evaluateContext.srcId == record.context.srcId
        }
    }
}

data class EvaRecordState(
    val data: List<EvaRecord> = emptyList(),
    val isLoading: Boolean = false,

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
    val onlyShowSrc: EvaluateContext? = null
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

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class EvaRecordViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel(),
    ContainerHost<EvaRecordState, String> {
    private val logger = Logger("EvaRecordViewModel")

    override val container: Container<EvaRecordState, String> = container(
        initialState = EvaRecordState()
    )

    fun setOnlyShowSrcId(onlyShowSrcId: String) {
        val title =
            (shortXManager.getDirectActionById(onlyShowSrcId)?.title) ?: (shortXManager.getRuleById(
                onlyShowSrcId
            )?.title)
        val context = EvaluateContext.newBuilder().setSrcId(onlyShowSrcId)
            .setSrcTitle(title.fallbackOnEmpty("")).build()

        setOnlyShowSrcId(context)
    }

    fun setOnlyShowSrcId(onlyShowSrc: EvaluateContext?) = intent {
        val showSrc = if (state.onlyShowSrc == onlyShowSrc) {
            null
        } else {
            onlyShowSrc
        }
        reduce {
            state.copy(onlyShowSrc = showSrc)
        }
        if (showSrc != null) {
            postSideEffect(i18NIns["ui.common.only.show"] + " ${showSrc.srcTitle}")
        } else {
            postSideEffect(i18NIns["ui.common.show.all"])
        }
        loadRecords()
    }

    fun loadSwitch() = intent {
        reduce {
            state.copy(
                isRecordEnabled = shortXManager.isEvaluateRecordEnabled(),
            )
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
                state.copy(isLoading = true)
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

            val onlyShowSrc = state.onlyShowSrc
            val finalRecords = withContext(Dispatchers.IO) {
                (facts + conditions + actions)
                    .filter {
                        onlyShowSrc == null || it.filterSrc(onlyShowSrc)
                    }
                    .sortedByDescending {
                        it.timeMillis
                    }
            }

            reduce {
                state.copy(
                    data = finalRecords,
                    isLoading = false
                )
            }
        }

}