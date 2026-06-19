@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.InfoCard
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.Md3ExpPullRefreshIndicator
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class DynamicShortcutSettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context, pick: Boolean = false) {
            val starter = intent(context, pick)
            context.startActivity(starter)
        }

        fun intent(context: Context, pick: Boolean) =
            Intent(context, DynamicShortcutSettingsActivity::class.java).putExtra("pick", pick)
    }

    @Composable
    override fun Content() {
        val pick = remember {
            intent.getBooleanExtra("pick", false)
        }
        ShortcutSettings(
            back = {
                setResult(RESULT_CANCELED)
                finish()
            },
            onShortcutClick = {
                if (pick) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra("shortcut", it.number)
                    })
                    finish()
                }
            })
    }
}


@Composable
private fun ShortcutSettings(back: () -> Unit, onShortcutClick: (DynamicShortcut) -> Unit) {
    val i18N = LocalI18N.current
    Scaffold(
        modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(text = i18N["ui.settings.dynamic.shortcut.tile"]) },
                navigationIcon = {
                    IconButton(onClick = {
                        back()
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {

                }
            )
        }) { paddingValues ->

        val vm = hiltViewModel<DynamicShortcutSettingsViewModel>()
        val state by vm.state.collectAsState()

        DisposableEffectWithLifeCycle(
            onResume = {
                vm.loadState()
            }
        )
        val pullRefreshState = rememberPullRefreshState(state.isLoading, { vm.loadState() })

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                state.shortcuts.forEach {
                    ShortcutItem(
                        shortcut = it,
                        usage = (state.usages[it.shortcut]) ?: emptyList(),
                        onClick = { onShortcutClick(it.shortcut) },
                        rename = { tile, newName ->
                            vm.rename(tile, newName)
                        },
                        setIcon = { tile, newName ->
                            vm.setIcon(tile, newName)
                        })
                }

                SectionSpacer()
                InfoCard(
                    title = null,
                    infos = listOf(LocalI18N.current[I18N_R.UI_SETTINGS_DYNAMIC_SHORTCUT_TIP])
                )

            }

            Md3ExpPullRefreshIndicator(
                state.isLoading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@Composable
private fun ShortcutItem(
    shortcut: DynamicShortcutSetting,
    usage: List<RuleUM>,
    onClick: () -> Unit,
    rename: (DynamicShortcut, String) -> Unit,
    setIcon: (DynamicShortcut, String) -> Unit,
) {
    val context = LocalContext.current

    val renameDialogState =
        rememberTextInputState(
            title = LocalI18N.current[I18N_R.UI_COMMON_RENAME], message = null,
            onSelected = {
                rename(shortcut.shortcut, it)
            })
    TextInputDialog(state = renameDialogState)

    val editDropdownState = rememberDropdownSelectorState()
    Box(modifier = Modifier) {
        ShortXCard(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShortXCardRoundedCornerShape)
                    .clickable {
                        onClick()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(min = 60.dp)
                        .weight(1f, fill = false)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier) {
                        Text(text = shortcut.label, style = MaterialTheme.typography.titleMedium)
                        if (usage.isEmpty()) {
                            Text(
                                text = LocalI18N.current[I18N_R.UI_SETTINGS_DYNAMIC_SHORTCUT_UNUSED],
                                style = MaterialTheme.typography.bodySmall,
                            )

                        } else {
                            usage.forEach {
                                LinkText(
                                    text = LocalI18N.current[I18N_R.UI_SETTINGS_DYNAMIC_SHORTCUT_USED, mapOf(
                                        "title" to it.title
                                    )]
                                ) {
                                    AddRuleActivity.edit(context, it.id)
                                }
                            }
                        }
                    }
                }
                IconButton(onClick = {
                    editDropdownState.open()
                }) {
                    RemixIcon(remixName = Remix.Document.file_edit_line)
                }
            }
        }

        DropdownSelector(
            state = editDropdownState,
            items = listOf(
                DropdownItem(listOf(LocalI18N.current[I18N_R.UI_COMMON_RENAME]), "rename"),
            ),
            onSelect = {
                if (it.data == "rename") {
                    renameDialogState.show(shortcut.label)
                }
            })
    }

}



