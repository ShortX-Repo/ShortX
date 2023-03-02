@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.popBackWithResult
import tornaco.apps.shortx.ui.theme.ShortXTheme

const val KEY_ACTION = "selector.key.action"

@Composable
fun ActionSelector(
) {
    val navController: NavController = LocalNavHostController.current
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.pkg.set.title"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = paddingValues
                ) {
                    stickyHeader {
                        Button(onClick = {
                            navController.popBackWithResult(
                                mapOf(
                                    KEY_ACTION to Action.MVEL(
                                        "hello.exe()"
                                    )
                                )
                            )
                        }) {
                            Text(text = "Back")
                        }
                    }
                }
            }
        }
    }
}