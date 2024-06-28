@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.allConditionClasses
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ConditionCategory(val i18NKey: String, val icon: String) {
    Ui("UI", Remix.System.apps_2_line),
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    System("ui.action.system.settings", Remix.System.settings_2_line),
    Other("ui.action.misc", Remix.System.settings_2_line),
}


@Composable
fun ConditionSelector(
    close: () -> Unit, onSelected: (Condition) -> Unit
) {
    ShortXTheme {
        ShortXAppBarScaffold(
            title = LocalI18N.current["ui.conditions.add"],
            onBackPressed = { close() },
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                val searchBarState = rememberSearchBarState(
                    itemCount = allConditionClasses.size,
                    keywordChanged = { })
                SearchBar(searchBarState = searchBarState)

                val setCondition: (Condition) -> Unit = {
                    onSelected(it)
                }

                val subscribeState by LVLStateHolder.collectAsState()

                UIConditions(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    setCondition = setCondition
                )
                StandardSpacer()

                AppConditions(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    setCondition = setCondition
                )
                StandardSpacer()

                SystemConditions(
                    searchBarState.keyword,
                    subscribeState,
                    setCondition = setCondition
                )
                StandardSpacer()

                OtherConditions(
                    searchBarState.keyword,
                    subscribeState,
                    setCondition = setCondition
                )
                StandardSpacer()
            }
        }
    }
}

@Composable
fun ConditionCategoryTitle(category: ConditionCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}