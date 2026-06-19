@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class StartServiceSelectorActivity : BaseSelectorActivity<Action.StartService>() {
    @Composable
    override fun BaseSelectorActivity<Action.StartService>.SelectorContent(currentData: Action.StartService?) {
        StartServiceSelector(currentData)
    }
}

@Composable
private fun BaseSelectorActivity<Action.StartService>.StartServiceSelector(current: Action.StartService?) {
    val vm = hiltViewModel<StartServiceSelectorViewModel>()
    LaunchedEffect(vm) {
        if (current != null) {
            vm.initFrom(current)
        }
    }
    val vmState by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_ACTION_START_SERVICE]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                setResultAndFinish(
                                    Action.StartService(
                                        id = vmState.id,
                                        userId = vmState.userId,
                                        action = vmState.action,
                                        flags = 0,
                                        pkgName = vmState.pkgName,
                                        className = vmState.className,
                                        data = vmState.data,
                                        extras = vmState.extras,
                                        isFG = vmState.isFG
                                    )
                                )
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: StartServiceSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        val singleAppSelectorState = rememberSingleAppPkgSelectorState {
            vm.updatePkgName(it.pkg.pkgName)
            vm.updateUserId(it.pkg.userId)
        }
        SingleAppSelectorDialog(state = singleAppSelectorState)
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_COMMON_PKG_NAME] + " - " + LocalI18N.current[I18N_R.UI_COMMON_OPTIONAL]
        )
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.pkgName ?: "",
            trailingIcon = {
                IconButton(onClick = {
                    singleAppSelectorState.show()
                }) {
                    RemixIcon(remixName = Remix.System.search_2_line)
                }
            },
            onValueChange = {
                vm.updatePkgName(it)
            })
        StandardSpacer()

        val componentSelectorState = rememberAppComponentSelectorState(
            types = listOf(ComponentType.Service),
            maxItemCount = 1,
            onAppComponentSelected = { infoList ->
                if (infoList.isNotEmpty()) {
                    val comp = infoList[0]
                    vm.updatePkgName(comp.component.pkg.pkgName)
                    vm.updateUserId(comp.component.pkg.userId)
                    vm.updateClassName(comp.component.className)
                }
            }
        )
        AppComponentSelectorDialog(componentSelectorState)
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_COMMON_CLASS_NAME] + " - " + LocalI18N.current[I18N_R.UI_COMMON_OPTIONAL]
        )
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.className ?: "",
            trailingIcon = {
                AnimatedVisibility(visible = state.pkgName != null && state.app != null) {
                    IconButton(onClick = {
                        val currentPkgName = state.pkgName
                        currentPkgName?.let {
                            componentSelectorState.show(
                                AppPkg.newBuilder()
                                    .setPkgName(currentPkgName)
                                    .setUserId(state.userId)
                                    .build()
                            )
                        }
                    }) {
                        RemixIcon(remixName = Remix.System.search_2_line)
                    }
                }
            },
            onValueChange = {
                vm.updateClassName(it)
            })
        StandardSpacer()

        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_COMMON_USER_ID]
        )
        val userSelectorState = rememberUserSelectorDialogState(onSelected = {
            vm.updateUserId(it.userId)
        })
        UserSelectorDialog(state = userSelectorState)
        FilledTonalButton(onClick = {
            userSelectorState.show()
        }) {
            Row(verticalAlignment = CenterVertically) {
                Text(text = state.userLabel)
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
        StandardSpacer()

        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_COMMON_DATA] + " - " + LocalI18N.current[I18N_R.UI_COMMON_OPTIONAL]
        )
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),


            value = state.data ?: "",
            onValueChange = {
                vm.updateData(it)
            })
        StandardSpacer()

        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_COMMON_EXTRAS]
        )
        state.extras.forEachIndexed { index, extra ->
            Row(verticalAlignment = CenterVertically, horizontalArrangement = SpaceBetween) {
                // Key
                TextField(
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = LocalI18N.current[I18N_R.UI_COMMON_EXTRAS_K]) },
                    value = extra.key,
                    onValueChange = {
                        vm.updateExtraKey(index, it)
                    })
                StandardSpacer()

                // Value
                TextField(
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = LocalI18N.current[I18N_R.UI_COMMON_EXTRAS_V]) },
                    value = extra.value,
                    onValueChange = {
                        vm.updateExtraValue(index, it)
                    })
                StandardSpacer()

                // Type.
                Box(modifier = Modifier.weight(1f)) {
                    val typeDropdownState = rememberDropdownSelectorState()

                    DropdownSelector(
                        state = typeDropdownState,
                        items = AndroidIntentExtraType.values()
                            .filter { it != AndroidIntentExtraType.UNRECOGNIZED }.map {
                                DropdownItem(
                                    labelLines = listOf(it.name),
                                    data = it
                                )
                            }
                    ) {
                        vm.updateExtraType(index, it.data)
                    }

                    FilledTonalButton(onClick = {
                        typeDropdownState.open()
                    }) {
                        Row(verticalAlignment = CenterVertically) {
                            Text(text = extra.type.name)
                            StandardSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                }
            }
        }
        StandardSpacer()

        // Add extra
        Button(onClick = {
            vm.newExtra()
        }) {
            Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
        }

        StandardSpacer()
        SwitchRow(
            text = LocalI18N.current[I18N_R.UI_ACTION_START_SERVICE_IS_FG],
            isChecked = state.isFG,
            onCheckChange = {
                vm.updateFG(it)
            })

        StandardSpacer()

        SuggestedNavEntry()
    }
}

@Preview
@Composable
private fun ContentPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>, fallback: String?): String {
            return key.substringAfterLast(".")
        }
    }) {
        Content(
            paddingValues = PaddingValues(), vm = StartServiceSelectorViewModel(
                LocalContext.current
            ).apply {
                addExtra(
                    AndroidIntentExtra.newBuilder().setType(AndroidIntentExtraType.String)
                        .setKey("name").setValue("Tornaco")
                        .build()
                )
                addExtra(
                    AndroidIntentExtra.newBuilder().setType(AndroidIntentExtraType.Bool)
                        .build()
                )
            }
        )
    }
}