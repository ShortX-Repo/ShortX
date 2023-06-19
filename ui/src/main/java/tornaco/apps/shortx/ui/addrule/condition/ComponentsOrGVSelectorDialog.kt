@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import android.content.ComponentName
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.appComponentToString
import tornaco.apps.shortx.core.os.isMainUser
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.addrule.action.selector.UserSelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberUserSelectorDialogState
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.ShortXTheme

private enum class SelectorType {
    GV,
    Component
}

data class ComponentsOrGV(
    val components: List<AppComponent> = emptyList(),
    val gvName: String = ""
)

class AppComponentOrGVSelectorDialogState(
    val types: List<ComponentType>,
    val onSelected: (ComponentsOrGV) -> Unit
) : CommonDialogState() {

    private var _componentsOrGV by mutableStateOf(
        ComponentsOrGV()
    )

    val componentsOrGV get() = _componentsOrGV

    fun show(components: List<AppComponent>? = emptyList()) {
        this._componentsOrGV = ComponentsOrGV(components = components ?: emptyList())
        show()
    }

    fun removeComponent(appComponent: AppComponent) {
        this._componentsOrGV =
            _componentsOrGV.copy(components = componentsOrGV.components.toMutableList().apply {
                remove(appComponent)
            })
    }

    fun addComponent(appComponent: AppComponent) {
        this._componentsOrGV =
            _componentsOrGV.copy(components = componentsOrGV.components.toMutableList().apply {
                add(appComponent)
            })
    }

    fun addComponents(appComponents: List<AppComponent>) {
        this._componentsOrGV =
            _componentsOrGV.copy(components = componentsOrGV.components.toMutableList().apply {
                addAll(appComponents)
            })
    }

    fun addGV(name: String) {
        this._componentsOrGV =
            _componentsOrGV.copy(gvName = name)
    }

    fun removeGV() {
        this._componentsOrGV =
            _componentsOrGV.copy(gvName = "")
    }
}

@Composable
fun rememberComponentOrGVSelectorState(
    types: List<ComponentType>,
    onSelected: (ComponentsOrGV) -> Unit
): AppComponentOrGVSelectorDialogState {
    return remember {
        AppComponentOrGVSelectorDialogState(types, onSelected)
    }
}

@Composable
fun ComponentOrGVSelectorDialog(state: AppComponentOrGVSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ComponentOrGVSelectorContent(state)
        }
    }
}

@Composable
private fun ComponentOrGVSelectorContent(dialogState: AppComponentOrGVSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.common.select.component"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                                dialogState.onSelected(dialogState.componentsOrGV)
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
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
                    ComponentContent(dialogState = dialogState)
                }
            }
        }
    }
}

@Composable
private fun ComponentOrGVEntry(dialogState: AppComponentOrGVSelectorDialogState) {
    val i18N = LocalI18N.current
    val typeDropdownState = rememberDropdownSelectorState()
    var type by remember {
        mutableStateOf(SelectorType.Component)
    }

    DropdownSelector(
        state = typeDropdownState,
        items = listOf(
            DropdownItem(
                labelLines = listOf(i18N["ui.common.from.component"]),
                data = SelectorType.Component.name
            ),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.from.global.var"]),
                data = SelectorType.GV.name
            )
        )
    ) {
        type = SelectorType.valueOf(it.data)
    }

    FilledTonalButton(onClick = {
        typeDropdownState.open()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (type == SelectorType.GV) i18N["ui.common.from.global.var"] else i18N["ui.common.from.component"])
            StandardSpacer()
            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
        }
    }

    when (type) {
        SelectorType.Component -> {
            ComponentContent(dialogState = dialogState)
        }

        SelectorType.GV -> {
            GVContent(dialogState = dialogState)
        }
    }
}

@Composable
private fun GVContent(dialogState: AppComponentOrGVSelectorDialogState) {
    val globalVarToWriteSelectorState = rememberGlobalVarSelectorState {
        dialogState.addGV(it.name)
    }
    GlobalVarSelectorDialog(globalVarToWriteSelectorState)

    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp),
        text = LocalI18N.current["ui.action.select.global.var"]
    )
    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        value = dialogState.componentsOrGV.gvName,
        onValueChange = {
            dialogState.addGV(it)
        },
        trailingIcon = {
            IconButton(onClick = {
                globalVarToWriteSelectorState.show()
            }) {
                RemixIcon(modifier = Modifier, remixName = Remix.System.search_2_line)
            }
        })
}

@Composable
private fun ComponentContent(dialogState: AppComponentOrGVSelectorDialogState) {
    MediumTitle(
        text = "${LocalI18N.current["ui.common.selected.components"]} ${dialogState.componentsOrGV.components.size}"
    )
    if (dialogState.componentsOrGV.components.isEmpty()) {
        TipBody(
            text = LocalI18N.current["ui.common.select.component.empty"]
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
        ) {
            items(
                items = dialogState.componentsOrGV.components.distinctBy { it },
                key = { it.appComponentToString() }) { appComponent ->
                AppComponentItem(component = appComponent, remove = {
                    dialogState.removeComponent(appComponent)
                })
            }
        }
    }

    StandardSpacer()
    StandardSpacer()

    val componentSelectorState = rememberAppComponentSelectorState(
        types = dialogState.types,
        onAppComponentSelected = { infos ->
            dialogState.addComponents(infos.map { it.component })
        }
    )
    AppComponentSelectorDialog(state = componentSelectorState)
    val singleAppSelectorState = rememberSingleAppPkgSelectorState(onAppSelected = {
        componentSelectorState.show(
            pkg = it.pkg,
            excludeList = dialogState.componentsOrGV.components
        )
    })

    SingleAppSelectorDialog(state = singleAppSelectorState)
    FilledTonalButton(onClick = {
        singleAppSelectorState.show()
    }) {
        Text(text = LocalI18N.current["ui.common.select"])
    }



    StandardSpacer()
    StandardSpacer()

    Canvas(modifier = Modifier.fillMaxWidth(), onDraw = {
        drawLine(
            color = NoteTextColor,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    })

    StandardSpacer()
    StandardSpacer()

    MediumTitle(text = LocalI18N.current["ui.common.manually.input"])

    // Manually input section
    val i18N = LocalI18N.current
    var inputComponentString by remember {
        mutableStateOf("")
    }
    var userId by remember {
        mutableStateOf(0)
    }
    var userLabel by remember {
        mutableStateOf(i18N["ui.common.user.main"])
    }


    Row(verticalAlignment = Alignment.CenterVertically) {
        ShortXTextField(
            modifier = Modifier.weight(1f),
            value = inputComponentString,
            onValueChange = {
                inputComponentString = it
            },
            showSymbolButton = true,
            label = {
                Text(text = i18N["ui.common.select.component.input"])
            }
        )
        StandardSpacer()

        FilledTonalButton(onClick = {
            val name: ComponentName? =
                ComponentName.unflattenFromString(inputComponentString)
            name?.let {
                dialogState.addComponent(
                    it.toAppComponent(userId)
                )
            }
        }) {
            RemixIcon(remixName = Remix.System.add_fill)
        }
    }

    StandardSpacer()

    val userSelectorState =
        rememberUserSelectorDialogState(onSelected = { userInfo ->
            userId = userInfo.userId
            userLabel = if (userId.isMainUser()) {
                i18N["ui.common.user.main"]
            } else {
                shortXManager.getAllUsers()
                    .firstOrNull { it.userId == userId }?.name
                    ?: "N/A"
            }
        })
    UserSelectorDialog(state = userSelectorState)
    FilledTonalButton(onClick = {
        userSelectorState.show()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = userLabel)
            StandardSpacer()
            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
        }
    }


    StandardSpacer()
    SuggestedNavEntry()
}


@Composable
private fun AppComponentItem(
    component: AppComponent,
    remove: (AppComponent) -> Unit
) {
    ShortXCard(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        AppIcon(modifier = Modifier.size(38.dp), component.pkg)
                        Spacer(modifier = Modifier.size(12.dp))

                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = component.toComponentName().flattenToShortString(),
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = {
                        remove(component)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val userId = component.pkg.userId
                    if (!userId.isMainUser()) {
                        val name = shortXManager.getAllUsers()
                            .firstOrNull { it.userId == userId }?.name
                            ?: "user: $userId"
                        OutlineBadge(text = name)
                    }
                }
            }
        }
    }
}





