@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.discover.contribute

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold

class ContributorActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ContributorActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ContributorScreen(close = { finish() })
    }
}

@Composable
fun ContributorScreen(close: () -> Unit) {
    ShortXAppBarScaffold(title = "Contributors", onBackPressed = {
        close()
    }) {
        var contributors by remember { mutableStateOf<List<Contributor>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            contributors = kotlin.runCatching { fetchContributors() }.getOrElse { emptyList() }
            isLoading = false
        }

        if (isLoading) {
            Loading(modifier = Modifier.fillMaxSize())
        } else {
            ContributorList(contributors = contributors)
        }
    }
}