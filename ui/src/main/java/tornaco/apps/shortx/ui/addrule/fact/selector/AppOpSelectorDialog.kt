package tornaco.apps.shortx.ui.addrule.fact.selector


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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.compat.AppOpsManagerCompat
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.ops.opsLabelKey
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.opsIconName
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
import tornaco.apps.shortx.ui.theme.ShortXTheme


data class AppOpSelectorData(
    val appList: List<App>,
    val pkgSetList: List<PkgSet>,
    val opCodes: List<Int>
) {
    val isValid get() = (appList.isNotEmpty() || pkgSetList.isNotEmpty()) && opCodes.isNotEmpty()
}


class AppOpSelectorState(
    val title: String,
    val message: String?,
    val onSelected: (AppOpSelectorData) -> Unit
) : CommonDialogState() {
    private val logger = Logger("AppOpSelectorState")
    private val defaultData get() = AppOpSelectorData(emptyList(), emptyList(), emptyList())

    var currentValue by mutableStateOf(defaultData)

    var isValid by mutableStateOf(false)

    fun show(initialValue: AppOpSelectorData? = null) {
        logger.logOnDebug("initialValue: $initialValue")
        this.currentValue = initialValue ?: defaultData
        this.isValid = true

        this.isValid = currentValue.isValid
        show()
    }

    fun selectApps(list: List<App>) {
        currentValue = currentValue.copy(appList = list)
        this.isValid = currentValue.isValid
    }

    fun selectPkgSets(list: List<PkgSet>) {
        currentValue = currentValue.copy(pkgSetList = list)
        this.isValid = currentValue.isValid
    }

    fun addOp(code: Int) {
        currentValue = currentValue.copy(opCodes = currentValue.opCodes.toMutableList().apply {
            add(code)
        })
        this.isValid = currentValue.isValid
    }

    fun removeOp(code: Int) {
        currentValue = currentValue.copy(opCodes = currentValue.opCodes.toMutableList().apply {
            remove(code)
        })
        this.isValid = currentValue.isValid
    }
}

@Composable
fun rememberAppOpSelectorState(
    title: String,
    message: String?,
    onSelected: (AppOpSelectorData) -> Unit
): AppOpSelectorState {
    return remember {
        AppOpSelectorState(
            title,
            message,
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOpSelectorDialog(state: AppOpSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = state.title) },
                                actions = {
                                    IconButton(
                                        enabled = state.isValid,
                                        onClick = {
                                            if (state.isValid) {
                                                state.onSelected(state.currentValue)
                                                state.dismiss()
                                            }
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

                            FieldTitle(text = LocalI18N.current["ui.fact.op.start.app"])
                            ListItem(
                                titleLines = mutableListOf(i18N["ui.fact.op.start.app"])
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

                            FieldTitle(text = i18N["ui.fact.op.start.op"])
                            arrayOf(
                                AppOpsManagerCompat.OP_GPS,
                                AppOpsManagerCompat.OP_CAMERA,
                                AppOpsManagerCompat.OP_RECORD_AUDIO
                            ).forEach { code ->
                                ListItem(title = i18N[code.opsLabelKey()],
                                    icon = {
                                        RemixIcon(remixName = code.opsIconName())
                                    },
                                    checked = state.currentValue.opCodes.contains(code),
                                    onCheckedChange = {
                                        if (it) {
                                            state.addOp(code)
                                        } else {
                                            state.removeOp(code)
                                        }
                                    },
                                    onClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}
