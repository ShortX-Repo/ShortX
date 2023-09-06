package tornaco.apps.shortx.ui.settings

import android.Manifest
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.fact.selector.EdgeGestureSizeValueSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberEdgeGestureSizeValueSelectorState
import tornaco.apps.shortx.ui.backup.autoGenBackupFileName
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalAppName
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.LottieLoadingView
import tornaco.apps.shortx.ui.subscribe.SubscribeActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    back: () -> Unit,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToDanmuUISettings: () -> Unit,
    goToEvaRecord: () -> Unit,
    goToAudioRecord: () -> Unit,
    goToGestureRecord: () -> Unit,
    viewOpenSourceLicense: () -> Unit,
    goToSettingsCatcherRecord: () -> Unit,
) {
    val vm = hiltViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    val subscribeState by LVLStateHolder.subscribeState.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            Scaffold(
                modifier = Modifier,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(
                                    modifier = Modifier.size(32.dp),
                                    pkg = Pkg(BuildProp.APP_PACKAGE_NAME)
                                )
                                TinySpacer()
                                Text(text = LocalAppName.current)
                                TinySpacer()
                                BuildProp.APP_VERSION_NAME_SUFFIX.ifEmpty { null }?.let {
                                    OutlineBadge(text = it)
                                }
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(bottom = 32.dp)
                        .animateContentSize()

                ) {
                    AnimatedVisibility(visible = state.hasFatalError) {
                        ErrorCard(
                            modifier = Modifier
                                .padding(16.dp),
                            title = LocalI18N.current["ui.settings.fatal.error.title"],
                            warnings = listOf(
                                LocalI18N.current["ui.settings.fatal.error.message", mapOf("dir" to state.logDir)]
                            )
                        ) {

                        }

                        StandardSpacer()
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val context = LocalContext.current
                        MediumTitle(text = LocalI18N.current["ui.common.settings"])
                        LinkText(text = LocalI18N.current["ui.user.guide"]) {
                            BrowserUtils.launch(context, BuildProp.WIKI_URL)
                        }
                    }

                    val i18N = LocalI18N.current
                    LaunchedEffect(vm) {
                        vm.backupPerformed.collectLatest {
                            when (it) {
                                is BackupResult.Success -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N["ui.settings.toast.backup.complete", mapOf("path" to it.path)]
                                        )
                                    }
                                }

                                is BackupResult.Failed -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N["ui.settings.toast.backup.error", mapOf("err" to it.error)],
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(vm) {
                        vm.restorePerformed.collectLatest {
                            when (it) {
                                is RestoreResult.Success -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N["ui.settings.toast.restore.complete"]
                                        )
                                    }
                                }

                                is RestoreResult.Failed -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N["ui.settings.toast.backup.error", mapOf("err" to it.error)],
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(vm) {
                        vm.logExportPerformed.collectLatest {
                            when (it) {
                                is ExportLogResult.Success -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            it.path
                                        )
                                    }
                                }

                                is ExportLogResult.Failed -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            it.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val props = LocalShortXProps.current
                    val pickFileLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.GetContent()
                    ) { fileUri ->
                        if (fileUri != null) {
                            vm.restoreFrom(fileUri, props.firstOrNull())
                        }
                    }

                    val deleteDialogState = rememberConfirmDialogState()
                    ConfirmDialog(
                        title = LocalI18N.current["ui.settings.data.reset.title"],
                        state = deleteDialogState,
                        data = "",
                        messageHint = { i18N["ui.delete.dialog.confirm", mapOf("wht" to i18N["ui.settings.data.reset.dialog.hint"])] },
                        onConfirm = {
                            vm.resetData()
                        }
                    )

                    PreferenceUi(mutableListOf<Preference>().apply {
                        addAll(
                            generalSettings(
                                state = state,
                                mainState = subscribeState,
                                vm = vm,
                                goToPkgSetList = goToPkgSetList,
                                goToGV = goToGV,
                                goToDanmuUISettings = goToDanmuUISettings,
                            )
                        )
                        addAll(
                            recordSettings(
                                state = state,
                                mainState = subscribeState,
                                vm = vm,
                                goToEvaRecord = goToEvaRecord,
                                goToAudioRecord = goToAudioRecord,
                                goToGestureRecord = goToGestureRecord,
                            )
                        )
                        addAll(dataSettings(
                            state = state,
                            mainState = subscribeState,
                            vm = vm,
                            pickRestoreFile = {
                                pickFileLauncher.launch("application/zip")
                            }, resetData = {
                                deleteDialogState.show()
                            })
                        )
                        addAll(shareSettings(state = state, vm = vm))
                        addAll(
                            systemSettings(
                                state = state,
                                mainState = subscribeState,
                                vm = vm,
                            )
                        )
                        addAll(
                            engineSettings(
                                state = state,
                                mainState = subscribeState,
                                vm = vm,
                            )
                        )
                        addAll(devSettings(state = state, vm = vm))
                        addAll(
                            aboutSettings(
                                state = state,
                                viewOpenSourceLicense = viewOpenSourceLicense
                            )
                        )
                    })

                    StandardSpacer()
                    SubscriptionStatus(subscribeState)
                }
            }
        }
    }
}

@Composable
private fun SubscriptionStatus(mainState: LVLStateHolder.State) {
    Box(modifier = Modifier) {
        val context = LocalContext.current
        if (mainState.isSubscribed) {
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                onClick = {
                    SubscribeActivity.start(context)
                }) {
                LottieLoadingView(
                    file = "47603-twinkle-crown.json",
                    modifier = Modifier.size(30.dp),
                )
                SmallSpacer()
                Text(text = LocalI18N.current["ui.subscription.subscribed"], fontSize = 12.5.sp)
            }
        }
    }
}

@Composable
private fun generalSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToDanmuUISettings: () -> Unit,
): List<Preference> {
    val edgeGestureSizeSelectorState = rememberEdgeGestureSizeValueSelectorState(selected = {
        vm.setGestureAreaSize(it)
    })
    EdgeGestureSizeValueSelector(state = edgeGestureSizeSelectorState)
    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.general"]),

        Preference.TextPreference(icon = Remix.System.apps_fill,
            title = LocalI18N.current["ui.pkg.set.title"],
            summary = LocalI18N.current["ui.pkg.set.desc"],
            onClick = {
                goToPkgSetList()
            }
        ),

        Preference.TextPreference(icon = Remix.Document.file_list_fill,
            title = LocalI18N.current["ui.global.var.title"],
            summary = LocalI18N.current["ui.global.var.desc"],
            onClick = {
                goToGV()
            }
        ),

        Preference.TextPreference(icon = Remix.Communication.message_fill,
            title = LocalI18N.current["ui.settings.danmu"],
            summary = LocalI18N.current["ui.settings.danmu.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select({ goToDanmuUISettings() },
                    { subscribeDialogState.show() })
            }
        ),


        Preference.TextPreference(icon = Remix.Editor.sketching,
            title = LocalI18N.current["ui.settings.edge.gesture.size"],
            summary = LocalI18N.current["ui.settings.edge.gesture.size.summary"],
            onClick = {
                edgeGestureSizeSelectorState.show(state.gestureAreaSize)
            }
        ),
    )
}


@Composable
private fun recordSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    goToEvaRecord: () -> Unit,
    goToAudioRecord: () -> Unit,
    goToGestureRecord: () -> Unit,
): List<Preference> {
    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    return listOfNotNull(
        Preference.Category(LocalI18N.current["ui.settings.record"]),

        Preference.TextPreference(icon = Remix.System.history_fill,
            title = LocalI18N.current["ui.settings.eva.record"],
            summary = LocalI18N.current["ui.settings.eva.record.summary"],
            onClick = {
                goToEvaRecord()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Media.mic_2_fill,
            title = LocalI18N.current["ui.settings.audio.record"],
            summary = LocalI18N.current["ui.settings.audio.record.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select({ goToAudioRecord() },
                    { subscribeDialogState.show() })
            }
        ),
        Preference.TextPreference(
            icon = Remix.Editor.sketching,
            title = LocalI18N.current["ui.settings.gesture.record"],
            summary = LocalI18N.current["ui.settings.gesture.record.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select({ goToGestureRecord() },
                    { subscribeDialogState.show() })
            }
        )
    )
}

@Composable
private fun dataSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    pickRestoreFile: () -> Unit,
    resetData: () -> Unit,
): List<Preference> {
    val backUpFileNameDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.settings.data.backup.title"],
        message = LocalI18N.current["ui.settings.data.backup.input.file.name"],
        showSymbolButton = false,
        onSelected = {
            vm.backup(it)
        })
    TextInputDialog(state = backUpFileNameDialogState)

    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.data"]),
        Preference.TextPreference(
            icon = Remix.Device.save_3_fill,
            title = LocalI18N.current["ui.settings.data.backup.title"],
            summary = LocalI18N.current["ui.settings.data.backup.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { backUpFileNameDialogState.show(autoGenBackupFileName()) },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.ExpandablePreference(
            icon = Remix.System.download_2_fill,
            title = LocalI18N.current["ui.settings.data.restore.title"],
            summary = LocalI18N.current["ui.settings.data.restore.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { pickRestoreFile() },
                    { subscribeDialogState.show() })
            },
            preferences = listOf(
                Preference.TextPreference(
                    icon = Remix.System.history_fill,
                    title = LocalI18N.current["ui.settings.data.reset.title"],
                    summary = LocalI18N.current["ui.settings.data.reset.summary"],
                    onClick = {
                        resetData()
                    }
                )
            ),
        ),
    )
}


@Composable
private fun shareSettings(
    state: SettingsState,
    vm: SettingsViewModel
): List<Preference> {
    val authorNameDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.settings.share.author.info"],
        message = null,
        onSelected = {
            vm.setShareAuthor(Author.newBuilder().setName(it).build())
        }
    )
    TextInputDialog(state = authorNameDialogState)

    return mutableListOf(
        Preference.Category(LocalI18N.current["ui.settings.share"]),

        Preference.TextPreference(
            icon = Remix.System.share_box_fill,
            title = LocalI18N.current["ui.settings.share.author.info"],
            summary = LocalI18N.current["ui.settings.share.author.info.description"],
            onClick = {
                authorNameDialogState.show(state.shareAuthor?.name)
            }
        )
    )
}


@Composable
private fun devSettings(
    state: SettingsState,
    vm: SettingsViewModel
): List<Preference> {
    val permissionRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
    }
    val props = LocalShortXProps.current
    return mutableListOf(
        Preference.Category(LocalI18N.current["ui.settings.dev"]),

        Preference.SwitchPreference(icon = Remix.Document.article_fill,
            title = LocalI18N.current["ui.settings.enable.debug.log"],
            summary = LocalI18N.current["ui.settings.enable.debug.log.description", mapOf("dir" to state.logDir)],
            value = state.isDebugLogEnabled,
            onCheckChanged = { enable ->
                vm.setLogDebugEnable(enable)
            }),

        Preference.TextPreference(
            icon = Remix.Device.save_3_fill,
            title = LocalI18N.current["ui.settings.debug.log.export"],
            summary = LocalI18N.current["ui.settings.debug.log.export.description", mapOf("dir" to Environment.DIRECTORY_DOWNLOADS)],
            onClick = {
                vm.exportLogs()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Device.keyboard_box_fill,
            title = LocalI18N.current["ui.settings.key.event.prompt"],
            summary = LocalI18N.current["ui.settings.key.event.prompt.description"],
            value = state.isKeyEventPromptEnabled,
            onCheckChanged = { enable ->
                vm.toggleKeyEventPromptEnabled()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Development.cursor_fill,
            title = LocalI18N.current["ui.common.show.pointer.location"],
            summary = LocalI18N.current["ui.common.show.pointer.location.description"],
            value = state.isPointerLocationEnabled,
            onCheckChanged = { enable ->
                vm.togglePointerLocation()
            }
        )
    ).apply {
        if (BuildProp.APP_IS_DEBUG) {
            add(Preference.SwitchPreference(
                icon = Remix.Business.global_fill,
                title = LocalI18N.current["ui.settings.http:chucker"],
                summary = LocalI18N.current["ui.settings.http:chucker.description"],
                value = state.enableHttpChucker,
                onCheckChanged = { enable ->
                    vm.setEnableHttpChucker(enable)
                    if (enable && OsUtils.isTOrAbove()) {
                        permissionRequester.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            ))
            add(Preference.TextPreference(
                icon = Remix.System.download_fill,
                title = "Import prebuilt data",
                summary = "Include prebuilt direct actions and rules.",
                onClick = {
                    vm.importPrebuilts(props.firstOrNull())
                }
            ))

            add(Preference.SwitchPreference(
                icon = Remix.Device.install_fill,
                title = "Install fake service",
                summary = "For debug purpose, restart app to take effect",
                value = state.installFakeService,
                onCheckChanged = { enable ->
                    vm.setInstallFakeService(enable)
                }
            ))

            add(Preference.SwitchPreference(
                icon = Remix.Device.install_fill,
                title = "Ignore framework update",
                summary = "Do not show framework updated tips on main screen",
                value = state.isIgnoreFrameworkUpdate,
                onCheckChanged = { enable ->
                    vm.setIgnoreFrameworkUpdate(enable)
                }
            ))
        }
    }
}


@Composable
private fun systemSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
): List<Preference> {
    val i18N = LocalI18N.current
    val badge = if (mainState.isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)
    return mutableListOf(
        Preference.Category(i18N["ui.settings.system"]),
        Preference.SwitchPreference(
            icon = Remix.Media.equalizer_fill,
            title = i18N["ui.settings.sb.tile.opt"],
            summary = i18N["ui.settings.sb.tile.opt.summary"],
            hasLongSummary = true,
            badge = badge,
            value = state.isQSTileOptEnabled,
            onCheckChanged = {
                if (mainState.isSubscribed) {
                    vm.toggleQSTileOpt()
                } else {
                    subscribeDialogState.show()
                }
            }
        )
    )
}

@Composable
private fun engineSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
): List<Preference> {
    val i18N = LocalI18N.current
    val badge = if (mainState.isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    val shellConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.settings.rule.engine.shell.confirm.title"],
        messageHint = {
            i18N["ui.settings.rule.engine.shell.confirm.message"]
        },
        state = shellConfirmDialogState,
        data = Unit,
        onConfirm = {
            vm.setShellEngineEnable(true)
        }
    )

    val mvelConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.settings.rule.engine.mvel.confirm.title"],
        messageHint = {
            i18N["ui.settings.rule.engine.mvel.confirm.message"]
        },
        state = mvelConfirmDialogState,
        data = Unit,
        onConfirm = {
            vm.setMVELEngineEnable(true)
        }
    )

    return mutableListOf(
        Preference.Category(i18N["ui.settings.engine"]),

        Preference.SwitchPreference(
            icon = Remix.Editor.hashtag,
            title = i18N["ui.settings.rule.engine.shell"],
            summary = i18N["ui.settings.rule.engine.shell.summary"],
            hasLongSummary = false,
            value = state.shellEngineEnabled,
            onCheckChanged = { enable ->
                if (enable) {
                    shellConfirmDialogState.show()
                } else {
                    vm.setShellEngineEnable(false)
                }
            }
        ),
        state.shellEngineEnabled.select(
            Preference.SwitchPreference(
                icon = Remix.Editor.hashtag,
                title = i18N["ui.settings.injected.shell"],
                summary = i18N["ui.settings.injected.shell.description"],
                hasLongSummary = true,
                badge = badge,
                value = state.isInjectedShellEnabled,
                onCheckChanged = {
                    if (mainState.isSubscribed) {
                        vm.toggleInjectedShell()
                    } else {
                        subscribeDialogState.show()
                    }
                }
            ),
            null
        ),

        Preference.SwitchPreference(
            icon = Remix.Development.code_fill,
            title = i18N["ui.settings.rule.engine.mvel"],
            summary = i18N["ui.settings.rule.engine.mvel.summary"],
            hasLongSummary = false,
            value = state.mvelEngineEnabled,
            onCheckChanged = { enable ->
                if (enable) {
                    mvelConfirmDialogState.show()
                } else {
                    vm.setMVELEngineEnable(false)
                }
            }
        )
    ).filterNotNull()
}

@Composable
private fun aboutSettings(
    state: SettingsState,
    viewOpenSourceLicense: () -> Unit
): List<Preference> {
    val context = LocalContext.current
    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.about"]),

        Preference.ExpandablePreference(
            icon = Remix.System.information_fill,
            title = LocalI18N.current["ui.settings.app.info"],
            summary = "${BuildProp.APP_VERSION_NAME}\n${BuildProp.APP_VERSION_CODE}",
            preferences = listOf(
                Preference.TextPreference(icon = Remix.Device.instance_fill,
                    title = LocalI18N.current["ui.settings.core.info"],
                    summary = if (state.isActivated) {
                        "${state.coreVersion.name}\n${state.coreVersion.code}"
                    } else {
                        LocalI18N.current["ui.settings.core.info.na"]
                    },
                    onClick = {}
                )
            ),
        ),

        Preference.TextPreference(icon = Remix.Logos.telegram_fill,
            title = LocalI18N.current["ui.settings.tg"],
            summary = LocalI18N.current["ui.settings.tg.summary"],
            onClick = {
                BrowserUtils.launch(context, BuildProp.TG_GROUP)
            }
        ),
        Preference.TextPreference(icon = Remix.Logos.open_source_fill,
            title = LocalI18N.current["ui.settings.open.source.license"],
            onClick = {
                viewOpenSourceLicense()
            }
        )
    )
}