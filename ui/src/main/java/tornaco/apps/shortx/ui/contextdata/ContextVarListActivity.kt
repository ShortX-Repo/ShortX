@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.contextdata

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.Md3ExpPullRefreshIndicator
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

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
    val scope = rememberCoroutineScope()

    // rawKey -> current value
    var valuesMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isRefreshing by remember { mutableStateOf(false) }

    fun loadValues() {
        isRefreshing = true
        scope.launch {
            val rawKeys = allEnvs.map { it.keyDecored.removeSurrounding("%") }
            val values = withContext(Dispatchers.IO) {
                shortXManager.resolveSystemEnvValues(rawKeys)
            }
            valuesMap = rawKeys.zip(values).toMap()
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) { loadValues() }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N[I18N_R.UI_SYSTEM_ENV]) },
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
                                    i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
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
                val pullRefreshState =
                    rememberPullRefreshState(isRefreshing, { loadValues() })

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val searchBarState = rememberSearchBarState(allEnvs.size)
                        SearchBar(
                            searchBarState = searchBarState,
                            modifier = Modifier.padding(16.dp)
                        )

                        val filteredEnvs = allEnvs.filter { env ->
                            searchBarState.keyword.isBlank() ||
                                    env.keyDecored.contains(
                                        searchBarState.keyword,
                                        ignoreCase = true
                                    ) ||
                                    i18N[env.displayName].contains(
                                        searchBarState.keyword,
                                        ignoreCase = true
                                    )
                        }

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredEnvs) { env ->
                                val rawKey = env.keyDecored.removeSurrounding("%")
                                val currentValue = valuesMap[rawKey]
                                val displayName = i18N[env.displayName]
                                val tip =
                                    env.tip.takeIf { it.isNotBlank() }?.let { i18N[it] }
                                ListItem(
                                    title = displayName,
                                    text1 = currentValue ?: env.keyDecored,
                                    text2 = buildString {
                                        if (currentValue != null) {
                                            append(env.keyDecored)
                                            append("  ")
                                        }
                                        tip?.let {
                                            if (isNotEmpty()) append(" · ")
                                            append(it)
                                        }
                                        if (env.requireParam) {
                                            if (isNotEmpty()) append(" · ")
                                            append(i18N[I18N_R.UI_SYSTEM_ENV_REQUIRE_PARAM])
                                        }
                                    }.takeIf { it.isNotBlank() },
                                    onClick = {
                                        context.clipboardManager.setPrimaryClip(
                                            ClipData.newPlainText("ShortX", env.keyDecored)
                                        )
                                        Toast.makeText(
                                            context,
                                            i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    }

                    Md3ExpPullRefreshIndicator(
                        isRefreshing,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
