package tornaco.apps.shortx.ui.main

import tornaco.apps.shortx.ui.base.i18NIns

sealed interface CommonSetFilter {
    val title: String
    val isStock: Boolean
    val filterId: String

    data object PinnedAllFlat : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.flat"]
        override val isStock: Boolean = true
        override val filterId: String
            get() = "PinnedAllFlat"
    }

    data object PinnedAll : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.all"]
        override val isStock: Boolean = true
        override val filterId: String
            get() = "PinnedAll"
    }

    data object PinnedItems : CommonSetFilter {
        override val title: String
            get() = i18NIns["ui.common.pinned"]
        override val isStock: Boolean = true
        override val filterId: String
            get() = "PinnedItems"
    }

    data class UserSet(val id: String, override val title: String) : CommonSetFilter {
        override val isStock: Boolean = false
        override val filterId: String get() = id
    }

    companion object {
        fun prebuiltOfId(id: String?): CommonSetFilter =
            when (id) {
                PinnedAllFlat.filterId -> PinnedAllFlat
                PinnedItems.filterId -> PinnedItems
                else -> PinnedAll
            }
    }
}

val CommonSetFilter.userSetIdOrNull: String? get() = if (this is CommonSetFilter.UserSet) this.id else null