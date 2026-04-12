package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    All("ui.fact.all", Remix.Buildings.store_2_line),
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    Input("ui.fact.category.input", Remix.System.settings_2_line),
    ScreenTime("ui.fact.category.screen.time", Remix.System.history_line),
    State("ui.fact.category.state", Remix.System.settings_2_fill),
    Power("ui.fact.category.power", Remix.Map.riding_fill),
    Sensor("ui.fact.sensor", Remix.System.settings_2_line),
    System("ui.fact.category.system", Remix.System.settings_2_line),
    Advanced("ui.fact.category.advanced", Remix.Development.bug_line),
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
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
            var selectedFilter by remember { mutableStateOf(FactCategory.All) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val searchBarState = rememberSearchBarState(
                    itemCount = allFactClasses.size,
                    keywordChanged = { })
                SearchBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    searchBarState = searchBarState
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FactCategory.entries.forEachIndexed { index, category ->
                        OutlinedToggleButton(
                            checked = selectedFilter == category,
                            onCheckedChange = {
                                selectedFilter = category
                            },
                            modifier = Modifier.padding(start = 2.dp),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                FactCategory.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        ) {
                            Text(text = LocalI18N.current[category.i18NKey])
                        }
                    }
                }

                val subscribeState by LVLStateHolder.collectAsState()

                AppFacts(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setFact = setFact
                )
                StandardSpacer()

                SystemFacts(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setFact = setFact
                )
                StandardSpacer()

                SensorFacts(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setFact = setFact
                )
                StandardSpacer()

                MiscFacts(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setFact = setFact
                )
                StandardSpacer()
            }
        }
    }
}

@Composable
fun FactCategoryTitle(category: FactCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}