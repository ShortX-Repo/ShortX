package tornaco.apps.shortx.ui.addda

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.ConflictStrategy
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionContentState
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class AddDirectActionActivity : BaseActivity() {
    companion object {
        private const val KEY_DA_ID = "daId"

        @JvmStatic
        fun start(context: Context, daId: String? = null) {
            val starter = Intent(context, AddDirectActionActivity::class.java)
                .putExtra(KEY_DA_ID, daId)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val daId = intent.getStringExtra(KEY_DA_ID)
        val screenTitle = if (daId == null) {
            LocalI18N.current["ui.title.add.da"]
        } else {
            LocalI18N.current["ui.title.edit.da"]
        }
        AddDirectActionScreen(daId = daId, screenTitle = screenTitle) {
            setResult(it.select(RESULT_OK, RESULT_CANCELED))
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDirectActionScreen(
    daId: String? = null,
    da: DirectAction? = null,
    url: String? = null,
    screenTitle: String,
    canSaveWithoutChanges: Boolean = false,
    readOnly: Boolean = false,
    saveIconName: String = Remix.System.check_line,
    back: (Boolean) -> Unit
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
        vm.initFromUrl(url)
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
                                back(false)
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = canSaveWithoutChanges || addDirectActionState.daChanged || actionContentState.isChanged,
                                onClick = {
                                    vm.save()
                                    back(true)
                                }) {
                                RemixIcon(saveIconName)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                ClickOutsideClearFocusBox(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(it)
                            .padding(horizontal = 16.dp)
                            .animateContentSize()
                    ) {
                        when (addDirectActionState.onlineDALoadState) {
                            is LoadState.Error -> {
                                LoadError(modifier = Modifier.fillMaxSize()) {
                                    vm.initFromUrl(url)
                                }
                            }

                            LoadState.Loading -> {
                                Loading(modifier = Modifier.fillMaxSize())
                            }

                            is LoadState.Success -> {
                                DAContent(
                                    addDirectActionState,
                                    vm,
                                    readOnly,
                                    actionContentState,
                                    actionContentStateHandler
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DAContent(
    addDirectActionState: AddDirectActionState,
    vm: AddDirectActionViewModel,
    readOnly: Boolean,
    actionContentState: ActionContentState,
    actionContentStateHandler: ActionContentStateHandler
) {
    TitleAndDescription(state = addDirectActionState, vm = vm, readOnly = readOnly)
    Spacer(modifier = Modifier.size(24.dp))

    ActionContent(
        actions = actionContentState.actions,
        handler = actionContentStateHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionContentState.actionWarningMessages
    )
    SectionSpacer()

    TextButton(onClick = {
        vm.setShowAdvancedOptions(!addDirectActionState.showAdvancedOptions)

    }) {
        Text(text = LocalI18N.current["ui.advanced.options"])
    }
    StandardSpacer()
    AnimatedVisibility(visible = addDirectActionState.showAdvancedOptions) {
        Column {
            ConflictStrategy(
                conflictStrategy = addDirectActionState.conflictStrategy,
                setConflictStrategy = {
                    vm.setConflictStrategy(it)
                })
        }
    }

    SectionSpacer()
    SuggestedNavEntry()
}

@Composable
private fun TitleAndDescription(
    state: AddDirectActionState,
    vm: AddDirectActionViewModel,
    readOnly: Boolean
) {
    ShortXTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.title"])
    }, value = state.title, readOnly = readOnly, onValueChange = { title ->
        vm.setTitle(title)
    }, showSymbolButton = false)
    Spacer(modifier = Modifier.size(16.dp))
    ShortXTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.da.description"])
    }, value = state.description, readOnly = readOnly, onValueChange = { description ->
        vm.setDescription(description)
    }, showSymbolButton = false)
}