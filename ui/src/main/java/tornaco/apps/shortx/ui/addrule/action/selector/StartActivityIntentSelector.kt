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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class StartActivityIntentDialogState(
    val onSelected: (Action.StartActivityIntent) -> Unit
) {
    var isShowing by mutableStateOf(false)
    var current: Action.StartActivityIntent? = null

    fun show(current: Action.StartActivityIntent? = null) {
        this.current = current
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberStartActivityIntentDialogState(
    onSelected: (Action.StartActivityIntent) -> Unit
): StartActivityIntentDialogState {
    return remember {
        StartActivityIntentDialogState(onSelected)
    }
}

@Composable
fun StartActivityIntentDialog(state: StartActivityIntentDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            StartActivityIntentSelector(state)
        }
    }
}

@Composable
private fun StartActivityIntentSelector(dialogState: StartActivityIntentDialogState) {
    val vm = hiltViewModel<StartActivityIntentSelectorViewModel>()
    LaunchedEffect(vm) {
        val current = dialogState.current
        if (current != null) {
            vm.initFrom(current)
        } else {
            vm.resetState()
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
                        title = { Text(LocalI18N.current["ui.action.start.activity.intent"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.StartActivityIntent(
                                        id = vmState.id,
                                        userId = vmState.userId,
                                        action = vmState.action,
                                        flags = 0,
                                        pkgName = vmState.pkgName,
                                        className = vmState.className,
                                        data = vmState.data,
                                        extras = vmState.extras
                                    )
                                )
                                dialogState.dismiss()
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
private fun Content(paddingValues: PaddingValues, vm: StartActivityIntentSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current["ui.common.action"]
        )
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.action,
            onValueChange = {
                vm.updateAction(it)
            })
        StandardSpacer()

        val singleAppSelectorState = rememberSingleAppPkgSelectorState {
            vm.updatePkgName(it.pkg.pkgName)
            vm.updateUserId(it.pkg.userId)
        }
        SingleAppSelectorDialog(state = singleAppSelectorState)
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current["ui.common.pkg.name"] + " - " + LocalI18N.current["ui.common.optional"]
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
            types = listOf(ComponentType.Activity),
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
            text = LocalI18N.current["ui.common.class.name"] + " - " + LocalI18N.current["ui.common.optional"]
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
            text = LocalI18N.current["ui.common.user.id"]
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
            text = LocalI18N.current["ui.common.data"] + " - " + LocalI18N.current["ui.common.optional"]
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
            text = LocalI18N.current["ui.common.extras"]
        )
        state.extras.forEachIndexed { index, extra ->
            Row(verticalAlignment = CenterVertically, horizontalArrangement = SpaceBetween) {
                // Key
                TextField(
                    modifier = Modifier.weight(1f),

                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    placeholder = { Text(text = LocalI18N.current["ui.common.extras.k"]) },
                    value = extra.key,
                    onValueChange = {
                        vm.updateExtraKey(index, it)
                    })
                StandardSpacer()

                // Value
                TextField(
                    modifier = Modifier.weight(1f),

                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    placeholder = { Text(text = LocalI18N.current["ui.common.extras.v"]) },
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
        FilledTonalIconButton(onClick = {
            vm.newExtra()
        }) {
            RemixIcon(remixName = Remix.System.add_fill)
        }

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
            paddingValues = PaddingValues(), vm = StartActivityIntentSelectorViewModel(
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