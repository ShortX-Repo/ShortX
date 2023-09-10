package tornaco.apps.shortx.ui.main

import tornaco.apps.shortx.ui.base.i18NIns

sealed interface CommonSetFilter {
    val title: String
    val isStock: Boolean

    data object PinnedAllFlat : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.flat"]
        override val isStock: Boolean = true
    }

    data object PinnedAll : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.all"]
        override val isStock: Boolean = true
    }

    data class UserSet(val id: String, override val title: String) : CommonSetFilter {
        override val isStock: Boolean = false
    }
}

val CommonSetFilter.id: String? get() = if (this is CommonSetFilter.UserSet) this.id else null