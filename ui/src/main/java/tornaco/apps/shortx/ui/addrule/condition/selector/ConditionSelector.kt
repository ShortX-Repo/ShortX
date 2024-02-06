@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.allConditionClasses
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
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
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.conditions.add"]) },
                        actions = {
                            IconButton(onClick = {
                                close()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
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
}

@Composable
fun ConditionCategoryTitle(category: ConditionCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}