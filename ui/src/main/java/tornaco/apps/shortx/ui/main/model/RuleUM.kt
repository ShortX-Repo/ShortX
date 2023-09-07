package tornaco.apps.shortx.ui.main.model

import tornaco.apps.shortx.core.proto.rule.Rule

data class RuleUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val isEnabled: Boolean,
    val factIcon: String,
    val factIconColor: ULong,
    val actionIcon: String,
    val actionIconColor: ULong,
    val runningInsCount: Int,
    val rule: Rule,
    val versionCode: Long,

    // For online.
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false,

    // For Set
    val isRuleSet: Boolean = false,
    val hasAnyEnabled: Boolean = false,
    val itemsCount: Int = 0,
)