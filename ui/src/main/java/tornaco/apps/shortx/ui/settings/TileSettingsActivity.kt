@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.settings

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.addtoggle.AddToggleActivity
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.InfoCard
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.Md3ExpPullRefreshIndicator
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class TileSettingsActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context, pick: Boolean = false) {
            val starter = intent(context, pick)
            context.startActivity(starter)
        }

        fun intent(context: Context, pick: Boolean) =
            Intent(context, TileSettingsActivity::class.java).putExtra("pick", pick)
    }

    @Composable
    override fun Content() {
        val pick = remember {
            intent.getBooleanExtra("pick", false)
        }
        TileSettings(
            back = {
                setResult(RESULT_CANCELED)
                finish()
            },
            onTileClick = {
                if (pick) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra("tile", it.number)
                    })
                    finish()
                }
            })
    }
}

@Composable
private fun TileSettings(back: () -> Unit, onTileClick: (StatusBarTile) -> Unit) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.settings.sb.tile"]) },
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

                val vm = hiltViewModel<TileSettingsViewModel>()
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
                        state.tiles.forEach {
                            TileItem(
                                tile = it,
                                usage = (state.usages[it.tile]) ?: emptyList(),
                                onClick = { onTileClick(it.tile) },
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
                            infos = listOf(LocalI18N.current["ui.settings.sb.tile.tip"])
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
    }
}


@Composable
private fun TileItem(
    tile: StatusBarTileSetting,
    usage: List<TileUsageOwner>,
    onClick: () -> Unit,
    rename: (StatusBarTile, String) -> Unit,
    setIcon: (StatusBarTile, String) -> Unit,
) {
    val context = LocalContext.current

    val renameDialogState =
        rememberTextInputState(title = LocalI18N.current["ui.common.rename"], message = null,
            onSelected = {
                rename(tile.tile, it)
            })
    TextInputDialog(state = renameDialogState)

    val iconLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val icon = result.popupSelectorResult<MDIcon>()
            icon?.let {
                setIcon(tile.tile, it.iconNameOrBase64)
            }
        }
    )

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
                    ColoredIcon(size = 28.dp, color = MaterialTheme.colorScheme.primary) {
                        RemixIcon(
                            remixName = tile.iconName,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    StandardSpacer()

                    Column(modifier = Modifier) {
                        Text(text = tile.label, style = MaterialTheme.typography.titleMedium)

                        if (usage.isEmpty()) {
                            Text(
                                text = LocalI18N.current["ui.settings.sb.unused"],
                                style = MaterialTheme.typography.bodySmall,
                            )

                        } else {
                            usage.forEach {
                                when (it) {
                                    is TileUsageOwner.RuleOwner -> {
                                        LinkText(
                                            text = LocalI18N.current["ui.settings.sb.used", mapOf(
                                                "title" to it.ruleUM.title
                                            )]
                                        ) {
                                            AddRuleActivity.edit(context, it.ruleUM.id)
                                        }
                                    }

                                    is TileUsageOwner.ToggleOwner -> {
                                        LinkText(
                                            text = LocalI18N.current["ui.settings.sb.used", mapOf(
                                                "title" to it.toggleUM.title
                                            )]
                                        ) {
                                            AddToggleActivity.edit(context, it.toggleUM.id)
                                        }
                                    }
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
                DropdownItem(listOf(LocalI18N.current["ui.common.rename"]), "rename"),
                DropdownItem(listOf(LocalI18N.current["ui.common.icon"]), "icon"),
            ),
            onSelect = {
                if (it.data == "rename") {
                    renameDialogState.show(tile.label)
                }
                if (it.data == "icon") {
                    iconLauncher.launch(
                        BaseSelectorActivity.newIntent(
                            context,
                            null,
                            MDIconSelectorActivity::class.java
                        )
                    )
                }
            })
    }

}











