@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addda

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SECTION_BAR_CORNER_SIZE
import tornaco.apps.shortx.ui.base.SECTION_BAR_H_PADDING
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.WarningCard
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.itemWithPadding
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
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        verticalScroll = false
    ) {
        ClickOutsideClearFocusBox(
            modifier = Modifier.fillMaxSize()
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
                    val lazyListState = rememberLazyListState()
                    // 监听第一个可见项的索引
                    val currentStickyHeaderIndex by remember {
                        derivedStateOf {
                            // 判断当前 sticky header 的索引（可能是第一个可见项）
                            lazyListState.firstVisibleItemIndex
                        }
                    }
                    LazyColumn(state = lazyListState) {
                        daContent(
                            currentStickyHeaderIndex = currentStickyHeaderIndex,
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

private fun LazyListScope.daContent(
    currentStickyHeaderIndex: Int,
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
    // 0
    itemWithPadding {
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
    }

    // 1
    itemWithPadding {
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
                StandardSpacer()
            }
        }
    }

    // 2
    itemWithPadding {
        TitleAndDescription(state = addDirectActionState, vm = vm, readOnly = readOnly)
        Spacer(modifier = Modifier.size(24.dp))
    }

    // 3
    itemWithPadding {
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
    }

    // 4
    itemWithPadding {
        EvaRecordSetting(id = addDirectActionState.id)
        SectionSpacer()
    }

    // Adv START
    // 5
    stickyHeader {
        val cornerDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 5) 0.dp else SECTION_BAR_CORNER_SIZE.dp,
            label = "Dp"
        )
        val paddingDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 5) 0.dp else SECTION_BAR_H_PADDING.dp,
            label = "Dp"
        )
        ExpandableSectionBar(
            isExpand = addDirectActionState.showAdvancedOptions,
            text = LocalI18N.current["ui.advanced.options"],
            iconName = Remix.System.star_fill,
            shape = RoundedCornerShape(cornerDp),
            modifier = Modifier.padding(horizontal = paddingDp)
        ) {
            vm.setShowAdvancedOptions(!addDirectActionState.showAdvancedOptions)
        }
        StandardSpacer()
    }
    // 6
    itemWithPadding {
        AnimatedVisibility(visible = addDirectActionState.showAdvancedOptions) {
            Column {
                // Conflict
                ConflictStrategy(
                    conflictStrategy = addDirectActionState.conflictStrategy,
                    setConflictStrategy = {
                        vm.setConflictStrategy(it)
                    })
                StandardSpacer()

                // Params
                FuncParamList(addDirectActionState.parameters,
                    add = { param, override ->
                        vm.addParameter(param, override)
                    },
                    remove = { vm.removeParameter(it) }
                )
                StandardSpacer()
            }
        }
    }

    // Quit START
    // 7
    stickyHeader {
        val cornerDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 7) 0.dp else SECTION_BAR_CORNER_SIZE.dp,
            label = "Dp"
        )
        val paddingDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 7) 0.dp else SECTION_BAR_H_PADDING.dp,
            label = "Dp"
        )
        ExpandableSectionBar(
            isExpand = addDirectActionState.isQuitEnabled,
            text = LocalI18N.current["ui.rule.quit.title"],
            iconName = Remix.System.logout_circle_fill,
            shape = RoundedCornerShape(cornerDp),
            modifier = Modifier.padding(horizontal = paddingDp)
        ) {
            vm.toggleQuitEnable()
        }
        StandardSpacer()
    }
    itemWithPadding {
        AnimatedVisibility(visible = addDirectActionState.isQuitEnabled) {
            Column {
                StandardSpacer()
                TipBody(LocalI18N.current["ui.rule.quit.description"])
                StandardSpacer()
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
    }
    // Quit END

    itemWithPadding {
        SectionSpacer()
        SuggestedNavEntry()
    }
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