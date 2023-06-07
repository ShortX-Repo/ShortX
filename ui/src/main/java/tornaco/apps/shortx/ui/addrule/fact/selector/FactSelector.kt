@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class FactCategory(val i18NKey: String, val icon: String) {
    Apps("ui.action.category.app", Remix.System.apps_2_line),
}

class FactSelectorDialogState<ARGS>(val onSelected: (Fact, ARGS?) -> Unit) {
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
fun <ARGS> rememberFactSelectorDialogState(onSelected: (Fact, ARGS?) -> Unit): FactSelectorDialogState<ARGS> {
    return remember {
        FactSelectorDialogState(onSelected)
    }
}

@Composable
fun <ARGS> rememberFactSelectorDialogState(onSelected: (Fact) -> Unit): FactSelectorDialogState<ARGS> {
    return remember {
        FactSelectorDialogState { action, _ ->
            onSelected(action)
        }
    }
}

@Composable
fun <ARGS> FactSelectorDialog(state: FactSelectorDialogState<ARGS>) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            FactSelector(state)
        }
    }
}

@Composable
private fun <ARGS> FactSelector(
    state: FactSelectorDialogState<ARGS>
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        actions = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->
                val setFact: (Fact) -> Unit = {
                    state.onSelected(it, state.args)
                    state.dismiss()
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        FactCategoryTitle(FactCategory.Apps)
                        AppFacts(setFact = setFact)
                        StandardSpacer()
                    }
                }
            }
        }
    }
}

@Composable
private fun FactCategoryTitle(category: FactCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}