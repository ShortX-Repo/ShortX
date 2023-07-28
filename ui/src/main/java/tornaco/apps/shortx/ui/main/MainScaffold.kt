@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
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
import tornaco.apps.shortx.ui.base.MultiFloatingActionButton
import tornaco.apps.shortx.ui.base.RebootConfirmDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberFabState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.toAnnotatedString
import tornaco.apps.shortx.ui.subscribe.SubscribeActivity
import tornaco.apps.shortx.ui.util.AssetUtils

@Composable
fun MainScaffold(
    state: MainState,
    appBarTitleKey: String,
    appBarActions: @Composable () -> Unit = {},
    onClipboardClick: () -> Unit,
    onFileClick: () -> Unit,
    onAddClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    acceptPrivacyStatement: () -> Unit,
    content: @Composable () -> Unit
) {
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

    val fabState = rememberFabState(
        onCollapsed = {
            enableFabMask = false
        },
        onExpanded = {
            enableFabMask = true
        },
        onFabItemClicked = {
            when (it.id) {
                "clipboard" -> {
                    onClipboardClick()
                }

                "file" -> {
                    onFileClick()
                }

                "new" -> {
                    onAddClick()
                }

                "url" -> {
                    urlInputState.show()
                }
            }
        })

    MainScaffold(
        state = state,
        appBarTitleKey = appBarTitleKey,
        enableMask = enableFabMask,
        appBarActions = appBarActions,
        acceptPrivacyStatement = acceptPrivacyStatement,
        floatingActionButton = {
            AddOptionsFab(fabState)
        },
        onMaskClickOutside = { fabState.collapse() },
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
    onMaskClickOutside: () -> Unit,
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
                            it.blur(12.dp)
                        } else it
                    },
            ) {
                val context = LocalContext.current

                AnimatedVisibility(visible = state.isSubscribed.not()) {
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
                        }
                    }
                }

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
            }

            if (enableMask) {
                Box(modifier = Modifier
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
                Text(
                    text = HtmlCompat.fromHtml(privacyAgreement, HtmlCompat.FROM_HTML_MODE_COMPACT)
                        .toAnnotatedString()
                )
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
fun AddOptionsFab(
    fabState: FabState
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
            id = "url", icon = Remix.Business.global_line, label = "URL"
        ),
        MultiFabItem(
            id = "new", icon = Remix.System.add_line, label = "New"
        )
    )

    MultiFloatingActionButton(
        srcIcon = Icons.Outlined.Add,
        showLabels = true,
        items = expandFbItemList,
        state = fabState
    )
}

