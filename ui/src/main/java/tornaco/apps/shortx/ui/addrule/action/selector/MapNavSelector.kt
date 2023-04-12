@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class MapNavDialogState(
    val onSelected: (Action.MapNav) -> Unit
) {
    var isShowing by mutableStateOf(false)
    var currentSettings: Action.MapNav? = null

    fun show(currentSettings: Action.MapNav? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberMapNavDialogState(
    onSelected: (Action.MapNav) -> Unit
): MapNavDialogState {
    return remember {
        MapNavDialogState(onSelected)
    }
}

@Composable
fun MapNavDialog(state: MapNavDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            MapNavSelector(state)
        }
    }
}

@Composable
private fun MapNavSelector(dialogState: MapNavDialogState) {
    var latitude by remember {
        mutableStateOf("")
    }
    var longitude by remember {
        mutableStateOf("")
    }
    var poi by remember {
        mutableStateOf("")
    }
    var mapApp by remember {
        mutableStateOf(MapApp.Gaode)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(i18N["ui.action.map.nav"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (longitude.isNotBlank() && latitude.isNotBlank()) {
                                    dialogState.onSelected(
                                        Action.MapNav(
                                            latitude = latitude,
                                            longitude = longitude,
                                            poi = poi,
                                            mapApp = mapApp
                                        )
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }

                        }
                    )
                }) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    TipBody(i18N["ui.action.map.nav"])
                    FilledTonalButton(onClick = {
                        mapApp = if (mapApp == MapApp.Baidu) {
                            MapApp.Gaode
                        } else {
                            MapApp.Baidu
                        }
                    }) {
                        Text(
                            text = when (mapApp) {
                                MapApp.Gaode -> {
                                    i18N["ui.action.map.gaode"]
                                }
                                MapApp.Baidu -> {
                                    i18N["ui.action.map.baidu"]
                                }
                                else -> {
                                    "N/A"
                                }
                            }
                        )
                    }
                    StandardSpacer()
                    StandardSpacer()

                    TipBody(i18N["ui.action.map.nav.latitude"])
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        shape = TextFieldDefaults.outlinedShape,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent
                        ),
                        value = latitude,
                        onValueChange = {
                            latitude = it
                        })
                    StandardSpacer()
                    StandardSpacer()

                    TipBody(i18N["ui.action.map.nav.longitude"])
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        shape = TextFieldDefaults.outlinedShape,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent
                        ),
                        value = longitude,
                        onValueChange = {
                            longitude = it
                        })
                    StandardSpacer()
                    StandardSpacer()

                    AnimatedVisibility(visible = mapApp == MapApp.Gaode) {
                        Column {
                            TipBody(i18N["ui.action.map.poi"])
                            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                                shape = TextFieldDefaults.outlinedShape,
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent
                                ),
                                value = poi,
                                onValueChange = {
                                    poi = it
                                })
                            StandardSpacer()
                            StandardSpacer()
                        }
                    }
                }

            }
        }
    }

}