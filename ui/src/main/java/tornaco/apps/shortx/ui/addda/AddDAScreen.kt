package tornaco.apps.shortx.ui.addda

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDirectActionScreen(daId: String? = null, back: () -> Unit) {
    val vm = hiltViewModel<AddDirectActionViewModel>()
    val addDirectActionState by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.initFromDirectActionId(daId)
        vm.daSaved.collectLatest {

        }
    }

    val actionContentStateHandler = remember(vm) {
        vm.actionContentStateHandler
    }
    val actionContentState by actionContentStateHandler.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = if (addDirectActionState.isEditDirectAction) LocalI18N.current["ui.title.edit.da"] else LocalI18N.current["ui.title.add.da"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = addDirectActionState.daChanged || actionContentState.isChanged,
                                onClick = {
                                    vm.save()
                                    back()
                                }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {

                    TitleAndDescription(addDirectActionState, vm)
                    Spacer(modifier = Modifier.size(24.dp))

                    ActionContent(
                        actions = actionContentState.actions,
                        handler = actionContentStateHandler.asActionHandler()
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TitleAndDescription(
    state: AddDirectActionState,
    vm: AddDirectActionViewModel,
) {
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.title"])
    }, value = state.title, onValueChange = { title ->
        vm.setTitle(title)
    })
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.description"])
    }, value = state.description, onValueChange = { description ->
        vm.setDescription(description)
    })
}