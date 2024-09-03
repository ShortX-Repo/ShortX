@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.da.ActiveJobIds
import tornaco.apps.shortx.ui.main.da.DAItemHandler
import tornaco.apps.shortx.ui.main.da.DAList
import tornaco.apps.shortx.ui.main.da.DAListViewModel
import tornaco.apps.shortx.ui.main.da.DASetFilterFlowRow
import tornaco.apps.shortx.ui.main.da.DASetItemHandler
import tornaco.apps.shortx.ui.main.da.DirectActionListContent
import tornaco.apps.shortx.ui.main.da.LocalDAItemHandler
import tornaco.apps.shortx.ui.main.da.LocalDASetItemHandler
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class CreateShortcutActivity : BaseActivity() {
    @Composable
    override fun Content() {
        ShortXTheme {
            Surface {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = stringResource(id = R.string.ui_create_shortcut)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {

                            }
                        )
                    }) { paddingValues ->

                    val daListViewModel = hiltViewModel<DAListViewModel>().apply { resetState() }
                    val daListState by daListViewModel.state.collectAsState()

                    LaunchedEffect(daListViewModel) {
                        daListViewModel.selectDASetFilter(CommonSetFilter.PinnedAllFlat)
                    }

                    val context = LocalContext.current

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            TipRow(text = LocalI18N.current["ui.common.click.item.to.pick"])
                        }

                        val daItemHandler = object : DAItemHandler {
                            override fun click(da: DirectActionUM) {
                                val shortcutIntent =
                                    createResultIntent(context, da)
                                setResult(RESULT_OK, shortcutIntent)
                                finish()
                            }
                        }
                        val setItemHandler = object : DASetItemHandler {}
                        CompositionLocalProvider(
                            LocalSnackbarHostState provides SnackbarHostState(),
                            LocalDAItemHandler provides daItemHandler,
                            LocalDASetItemHandler provides setItemHandler
                        ) {
                            val daList by daListViewModel.daList.collectAsState()
                            when (daList) {
                                is LoadState.Error -> {
                                    LoadError(modifier = Modifier.fillMaxSize()) {
                                    }
                                }

                                LoadState.Loading -> {
                                    Loading(modifier = Modifier.fillMaxSize())
                                }

                                is LoadState.Success -> {
                                    DASetFilterFlowRow(
                                        daSetFilters = (daList as LoadState.Success<DAList>).data.daSetFilters,
                                        selectedDASetFilter = (daList as LoadState.Success<DAList>).data.selectedDASetFilter,
                                        selectDASetFilter = { daListViewModel.selectDASetFilter(it) },
                                    )
                                    DirectActionListContent(
                                        isShowDetailedDAItem = daListState.isShowDetailedDAItem,
                                        das = (daList as LoadState.Success<DAList>).data.das,
                                        showOverflowMenu = false,
                                        colorfulCard = false,
                                        activeJobs = ActiveJobIds()
                                    )
                                }
                            }

                        }
                    }

                }
            }
        }
    }

    fun createResultIntent(
        context: Context,
        da: DirectActionUM
    ): Intent {
        val info: ShortcutInfoCompat =
            ActionShortcutActivity.createShortcutInfo(context, da)
        return ShortcutManagerCompat.createShortcutResultIntent(context, info)
    }
}