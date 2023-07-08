@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.demo

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

class DemoActivity : BaseActivity() {
    @Composable
    override fun Content() {
        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        ShortXMediumTopAppBar(
                            title = {
                                Text(
                                    text = "Demos"
                                )
                            }
                        )
                    }) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                    ) {
                        NotificationDemo()
                        MapDemo()
                        VibrateDemo()

                        Button(onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.bing.com/?toWww=1&redig=9722F55035FE421E9F77031E6B4E3209")
                            )
                            Logger().d("startActivity: $intent")
                            startActivity(intent)
                        }) {
                            Text(text = "Bing.com")
                        }
                    }
                }
            }
        }
    }
}