@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.os.isMainUser
import tornaco.apps.shortx.core.proto.common.ProcessName
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.toProcessName
import tornaco.apps.shortx.core.util.times
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.ThanoxBlue
import tornaco.apps.shortx.ui.util.ThanoxFuncs
import tornaco.apps.shortx.core.i18n.I18N_R

class ProcessListSelectorDialogState(
    val onSelected: (List<ProcessName>) -> Unit
) : CommonDialogState() {

    private var _processNameList: List<ProcessName> by mutableStateOf(emptyList())
    val processNameList get() = _processNameList

    val isThanoxAvailable by lazy { ThanoxFuncs.isThanoxActivated }

    fun show(current: List<ProcessName>? = emptyList()) {
        this._processNameList = current ?: emptyList()
        show()
    }

    fun addProcessName(name: ProcessName) {
        if (!this._processNameList.contains(name)) {
            this._processNameList =
                _processNameList.toMutableList().apply {
                    add(name)
                }
        }
    }

    fun removeProcessName(name: ProcessName) {
        this._processNameList =
            _processNameList.toMutableList().apply {
                remove(name)
            }
    }
}

@Composable
fun rememberProcessListSelectorState(
    onSelected: (List<ProcessName>) -> Unit
): ProcessListSelectorDialogState {
    return remember {
        ProcessListSelectorDialogState(onSelected)
    }
}

@Composable
fun ProcessListSelectorDialog(state: ProcessListSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ProcessListSelectorContent(state)
        }
    }
}

@Composable
private fun ProcessListSelectorContent(dialogState: ProcessListSelectorDialogState) {
    val context = LocalContext.current
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_COMMON_SELECT_PROCESS]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {

                            if (dialogState.isThanoxAvailable) {
                                IconButton(onClick = {
                                    ThanoxFuncs.goToProcessManagement(context)
                                }) {
                                    ColoredIcon(color = ThanoxBlue, size = 24.dp) {
                                        RemixIcon(
                                            remixName = Remix.System.shield_flash_fill,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = {
                                dialogState.dismiss()
                                dialogState.onSelected(dialogState.processNameList)
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
                    ProcessListContent(dialogState = dialogState)
                }
            }
        }
    }
}

@Composable
private fun ProcessListContent(dialogState: ProcessListSelectorDialogState) {
    MediumTitle(
        text = "${LocalI18N.current[I18N_R.UI_COMMON_SELECTED_PROCESS]} ${dialogState.processNameList.size}"
    )
    if (dialogState.processNameList.isEmpty()) {
        TipBody(
            text = LocalI18N.current[I18N_R.UI_COMMON_SELECTED_PROCESS_EMPTY]
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
        ) {
            items(
                items = dialogState.processNameList.distinctBy { it },
                key = { it }) { name ->
                AppComponentItem(name = name, remove = {
                    dialogState.removeProcessName(name)
                })
            }
        }
    }

    StandardSpacer()

    // Manually input section
    val i18N = LocalI18N.current
    var inputNameString by remember {
        mutableStateOf("")
    }
    var userId by remember {
        mutableIntStateOf(0)
    }
    var userLabel by remember {
        mutableStateOf(i18N[I18N_R.UI_COMMON_USER_MAIN])
    }
    val userSelectorState =
        rememberUserSelectorDialogState(onSelected = { userInfo ->
            userId = userInfo.userId
            userLabel = if (userId.isMainUser()) {
                i18N[I18N_R.UI_COMMON_USER_MAIN]
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

    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        value = inputNameString,
        onValueChange = {
            inputNameString = it
        },
        showSymbolButton = false,
        label = {
            Text(text = i18N[I18N_R.UI_COMMON_SELECTED_PROCESS_INPUT])
        }
    )
    MediumSpacer()
    Button(
        onClick = {
            if (inputNameString.isNotBlank()) {
                dialogState.addProcessName(
                    inputNameString.toProcessName(userId)
                )
                inputNameString = ""
            }
        }) {
        Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
    }


    3.times { SectionSpacer() }
    if (dialogState.isThanoxAvailable) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TipRow(
                text = LocalI18N.current[I18N_R.UI_COMMON_HANDLE_PROCESS_VIA_THANOX],
                icon = {
                    ColoredIcon(color = ThanoxBlue, size = 16.dp) {
                        RemixIcon(remixName = Remix.System.shield_flash_fill, tint = Color.White)
                    }
                })
        }
    }
    3.times { SectionSpacer() }
    SuggestedNavEntry()
}


@Composable
private fun AppComponentItem(
    name: ProcessName,
    remove: (ProcessName) -> Unit
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
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = name.processName,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = {
                        remove(name)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val userId = name.userId
                    if (!userId.isMainUser()) {
                        val userName = shortXManager.getAllUsers()
                            .firstOrNull { it.userId == userId }?.name
                            ?: "user: $userId"
                        OutlineBadge(text = userName)
                    }
                }
            }
        }
    }
}





