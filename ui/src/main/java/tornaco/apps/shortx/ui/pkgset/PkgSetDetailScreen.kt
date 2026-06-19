@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.pkgset

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.CategoryTitle
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.copy
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class PkgSetDetailActivity : BaseActivity() {
    companion object {
        private const val KEY_ID = "ID"

        @JvmStatic
        fun start(context: Context, id: String) {
            val starter = Intent(context, PkgSetDetailActivity::class.java)
                .putExtra(KEY_ID, id)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        intent.getStringExtra(KEY_ID)?.let {
            PkgSetDetailScreen(id = it) {
                finish()
            }
        }
    }
}


@Composable
fun PkgSetDetailScreen(id: String, back: () -> Unit) {
    val vm = hiltViewModel<PkgSetDetailViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState(id)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val noteInputState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_COMMON_NOTE],
                message = null,
                showSymbolButton = false,
                inputValidator = { InputValidateResult.OK },
                onSelected = {
                    vm.updateDescription(it)
                }
            )
            TextInputDialog(state = noteInputState)

            val appSelectorDialogState = rememberAppPkgSelectorState(
                onAppListSelected = {
                    vm.setApps(it)
                }
            )
            AppPkgSelectorDialog(state = appSelectorDialogState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current[I18N_R.UI_PKG_SET_TITLE] + " " + LocalI18N.current["ui.common.app.${state.label.lowercase()}", emptyMap(), state.label]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            if (!state.isPrebuilt) {
                                IconButton(enabled = state.isChanged, onClick = {
                                    vm.save()
                                    back()
                                }) {
                                    RemixIcon(remixName = Remix.System.check_line)
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    if (!state.isPrebuilt) {
                        FloatingActionButton(onClick = {
                            appSelectorDialogState.show(state.apps)
                        }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                        }
                    }
                }) { paddingValues ->

                Box(modifier = Modifier.padding(paddingValues)) {
                    if (state.isLoading) {
                        Loading(
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    } else {
                        PkgSetContent(
                            paddingValues = paddingValues.copy(
                                bottom = { it + 120.dp }
                            ),
                            state = state,
                            vm = vm,
                            noteInputState = noteInputState)
                    }
                }

            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PkgSetContent(
    paddingValues: PaddingValues,
    state: PkgSetDetailState,
    vm: PkgSetDetailViewModel,
    noteInputState: tornaco.apps.shortx.ui.base.TextInputState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = paddingValues
    ) {
        if (!state.isPrebuilt) {
            item {
                Column {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current[I18N_R.UI_PKG_SET_ADD_INPUT_LABEL]
                    )
                    ShortXTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, top = 16.dp),
                        placeholder = {
                            Text(LocalI18N.current[I18N_R.UI_PKG_SET_ADD_INPUT_LABEL])
                        },
                        value = state.label,
                        onValueChange = { value ->
                            vm.updateLabel(value)
                        })

                    CategoryTitle(title = LocalI18N.current[I18N_R.UI_COMMON_NOTE])
                    ListItem(
                        title = state.description.ifEmpty { LocalI18N.current[I18N_R.UI_COMMON_NOTE] },
                        onClick = {
                            noteInputState.show(state.description)
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            SmallTitle(
                                text = LocalI18N.current[I18N_R.UI_PKG_SET_AUTO_ADD_NEWLY_INSTALLED_APPS_LABEL]
                                    ?: "Auto add newly installed apps"
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = LocalI18N.current[I18N_R.UI_PKG_SET_AUTO_ADD_NEWLY_INSTALLED_APPS_DESC]
                                    ?: "Automatically add newly installed apps to this set.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = state.autoAddNewlyInstalledApps,
                            onCheckedChange = { vm.updateAutoAddNewlyInstalledApps(it) }
                        )
                    }
                }
            }
        }

        items(state.apps) {
            AppItem(app = it, canRemove = !state.isPrebuilt) { app ->
                vm.removeApps(listOf(app))
            }
        }
    }
}


@Composable
private fun AppItem(
    app: App,
    canRemove: Boolean,
    removeApp: (App) -> Unit
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            ClipboardUtils.copyToClipboard(context, "packageName", app.pkg.pkgName)
            Toast
                .makeText(
                    context,
                    i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                    Toast.LENGTH_LONG
                )
                .show()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    AppIcon(modifier = Modifier.size(38.dp), app.pkg)
                    Spacer(modifier = Modifier.size(12.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = app.label, fontSize = 14.sp
                        )
                        SmallSpacer()
                        Text(
                            text = app.pkg.pkgName, fontSize = 12.sp
                        )
                    }
                }

                if (canRemove) {
                    IconButton(onClick = { removeApp(app) }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                }
            }
        }
    }
}
