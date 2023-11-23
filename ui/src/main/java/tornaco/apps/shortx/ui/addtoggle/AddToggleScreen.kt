package tornaco.apps.shortx.ui.addtoggle

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionContentState
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class AddToggleActivity : BaseActivity() {
    companion object {
        private const val KEY_TOGGLE_ID = "toggleId"
        private const val KEY_TOGGLE_SET_ID = "toggleSetId"

        @JvmStatic
        fun edit(context: Context, toggleId: String) {
            val starter = Intent(context, AddToggleActivity::class.java)
                .putExtra(KEY_TOGGLE_ID, toggleId)
            context.startActivity(starter)
        }

        @JvmStatic
        fun create(context: Context, toggleSetId: String? = null) {
            val starter = Intent(context, AddToggleActivity::class.java)
                .putExtra(KEY_TOGGLE_SET_ID, toggleSetId)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val toggleId = intent.getStringExtra(KEY_TOGGLE_ID)
        val toggleSetId = intent.getStringExtra(KEY_TOGGLE_SET_ID)
        AddToggleScreen(
            toggleId = toggleId,
            toggleSetId = toggleSetId,
            screenTitle = if (toggleId != null) {
                LocalI18N.current["ui.title.edit.toggle"]
            } else {
                LocalI18N.current["ui.title.add.toggle"]
            }
        ) {
            finish()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToggleScreen(
    toggleId: String? = null,
    toggleSetId: String? = null,
    toggle: Toggle? = null,
    url: String? = null,
    screenTitle: String,
    canSaveWithoutChanges: Boolean = false,
    readOnly: Boolean = false,
    saveIconName: String = Remix.System.check_line,
    back: (Boolean) -> Unit
) {
    val vm = hiltViewModel<AddToggleViewModel>()
    val addToggleState by vm.state.collectAsState()

    val context = LocalContext.current
    val i18n = LocalI18N.current
    val savedMessage = i18n["ui.toast.toggle.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    val shortXProps = LocalShortXProps.current

    LaunchedEffect(vm) {
        vm.initFromToggleId(toggleId)
        vm.initFromToggle(toggle)
        vm.toggleSaved.collectLatest {
            showToast()
        }
    }

    val actionOnEnabledHandler = remember(vm) { vm.actionOnEnabledStateHandler }
    val actionOnEnabledState by actionOnEnabledHandler.state.collectAsState()

    val actionOnDisabledHandler = remember(vm) { vm.actionOnDisabledStateHandler }
    val actionOnDisabledState by actionOnDisabledHandler.state.collectAsState()

    val actionOnDeletedHandler = remember(vm) { vm.actionOnDeletedStateHandler }
    val actionOnDeletedState by actionOnDeletedHandler.state.collectAsState()

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

    val hasAnyChanges = remember(
        addToggleState,
        actionOnEnabledState,
        actionOnDisabledState,
        actionOnDeletedState,
    ) {
        addToggleState.toggleChanged
                || actionOnEnabledState.isChanged
                || actionOnDisabledState.isChanged
                || actionOnDeletedState.isChanged
    }


    BackHandler {
        if (hasAnyChanges) {
            backConfirmDialogState.show()
        } else {
            back(false)
        }
    }

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
                                RemixIcon(remixName = saveIconName)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->
                val scrollState = rememberScrollState()
                ClickOutsideClearFocusBox {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .animateContentSize()
                    ) {
                        ToggleContent(
                            addToggleState = addToggleState,
                            vm = vm,
                            readOnly = readOnly,
                            actionOnEnabledState = actionOnEnabledState,
                            actionOnEnabledHandler = actionOnEnabledHandler,
                            actionOnDisabledState = actionOnDisabledState,
                            actionOnDisabledHandler = actionOnDisabledHandler,
                            actionOnDeletedState = actionOnDeletedState,
                            actionOnDeletedHandler = actionOnDeletedHandler
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleContent(
    addToggleState: AddToggleState,
    vm: AddToggleViewModel,
    readOnly: Boolean,
    actionOnEnabledState: ActionContentState,
    actionOnEnabledHandler: ActionContentStateHandler,
    actionOnDisabledState: ActionContentState,
    actionOnDisabledHandler: ActionContentStateHandler,
    actionOnDeletedState: ActionContentState,
    actionOnDeletedHandler: ActionContentStateHandler
) {
    TitleAndDescription(state = addToggleState, vm = vm, readOnly = readOnly)
    SectionSpacer()

    ActionContent(
        header = {
            FieldTitle(text = LocalI18N.current["ui.title.on.toggle.on"])
        },
        srcId = addToggleState.id,
        actions = actionOnEnabledState.actions,
        evaluatingActions = addToggleState.evaluatingActions,
        handler = actionOnEnabledHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionOnEnabledState.actionWarningMessages
    )
    SectionSpacer()

    ActionContent(
        header = {
            FieldTitle(text = LocalI18N.current["ui.title.on.toggle.off"])
        },
        srcId = addToggleState.id,
        actions = actionOnDisabledState.actions,
        evaluatingActions = addToggleState.evaluatingActions,
        handler = actionOnDisabledHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionOnDisabledState.actionWarningMessages
    )
    SectionSpacer()

    ActionContent(
        header = {
            FieldTitleWithTip(
                LocalI18N.current["ui.title.on.toggle.deleted"],
                LocalI18N.current["ui.actions.hook.on.deleted.description"]
            )
        },
        srcId = addToggleState.id,
        actions = actionOnDeletedState.actions,
        evaluatingActions = addToggleState.evaluatingActions,
        handler = actionOnDeletedHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionOnDeletedState.actionWarningMessages
    )
    SectionSpacer()

    SectionSpacer()
    SuggestedNavEntry()
}

@Composable
private fun TitleAndDescription(
    state: AddToggleState,
    vm: AddToggleViewModel,
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
            Text(text = LocalI18N.current["ui.rule.title"])
        },
        value = state.title,
        readOnly = readOnly,
        showSymbolButton = false,
        trailingIcon = {
            IconButton(onClick = { showDescription = !showDescription }) {
                RemixIcon(
                    modifier = Modifier.rotate(dropdownRotate),
                    remixName = Remix.Arrows.arrow_drop_down_fill
                )
            }
        },
        onValueChange = { title ->
            vm.setTitle(title)
        })
    AnimatedVisibility(visible = showDescription) {
        ShortXTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
            label = {
                Text(text = LocalI18N.current["ui.rule.description"])
            },
            value = state.description,
            readOnly = readOnly,
            showSymbolButton = true,
            onValueChange = { description ->
                vm.setDescription(description)
            })
    }
}
