@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.allFactClasses
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class FactCategory(val i18NKey: String, val icon: String) {
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    System("ui.action.system.settings", Remix.System.settings_2_line),
    Misc("ui.action.misc", Remix.System.settings_2_line),
}

@Composable
fun FactSelector(
    close: () -> Unit, onSelected: (Fact) -> Unit
) {
    ShortXTheme {
        ShortXAppBarScaffold(
            title = LocalI18N.current["ui.fact.select"],
            onBackPressed = { close() },
        ) {
            val setFact: (Fact) -> Unit = {
                onSelected(it)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                val searchBarState = rememberSearchBarState(
                    itemCount = allFactClasses.size,
                    keywordChanged = { })
                SearchBar(searchBarState = searchBarState)

                val subscribeState by LVLStateHolder.collectAsState()

                AppFacts(searchBarState.keyword, subscribeState, setFact = setFact)
                StandardSpacer()

                SystemFacts(searchBarState.keyword, subscribeState, setFact = setFact)
                StandardSpacer()

                MiscFacts(searchBarState.keyword, subscribeState, setFact = setFact)
                StandardSpacer()
            }
        }
    }
}

@Composable
fun FactCategoryTitle(category: FactCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}