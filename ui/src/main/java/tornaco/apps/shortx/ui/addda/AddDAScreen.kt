package tornaco.apps.shortx.ui.addda

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.ConflictStrategy
import tornaco.apps.shortx.ui.addrule.QuitEvaluatePolicy
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionContentState
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentState
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.addrule.fact.FactContentState
import tornaco.apps.shortx.ui.addrule.fact.FactContentStateHandler
import tornaco.apps.shortx.ui.addrule.fact.FactSelectors
import tornaco.apps.shortx.ui.addrule.fact.asFactHandler
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.ExpandableSectionBar
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LargeTitleWithTip
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SplitLines
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.WarningCard
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.record.EvaRecordSetting
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

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
    val showDASavedToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }
    val showParamAlreadyExistsToast: (String) -> Unit = {
        Toast.makeText(context, "$it already exists", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.initDASetIdIfNonNull(daSetId)
        vm.initFromDirectActionId(daId)
        vm.initFromDirectAction(da)
        vm.initFromUrl(url)
    }

    vm.handleSideEffect {
        when (it) {
            is AddDAEffect.AddParamFailNameExists -> {
                showParamAlreadyExistsToast(it.name)
            }

            is AddDAEffect.DASaved -> {
                showDASavedToast()
            }
        }
    }

    val actionContentStateHandler = remember(vm) {
        vm.actionContentStateHandler
    }
    val actionContentState by actionContentStateHandler.state.collectAsState()

    val quitFactHandler = remember(vm) { vm.quitFactContentStateHandler }
    val quitFactContentState by quitFactHandler.state.collectAsState()
    val quitConditionHandler = remember(vm) { vm.quitConditionContentStateHandler }
    val quitConditionContentState by quitConditionHandler.state.collectAsState()
    val quitActionHandler = remember(vm) { vm.quitActionStateHandler }
    val quitActionState by quitActionHandler.state.collectAsState()

    val shortXProps = LocalShortXProps.current

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
        addDirectActionState,
        actionContentState,
        quitFactContentState,
        quitConditionContentState,
        quitActionState
    ) {
        addDirectActionState.daChanged
                || actionContentState.isChanged
                || quitFactContentState.isChanged
                || quitConditionContentState.isChanged
                || quitActionState.isChanged
    }

    BackHandler {
        if (hasAnyChanges) {
            backConfirmDialogState.show()
        } else {
            back(false)
        }
    }

    ShortXAppBarScaffold(
        title = screenTitle,
        onBackPressed = {
            if (hasAnyChanges) {
                backConfirmDialogState.show()
            } else {
                back(false)
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
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ) {
        ClickOutsideClearFocusBox(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                            addDirectActionState = addDirectActionState,
                            vm = vm,
                            readOnly = readOnly,
                            actionContentState = actionContentState,
                            actionContentStateHandler = actionContentStateHandler,
                            quitFactContentState = quitFactContentState,
                            quitFactHandler = quitFactHandler,
                            quitConditionContentState = quitConditionContentState,
                            quitConditionHandler = quitConditionHandler,
                            quitActionState = quitActionState,
                            quitActionHandler = quitActionHandler,
                        )
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
    actionContentStateHandler: ActionContentStateHandler,
    quitFactContentState: FactContentState,
    quitFactHandler: FactContentStateHandler,
    quitConditionContentState: ConditionContentState,
    quitConditionHandler: ConditionContentStateHandler,
    quitActionState: ActionContentState,
    quitActionHandler: ActionContentStateHandler,
) {
    AnimatedVisibility(visible = !addDirectActionState.securityCheckRes) {
        Column {
            ErrorCard(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                iconName = Remix.UserAndFaces.skull_2_fill,
                title = LocalI18N.current["ui.rule.action.secure.warning.title"],
                warnings = listOf(LocalI18N.current["ui.rule.action.secure.warning.message"])
            )
            LargeSpacer()
        }
    }

    AnimatedVisibility(visible = addDirectActionState.hasSMSAction) {
        Column {
            WarningCard(
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = LocalI18N.current["ui.rule.contains.send.sms.message"],
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            LargeSpacer()
        }
    }

    TitleAndDescription(state = addDirectActionState, vm = vm, readOnly = readOnly)
    Spacer(modifier = Modifier.size(24.dp))

    ActionContent(
        srcId = addDirectActionState.id,
        actions = actionContentState.actions,
        actionAsyncMode = actionContentState.asyncMode,
        canSelectActionAsyncMode = true,
        evaluatingActions = addDirectActionState.evaluatingActions,
        handler = actionContentStateHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionContentState.actionWarningMessages
    )
    SectionSpacer()

    EvaRecordSetting(id = addDirectActionState.id)

    SectionSpacer()
    StandardSpacer()

    ExpandableSectionBar(
        isExpand = addDirectActionState.showAdvancedOptions,
        text = LocalI18N.current["ui.advanced.options"],
        iconName = Remix.Media.equalizer_fill
    ) {
        vm.setShowAdvancedOptions(!addDirectActionState.showAdvancedOptions)
    }
    StandardSpacer()
    AnimatedVisibility(visible = addDirectActionState.showAdvancedOptions) {
        Column {
            ConflictStrategy(
                conflictStrategy = addDirectActionState.conflictStrategy,
                setConflictStrategy = {
                    vm.setConflictStrategy(it)
                })

            SectionSpacer()

            FuncParamList(addDirectActionState.parameters,
                add = { param, override ->
                    vm.addParameter(param, override)
                },
                remove = { vm.removeParameter(it) })

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LargeTitleWithTip(
                    LocalI18N.current["ui.rule.quit.title"],
                    LocalI18N.current["ui.rule.quit.description"]
                )
                ShortXSwitch(checked = addDirectActionState.isQuitEnabled, onCheckedChange = {
                    vm.toggleQuitEnable()
                })
            }
            AnimatedVisibility(visible = addDirectActionState.isQuitEnabled) {
                Column {
                    // Policy
                    QuitEvaluatePolicy(addDirectActionState.quitEvaluatePolicy) {
                        vm.setQuitEvaluatePolicy(it)
                    }

                    StandardSpacer()
                    FactSelectors(
                        header = {
                            FieldTitle(
                                modifier = Modifier.padding(bottom = 12.dp),
                                text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.fact.title"]}"
                            )
                        },
                        state = quitFactContentState,
                        handler = quitFactHandler.asFactHandler(),
                        readOnly = readOnly
                    )
                    SectionSpacer()

                    ConditionContent(
                        header = {
                            FieldTitle(
                                modifier = Modifier.padding(bottom = 12.dp),
                                text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.conditions.title"]}"
                            )
                        },
                        state = quitConditionContentState,
                        readOnly = readOnly,
                        handler = quitConditionHandler.asConditionHandler()
                    )

                    SectionSpacer()

                    ActionContent(
                        header = {
                            FieldTitle(
                                modifier = Modifier.padding(bottom = 6.dp),
                                text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.actions.title"]}"
                            )
                        },
                        summary = {
                            TipBody(text = LocalI18N.current["ui.rule.quit.action.description"])
                        },
                        srcId = addDirectActionState.id,
                        actions = quitActionState.actions,
                        actionAsyncMode = quitActionState.asyncMode,
                        canSelectActionAsyncMode = true,
                        evaluatingActions = addDirectActionState.evaluatingActions,
                        warnings = emptyMap(),
                        handler = quitActionHandler.asActionHandler()
                    )

                    SectionSpacer()
                }
            }

            SplitLines(modifier = Modifier.padding(vertical = 30.dp))
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