@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.NavType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils
import tornaco.apps.shortx.core.i18n.I18N_R

const val BAIDU_LOC_PICK_URL = "http://api.map.baidu.com/lbsapi/getpoint/index.html"
const val GAODE_LOC_PICK_URL = "https://lbs.amap.com/tools/picker"

class MapNavDialogState(
    val onSelected: (Action.MapNav) -> Unit
) : CommonDialogState() {
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var mapApp by mutableStateOf(MapApp.Gaode)
    var navType by mutableStateOf(NavType.NavType_Car)

    fun show(currentSettings: Action.MapNav? = null) {
        this.latitude = currentSettings?.latitude ?: ""
        this.longitude = currentSettings?.longitude ?: ""
        this.mapApp = currentSettings?.mapApp ?: MapApp.Gaode
        this.navType = currentSettings?.navType ?: NavType.NavType_Car
        show()
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
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18n = LocalI18N.current
            Scaffold(
                modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(i18n[I18N_R.UI_ACTION_MAP_NAV]) },
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
                                    if (longitude.isNotBlank() && latitude.isNotBlank()) {
                                        dialogState.onSelected(
                                            Action.MapNav(
                                                latitude = latitude,
                                                longitude = longitude,
                                                poi = "",
                                                mapApp = mapApp,
                                                navType = navType
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
                    FieldTitle(text = i18n[I18N_R.UI_ACTION_MAP])
                    SmallSpacer()
                    ListItem(title = when (dialogState.mapApp) {
                        MapApp.Gaode -> {
                            i18n[I18N_R.UI_ACTION_MAP_GAODE]
                        }

                        MapApp.Baidu -> {
                            i18n[I18N_R.UI_ACTION_MAP_BAIDU]
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
                    }, action1 = {
                        val context = LocalContext.current
                        ShortXFilledTonalButton(onClick = {
                            if (dialogState.mapApp == MapApp.Gaode) {
                                BrowserUtils.launch(context, GAODE_LOC_PICK_URL)
                            } else {
                                BrowserUtils.launch(context, BAIDU_LOC_PICK_URL)
                            }

                        }) {
                            Text(i18n[I18N_R.UI_ACTION_MAP_NAV_PICK_LOC])
                        }
                    })
                    StandardSpacer()
                    StandardSpacer()

                    FieldTitle(text = i18n[I18N_R.UI_ACTION_MAP_NAV_LATITUDE] + " & " + i18n[I18N_R.UI_ACTION_MAP_NAV_LONGITUDE])
                    SmallSpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = dialogState.latitude,
                        label = {
                            Text(text = i18n[I18N_R.UI_ACTION_MAP_NAV_LATITUDE])
                        },
                        onValueChange = {
                            dialogState.latitude = it
                        })
                    MediumSpacer()

                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = dialogState.longitude,
                        label = {
                            Text(text = i18n[I18N_R.UI_ACTION_MAP_NAV_LONGITUDE])
                        },
                        onValueChange = {
                            dialogState.longitude = it
                        })
                    StandardSpacer()
                    StandardSpacer()

                    FieldTitle(text = "Nav")
                    val navTypeDropState = rememberDropdownSelectorState()
                    Box {
                        ShortXFilledTonalButton(onClick = {
                            navTypeDropState.open()
                        }) {
                            Text(text = i18n[dialogState.navType.labelKey()])
                        }
                        DropdownSelector(
                            state = navTypeDropState,
                            items = NavType.values().toMutableList().apply {
                                remove(NavType.UNRECOGNIZED)
                            }.map {
                                DropdownItem(
                                    labelLines = listOf(i18n[it.labelKey()]),
                                    data = it,
                                )
                            }
                        ) {
                            dialogState.navType = it.data
                        }
                    }
                }
            }
        }
    }

}