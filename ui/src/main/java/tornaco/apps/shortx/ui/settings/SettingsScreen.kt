package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIconSelector
import tornaco.apps.shortx.ui.base.MDIconSelectorState
import tornaco.apps.shortx.ui.base.MDIcons.filled
import tornaco.apps.shortx.ui.base.rememberMDIconSelectorState
import tornaco.apps.shortx.ui.overlay.BUTTON_COUNT
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
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
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.common.settings"]) },
                        navigationIcon = {
                            IconButton(onClick = {
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

                    val mdIconSelectorStates = mutableListOf<MDIconSelectorState>().apply {
                        repeat(BUTTON_COUNT) { index ->
                            val iconSelectorState = rememberMDIconSelectorState { name ->
                                vm.setIconForButtonIndex(index, name)
                            }
                            MDIconSelector(state = iconSelectorState)
                            add(iconSelectorState)
                        }
                    }

                    PreferenceUi(mutableListOf<Preference>().apply {
                        repeat(BUTTON_COUNT) { index ->
                            add(
                                Preference.TextPreference(icon = filled()[state.buttonIcons[index]],
                                    title = "Button Icon $index",
                                    summary = "Select button icon for float widget $index",
                                    onClick = {
                                        mdIconSelectorStates[index].show()
                                    }),
                            )
                        }
                        add(
                            Preference.SwitchPreference(icon = Icons.Filled.Settings,
                                title = "Enable DOC",
                                summary = "让技术向善？恐怕只能天方夜谭了？\n" + "有人说：科技就是一块板砖，哪里需要哪里搬。",
                                value = true,
                                onCheckChanged = {
                                    Logger("").d("onCheckChanged-- $it")
                                })
                        )
                    })
                }
            }
        }
    }
}