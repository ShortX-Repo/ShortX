@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

private val logger = Logger("ExtActions")
const val KEY_ACTION = "selector.key.action"

enum class ActionCategory(val i18NKey: String, val icon: ImageVector) {
    Ui("UI", Icons.Outlined.Preview),
    Apps("ui.action.category.app", Icons.Outlined.Apps),
    Settings("ui.action.system.settings", Icons.Outlined.ToggleOn),
    Advanced("ui.action.advanced", Icons.Outlined.Extension)
}

@Composable
fun ActionSelector(
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    val navController: NavController = LocalNavHostController.current
                    MediumTopAppBar(
                        modifier = Modifier,
                        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = ColorDefaults.backgroundSurfaceColor()),
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                        },
                    )
                }) { paddingValues ->

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var selectedItem by remember { mutableStateOf(ActionCategory.Ui.ordinal) }
                    val items = ActionCategory.values().map { it.i18NKey }
                    val icons = ActionCategory.values().map { it.icon }

                    NavigationRail(
                        modifier = Modifier
                            .fillMaxHeight()
                            .shadow(4.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = { Icon(imageVector = icons[index]) },
                                label = { Text(LocalI18N.current[item]) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }

                    StandardSpacer()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val navController: NavController = LocalNavHostController.current
                        val addAction: (Action) -> Unit = {
                            navController.popBackWithResult(mapOf(KEY_ACTION to it))
                        }

                        // For Overlay button selector
                        val result =
                            navController.getStateFlow<ShowOverlayButtonSettings?>(
                                KEY_OVERLAY_BUTTON_SETTINGS,
                                null
                            )?.collectAsState()
                        LaunchedEffect(result) {
                            logger.d("KEY_OVERLAY_BUTTON_SETTINGS result: ${result?.value}")
                            result?.value?.let { buttonSettings ->
                                val action = Action.ShowOverlay(
                                    autoCollapse = buttonSettings.autoCollapse,
                                    settings = buttonSettings.items.map { it.toOverlayButtonSetting() }
                                )
                                addAction(action)
                            }
                        }

                        when (selectedItem) {
                            ActionCategory.Ui.ordinal -> {
                                UiActions(addAction = addAction)
                            }
                            ActionCategory.Apps.ordinal -> {
                                AppActions(addAction = addAction)
                            }
                            ActionCategory.Settings.ordinal -> {
                                SettingsActions(addAction = addAction)
                            }
                            ActionCategory.Advanced.ordinal -> {
                                ExtActions(addAction = addAction)
                            }
                            else -> {}
                        }
                    }
                }


            }
        }
    }
}