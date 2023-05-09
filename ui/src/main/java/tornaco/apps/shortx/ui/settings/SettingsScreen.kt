package tornaco.apps.shortx.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.DeleteConfirmDialog
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.rememberDeleteConfirmDialogState
import tornaco.apps.shortx.ui.row.featureAccessCheckUi
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
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.common.settings"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {},
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp)
                        .animateContentSize()
                ) {
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
                }
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

    Preference.TextPreference(icon = Remix.System.apps_line,
        title = LocalI18N.current["ui.pkg.set.title"],
        summary = LocalI18N.current["ui.pkg.set.desc"],
        onClick = {
            goToPkgSetList()
        }
    ),

    Preference.TextPreference(icon = Remix.Document.file_list_line,
        title = LocalI18N.current["ui.global.var.title"],
        summary = LocalI18N.current["ui.global.var.desc"],
        onClick = {
            goToGV()
        }
    ),

    Preference.TextPreference(icon = Remix.Communication.message_line,
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
    return listOf(
        Preference.Category(LocalI18N.current["ui.settings.data"]),
        Preference.TextPreference(
            icon = Remix.Device.save_3_line,
            title = LocalI18N.current["ui.settings.data.backup.title"],
            summary = LocalI18N.current["ui.settings.data.backup.summary"],
            onClick = {
                featureAccess.accessOrShowDialog(AppFeature.AppFeature_BackupRestore) {
                    vm.backup()
                }
            }
        ),
        Preference.TextPreference(
            icon = Remix.System.download_2_line,
            title = LocalI18N.current["ui.settings.data.restore.title"],
            summary = LocalI18N.current["ui.settings.data.restore.summary"],
            onClick = {
                featureAccess.accessOrShowDialog(AppFeature.AppFeature_BackupRestore) {
                    pickRestoreFile()
                }
            }
        ),
        Preference.TextPreference(
            icon = Remix.System.history_line,
            title = LocalI18N.current["ui.settings.data.reset.title"],
            summary = LocalI18N.current["ui.settings.data.reset.summary"],
            onClick = {
                resetData()
            }
        )
    )
}

@Composable
private fun devSettings(
    state: SettingsState,
    vm: SettingsViewModel
) = mutableListOf(
    Preference.Category(LocalI18N.current["ui.settings.dev"]),

    Preference.SwitchPreference(icon = Remix.Document.article_line,
        title = LocalI18N.current["ui.settings.enable.debug.log"],
        summary = LocalI18N.current["ui.settings.enable.debug.log.description"],
        value = state.isDebugLogEnabled,
        onCheckChanged = { enable ->
            vm.setLogDebugEnable(enable)
        }),
).apply {
    if (BuildProp.APP_IS_DEBUG) {
        add(Preference.TextPreference(
            icon = Remix.System.download_line,
            title = "Import prebuilt data",
            summary = "Include prebuilt direct actions and rules.",
            onClick = {
                vm.importPrebuilts()
            }
        ))

        add(Preference.SwitchPreference(
            icon = Remix.Device.install_line,
            title = "Install fake service",
            summary = "For debug purpose, restart app to take effect",
            value = state.installFakeService,
            onCheckChanged = { enable ->
                vm.setInstallFakeService(enable)
            }
        ))

        add(Preference.SwitchPreference(
            icon = Remix.Device.install_line,
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
        icon = Remix.System.information_line,
        title = LocalI18N.current["ui.settings.app.info"],
        summary = "${BuildProp.APP_VERSION_NAME}\n${BuildProp.APP_VERSION_CODE}",
        preferences = listOf(
            Preference.TextPreference(icon = Remix.Device.instance_line,
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

    Preference.TextPreference(icon = Remix.Logos.open_source_line,
        title = LocalI18N.current["ui.settings.open.source.license"],
        onClick = {
            viewOpenSourceLicense()
        }
    )
)