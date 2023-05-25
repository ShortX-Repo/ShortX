package tornaco.apps.shortx.ui.addda

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDirectActionScreen(
    daId: String? = null,
    da: DirectAction? = null,
    screenTitle: String,
    canSaveWithoutChanges: Boolean = false,
    back: () -> Unit
) {
    val vm = hiltViewModel<AddDirectActionViewModel>()
    val addDirectActionState by vm.state.collectAsState()

    val context = LocalContext.current
    val savedMessage = LocalI18N.current["ui.toast.da.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.initFromDirectActionId(daId)
        vm.initFromDirectAction(da)
        vm.daSaved.collectLatest {
            showToast()
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
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = screenTitle) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = canSaveWithoutChanges || addDirectActionState.daChanged || actionContentState.isChanged,
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
                        handler = actionContentStateHandler.asActionHandler(),
                        warnings = actionContentState.actionWarningMessages
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
    ShortXTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.title"])
    }, value = state.title, onValueChange = { title ->
        vm.setTitle(title)
    })
    Spacer(modifier = Modifier.size(16.dp))
    ShortXTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.description"])
    }, value = state.description, onValueChange = { description ->
        vm.setDescription(description)
    })
}