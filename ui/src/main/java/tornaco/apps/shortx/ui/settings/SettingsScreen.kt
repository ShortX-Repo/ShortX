package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SafetyDivider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.enro.core.NavigationKey
import dev.enro.core.close
import dev.enro.core.compose.navigationHandle
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Parcelize
object AppSettings : NavigationKey.SupportsPresent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val navigation = navigationHandle<AppSettings>()
    val vm = hiltViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {

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
                                navigation.close()
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

                    PreferenceUi(
                        listOf(
                            Preference.TextPreference(
                                icon = Icons.Filled.SafetyDivider,
                                title = "First Item",
                                summary = "Use item instead of items(1) if you have a single item as they are equivalent, but this would be more clear."
                            ),
                            Preference.SwitchPreference(
                                icon = Icons.Filled.SafetyDivider,
                                title = "Enable DOC",
                                summary = "让技术向善？恐怕只能天方夜谭了？\n" +
                                        "有人说：科技就是一块板砖，哪里需要哪里搬。",
                                value = true,
                                onCheckChanged = {
                                    Logger("").d("onCheckChanged-- $it")
                                }
                            ))
                    )
                }
            }
        }
    }
}