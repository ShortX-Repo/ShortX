@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.contextdata

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ContextVarListActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ContextVarListActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ContextVarListScreen(back = { finish() })
    }
}

@Composable
fun ContextVarListScreen(back: () -> Unit) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val allEnvs = remember { shortXManager.getAllSystemEnv() }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.system.env"]) },
                        actions = {
                            IconButton(onClick = {
                                context.clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(
                                        "ShortX",
                                        allEnvs.joinToString(separator = "\n") { it.keyDecored }
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    i18N["ui.common.copied.to.clipboard"],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                RemixIcon(remixName = Remix.Document.clipboard_fill)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { back() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val searchBarState = rememberSearchBarState(allEnvs.size)
                    SearchBar(
                        searchBarState = searchBarState,
                        modifier = Modifier.padding(16.dp)
                    )

                    val filteredEnvs = allEnvs.filter { env ->
                        searchBarState.keyword.isBlank() ||
                                env.keyDecored.contains(searchBarState.keyword, ignoreCase = true) ||
                                i18N[env.displayName].contains(searchBarState.keyword, ignoreCase = true)
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredEnvs) { env ->
                            val displayName = i18N[env.displayName]
                            val tip = env.tip.takeIf { it.isNotBlank() }?.let { i18N[it] }
                            ListItem(
                                title = displayName,
                                text1 = env.keyDecored,
                                text2 = buildString {
                                    tip?.let { append(it) }
                                    if (env.requireParam) {
                                        if (isNotEmpty()) append(" · ")
                                        append(i18N["ui.system.env.require.param"])
                                    }
                                }.takeIf { it.isNotBlank() },
                                onClick = {
                                    context.clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText("ShortX", env.keyDecored)
                                    )
                                    Toast.makeText(
                                        context,
                                        i18N["ui.common.copied.to.clipboard"],
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
