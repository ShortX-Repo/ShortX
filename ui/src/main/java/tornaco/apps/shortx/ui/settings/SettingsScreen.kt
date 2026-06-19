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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
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
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.LocalUiSettings
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.fontFamilyProductSansBold
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.changelog.ChangeLogBottomSheet
import tornaco.apps.shortx.ui.code.CodeLibraryListActivity
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.LottieLoadingView
import tornaco.apps.shortx.ui.subscribe.SubscribeActivity
import tornaco.apps.shortx.ui.theme.ShortXRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.UiThemeMode
import tornaco.apps.shortx.ui.util.BrowserUtils
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.core.i18n.I18N_R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    back: () -> Unit,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToSystemEnv: () -> Unit,
    goToFunctionList: () -> Unit,
    goToShellCommandHelp: () -> Unit,
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
                title = LocalI18N.current[I18N_R.UI_USER_GUIDE],
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

            val themeModeDialogState = rememberMenuDialogState<String>(
                title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_MODE],
                menuItems = listOf(
                    MenuDialogItem(
                        id = UiThemeMode.System.name,
                        title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_MODE_SYSTEM],
                        iconName = Remix.Design.contrast_2_fill
                    ),
                    MenuDialogItem(
                        id = UiThemeMode.Light.name,
                        title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_MODE_LIGHT],
                        iconName = Remix.Weather.sun_fill
                    ),
                    MenuDialogItem(
                        id = UiThemeMode.Dark.name,
                        title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_MODE_DARK],
                        iconName = Remix.Weather.moon_fill
                    ),
                )
            ) { _, mode ->
                runCatching { UiThemeMode.valueOf(mode) }.getOrNull()?.let(vm::setThemeMode)
            }
            MenuDialog(state = themeModeDialogState)

            val props = LocalShortXProps.current
            val pickFileLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { fileUri ->
                if (fileUri != null) {
                    vm.restoreFrom(fileUri, props.firstOrNull())
                }
            }

            val deleteDialogState = rememberConfirmDialogState()
            val resetDataMessageHint = LocalI18N.current[
                I18N_R.UI_DELETE_DIALOG_CONFIRM,
                mapOf("wht" to LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESET_DIALOG_HINT])
            ]
            ConfirmDialog(
                title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESET_TITLE],
                state = deleteDialogState,
                data = "",
                messageHint = { resetDataMessageHint },
                onConfirm = {
                    vm.resetData()
                }
            )

            val changeLogState = rememberShortXBottomSheetState()
            ChangeLogBottomSheet(state = changeLogState)

            val allPreferences = mutableListOf<Preference>().apply {
                addAll(
                    generalSettings(
                        state = state,
                        mainState = subscribeState,
                        vm = vm,
                        goToPkgSetList = goToPkgSetList,
                        goToGV = goToGV,
                        goToSystemEnv = goToSystemEnv,
                        goToFunctionList = goToFunctionList,
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
                        goToShellCommandHelp = goToShellCommandHelp,
                        manageShortcutApps = manageShortcutApps,
                        goToWebdavProfile = goToWebdavProfile
                    )
                )
                addAll(
                    miscSettings(
                        state = state,
                        mainState = subscribeState,
                        vm = vm,
                        goToFeatToggle = goToFeatToggle,
                        showThemeModeSelector = {
                            themeModeDialogState.show(
                                currentSelection = listOf(state.themeMode.name)
                            )
                        }
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
                        showChangeLog = { changeLogState.show() },
                    )
                )
            }

            val categoryTitles = remember(allPreferences) {
                extractSettingsCategoryTitles(allPreferences).toSet()
            }
            val searchableCount = remember(allPreferences) {
                allPreferences.count { !it.isDecor && it !is Preference.Category }
            }
            val searchBarState = rememberSearchBarState(itemCount = searchableCount)
            var isSearchToolsVisible by remember { mutableStateOf(false) }
            val isSearching = searchBarState.keyword.isNotBlank()
            val visiblePreferences = remember(
                allPreferences,
                searchBarState.keyword,
                state.collapsedCategories,
            ) {
                filterSettingsPreferences(
                    preferences = allPreferences,
                    keyword = searchBarState.keyword,
                    collapsedCategories = state.collapsedCategories,
                )
            }
            val hasVisibleEntries = remember(visiblePreferences) {
                visiblePreferences.any { !it.isDecor && it !is Preference.Category }
            }

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

                                LinkText(text = LocalI18N.current[I18N_R.UI_USER_GUIDE]) {
                                    guideSelectorDialogState.show()
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (isSearchToolsVisible) {
                                    searchBarState.inputKeyword("")
                                }
                                isSearchToolsVisible = !isSearchToolsVisible
                            }) {
                                RemixIcon(
                                    remixName = if (isSearchToolsVisible) {
                                        Remix.System.close_line
                                    } else {
                                        Remix.System.search_2_line
                                    }
                                )
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
                            title = LocalI18N.current[I18N_R.UI_SETTINGS_FATAL_ERROR_TITLE],
                            warnings = listOf(
                                LocalI18N.current[I18N_R.UI_SETTINGS_FATAL_ERROR_MESSAGE, mapOf("dir" to state.logDir)]
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
                        modifier = Modifier.padding(horizontal = 16.dp),
                        isChecked = state.ruleFeatureEnabled,
                        text = LocalI18N.current[I18N_R.UI_SETTINGS_ENABLE_RULE_FEATURE],
                        tip = LocalI18N.current[I18N_R.UI_SETTINGS_ENABLE_RULE_FEATURE_DESCRIPTION]
                    ) {
                        vm.toggleRuleFeatureEnable(it)
                    }

                    val i18N = LocalI18N.current
                    val backupErrorTitle = i18N[I18N_R.UI_SETTINGS_BACKUP_ERROR_TITLE]
                    val backupErrorAction = i18N[I18N_R.UI_SETTINGS_BACKUP_ERROR_ACTION]
                    LaunchedEffect(vm) {
                        vm.backupPerformed.collectLatest {
                            when (it) {
                                is BackupResult.Success -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N[I18N_R.UI_SETTINGS_TOAST_BACKUP_COMPLETE, mapOf("path" to it.path)]
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
                                            i18N[I18N_R.UI_SETTINGS_TOAST_RESTORE_COMPLETE]
                                        )
                                    }
                                }

                                is RestoreResult.Failed -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N[I18N_R.UI_SETTINGS_TOAST_BACKUP_ERROR, mapOf("err" to it.error)],
                                        )
                                    }
                                }
                            }
                        }
                    }
                    val exportLogErrorTitle =
                        i18N[I18N_R.UI_SETTINGS_EXPORT_LOG_ERROR_TITLE]
                    val exportLogErrorAction =
                        i18N[I18N_R.UI_SETTINGS_EXPORT_LOG_ERROR_ACTION]

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

                    LaunchedEffect(vm) {
                        vm.workdayRefreshPerformed.collectLatest {
                            when (it) {
                                is WorkdayRefreshResult.Success -> {
                                    scope.launch {
                                        val message = if (it.missingYears.isEmpty()) {
                                            i18N[
                                                I18N_R.UI_SETTINGS_DATA_WORKDAY_UPDATED,
                                                mapOf(
                                                    "years" to it.refreshedYears.sorted()
                                                        .joinToString()
                                                )
                                            ]
                                        } else {
                                            i18N[
                                                I18N_R.UI_SETTINGS_DATA_WORKDAY_UPDATED_MISSING,
                                                mapOf(
                                                    "years" to it.refreshedYears.sorted()
                                                        .joinToString(),
                                                    "missing" to it.missingYears.sorted()
                                                        .joinToString()
                                                )
                                            ]
                                        }
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }

                                is WorkdayRefreshResult.Failed -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            i18N[
                                                I18N_R.UI_SETTINGS_DATA_WORKDAY_REFRESH_FAILED,
                                                mapOf("err" to it.error)
                                            ]
                                        )
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = isSearchToolsVisible) {
                        Column {
                            SearchBar(
                                searchBarState = searchBarState,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                autoFocus = true,
                            )
                            SmallSpacer()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ShortXTextButton(onClick = { vm.expandAllSettingsCategories() }) {
                                    Text(text = i18N[I18N_R.UI_COMMON_EXPAND_ALL])
                                }
                                ShortXTextButton(onClick = {
                                    vm.collapseAllSettingsCategories(
                                        categoryTitles
                                    )
                                }) {
                                    Text(text = i18N[I18N_R.UI_COMMON_COLLAPSE_ALL])
                                }
                            }
                        }
                    }

                    if (hasVisibleEntries) {
                        PreferenceUi(
                            preferences = visiblePreferences,
                            collapsedCategories = if (isSearching) emptySet() else state.collapsedCategories,
                            onToggleCategory = { vm.toggleSettingsCategoryCollapsed(it) }
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            text = i18N[I18N_R.UI_SETTINGS_SEARCH_EMPTY],
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
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
        text = "Proto version: $shortXProtoVersion bpok edition",
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
            ShortXTextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                liquidGlassContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    SubscribeActivity.start(context)
                }) {
                LottieLoadingView(
                    file = "47603-twinkle-crown.json",
                    modifier = Modifier.size(30.dp),
                )
                SmallSpacer()
                Text(text = LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED], fontSize = 12.5.sp)
            }
        }
    }
}

private fun extractSettingsCategoryTitles(preferences: List<Preference>): List<String> {
    return preferences.mapNotNull {
        (it as? Preference.Category)?.title
    }
}

private fun filterSettingsPreferences(
    preferences: List<Preference>,
    keyword: String,
    collapsedCategories: Set<String>,
): List<Preference> {
    val groups = mutableListOf<MutableList<Preference>>()
    var currentGroup = mutableListOf<Preference>()

    preferences.forEach { preference ->
        if (preference is Preference.Category && currentGroup.isNotEmpty()) {
            groups.add(currentGroup)
            currentGroup = mutableListOf()
        }
        currentGroup.add(preference)
    }
    if (currentGroup.isNotEmpty()) {
        groups.add(currentGroup)
    }

    if (keyword.isBlank()) {
        return groups.flatMap { group ->
            val category = group.firstOrNull() as? Preference.Category ?: return@flatMap group
            if (collapsedCategories.contains(category.title)) {
                listOf(category)
            } else {
                group
            }
        }
    }

    val normalizedKeyword = keyword.trim().lowercase()
    return groups.flatMap { group ->
        val category = group.firstOrNull() as? Preference.Category ?: return@flatMap group
        val matched = group.drop(1).filter { pref ->
            !pref.isDecor && buildString {
                append(pref.title)
                pref.summary?.let {
                    append('\n')
                    append(it)
                }
            }.lowercase().contains(normalizedKeyword)
        }
        if (matched.isEmpty()) emptyList() else listOf(category) + matched
    }
}

@Composable
private fun generalSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    goToPkgSetList: () -> Unit,
    goToGV: () -> Unit,
    goToSystemEnv: () -> Unit,
    goToFunctionList: () -> Unit,
    goToDanmuUISettings: () -> Unit,
    goToStatusBarClipUISettings: () -> Unit,
): List<Preference> {
    val context = LocalContext.current

    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOfNotNull(
        Preference.Category(LocalI18N.current[I18N_R.UI_SETTINGS_GENERAL]),

        Preference.TextPreference(
            icon = Remix.System.apps_fill,
            title = LocalI18N.current[I18N_R.UI_PKG_SET_TITLE],
            summary = LocalI18N.current[I18N_R.UI_PKG_SET_DESC],
            onClick = {
                goToPkgSetList()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Document.file_list_fill,
            title = LocalI18N.current[I18N_R.UI_GLOBAL_VAR_TITLE],
            summary = LocalI18N.current[I18N_R.UI_GLOBAL_VAR_DESC],
            onClick = {
                goToGV()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Device.cpu_fill,
            title = LocalI18N.current[I18N_R.UI_SYSTEM_ENV],
            summary = LocalI18N.current[I18N_R.UI_SYSTEM_ENV],
            onClick = {
                goToSystemEnv()
            }
        ),

        Preference.TextPreference(
            icon = Remix.System.function_fill,
            title = LocalI18N.current[I18N_R.UI_ACTION_FUNCTIONS],
            summary = LocalI18N.current[I18N_R.UI_FUNCTION_DESC],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToFunctionList() },
                    { subscribeDialogState.show() }
                )
            }
        ),

        Preference.TextPreference(
            icon = Remix.Development.code_fill,
            title = LocalI18N.current[I18N_R.UI_CODE_LIBRARY_TITLE],
            summary = LocalI18N.current[I18N_R.UI_CODE_LIBRARY_SUMMARY],
            onClick = {
                CodeLibraryListActivity.start(context)
            }
        ),

        Preference.TextPreference(
            icon = Remix.Communication.message_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_DANMU],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_DANMU_SUMMARY],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToDanmuUISettings() },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.TextPreference(
            icon = Remix.Communication.message_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_STATUSBAR_CHIP],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_STATUSBAR_CHIP_SUMMARY],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToStatusBarClipUISettings() },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.TextPreference(
            icon = Remix.Editor.sketching,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_KEY_AND_GESTURE],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_KEY_AND_GESTURE_SUMMARY],
            onClick = {
                KeyAndGestureSettingsActivity.start(context)
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.System.forbid_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_FORCE_DISABLE_BY_KEY_PRESS_TITLE],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_FORCE_DISABLE_BY_KEY_PRESS_DESCRIPTION],
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
    showThemeModeSelector: () -> Unit,
): List<Preference> {
    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOf(
        Preference.Category(LocalI18N.current[I18N_R.UI_ACTION_MISC]),
        Preference.TextPreference(
            icon = Remix.Design.magic_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_FEAT_TOGGLE],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_FEAT_TOGGLE_SUMMARY],
            onClick = {
                goToFeatToggle()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Design.contrast_2_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_MODE],
            summary = LocalI18N.current[themeModeLabelKey(state.themeMode)],
            onClick = showThemeModeSelector
        ),
        Preference.SwitchPreference(
            icon = Remix.Design.palette_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_DYNAMIC_COLOR],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_THEME_DYNAMIC_COLOR_SUMMARY],
            value = state.dynamicColor,
            onCheckChanged = {
                vm.setDynamicColor(!state.dynamicColor)
            }
        ),
        Preference.SwitchPreference(
            icon = Remix.Design.pantone_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_COLORFUL_CARD],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_COLORFUL_CARD_SUMMARY],
            value = LocalUiSettings.current.uiColorfulCard,
            badge = badge,
            onCheckChanged = {
                if (mainState.isSubscribed) {
                    vm.setColorfulCard(it)
                } else {
                    subscribeDialogState.show()
                }
            }
        ),
        Preference.SwitchPreference(
            icon = Remix.Design.ink_bottle_fill,
            title = LocalI18N.current["Experimental UI"],
            summary = LocalI18N.current["Explore Experimental UI design"],
            value = LocalUiSettings.current.uiExperimental,
            onCheckChanged = {
                vm.setUiExperimental(it)
            }
        ),
    )
}

private fun themeModeLabelKey(mode: UiThemeMode): String {
    return when (mode) {
        UiThemeMode.System -> I18N_R.UI_SETTINGS_THEME_MODE_SYSTEM
        UiThemeMode.Light -> I18N_R.UI_SETTINGS_THEME_MODE_LIGHT
        UiThemeMode.Dark -> I18N_R.UI_SETTINGS_THEME_MODE_DARK
    }
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
    } else LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    return listOfNotNull(
        Preference.Category(LocalI18N.current[I18N_R.UI_SETTINGS_RECORD]),

        Preference.TextPreference(
            icon = Remix.System.history_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_EVA_RECORD],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_EVA_RECORD_SUMMARY],
            onClick = {
                goToEvaRecord()
            }
        ),

        Preference.TextPreference(
            icon = Remix.Media.mic_2_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_AUDIO_RECORD],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_AUDIO_RECORD_SUMMARY],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { goToAudioRecord() },
                    { subscribeDialogState.show() })
            }
        ),
        Preference.TextPreference(
            icon = Remix.Editor.sketching,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_GESTURE_RECORD],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_GESTURE_RECORD_SUMMARY],
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
        title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_BACKUP_TITLE],
        message = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_BACKUP_INPUT_FILE_NAME],
        showSymbolButton = false,
        onSelected = {
            vm.backup(it)
        })
    TextInputDialog(state = backUpFileNameDialogState)

    val badge = if (mainState.isSubscribed) {
        null
    } else LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val i18N = LocalI18N.current
    val workdaySummary = buildString {
        if (state.isWorkdayRefreshing) {
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_REFRESHING])
        } else if (state.workdayRefreshStatus == "failed" && state.workdayRefreshError.isNotBlank()) {
            append(
                i18N[
                    I18N_R.UI_SETTINGS_DATA_WORKDAY_REFRESH_FAILED,
                    mapOf("err" to state.workdayRefreshError)
                ]
            )
        } else if (state.workdayRefreshStatus == "success") {
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_SUMMARY_UPDATED])
        } else {
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_SUMMARY])
        }
        if (state.workdayRefreshYears.isNotEmpty()) {
            append('\n')
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_CACHED_YEARS])
            append(' ')
            append(state.workdayRefreshYears.sorted().joinToString())
        }
        state.workdayRefreshAt.ifBlank { null }?.let {
            append('\n')
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_LAST_SUCCESS])
            append(' ')
            append(formatWorkdayRefreshTime(it))
        }
        state.workdayRefreshSource.ifBlank { null }?.let {
            append('\n')
            append(i18N[I18N_R.UI_SETTINGS_DATA_WORKDAY_SOURCE])
            append(' ')
            append(it)
        }
    }

    return listOf(
        Preference.Category(LocalI18N.current[I18N_R.UI_SETTINGS_DATA]),
        Preference.TextPreference(
            icon = Remix.System.refresh_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_WORKDAY_TITLE],
            summary = workdaySummary,
            hasLongSummary = true,
            onClick = {
                vm.refreshWorkdayCalendar()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Device.save_3_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_BACKUP_TITLE],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_BACKUP_SUMMARY],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { backUpFileNameDialogState.show(autoGenBackupFileName()) },
                    { subscribeDialogState.show() })
            }
        ),

        Preference.ExpandablePreference(
            icon = Remix.System.download_2_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESTORE_TITLE],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESTORE_SUMMARY],
            badge = badge,
            onClick = {
                mainState.isSubscribed.select(
                    { pickRestoreFile() },
                    { subscribeDialogState.show() })
            },
            preferences = listOf(
                Preference.TextPreference(
                    icon = Remix.System.history_fill,
                    title = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESET_TITLE],
                    summary = LocalI18N.current[I18N_R.UI_SETTINGS_DATA_RESET_SUMMARY],
                    onClick = {
                        resetData()
                    }
                )
            ),
        ),
    )
}

private fun formatWorkdayRefreshTime(value: String): String {
    return value.replace('T', ' ').substringBefore('.').removeSuffix("Z")
}


@Composable
private fun shareSettings(
    state: SettingsState,
    vm: SettingsViewModel
): List<Preference> {
    val authorNameDialogState = rememberTextInputState(
        title = LocalI18N.current[I18N_R.UI_SETTINGS_SHARE_AUTHOR_INFO],
        message = null,
        onSelected = {
            vm.setShareAuthor(Author.newBuilder().setName(it).build())
        }
    )
    TextInputDialog(state = authorNameDialogState)

    return mutableListOf(
        Preference.Category(LocalI18N.current[I18N_R.UI_SETTINGS_SHARE]),

        Preference.TextPreference(
            icon = Remix.System.share_box_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_SHARE_AUTHOR_INFO],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_SHARE_AUTHOR_INFO_DESCRIPTION],
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
    val scope = rememberCoroutineScope()
    fun parseClipboardSeedArgs(value: String): Pair<Int, Int>? {
        val parts = value.split(',').map { it.trim() }
        if (parts.size != 2) return null
        val totalCount = parts[0].toIntOrNull() ?: return null
        val duplicatePercent = parts[1].toIntOrNull() ?: return null
        if (totalCount <= 0 || duplicatePercent !in 0..100) return null
        return totalCount to duplicatePercent
    }

    val clipboardSeedDialogState = rememberTextInputState(
        title = "Insert Clipboard",
        message = "Format: totalCount,duplicatePercent\nExample: 100,30",
        showSymbolButton = false,
        inputValidator = {
            if (parseClipboardSeedArgs(it) != null) {
                InputValidateResult.OK
            } else {
                InputValidateResult.Error("Use format: count,duplicatePercent and duplicatePercent must be 0-100")
            }
        },
        onSelected = {
            parseClipboardSeedArgs(it)?.let { (totalCount, duplicatePercent) ->
                scope.launch(Dispatchers.IO) {
                    TestData.insertClipboardTestData(totalCount, duplicatePercent)
                }
            }
        }
    )
    TextInputDialog(state = clipboardSeedDialogState)

    val permissionRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
    }
    val props = LocalShortXProps.current
    return mutableListOf(
        Preference.Category(LocalI18N.current[I18N_R.UI_SETTINGS_DEV]),

        Preference.SwitchPreference(
            icon = Remix.Document.article_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_ENABLE_DEBUG_LOG],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_ENABLE_DEBUG_LOG_DESCRIPTION, mapOf("dir" to state.logDir)],
            value = state.isDebugLogEnabled,
            onCheckChanged = { enable ->
                vm.setLogDebugEnable(enable)
            }),

        Preference.TextPreference(
            icon = Remix.Device.save_3_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_DEBUG_LOG_EXPORT],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_DEBUG_LOG_EXPORT_DESCRIPTION, mapOf("dir" to Environment.DIRECTORY_DOWNLOADS)],
            onClick = {
                vm.exportLogs()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Device.keyboard_box_fill,
            title = LocalI18N.current[I18N_R.UI_SETTINGS_KEY_EVENT_PROMPT],
            summary = LocalI18N.current[I18N_R.UI_SETTINGS_KEY_EVENT_PROMPT_DESCRIPTION],
            value = state.isKeyEventPromptEnabled,
            onCheckChanged = { enable ->
                vm.toggleKeyEventPromptEnabled()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Development.cursor_fill,
            title = LocalI18N.current[I18N_R.UI_COMMON_SHOW_POINTER_LOCATION],
            summary = LocalI18N.current[I18N_R.UI_COMMON_SHOW_POINTER_LOCATION_DESCRIPTION],
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
                                title = LocalI18N.current[I18N_R.UI_SETTINGS_HTTP_CHUCKER],
                                summary = LocalI18N.current[I18N_R.UI_SETTINGS_HTTP_CHUCKER_DESCRIPTION],
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
                                title = "Insert Clipboard",
                                summary = "Insert clipboard test data by count and duplicate ratio",
                                onClick = {
                                    clipboardSeedDialogState.show("100,30")
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
    } else i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]
    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)
    return mutableListOf(
        Preference.Category(i18N[I18N_R.UI_SETTINGS_SYSTEM]),
        Preference.SwitchPreference(
            icon = Remix.Media.equalizer_fill,
            title = i18N[I18N_R.UI_SETTINGS_SB_TILE_OPT],
            summary = i18N[I18N_R.UI_SETTINGS_SB_TILE_OPT_SUMMARY],
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
        ),
    )
}

@Composable
private fun engineSettings(
    state: SettingsState,
    mainState: LVLStateHolder.State,
    vm: SettingsViewModel,
    goToShellCommandHelp: () -> Unit,
    manageShortcutApps: () -> Unit,
    goToWebdavProfile: () -> Unit,
): List<Preference> {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val badge = if (mainState.isSubscribed) {
        null
    } else i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val shellConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_SHELL_CONFIRM_TITLE],
        messageHint = {
            i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_SHELL_CONFIRM_MESSAGE]
        },
        state = shellConfirmDialogState,
        data = Unit,
        onConfirm = {
            vm.setShellEngineEnable(true)
        }
    )

    val mvelConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_MVEL_CONFIRM_TITLE],
        messageHint = {
            i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_MVEL_CONFIRM_MESSAGE]
        },
        state = mvelConfirmDialogState,
        data = Unit,
        onConfirm = {
            vm.setMVELEngineEnable(true)
        }
    )

    val smsConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N[I18N_R.UI_ENGINE_SEND_SMS],
        messageHint = {
            i18N[I18N_R.UI_ENGINE_SEND_SMS_ENABLE_MESSAGE]
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
        rememberTextInputState(title = i18N[I18N_R.UI_SETTINGS_CUSTOM_SU_PATH], message = null) {
            vm.setCustomSUPath(it)
        }
    TextInputDialog(customSUDialogState)

    val ocrEngineMenuState = rememberMenuDialogState<String>(
        title = i18N["OCR Engine"],
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
        Preference.Category(i18N[I18N_R.UI_SETTINGS_ENGINE]),

        Preference.SwitchPreference(
            icon = Remix.Editor.hashtag,
            title = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_SHELL],
            summary = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_SHELL_SUMMARY],
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
                title = i18N[I18N_R.UI_SETTINGS_INJECTED_SHELL],
                summary = i18N[I18N_R.UI_SETTINGS_INJECTED_SHELL_DESCRIPTION],
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
                title = i18N[I18N_R.UI_SETTINGS_CUSTOM_SU_PATH],
                summary = i18N[I18N_R.UI_SETTINGS_CUSTOM_SU_PATH_DESCRIPTION, mapOf("path" to state.customSUPath.orEmpty())],
                hasLongSummary = false,
                badge = badge,
                onClick = {
                    customSUDialogState.show(state.customSUPath)
                }
            ),
            null
        ),
        Preference.TextPreference(
            icon = Remix.Development.command_line,
            title = i18N[I18N_R.UI_SETTINGS_SHELL_COMMANDS],
            summary = i18N[I18N_R.UI_SETTINGS_SHELL_COMMANDS_DESCRIPTION],
            hasLongSummary = false,
            onClick = goToShellCommandHelp,
        ),

        Preference.SwitchPreference(
            icon = Remix.Development.code_fill,
            title = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_MVEL],
            summary = i18N[I18N_R.UI_SETTINGS_RULE_ENGINE_MVEL_SUMMARY],
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
            title = i18N[I18N_R.UI_ENGINE_SEND_SMS],
            summary = i18N[I18N_R.UI_ENGINE_SEND_SMS_DESCRIPTION],
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
            title = i18N["OCR Engine"],
            summary = i18N[state.ocrEngine.labelKey()],
            hasLongSummary = false,
            onClick = {
                ocrEngineMenuState.show()
            }
        ),

        Preference.SwitchPreference(
            icon = Remix.Communication.chat_smile_2_line,
            title = i18N[I18N_R.UI_SETTINGS_RULE_POST_N_APP],
            summary = i18N[I18N_R.UI_SETTINGS_RULE_POST_N_APP_SUMMARY],
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
                title = i18N[I18N_R.UI_SHORTX_STUB_APK],
                summary = i18N[I18N_R.UI_SHORTX_STUB_APK_DESCRIPTION] + " DEBUG VISIBLE",
                hasLongSummary = false,
                onClick = manageShortcutApps
            ),
            null
        ),

        Preference.TextPreference(
            icon = Remix.Document.file_cloud_fill,
            title = i18N[I18N_R.UI_SETTINGS_WEBDAV_PROFILE],
            summary = i18N[I18N_R.UI_SETTINGS_WEBDAV_PROFILE_DESCRIPTION],
            hasLongSummary = false,
            onClick = goToWebdavProfile
        ),

        Preference.TextPreference(
            icon = Remix.Edit.extend,
            title = i18N[I18N_R.UI_SHORTX_EXT],
            summary = if (state.extApp == null) i18N[I18N_R.UI_SHORTX_EXT_DESCRIPTION] else i18N[I18N_R.UI_SHORTX_EXT_APP_INFO],
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
                        val chooser = Intent.createChooser(intent, i18N[I18N_R.UI_SHORTX_EXT])
                        try {
                            context.startActivity(chooser)
                        } catch (e: ActivityNotFoundException) {
                            val intentGoogle = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                            val chooserGoogle =
                                Intent.createChooser(intentGoogle, i18N[I18N_R.UI_SHORTX_EXT])
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
    showChangeLog: () -> Unit,
): List<Preference> {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val qqGroups = listOf(BuildProp.QQ_GROUP_2, BuildProp.QQ_GROUP_1)
    return listOf(
        Preference.Category(i18N[I18N_R.UI_SETTINGS_ABOUT]),

        Preference.ExpandablePreference(
            icon = Remix.System.information_fill,
            title = i18N[I18N_R.UI_SETTINGS_APP_INFO],
            summary = "${BuildPropInternal.APP_VERSION_NAME}\n${BuildPropInternal.APP_VERSION_CODE}",
            preferences = listOf(
                Preference.TextPreference(
                    icon = Remix.Device.instance_fill,
                    title = i18N[I18N_R.UI_SETTINGS_CORE_INFO],
                    summary = if (state.isActivated) {
                        "${state.coreVersion.name}\n${state.coreVersion.code}"
                    } else {
                        i18N[I18N_R.UI_SETTINGS_CORE_INFO_NA]
                    },
                    onClick = {
                    }
                )
            ),
        ),

        Preference.TextPreference(
            icon = Remix.Logos.telegram_fill,
            title = i18N[I18N_R.UI_SETTINGS_TG],
            summary = i18N[I18N_R.UI_SETTINGS_TG_SUMMARY],
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
                    i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                    Toast.LENGTH_LONG
                ).show()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Document.article_fill,
            title = i18N[I18N_R.UI_CHANGELOG_TITLE],
            summary = i18N[I18N_R.UI_CHANGELOG_SETTINGS_SUMMARY],
            onClick = showChangeLog,
        ),
        Preference.TextPreference(
            icon = Remix.Logos.open_source_fill,
            title = i18N[I18N_R.UI_SETTINGS_OPEN_SOURCE_LICENSE],
            onClick = {
                viewOpenSourceLicense()
            }
        ),
        Preference.TextPreference(
            icon = Remix.Document.sticky_note_2_fill,
            title = i18N[I18N_R.UI_SETTINGS_PRIVACY_STATEMENT],
            onClick = {
                viewPrivacyStatement()
            }
        )
    )
}
