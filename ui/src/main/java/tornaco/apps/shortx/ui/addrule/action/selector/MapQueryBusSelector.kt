@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class MapQueryBusDialogState(
    val onSelected: (Action.MapQueryBus) -> Unit
) : CommonDialogState() {
    var city by mutableStateOf("")
    var bus by mutableStateOf("")
    var mapApp by mutableStateOf(MapApp.Gaode)

    fun show(currentSettings: Action.MapQueryBus? = null) {
        this.city = currentSettings?.city ?: ""
        this.bus = currentSettings?.bus ?: ""
        this.mapApp = currentSettings?.mapApp ?: MapApp.Gaode
        show()
    }
}

@Composable
fun rememberMapQueryBusDialogState(
    onSelected: (Action.MapQueryBus) -> Unit
): MapQueryBusDialogState {
    return remember {
        MapQueryBusDialogState(onSelected)
    }
}

@Composable
fun MapQueryBusDialog(state: MapQueryBusDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            MapQueryBusSelector(state)
        }
    }
}

@Composable
private fun MapQueryBusSelector(dialogState: MapQueryBusDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(
                modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(i18N[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                with(dialogState) {
                                    if (city.isNotBlank() && bus.isNotBlank()) {
                                        dialogState.onSelected(
                                            Action.MapQueryBus(
                                                bus = bus,
                                                city = city,
                                                mapApp = mapApp
                                            )
                                        )
                                        dialogState.dismiss()
                                    }
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
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
                    FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_MAP])
                    SmallSpacer()
                    ListItem(title = when (dialogState.mapApp) {
                        MapApp.Gaode -> {
                            i18N[I18N_R.UI_ACTION_MAP_GAODE]
                        }

                        MapApp.Baidu -> {
                            i18N[I18N_R.UI_ACTION_MAP_BAIDU]
                        }

                        else -> {
                            "N/A"
                        }
                    }, onClick = {
                        dialogState.mapApp = if (dialogState.mapApp == MapApp.Baidu) {
                            MapApp.Gaode
                        } else {
                            MapApp.Baidu
                        }
                    })
                    StandardSpacer()
                    StandardSpacer()

                    FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY_CITY] + " & " + LocalI18N.current[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY_BUS])
                    SmallSpacer()
                    TipRow(text = LocalI18N.current[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY_CITY_PLACEHOLDER])
                    SmallSpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = dialogState.city,
                        label = {
                            Text(text = i18N[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY_CITY])
                        },
                        onValueChange = {
                            dialogState.city = it
                        })
                    MediumSpacer()

                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = dialogState.bus,
                        label = {
                            Text(text = i18N[I18N_R.UI_ACTION_MAP_NAV_BUS_QUERY_BUS])
                        },
                        onValueChange = {
                            dialogState.bus = it
                        })
                    StandardSpacer()
                    StandardSpacer()
                }
            }
        }
    }
}