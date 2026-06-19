package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import tornaco.apps.shortx.core.i18n.I18N_R

enum class ConditionCategory(val i18NKey: String, val icon: String) {
    All(I18N_R.UI_FACT_ALL, Remix.Buildings.store_2_line),
    Ui("UI", Remix.System.apps_2_line),
    Apps(I18N_R.UI_ACTION_CATEGORY_APP, Remix.System.apps_2_line),
    ScreenTime(I18N_R.UI_FACT_CATEGORY_SCREEN_TIME, Remix.System.history_line),
    State(I18N_R.UI_FACT_CATEGORY_STATE, Remix.System.settings_2_fill),
    Device(I18N_R.UI_CONDITION_CATEGORY_DEVICE, Remix.Map.riding_fill),
    System(I18N_R.UI_FACT_CATEGORY_SYSTEM, Remix.System.settings_2_line),
    Advanced(I18N_R.UI_FACT_CATEGORY_ADVANCED, Remix.Development.bug_line),
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ConditionSelector(
    close: () -> Unit, onSelected: (Condition) -> Unit
) {
    ShortXTheme {
        ShortXAppBarScaffold(
            title = LocalI18N.current[I18N_R.UI_CONDITIONS_ADD],
            onBackPressed = { close() },
        ) {
            Column(
                modifier = Modifier
            ) {
                var selectedFilter by remember { mutableStateOf(ConditionCategory.All) }
                val searchBarState = rememberSearchBarState(
                    itemCount = allConditionClasses.size,
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
                    ConditionCategory.entries.forEachIndexed { index, category ->
                        OutlinedToggleButton(
                            checked = selectedFilter == category,
                            onCheckedChange = {
                                selectedFilter = category
                            },
                            modifier = Modifier.padding(start = 2.dp),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                ConditionCategory.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        ) {
                            Text(text = LocalI18N.current[category.i18NKey])
                        }
                    }
                }

                val setCondition: (Condition) -> Unit = {
                    onSelected(it)
                }

                val subscribeState by LVLStateHolder.collectAsState()

                UIConditions(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setCondition = setCondition
                )
                StandardSpacer()

                AppConditions(
                    keyword = searchBarState.keyword,
                    subscribeState = subscribeState,
                    selectedFilter = selectedFilter,
                    setCondition = setCondition
                )
                StandardSpacer()

                SystemConditions(
                    searchBarState.keyword,
                    subscribeState,
                    selectedFilter,
                    setCondition = setCondition
                )
                StandardSpacer()

                OtherConditions(
                    searchBarState.keyword,
                    subscribeState,
                    selectedFilter,
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
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}