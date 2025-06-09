package tornaco.apps.shortx.ui.addrule.fact.selector


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.settings.DynamicShortcutSettingsActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme


data class OnStartDynamicShortcutData(
    val appList: List<App>,
    val pkgSetList: List<PkgSet>,
    val shortcut: DynamicShortcut
)

class OnStartDynamicShortcutState(
    val title: String,
    val message: String?,
    val onSelected: (OnStartDynamicShortcutData) -> Unit
) : CommonDialogState() {
    private val defaultData
        get() = OnStartDynamicShortcutData(
            emptyList(),
            emptyList(),
            DynamicShortcut.ShortX_DynamicShortcut1
        )

    var currentValue by mutableStateOf(defaultData)

    fun show(initialValue: OnStartDynamicShortcutData? = null) {
        this.currentValue = initialValue ?: defaultData
        show()
    }

    fun selectApps(list: List<App>) {
        currentValue = currentValue.copy(appList = list)
    }

    fun selectPkgSets(list: List<PkgSet>) {
        currentValue = currentValue.copy(pkgSetList = list)
    }

    fun setShortcut(shortcut: DynamicShortcut) {
        currentValue = currentValue.copy(shortcut = shortcut)
    }
}

@Composable
fun rememberOnStartDynamicShortcutState(
    title: String,
    message: String?,
    onSelected: (OnStartDynamicShortcutData) -> Unit
): OnStartDynamicShortcutState {
    return remember {
        OnStartDynamicShortcutState(
            title,
            message,
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnStartDynamicShortcutDialog(state: OnStartDynamicShortcutState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = state.title) },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            state.onSelected(state.currentValue)
                                            state.dismiss()
                                        }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            state.message?.let {
                                DialogMessage(
                                    text = it,
                                )
                                StandardSpacer()
                            }

                            val appPkgSelectorState = rememberAppPkgSelectorState(
                                onAppListSelected = {
                                    state.selectApps(it)
                                },
                                onPkgSetSelected = {
                                    state.selectPkgSets(it)
                                }
                            )
                            AppPkgSelectorDialog(state = appPkgSelectorState)

                            val i18N = LocalI18N.current

                            FieldTitle(text = "App")
                            ListItem(
                                titleLines = mutableListOf("")
                                    .labelLinesFromAppsAndPkgSets(
                                        state.currentValue.appList,
                                        state.currentValue.pkgSetList
                                    ),
                                icon = {
                                    RemixIcon(remixName = Remix.System.apps_2_fill)
                                },
                                onClick = {
                                    appPkgSelectorState.show(
                                        state.currentValue.appList,
                                        state.currentValue.pkgSetList
                                    )
                                }
                            )

                            StandardSpacer()

                            val dynamicShortcutSelectState = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartActivityForResult(),
                                onResult = {
                                    if (it.resultCode == Activity.RESULT_OK) {
                                        val num = it.data?.getIntExtra("shortcut", 0) ?: 0
                                        state.setShortcut(DynamicShortcut.forNumber(num))
                                    }
                                }
                            )
                            val context = LocalContext.current
                            FieldTitle(text = i18N["ui.settings.dynamic.shortcut"])
                            ListItem(
                                title = i18N[state.currentValue.shortcut.name],
                                icon = {
                                },
                                onClick = {
                                    dynamicShortcutSelectState.launch(
                                        DynamicShortcutSettingsActivity.intent(
                                            context,
                                            true
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
