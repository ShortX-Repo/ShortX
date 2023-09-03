package tornaco.apps.shortx.ui.main.model

import tornaco.apps.shortx.ui.main.DASortBy
import tornaco.apps.shortx.ui.main.RuleSortBy

data class MainState(
    val isHideSubscribeInfo: Boolean = false,
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = false,
    val isShowRuleDescription: Boolean = true,
    val rules: List<RuleUM> = emptyList(),
    val ruleSortBy: RuleSortBy = RuleSortBy.CreateTime,
    val das: List<DirectActionUM> = emptyList(),
    val daSortBy: DASortBy = DASortBy.CreateTime,
    val isShowDetailedDAItem: Boolean = true,
    val isLoading: Boolean = true,
    val isAudioRecording: Boolean = false
)
