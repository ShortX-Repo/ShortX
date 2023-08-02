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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.allConditionClasses
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.main.SubscribeStateViewModel
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ConditionCategory(val i18NKey: String, val icon: String) {
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    System("ui.action.system.settings", Remix.System.settings_2_line),
    Other("Misc", Remix.System.settings_2_line),
}

class ConditionSelectorDialogState<ARGS>(val onSelected: (Condition, ARGS?) -> Unit) {
    var isShowing by mutableStateOf(false)
    var args: ARGS? = null

    fun show(args: ARGS? = null) {
        isShowing = true
        this.args = args
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun <ARGS> rememberConditionSelectorDialogState(onSelected: (Condition, ARGS?) -> Unit): ConditionSelectorDialogState<ARGS> {
    return remember {
        ConditionSelectorDialogState(onSelected)
    }
}

@Composable
fun <ARGS> ConditionSelectorDialog(state: ConditionSelectorDialogState<ARGS>) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ConditionSelector(state)
        }
    }
}

@Composable
private fun <ARGS> ConditionSelector(
    state: ConditionSelectorDialogState<ARGS>
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
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    val searchBarState = rememberSearchBarState(
                        itemCount = allConditionClasses.size,
                        keywordChanged = { })
                    SearchBar(searchBarState = searchBarState)

                    val setCondition: (Condition) -> Unit = {
                        state.onSelected(it, state.args)
                        state.dismiss()
                    }

                    val subscribeStateViewModel = hiltViewModel<SubscribeStateViewModel>()
                    LaunchedEffect(subscribeStateViewModel) {
                        subscribeStateViewModel.refreshStates()
                    }
                    DisposableEffectWithLifeCycle(onResume = {
                        subscribeStateViewModel.refreshStates()
                    })
                    val subscribeState by subscribeStateViewModel.state.collectAsState()

                    AppConditions(
                        searchBarState.keyword,
                        subscribeState,
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
        modifier = Modifier.padding(vertical = 12.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}