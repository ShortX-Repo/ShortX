@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code.mvel


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.app.CEComposeBinding
import io.github.rosemoe.sora.app.databinding.CodeEditorComposeBinding
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.mapper.summaryKey
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils

@AndroidEntryPoint
class MVELEditorActivity : BaseSelectorActivity<ParcelableCode>() {
    @Composable
    override fun BaseSelectorActivity<ParcelableCode>.SelectorContent(currentData: ParcelableCode?) {
        MVELEditor(currentData)
    }
}

@Composable
fun MVELEditorActivity.MVELEditor(
    currentAction: ParcelableCode?,
) {
    val outputTitle = LocalI18N.current["ui.title.mvel"]
    val context = LocalContext.current

    val vm = hiltViewModel<MVELEditorViewModel>()
    LaunchedEffect(vm) {
        vm.initWith(currentAction)
    }

    vm.collectSideEffect(sideEffect = {
        if (it.isBlank()) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        } else {
            TextDisplayActivity.start(context, outputTitle, it)
        }
    })

    val state by vm.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = {
                        val contextMenuState = rememberMenuDialogState<CoroutineContext>(
                            title = LocalI18N.current["ui.common.coroutine.context"],
                            menuItems = CoroutineContext.entries.filterNot { it == CoroutineContext.UNRECOGNIZED }
                                .map {
                                    MenuDialogItem(
                                        id = it.name,
                                        title = LocalI18N.current[it.labelKey()],
                                        summary = LocalI18N.current[it.summaryKey()]
                                    )
                                },
                            onItemSelected = { _, id ->
                                val type = CoroutineContext.valueOf(id)
                                vm.setCoroutineContext(type)
                            }
                        )
                        MenuDialog(state = contextMenuState)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = LocalI18N.current["ui.title.mvel"])
                                LinkText(text = LocalI18N.current["ui.user.guide"]) {
                                    BrowserUtils.launch(context, BuildProp.WIKI_URL_MVEL)
                                }
                            }
                            StandardSpacer()
                            AssistChip(label = {
                                Text(LocalI18N.current[state.coroutineContext.labelKey()])
                            }, onClick = {
                                contextMenuState.show()
                            })
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            cancelAndFinish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        if (!state.isExecuting) {
                            IconButton(
                                onClick = {
                                    vm.execute()
                                }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Evaluate"
                                )
                            }
                        }

                        if (state.isExecuting) {
                            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                                Loading(modifier = Modifier.size(28.dp))
                            }
                        }

                        IconButton(onClick = {
                            setResultAndFinish(
                                ParcelableCode(
                                    expression = state.expression,
                                    coroutineContext = state.coroutineContext
                                )
                            )
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    val subscribeState by LVLStateHolder.collectAsState()

                    AndroidViewBinding(
                        factory = { inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean ->
                            CodeEditorComposeBinding.inflate(inflater, parent, attachToParent)
                                .apply {
                                    CEComposeBinding(
                                        binding = this,
                                        showToolbar = subscribeState.isSubscribed,
                                        initialText = currentAction?.expression ?: ""
                                    ) {
                                        composeLogger.logOnDebug("Content change: $it")
                                        vm.updateExpression(it)
                                    }.onCreate()
                                }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        onRelease = {
                            CEComposeBinding(
                                binding = this,
                                showToolbar = subscribeState.isSubscribed,
                                initialText = ""
                            ).onDestroy()
                        }
                    )
                }
            }
        }
    }
}