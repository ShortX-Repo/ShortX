package tornaco.apps.shortx.core.rule.repo

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
data class Index(
    val directActions: List<Item>,
    val rules: List<Item>,
    val updateTimeMillis: Long
)

@DoNotStrip
data class Item(
    val id: String,
    val fileUrl: String,
    val title: String,
    val description: String,
    val versionCode: Long,
    val updateTimeMillis: Long,
    val author: String,
    val tags: List<String> = emptyList(),
    val icon: String? = null,
    val iconColor: String? = null,
)
