package tornaco.apps.shortx.ui.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.backup.autoGenBackupFileName
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.OCREngine
import tornaco.apps.shortx.core.proto.shortXProtoVersion
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.TestData
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.fontFamilyProductSansBold
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.LottieLoadingView
import tornaco.apps.shortx.ui.subscribe.SubscribeActivity
import tornaco.apps.shortx.ui.theme.ShortXRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils
import tornaco.apps.shortx.ui.util.ClipboardUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    back: () -> Unit,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToDanmuUISettings: () -> Unit,
    goToStatusBarClipUISettings: () -> Unit,
    goToEvaRecord: () -> Unit,
    goToAudioRecord: () -> Unit,
    goToGestureRecord: () -> Unit,
    viewOpenSourceLicense: () -> Unit,
    viewPrivacyStatement: () -> Unit,
    manageShortcutApps: () -> Unit,
    goToFeatToggle: () -> Unit,
    goToSettingsCatcherRecord: () -> Unit,
    goToWebdavProfile: () -> Unit,
) {
    val vm = hiltViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    val subscribeState by LVLStateHolder.collectAsState()
    val context = LocalContext.current

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val guideSelectorDialogState = rememberMenuDialogState<String>(
                title = LocalI18N.current["ui.user.guide"],
                menuItems = listOf(
                    MenuDialogItem(
                        id = "Bili",
                        title = "bilibili",
                        iconName = Remix.Logos.bilibili_fill
                    ),
                    MenuDialogItem(
                        id = "Github",
                        title = "Github",
                        iconName = Remix.Logos.github_fill
                    ),
                )
            ) { _, data ->
                if (data == "Github") {
                    BrowserUtils.launch(context, BuildProp.WIKI_URL)
                } else {
                    BrowserUtils.launch(context, BuildProp.WIKI_URL_BILI)
                }
            }
            MenuDialog(state = guideSelectorDialogState)

            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        scrollBehavior = scrollBehavior,
                        title = {
                            Row(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Short",
                                        fontFamily = fontFamilyProductSansBold(),
                                        modifier = Modifier.alignByBaseline(),
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = "X",
                                        fontFamily = fontFamilyProductSansBold(),
                                        modifier = Modifier.alignByBaseline(),
                                        color = ShortXRed,
                                        fontSize = 24.sp
                                    )

                                    TinySpacer()
                                    BuildPropInternal.APP_VERSION_NAME_SUFFIX.ifEmpty { null }
                                        ?.let {
                                            OutlineBadge(text = it)
                                        }
                                }

                                LinkText(text = LocalI18N.current["ui.user.guide"]) {
                                    guideSelectorDialogState.show()
                                }
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .consumeWindowInsets(paddingValues)
                        .padding(bottom = 32.dp)
                        .animateContentSize()

                ) {
                    Spacer(Modifier.size(paddingValues.calculateTopPadding()))
                    AnimatedVisibility(visible = state.hasFatalError) {
                        ErrorCard(
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            title = LocalI18N.current["ui.settings.fatal.error.title"],
                            warnings = listOf(
                                LocalI18N.current["ui.settings.fatal.error.message", mapOf("dir" to state.logDir)]
                            )
                        ) {

                        }

                        StandardSpacer()
                    }

                    AnimatedVisibility(visible = state.sensorUsages.isNotEmpty()) {
                        Column {
                            SensorUsage(state.sensorUsages)
                            StandardSpacer()
                        }
                    }

                    SwitchBar(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        isChecked = state.ruleFeatureEnabled,
                        text = LocalI18N.current["ui.settings.enable.rule.feature"],
                        tip = LocalI18N.current["ui.settings.enable.rule.feature.description"]
                    ) {
                        vm.toggleRuleFeatureEnable(it)
                    }

                    val i18N = LocalI18N.current
                    val backupErrorTitle = i18N["ui.settings.backup.error.title"]
                    val backupErrorAction = i18N["ui.settings.backup.error.action"]
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
                                        val res = snackbarHostState.showSnackbar(
                                            message = backupErrorTitle,
                                            actionLabel = backupErrorAction,
                                            withDismissAction = true
                                        )
                                        if (res == SnackbarResult.ActionPerformed) {
                                            kotlin.runCatching {
                                                PkgUtils.launchPkgSettings(
                                                    context,
                                                    BuildPropInternal.APP_PACKAGE_NAME
                                                )
                                            }
                                        }
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
                    val exportLogErrorTitle =
                        i18N["ui.settings.export.log.error.title"]
                    val exportLogErrorAction =
                        i18N["ui.settings.export.log.error.action"]

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
                                        composeLogger.e(it.error)
                                        val res = snackbarHostState.showSnackbar(
                                            message = exportLogErrorTitle,
                                            actionLabel = exportLogErrorAction,
                                            withDismissAction = true
                                        )
                                        if (res == SnackbarResult.ActionPerformed) {
                                            kotlin.runCatching {
                                                PkgUtils.launchPkgSettings(
                                                    context,
                                                    BuildPropInternal.APP_PACKAGE_NAME
                                                )
                                            }
                                        }
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
                                goToStatusBarClipUISettings = goToStatusBarClipUISettings,
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
                                manageShortcutApps = manageShortcutApps,
                                goToWebdavProfile = goToWebdavProfile
                            )
                        )
                        addAll(
                            miscSettings(
                                state = state,
                                mainState = subscribeState,
                                vm = vm,
                                goToFeatToggle = goToFeatToggle
                            )
                        )
                        addAll(
                            dataSettings(
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
                        addAll(devSettings(state = state, vm = vm))
                        addAll(
                            aboutSettings(
                                state = state,
                                viewOpenSourceLicense = viewOpenSourceLicense,
                                viewPrivacyStatement = viewPrivacyStatement,
                            )
                        )
                    })
                    StandardSpacer()
                    SubscriptionStatus(subscribeState)

                    LargeSpacer()
                    ProtoVersion()

                    Spacer(Modifier.size(paddingValues.calculateBottomPadding()))
                }
            }
        }
    }
}

@Composable
private fun ProtoVersion() {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Proto version: $shortXProtoVersion",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic
        ),
    )
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
    goToStatusBarClipUISettings: () -> Unit,
): List<Preference> {
    val context = LocalContext.current

    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.general"]),

        Preference.TextPreference(
            icon = Remix.System.apps_fill,
            title = LocalI18N.current["ui.pkg.set.title"],
            summary = LocalI18N.current["ui.pkg.set.desc"],
            onClick = {
                goToPkgSetList()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Document.file_list_fill,
            title = LocalI18N.current["ui.global.var.title"],
            summary = LocalI18N.current["ui.global.var.desc"],
            onClick = {
                goToGV()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Communication.message_fill,
            title = LocalI18N.current["ui.settings.danmu"],
            summary = LocalI18N.current["ui.settings.danmu.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToDanmuUISettings() },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.TextPreference(
            icon = Remix.Communication.message_fill,
            title = LocalI18N.current["ui.settings.statusbar.chip"],
            summary = LocalI18N.current["ui.settings.statusbar.chip.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToStatusBarClipUISettings() },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.TextPreference(
            icon = Remix.Editor.sketching,
            title = LocalI18N.current["ui.settings.key.and.gesture"],
            summary = LocalI18N.current["ui.settings.key.and.gesture.summary"],
            onClick = {
                KeyAndGestureSettingsActivity.start(context)
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.System.forbid_fill,
            title = LocalI18N.current["ui.settings.force.disable.by.key.press.title"],
            summary = LocalI18N.current["ui.settings.force.disable.by.key.press.description"],
            hasLongSummary = false,
            value = state.isForceDisableShortXByKeyPress,
            onCheckChanged = { enable ->
                vm.setForceDisableShortXByKeyPressEnabled(enable)
            }
        ),
    )
}

@Composable
private fun miscSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    goToFeatToggle: () -> Unit,
): List<Preference> {
    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOf(
        Preference.Category(LocalI18N.current["ui.action.misc"]),
        Preference.TextPreference(
            icon = Remix.Design.magic_fill,
            title = LocalI18N.current["ui.settings.feat.toggle"],
            summary = LocalI18N.current["ui.settings.feat.toggle.summary"],
            onClick = {
                goToFeatToggle()
            }
        ),
        Preference.SwitchPreference(
            icon = Remix.Design.palette_fill,
            title = LocalI18N.current["ui.settings.theme.dynamic.color"],
            summary = LocalI18N.current["ui.settings.theme.dynamic.color.summary"],
            value = state.dynamicColor,
            onCheckChanged = {
                vm.setDynamicColor(!state.dynamicColor)
            }
        ),
        Preference.SwitchPreference(
            icon = Remix.Design.pantone_fill,
            title = LocalI18N.current["ui.settings.colorful.card"],
            summary = LocalI18N.current["ui.settings.colorful.card.summary"],
            value = state.colorfulCard,
            badge = badge,
            onCheckChanged = {
                if (mainState.isSubscribed) {
                    vm.setColorfulCard(!state.colorfulCard)
                } else {
                    subscribeDialogState.show()
                }
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

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOfNotNull(
        Preference.Category(LocalI18N.current["ui.settings.record"]),

        Preference.TextPreference(
            icon = Remix.System.history_fill,
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
                mainState.isSubscribed.select(
                    { goToAudioRecord() },
                    { subscribeDialogState.show() })
            }
        ),
        Preference.TextPreference(
            icon = Remix.Editor.sketching,
            title = LocalI18N.current["ui.settings.gesture.record"],
            summary = LocalI18N.current["ui.settings.gesture.record.summary"],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToGestureRecord() },
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

    val subscribeDialogState = rememberShortXBottomSheetState()
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

        Preference.SwitchPreference(
            icon = Remix.Document.article_fill,
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
        if (BuildPropInternal.APP_IS_DEBUG) {
            add(
                Preference.ExpandablePreference(
                    icon = Remix.Development.code_fill,
                    title = "Dev options",
                    summary = "Tools settings for developer",
                    preferences = mutableListOf<Preference>().apply {
                        add(
                            Preference.SwitchPreference(
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
                        add(
                            Preference.TextPreference(
                                icon = Remix.System.download_fill,
                                title = "Import prebuilt data",
                                summary = "Include prebuilt direct actions and rules.",
                                onClick = {
                                    vm.importPrebuilts(props.firstOrNull())
                                }
                            ))

                        add(
                            Preference.SwitchPreference(
                                icon = Remix.Device.install_fill,
                                title = "Install fake service",
                                summary = "For debug purpose, restart app to take effect",
                                value = state.installFakeService,
                                onCheckChanged = { enable ->
                                    vm.setInstallFakeService(enable)
                                }
                            ))

                        add(
                            Preference.SwitchPreference(
                                icon = Remix.Device.install_fill,
                                title = "Ignore framework update",
                                summary = "Do not show framework updated tips on main screen",
                                value = state.isIgnoreFrameworkUpdate,
                                onCheckChanged = { enable ->
                                    vm.setIgnoreFrameworkUpdate(enable)
                                }
                            ))

                        add(
                            Preference.TextPreference(
                                icon = Remix.System.loader_2_line,
                                title = "Insert DA",
                                summary = "Insert 100 da test data",
                                onClick = {
                                    TestData.insertDATestData()
                                }
                            ))

                        add(
                            Preference.TextPreference(
                                icon = Remix.System.loader_2_line,
                                title = "Insert Rule",
                                summary = "Insert 100 rule test data",
                                onClick = {
                                    TestData.insertRuleTestData()
                                }
                            ))

                        add(
                            Preference.TextPreference(
                                icon = Remix.Animals.cat,
                                title = "Crash",
                                summary = "Crash app now",
                                onClick = {
                                    throw Error("Test app crash")
                                }
                            ))
                    },
                ),
            )
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

    val subscribeDialogState = rememberShortXBottomSheetState()
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
    manageShortcutApps: () -> Unit,
    goToWebdavProfile: () -> Unit,
): List<Preference> {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val badge = if (mainState.isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
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

    val smsConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.engine.send.sms"],
        messageHint = {
            i18N["ui.engine.send.sms.enable.message"]
        },
        state = smsConfirmDialogState,
        data = Unit,
        onConfirm = {
            vm.setSMSEngineEnable(true)
        }
    )

    val permissionRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
    }

    val customSUDialogState =
        rememberTextInputState(title = i18N["ui.settings.custom.su.path"], message = null) {
            vm.setCustomSUPath(it)
        }
    TextInputDialog(customSUDialogState)

    val ocrEngineMenuState = rememberMenuDialogState<String>(
        title = i18N["ORC Engine"],
        menuItems = listOf(
            OCREngine.OCREngine_Paddle,
            OCREngine.OCREngine_Tesseract
        ).map {
            MenuDialogItem(
                id = it.name,
                title = i18N[it.labelKey()]
            )
        },
        onItemSelected = { _, id ->
            val engine = OCREngine.valueOf(id)
            vm.setOCREngine(engine)
        }
    )
    MenuDialog(state = ocrEngineMenuState)

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
        state.shellEngineEnabled.select(
            Preference.TextPreference(
                icon = Remix.Editor.hashtag,
                title = i18N["ui.settings.custom.su.path"],
                summary = i18N["ui.settings.custom.su.path.description", mapOf("path" to state.customSUPath.orEmpty())],
                hasLongSummary = false,
                badge = badge,
                onClick = {
                    customSUDialogState.show(state.customSUPath)
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
        ),

        Preference.SwitchPreference(
            icon = Remix.Communication.chat_smile_fill,
            title = i18N["ui.engine.send.sms"],
            summary = i18N["ui.engine.send.sms.description"],
            hasLongSummary = false,
            value = state.smsEngineEnabled,
            onCheckChanged = { enable ->
                if (enable) {
                    smsConfirmDialogState.show()
                } else {
                    vm.setSMSEngineEnable(false)
                }
            }
        ),

        Preference.TextPreference(
            icon = Remix.Office.text_recognition,
            title = i18N["OCR engine"],
            summary = i18N[state.ocrEngine.labelKey()],
            hasLongSummary = false,
            onClick = {
                ocrEngineMenuState.show()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Communication.chat_smile_2_line,
            title = i18N["ui.settings.rule.post.n.app"],
            summary = i18N["ui.settings.rule.post.n.app.summary"],
            hasLongSummary = false,
            value = state.isPostNByApp,
            onCheckChanged = { enable ->
                vm.setPostNByAppEnabled(enable)
                if (enable && OsUtils.isTOrAbove()) {
                    permissionRequester.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        ),
        BuildPropInternal.APP_IS_DEBUG.select(
            Preference.TextPreference(
                icon = Remix.System.apps_fill,
                title = i18N["ui.shortx.stub.apk"],
                summary = i18N["ui.shortx.stub.apk.description"] + " DEBUG VISIBLE",
                hasLongSummary = false,
                onClick = manageShortcutApps
            ),
            null
        ),

        Preference.TextPreference(
            icon = Remix.Document.file_cloud_fill,
            title = i18N["ui.settings.webdav.profile"],
            summary = i18N["ui.settings.webdav.profile.description"],
            hasLongSummary = false,
            onClick = goToWebdavProfile
        ),

        Preference.TextPreference(
            icon = Remix.Edit.extend,
            title = i18N["ui.shortx.ext"],
            summary = if (state.extApp == null) i18N["ui.shortx.ext.description"] else i18N["ui.shortx.ext.app.info"],
            onClick = {
                if (state.extApp != null) {
                    PkgUtils.launchPkgSettings(context, state.extApp.pkg.pkgName)
                } else {
                    fun goToMarket() {
                        val packageName = BuildPropInternal.SHORTX_EXT_APP_PKG_NAME
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                        val chooser = Intent.createChooser(intent, i18N["ui.shortx.ext"])
                        try {
                            context.startActivity(chooser)
                        } catch (e: ActivityNotFoundException) {
                            val intentGoogle = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                            val chooserGoogle =
                                Intent.createChooser(intentGoogle, i18N["ui.shortx.ext"])
                            context.startActivity(chooserGoogle)
                        }
                    }
                    BrowserUtils.launch(context, BuildPropInternal.EXT_DOWNLOAD_URL)
                }
            }
        ),
    ).filterNotNull()
}

@Composable
private fun aboutSettings(
    state: SettingsState,
    viewOpenSourceLicense: () -> Unit,
    viewPrivacyStatement: () -> Unit,
): List<Preference> {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val qqGroups = listOf(BuildProp.QQ_GROUP_2, BuildProp.QQ_GROUP_1)
    return listOf(
        Preference.Category(i18N["ui.settings.about"]),

        Preference.ExpandablePreference(
            icon = Remix.System.information_fill,
            title = i18N["ui.settings.app.info"],
            summary = "${BuildPropInternal.APP_VERSION_NAME}\n${BuildPropInternal.APP_VERSION_CODE}",
            preferences = listOf(
                Preference.TextPreference(
                    icon = Remix.Device.instance_fill,
                    title = i18N["ui.settings.core.info"],
                    summary = if (state.isActivated) {
                        "${state.coreVersion.name}\n${state.coreVersion.code}"
                    } else {
                        i18N["ui.settings.core.info.na"]
                    },
                    onClick = {
                    }
                )
            ),
        ),

        Preference.TextPreference(
            icon = Remix.Logos.telegram_fill,
            title = i18N["ui.settings.tg"],
            summary = i18N["ui.settings.tg.summary"],
            onClick = {
                BrowserUtils.launch(context, BuildProp.TG_GROUP)
            }
        ),
        Preference.TextPreference(
            icon = Remix.Logos.qq_fill,
            title = "QQ",
            summary = qqGroups.joinToString(System.lineSeparator()),
            onClick = {
                ClipboardUtils.copyToClipboard(
                    context,
                    "shortx qq",
                    qqGroups.joinToString(System.lineSeparator())
                )
                Toast.makeText(
                    context,
                    i18N["ui.common.copied.to.clipboard"],
                    Toast.LENGTH_LONG
                ).show()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Logos.open_source_fill,
            title = i18N["ui.settings.open.source.license"],
            onClick = {
                viewOpenSourceLicense()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Document.sticky_note_2_fill,
            title = i18N["ui.settings.privacy.statement"],
            onClick = {
                viewPrivacyStatement()
            }
        )
    )
}