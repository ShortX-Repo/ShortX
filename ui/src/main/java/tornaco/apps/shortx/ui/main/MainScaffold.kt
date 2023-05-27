@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.util.AssetUtils

@Composable
fun MainScaffold(
    state: MainState,
    appBarTitleKey: String,
    appBarActions: @Composable () -> Unit = {},
    onClipboardClick: () -> Unit,
    onAddClick: () -> Unit,
    acceptPrivacyStatement: () -> Unit,
    content: @Composable () -> Unit
) {
    var enableFabMask by remember {
        mutableStateOf(false)
    }

    MainScaffold(
        state = state,
        appBarTitleKey = appBarTitleKey,
        enableMask = enableFabMask,
        appBarActions = appBarActions,
        acceptPrivacyStatement = acceptPrivacyStatement,
        floatingActionButton = {
            Fab(
                onClipboardClick = {
                    onClipboardClick()
                },
                onFileClick = {

                },
                onCreateClick = {
                    onAddClick()
                },
                onExpanded = {
                    enableFabMask = true
                },
                onCollapsed = {
                    enableFabMask = false
                })
        },
        content = content
    )
}

@Composable
fun MainScaffold(
    state: MainState,
    appBarTitleKey: String,
    enableMask: Boolean,
    appBarActions: @Composable () -> Unit = {},
    acceptPrivacyStatement: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isShowActiveDialog by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (state.isActivated) {
                    ShortXMediumTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current[appBarTitleKey]
                            )
                        },
                        navigationIcon = {
                            SettingsAppBarActions()
                        },
                        actions = {
                            appBarActions()
                        },
                        scrollBehavior = scrollBehavior,
                    )
                } else {
                    ShortXTopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = LocalAppName.current)
                                SmallSpacer()
                                ClickableBadge(text = LocalI18N.current["ui.settings.core.info.na"]) {
                                    isShowActiveDialog = true
                                }
                            }
                        },
                        navigationIcon = {
                            SettingsAppBarActions()
                        },
                        actions = {
                            appBarActions()
                        })
                }
            },
            bottomBar = {
            },
            floatingActionButton = floatingActionButton
        ) { paddingValues ->

            val shareDialogState = rememberShareDialogState("share.shortx")
            ShareDialog(state = shareDialogState)

            val rebootDialogState = rememberCommonDialogState()
            RebootConfirmDialog(state = rebootDialogState)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .let {
                        if (enableMask) {
                            it.blur(4.dp)
                        } else it
                    },
            ) {
                val navHost = LocalNavHostController.current

                AnimatedVisibility(visible = state.isSubscribed.not()) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable {
                                    navHost.navigate(AppRoute.SubscriptionIntro.name)
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
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.isSubscribed
                            && state.isFrameworkUpdated
                            && !state.isIgnoreFrameworkUpdate
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

                if (!state.isPrivacyStatementAccepted && !isShowActiveDialog) {
                    PrivacyStatementDialog {
                        acceptPrivacyStatement()
                    }
                }
            }

            if (enableMask) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                    .pointerInput(key1 = "MASK") {

                    }) {
                }
            }
        }
    }
}

@Composable
private fun SettingsAppBarActions() {
    val localDrawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        scope.launch { localDrawerState.open() }
    }) {
        Icon(imageVector = Icons.Outlined.Menu)
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
) {
    val context = LocalContext.current
    val privacyAgreement = remember {
        AssetUtils.readFileToString(context, "privacy/privacy_agreement.html")
    }
    ShortXAlertDialog(title = {
        Text(text = LocalI18N.current["ui.privacy.statement.title"])
    }, text = {
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.64f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = HtmlCompat.fromHtml(privacyAgreement, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    .toAnnotatedString()
            )
        }
    }, onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(text = stringResource(android.R.string.ok))
        }
    })
}


@Composable
private fun Fab(
    onClipboardClick: () -> Unit,
    onFileClick: () -> Unit,
    onCreateClick: () -> Unit,
    onCollapsed: () -> Unit = {},
    onExpanded: () -> Unit = {},
) {
    val expandFbItemList: MutableList<MultiFabItem> = mutableListOf(
        MultiFabItem(
            id = "file", icon = Remix.Document.file_line, label = "File"
        ),
        MultiFabItem(
            id = "clipboard",
            icon = Remix.Document.clipboard_line,
            label = LocalI18N.current["ui.common.share.clipboard"]
        ),
        MultiFabItem(
            id = "new", icon = Remix.System.add_line, label = "New"
        )
    )
    MultiFloatingActionButton(
        srcIcon = Icons.Outlined.Add,
        showLabels = true,
        items = expandFbItemList,
        onCollapsed = onCollapsed,
        onExpanded = onExpanded,
        onFabItemClicked = {
            when (it.id) {
                "clipboard" -> {
                    onClipboardClick()
                }

                "file" -> {
                    onFileClick()
                }

                "new" -> {
                    onCreateClick()
                }
            }
        })
}

