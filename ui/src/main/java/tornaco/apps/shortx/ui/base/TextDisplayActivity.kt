@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import android.R
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

class TextDisplayActivity : BaseActivity() {
    companion object {
        const val KEY_TEXT = "text"
        const val KEY_TITLE = "title"

        @JvmStatic
        fun start(context: Context, title: String, text: String) {
            val starter = Intent(context, TextDisplayActivity::class.java)
                .putExtra(KEY_TITLE, title)
                .putExtra(KEY_TEXT, text)
            context.startActivity(starter)
        }
    }


    @Composable
    override fun Content() {
        val title = intent.getStringExtra(KEY_TITLE) ?: "ERROR: Missing title extra."
        val text = intent.getStringExtra(KEY_TEXT) ?: "ERROR: Missing text extra."
        val context = LocalContext.current

        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(title) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                TextButton(onClick = {
                                    ClipboardUtils.copyToClipboard(
                                        context,
                                        "text",
                                        text
                                    )
                                }) {
                                    Text(text = stringResource(id = R.string.copy))
                                }

                            },
                        )
                    }) { paddingValues ->
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val lines = remember(text) {
                            text.lines()
                        }
                        LazyColumn(contentPadding = paddingValues.copy(all = { it + 16.dp })) {
                            items(lines) {
                                Text(text = it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

}