package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(back: () -> Unit, goToPkgSetList: () -> Unit) {
    val vm = hiltViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.common.settings"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {},
                        scrollBehavior = scrollBehavior
                    )
                }) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {
                    PreferenceUi(mutableListOf<Preference>().apply {
                        addAll(generalSettings(state, vm, goToPkgSetList))
                        addAll(devSettings(state, vm, goToPkgSetList))
                        addAll(aboutSettings(state))
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
    goToPkgSetList: () -> Unit
) = listOf(
    Preference.Category(LocalI18N.current["ui.settings.general"]),

    Preference.TextPreference(icon = Icons.Outlined.Apps,
        title = LocalI18N.current["ui.pkg.set.title"],
        summary = LocalI18N.current["ui.pkg.set.desc"],
        onClick = {
            goToPkgSetList()
        }
    ),
)

@Composable
private fun devSettings(
    state: SettingsState,
    vm: SettingsViewModel,
    goToPkgSetList: () -> Unit
) = listOf(
    Preference.Category(LocalI18N.current["ui.settings.dev"]),

    Preference.SwitchPreference(icon = Icons.Outlined.DeveloperMode,
        title = LocalI18N.current["ui.settings.enable.debug.log"],
        summary = LocalI18N.current["ui.settings.enable.debug.log.description"],
        value = state.isDebugLogEnabled,
        onCheckChanged = { enable ->
            vm.setLogDebugEnable(enable)
        })
)

@Composable
private fun aboutSettings(state: SettingsState) = listOf(
    Preference.Category(LocalI18N.current["ui.settings.about"]),

    Preference.ExpandablePreference(
        icon = Icons.Outlined.Info,
        title = LocalI18N.current["ui.settings.app.info"],
        summary = "${BuildProp.APP_VERSION_NAME}\n${BuildProp.APP_VERSION_CODE}",
        preferences = listOf(
            Preference.TextPreference(icon = Icons.Outlined.Extension,
                title = LocalI18N.current["ui.settings.core.info"],
                summary = if (state.isActivated) {
                    "${state.coreVersion.name}\n${state.coreVersion.code}"
                } else {
                    LocalI18N.current["ui.settings.core.info.na"]
                },
                onClick = {})
        ),
    ),
)