package tornaco.apps.shortx.ui.addda

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.record.EvaRecordSetting
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class AddDirectActionActivity : BaseActivity() {
    companion object {
        private const val KEY_DA_ID = "daId"
        private const val KEY_DA_SET_ID = "daSetId"

        @JvmStatic
        fun edit(context: Context, daId: String) {
            val starter = Intent(context, AddDirectActionActivity::class.java)
                .putExtra(KEY_DA_ID, daId)
            context.startActivity(starter)
        }

        @JvmStatic
        fun create(context: Context, daSetId: String? = null) {
            val starter = Intent(context, AddDirectActionActivity::class.java)
                .putExtra(KEY_DA_SET_ID, daSetId)
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
        // For create from a set chip.
        val daSetId = intent.getStringExtra(KEY_DA_SET_ID)
        AddDirectActionScreen(daId = daId, daSetId = daSetId, screenTitle = screenTitle) {
            setResult(it.select(RESULT_OK, RESULT_CANCELED))
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDirectActionScreen(
    daId: String? = null,
    daSetId: String? = null,
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
    val i18n = LocalI18N.current
    val savedMessage = i18n["ui.toast.da.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.initDASetIdIfNonNull(daSetId)
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
    val shortXProps = LocalShortXProps.current

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val backConfirmDialogState = rememberConfirmDialogState()
            ConfirmDialog(
                title = i18n["ui.edit.discard.dialog.title"],
                messageHint = {
                    i18n["ui.edit.discard.dialog.message"]
                },
                confirmButton = i18n["ui.edit.discard.dialog.discard"],
                dismissButton = i18n["ui.edit.discard.dialog.stay"],
                state = backConfirmDialogState,
                data = "",
                onConfirm = {
                    back(false)
                })

            val hasAnyChanges = remember(addDirectActionState, actionContentState) {
                addDirectActionState.daChanged || actionContentState.isChanged
            }

            BackHandler {
                if (hasAnyChanges) {
                    backConfirmDialogState.show()
                } else {
                    back(false)
                }
            }

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = screenTitle) },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (hasAnyChanges) {
                                    backConfirmDialogState.show()
                                } else {
                                    back(false)
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = canSaveWithoutChanges || hasAnyChanges,
                                onClick = {
                                    vm.save(shortXProps.firstOrNull())
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
    AnimatedVisibility(visible = !addDirectActionState.securityCheckRes) {
        Column {
            ErrorCard(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                iconName = Remix.UserAndFaces.skull_2_fill,
                title = "Suspicious Action found!!!",
                warnings = listOf("Suspicious Action found in this Rule, please carefully review all Actions, especially for MVEL/Shell related Actions")
            )
            LargeSpacer()
        }
    }

    TitleAndDescription(state = addDirectActionState, vm = vm, readOnly = readOnly)
    Spacer(modifier = Modifier.size(24.dp))

    ActionContent(
        srcId = addDirectActionState.id,
        actions = actionContentState.actions,
        evaluatingActions = addDirectActionState.evaluatingActions,
        handler = actionContentStateHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionContentState.actionWarningMessages
    )
    SectionSpacer()

    EvaRecordSetting(id = addDirectActionState.id)

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
    var showDescription by remember(state) { mutableStateOf(state.description.isNotEmpty()) }
    val dropdownRotate by animateFloatAsState(
        targetValue = if (showDescription) 180f else 0f,
        label = "dropdownRotate"
    )

    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = LocalI18N.current["ui.da.title"])
        },
        value = state.title,
        readOnly = readOnly,
        onValueChange = { title ->
            vm.setTitle(title)
        },
        trailingIcon = {
            IconButton(onClick = { showDescription = !showDescription }) {
                RemixIcon(
                    modifier = Modifier.rotate(dropdownRotate),
                    remixName = Remix.Arrows.arrow_drop_down_fill
                )
            }
        },
        showSymbolButton = false
    )

    AnimatedVisibility(visible = showDescription) {
        ShortXTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = {
                Text(text = LocalI18N.current["ui.da.description"])
            },
            value = state.description,
            readOnly = readOnly,
            onValueChange = { description ->
                vm.setDescription(description)
            },
            showSymbolButton = true
        )
    }
}