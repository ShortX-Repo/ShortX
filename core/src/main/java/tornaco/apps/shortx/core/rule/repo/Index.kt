package tornaco.apps.shortx.core.rule.repo

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
data class Index(
    val directActions: List<Item>,
    val rules: List<Item>,
)

@DoNotStrip
data class Item(
    val fileUrl: String,
    val title: String,
    val description: String,
    val author: String,
    val tag: String? = null,
    val icon: String? = null,
    val iconColor: String? = null,
)
