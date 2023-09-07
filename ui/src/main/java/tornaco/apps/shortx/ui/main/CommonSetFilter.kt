package tornaco.apps.shortx.ui.main

import tornaco.apps.shortx.ui.base.i18NIns

sealed interface CommonSetFilter {
    val title: String

    data object PinnedAllFlat : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.flat"]
    }

    data object PinnedAll : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.all"]
    }

    data class UserSet(val id: String, override val title: String) : CommonSetFilter
}

val CommonSetFilter.id: String? get() = if (this is CommonSetFilter.UserSet) this.id else null