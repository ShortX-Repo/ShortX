@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.base.AnimateExpand
import tornaco.apps.shortx.ui.base.ClickableBadge
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.FabState
import tornaco.apps.shortx.ui.base.Icon
import tornaco.apps.shortx.ui.base.LocalActivity
import tornaco.apps.shortx.ui.base.LocalAppName
import tornaco.apps.shortx.ui.base.LocalDrawerState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MultiFabItem
import tornaco.apps.shortx.ui.base.MultiFabState
import tornaco.apps.shortx.ui.base.RebootConfirmDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.SearchBarState
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TextFieldStyle
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberFabState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.main.model.MainState
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.SubscribeActivity
import tornaco.apps.shortx.ui.theme.WarningYellow
import tornaco.apps.shortx.ui.util.AssetUtils

@Composable
fun MainScaffold(
    state: MainState,
    appBarTitle: @Composable () -> Unit,
    appBarActions: @Composable () -> Unit = {},
    enableSearch: Boolean = false,
    searchBarState: SearchBarState = rememberSearchBarState(),
    onClipboardClick: () -> Unit,
    onFileClick: () -> Unit,
    onAddClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    onNewSetClick: (String) -> Unit,
    acceptPrivacyStatement: () -> Unit,
    appBarContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    val subscribeState by LVLStateHolder.collectAsState()

    var enableFabMask by remember {
        mutableStateOf(false)
    }

    val urlInputState = rememberTextInputState(
        title = "URL",
        message = null,
        onSelected = {
            onUrlClick(it)
        })
    TextInputDialog(state = urlInputState)

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val newSetDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.common.new.set"],
            message = null,
            onSelected = {
                onNewSetClick(it)
            })
    TextInputDialog(state = newSetDialogState)

    val fabState = rememberFabState(
        onCollapsed = {
            enableFabMask = false
        },
        onExpanded = {
            enableFabMask = true
        },
        onFabItemClicked = {
            when (MainScaffoldFabOptions.valueOf(it.id)) {
                MainScaffoldFabOptions.ImportClipboard -> {
                    onClipboardClick()
                }

                MainScaffoldFabOptions.ImportFile -> {
                    subscribeState.isSubscribed.select({
                        onFileClick()
                    }, { subscribeDialogState.show() })
                }

                MainScaffoldFabOptions.New -> {
                    onAddClick()
                }

                MainScaffoldFabOptions.ImportUrl -> {
                    subscribeState.isSubscribed.select({
                        urlInputState.show()
                    }, { subscribeDialogState.show() })
                }

                MainScaffoldFabOptions.NewSet -> {
                    subscribeState.isSubscribed.select({
                        newSetDialogState.show()
                    }, { subscribeDialogState.show() })
                }
            }
        })

    MainScaffold(
        state = state,
        subscribeState = subscribeState,
        appBarTitle = appBarTitle,
        enableSearch = enableSearch,
        searchBarState = searchBarState,
        enableMask = enableFabMask,
        appBarActions = appBarActions,
        acceptPrivacyStatement = acceptPrivacyStatement,
        floatingActionButton = {
            AddOptionsFab(fabState)
        },
        onMaskClickOutside = { fabState.collapse() },
        appBarContent = appBarContent,
        content = content
    )
}

@Composable
fun MainScaffold(
    state: MainState,
    subscribeState: LVLStateHolder.State,
    appBarTitle: @Composable () -> Unit,
    enableMask: Boolean,
    enableSearch: Boolean = false,
    searchBarState: SearchBarState = rememberSearchBarState(),
    appBarActions: @Composable () -> Unit = {},
    acceptPrivacyStatement: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    onMaskClickOutside: () -> Unit,
    appBarContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    var isShowActiveDialog by remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(modifier = Modifier) {
                var showSearch by remember { mutableStateOf(false) }
                BackHandler(enabled = showSearch) {
                    showSearch = false
                    searchBarState.inputKeyword("")
                }

                AnimateExpand(
                    expanded = showSearch,
                    expandedContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            IconButton(onClick = { showSearch = false }) {
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack)
                            }
                            SearchBar(
                                searchBarState = searchBarState,
                                autoFocus = true,
                                textFieldStyle = TextFieldStyle.Filled,
                                modifier = Modifier
                                    .systemBarsPadding()
                                    .padding(horizontal = 16.dp)
                            ) {
                                showSearch = false
                            }
                        }
                    },
                    unExpandedContent = {
                        Column {
                            ShortXTopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        appBarTitle()
                                        if (!state.isActivated) {
                                            SmallSpacer()
                                            ClickableBadge(text = LocalI18N.current["ui.settings.core.info.na"]) {
                                                isShowActiveDialog = true
                                            }
                                        }
                                    }
                                },
                                navigationIcon = {
                                    SettingsAppBarActions(state.hasSensorUsage)
                                },
                                actions = {
                                    appBarActions()
                                    if (enableSearch) {
                                        IconButton(onClick = {
                                            showSearch = true
                                        }) {
                                            RemixIcon(remixName = Remix.System.search_line)
                                        }
                                    }
                                },
                                scrollBehavior = scrollBehavior,
                            )
                            appBarContent()
                        }
                    }
                )
            }
        },
        bottomBar = {
        },
        floatingActionButton = floatingActionButton
    ) { paddingValues ->
        val rebootDialogState = rememberCommonDialogState()
        RebootConfirmDialog(state = rebootDialogState)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .let {
                    if (enableMask) {
                        it.blur(12.dp)
                    } else it
                },
        ) {
            Spacer(Modifier.size(paddingValues.calculateTopPadding()))

            val context = LocalContext.current
            AnimatedVisibility(
                visible = subscribeState.isInited && !subscribeState.isSubscribed
                        && !state.isHideSubscribeInfo
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                SubscribeActivity.start(context)
                            }
                            .padding(16.dp)
                    ) {
                        Row {
                            RemixIcon(
                                modifier = Modifier.offset(y = 2.dp),
                                remixName = Remix.Finance.gift_2_line
                            )
                            MediumSpacer()
                            Text(
                                LocalI18N.current["ui.subscription.intro.message"],
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            val scope = rememberCoroutineScope()
                            TextButton(onClick = {
                                scope.launch {
                                    Pref(context).hideSubscribeInfo()
                                }
                            }) {
                                Text(text = stringResource(id = android.R.string.ok))
                            }
                        }
                    }
                }
            }

            UserTip(tipId = state.globalNotice.first, message = state.globalNotice.second)

            AnimatedVisibility(
                visible = state.isAudioRecording
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                shortXManager.stopAudioRecording()
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ColoredIcon(size = 24.dp, color = Color.Red) {
                            RemixIcon(
                                modifier = Modifier.size(16.dp),
                                remixName = Remix.Media.mic_2_fill,
                                tint = Color.White
                            )
                        }
                        MediumSpacer()
                        Text(
                            LocalI18N.current["ui.action.audio.recording.card.text"],
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = subscribeState.isSubscribed
                        && state.isFrameworkUpdated
                        && !state.isIgnoreFrameworkUpdate
                        && state.globalNotice.first.isBlank()
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                rebootDialogState.show()
                            }
                            .padding(16.dp)
                    ) {
                        Row {
                            RemixIcon(
                                modifier = Modifier.offset(y = 2.dp),
                                remixName = Remix.System.alarm_warning_line
                            )
                            MediumSpacer()
                            Text(
                                LocalI18N.current["ui.framework.updated", mapOf("app_name" to LocalAppName.current)],
                            )
                        }
                    }
                }
            }

            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                content()
            }

            if (isShowActiveDialog) {
                ActiveDialog(onDismissRequest = {
                    isShowActiveDialog = false
                })
            }

            val activity = LocalActivity.current
            if (!state.isPrivacyStatementAccepted && !isShowActiveDialog) {
                PrivacyStatementDialog(
                    onDismissRequest = {
                        activity.finishAffinity()
                    },
                    onAccept = {
                        acceptPrivacyStatement()
                    })
            }

            Spacer(Modifier.size(paddingValues.calculateBottomPadding()))
        }

        if (enableMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                    .pointerInput(key1 = "MASK") {
                        detectTapGestures(onTap = {
                            onMaskClickOutside()
                        })
                    }) {
            }
        }
    }
}

@Composable
private fun SettingsAppBarActions(hasSensorUsage: Boolean) {
    val localDrawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        scope.launch { localDrawerState.open() }
    }) {
        BadgedBox(badge = {
            if (hasSensorUsage) {
                Badge(containerColor = WarningYellow)
            }
        }) {
            // 使用 animateDpAsState 为大小变化添加动画
            val iconSize by animateDpAsState(
                targetValue = if (hasSensorUsage) 18.dp else 24.dp,
                animationSpec = tween(durationMillis = 300), label = "Icon size"
            )
            Icon(
                imageVector = Icons.Outlined.Menu,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun ActiveDialog(
    onDismissRequest: () -> Unit,
) {
    ShortXAlertDialog(title = {
        Text(text = LocalI18N.current["ui.settings.core.info.na"])
    }, text = {
        Text(text = LocalI18N.current["ui.active.dialog.message", mapOf("app_name" to LocalAppName.current)])
    }, onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(text = stringResource(android.R.string.ok))
        }
    })
}


@Composable
private fun PrivacyStatementDialog(
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit
) {
    val context = LocalContext.current
    val privacyAgreement = remember {
        AssetUtils.readFileToString(context, "privacy/privacy_agreement.html")
    }
    ShortXAlertDialog(
        title = {
            Text(text = LocalI18N.current["ui.privacy.statement.title"])
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight(fraction = 0.64f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = privacyAgreement)
            }
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAccept()
            }) {
                Text(text = stringResource(android.R.string.ok))
            }
        })
}


@Composable
private fun AddOptionsFab(
    fabState: FabState,
    fabVisible: Boolean = true
) {
    val items: MutableList<MultiFabItem> = mutableListOf(
        MultiFabItem(
            id = MainScaffoldFabOptions.ImportFile.name,
            icon = Remix.Document.file_line,
            label = LocalI18N.current["ui.common.import.from.file"]
        ),
        MultiFabItem(
            id = MainScaffoldFabOptions.ImportClipboard.name,
            icon = Remix.Document.clipboard_line,
            label = LocalI18N.current["ui.common.import.from.clipboard"]
        ),
        MultiFabItem(
            id = MainScaffoldFabOptions.ImportUrl.name,
            icon = Remix.Business.global_line,
            label = LocalI18N.current["ui.common.import.from.url"]
        ),
        MultiFabItem(
            id = MainScaffoldFabOptions.NewSet.name,
            icon = Remix.System.add_line,
            label = LocalI18N.current["ui.common.new.set"]
        ),
        MultiFabItem(
            id = MainScaffoldFabOptions.New.name,
            icon = Remix.System.add_line,
            label = LocalI18N.current["ui.common.new"]
        ),
    )
    val fabMenuExpanded = fabState.currentState.value == MultiFabState.Expanded
    BackHandler(fabMenuExpanded) { fabState.collapse() }
    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                    Modifier
                        .semantics {
                            traversalIndex = -1f
                            stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                            contentDescription = "Toggle menu"
                        }
                        .animateFloatingActionButton(
                            visible = fabVisible || fabMenuExpanded,
                            alignment = Alignment.BottomEnd
                        ),
                checked = fabMenuExpanded,
                onCheckedChange = {
                    fabState.toggle()
                }
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                    }
                }
                androidx.compose.material3.Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        }
    ) {
        items.forEachIndexed { _, item ->
            FloatingActionButtonMenuItem(
                modifier = Modifier,
                onClick = {
                    fabState.collapse()
                    fabState.onFabItemClicked(item)
                },
                icon = {
                    RemixIcon(item.icon)
                },
                text = { Text(text = item.label) },
            )
        }
    }
}


enum class MainScaffoldFabOptions {
    ImportFile,
    ImportClipboard,
    ImportUrl,
    New,
    NewSet
}