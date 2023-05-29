package tornaco.apps.shortx.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.backup.autoGenBackupFileName
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.main.AppRoute
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.subscribe.LottieLoadingView
import tornaco.apps.shortx.ui.subscribe.featureAccessCheckUi
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    back: () -> Unit,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToDanmuUISettings: () -> Unit,
    viewOpenSourceLicense: () -> Unit
) {
    val vm = hiltViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

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
                        WarningCard(
                            title = LocalI18N.current["ui.settings.fatal.error.title"],
                            warnings = listOf(
                                LocalI18N.current["ui.settings.fatal.error.message", mapOf("dir" to state.logDir)]
                            )
                        ) {

                        }

                        StandardSpacer()
                    }

                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MediumTitle(text = LocalI18N.current["ui.common.settings"])
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


                    val pickFileLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.GetContent()
                    ) { fileUri ->
                        if (fileUri != null) {
                            vm.restoreFrom(fileUri)
                        }
                    }

                    val deleteDialogState = rememberDeleteConfirmDialogState()
                    DeleteConfirmDialog(
                        titleKey = "ui.settings.data.reset.title",
                        state = deleteDialogState,
                        data = "",
                        messageHint = { i18N["ui.settings.data.reset.dialog.hint"] },
                        onDelete = {
                            vm.resetData()
                        }
                    )

                    PreferenceUi(mutableListOf<Preference>().apply {
                        addAll(
                            generalSettings(
                                state = state,
                                vm = vm,
                                goToPkgSetList = goToPkgSetList,
                                goToGV = goToGV,
                                goToDanmuUISettings = goToDanmuUISettings
                            )
                        )
                        addAll(dataSettings(
                            state = state,
                            vm = vm,
                            pickRestoreFile = {
                                pickFileLauncher.launch("application/zip")
                            }, resetData = {
                                deleteDialogState.show()
                            })
                        )
                        addAll(devSettings(state, vm))
                        addAll(aboutSettings(state, viewOpenSourceLicense))
                    })

                    StandardSpacer()
                    SubscriptionStatus()
                }
            }
        }
    }
}

@Composable
private fun SubscriptionStatus() {
    Box(modifier = Modifier) {
        val navHost = LocalNavHostController.current
        val mainViewModel = hiltViewModel<MainViewModel>()
        LaunchedEffect(mainViewModel) {
            mainViewModel.refreshStates()
        }
        val mainState by mainViewModel.state.collectAsState()
        if (mainState.isSubscribed) {
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                onClick = {
                    navHost.navigate(AppRoute.SubscriptionIntro.name)
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
    vm: SettingsViewModel,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToDanmuUISettings: () -> Unit
) = listOf(
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
        onClick = {
            goToDanmuUISettings()
        }
    ),
)

@Composable
private fun dataSettings(
    state: SettingsState,
    vm: SettingsViewModel,
    pickRestoreFile: () -> Unit,
    resetData: () -> Unit,
): List<Preference> {
    val featureAccess = featureAccessCheckUi()
    val backUpFileNameDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.settings.data.backup.title"],
        message = LocalI18N.current["ui.settings.data.backup.input.file.name"],
        onSelected = {
            vm.backup(it)
        })
    TextInputDialog(state = backUpFileNameDialogState)


    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.data"]),
        Preference.TextPreference(
            icon = Remix.Device.save_3_fill,
            title = LocalI18N.current["ui.settings.data.backup.title"],
            summary = LocalI18N.current["ui.settings.data.backup.summary"],
            onClick = {
                featureAccess.accessOrShowDialog(AppFeature.AppFeature_BackupRestore) {
                    backUpFileNameDialogState.show(autoGenBackupFileName())
                }
            }
        ),

        Preference.ExpandablePreference(
            icon = Remix.System.download_2_fill,
            title = LocalI18N.current["ui.settings.data.restore.title"],
            summary = LocalI18N.current["ui.settings.data.restore.summary"],
            onClick = {
                featureAccess.accessOrShowDialog(AppFeature.AppFeature_BackupRestore) {
                    pickRestoreFile()
                }
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
private fun devSettings(
    state: SettingsState,
    vm: SettingsViewModel
) = mutableListOf(
    Preference.Category(LocalI18N.current["ui.settings.dev"]),

    Preference.SwitchPreference(icon = Remix.Document.article_fill,
        title = LocalI18N.current["ui.settings.enable.debug.log"],
        summary = LocalI18N.current["ui.settings.enable.debug.log.description", mapOf("dir" to state.logDir)],
        value = state.isDebugLogEnabled,
        onCheckChanged = { enable ->
            vm.setLogDebugEnable(enable)
        }),
).apply {
    if (BuildProp.APP_IS_DEBUG) {
        add(Preference.TextPreference(
            icon = Remix.System.download_fill,
            title = "Import prebuilt data",
            summary = "Include prebuilt direct actions and rules.",
            onClick = {
                vm.importPrebuilts()
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

@Composable
private fun aboutSettings(
    state: SettingsState,
    viewOpenSourceLicense: () -> Unit
) = listOf(
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

    Preference.TextPreference(icon = Remix.Logos.open_source_fill,
        title = LocalI18N.current["ui.settings.open.source.license"],
        onClick = {
            viewOpenSourceLicense()
        }
    )
)