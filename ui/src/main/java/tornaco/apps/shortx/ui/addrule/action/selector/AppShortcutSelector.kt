@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.content.Intent
import android.content.Intent.EXTRA_SHORTCUT_INTENT
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class AppShortcutSelectorActivity : BaseSelectorActivity<Action.AppShortcut>() {
    @Composable
    override fun BaseSelectorActivity<Action.AppShortcut>.SelectorContent(currentData: Action.AppShortcut?) {
        AppShortcutSelector(currentData)
    }
}

@Composable
private fun BaseSelectorActivity<Action.AppShortcut>.AppShortcutSelector(current: Action.AppShortcut?) {
    val vm = hiltViewModel<AppShortcutViewModel>()
    LaunchedEffect(vm) {
        vm.load()
    }
    val vmState by vm.state.collectAsState()

    val selectLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            composeLogger.d("selectLauncher: ${it.data}")
            val intent = it.data
            if (intent != null) {
                val name =
                    intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME) ?: PkgUtils.packageNameOf(
                        intent
                    )
                val shortcutIntent: Intent? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(EXTRA_SHORTCUT_INTENT, Intent::class.java)
                    } else {
                        intent.getParcelableExtra(EXTRA_SHORTCUT_INTENT) as Intent?
                    }
                composeLogger.d("shortcutIntent: $shortcutIntent")
                if (shortcutIntent != null) {
                    setResultAndFinish(Action.AppShortcut(intent = shortcutIntent, label = name))
                }
            }
        }
    )

    val context = LocalContext.current

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.shortcut"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->
                LazyColumn(contentPadding = paddingValues) {
                    items(vmState.createShortcutIntentItems) { intentItem ->
                        ListItem(title = intentItem.label, icon = {
                            AppIcon(modifier = Modifier.size(42.dp), pkg = intentItem.pkg)
                        }, onClick = {
                            runCatching {
                                selectLauncher.launch(
                                    Intent(Intent.ACTION_CREATE_SHORTCUT).setComponent(
                                        PkgUtils.getComponentName(intentItem.activity)
                                    )
                                )
                            }.onFailure {
                                TextDisplayActivity.start(context, "Oops!", it.stackTraceToString())
                                composeLogger.e(it, "selectLauncher")
                            }
                        })
                    }
                }
            }
        }
    }

}