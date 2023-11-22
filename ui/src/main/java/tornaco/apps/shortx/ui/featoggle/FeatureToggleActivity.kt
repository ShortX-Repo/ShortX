@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.featoggle

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.main.BottomNavItem
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.theme.ShortXTheme

class FeatureToggleActivity : BaseActivity() {
    companion object {
        private const val KEY_FEAT_OFF_PREFIX = "feat.toggle.off."
        const val FEAT_KEY_DISABLE_FEAT_DA = KEY_FEAT_OFF_PREFIX + "DA"
        const val FEAT_KEY_DISABLE_FEAT_RULE = KEY_FEAT_OFF_PREFIX + "RULE"
        const val FEAT_KEY_DISABLE_FEAT_DISCOVER = KEY_FEAT_OFF_PREFIX + "DISCOVER"
        const val FEAT_KEY_DISABLE_FEAT_TOGGLE = KEY_FEAT_OFF_PREFIX + "TOGGLE"

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, FeatureToggleActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                val i18N = LocalI18N.current
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = i18N["ui.settings.feat.toggle"]) },
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
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        val context = LocalContext.current
                        val pref = remember { Pref(context) }
                        val miscToggles by pref.miscUiToggles.collectAsState(initial = emptySet())

                        val scope = rememberCoroutineScope()
                        ListItem(
                            title = LocalI18N.current[BottomNavItem.DirectActions.title],
                            icon = {},
                            onClick = {},
                            checked = miscToggles.contains(FEAT_KEY_DISABLE_FEAT_DA),
                            onCheckedChange = {
                                scope.launch {
                                    if (it) {
                                        pref.miscUiTogglesOff(FEAT_KEY_DISABLE_FEAT_DA)
                                    } else {
                                        pref.miscUiTogglesOn(FEAT_KEY_DISABLE_FEAT_DA)
                                    }
                                }
                            }
                        )
                        ListItem(title = LocalI18N.current[BottomNavItem.Rules.title],
                            icon = {},
                            onClick = {},
                            checked = miscToggles.contains(FEAT_KEY_DISABLE_FEAT_RULE),
                            onCheckedChange = {
                                scope.launch {
                                    if (it) {
                                        pref.miscUiTogglesOff(FEAT_KEY_DISABLE_FEAT_RULE)
                                    } else {
                                        pref.miscUiTogglesOn(FEAT_KEY_DISABLE_FEAT_RULE)
                                    }
                                }
                            })
                        ListItem(title = LocalI18N.current[BottomNavItem.Toggles.title],
                            icon = {},
                            onClick = {},
                            checked = miscToggles.contains(FEAT_KEY_DISABLE_FEAT_TOGGLE),
                            onCheckedChange = {
                                scope.launch {
                                    if (it) {
                                        pref.miscUiTogglesOff(FEAT_KEY_DISABLE_FEAT_TOGGLE)
                                    } else {
                                        pref.miscUiTogglesOn(FEAT_KEY_DISABLE_FEAT_TOGGLE)
                                    }
                                }
                            })
                        ListItem(title = LocalI18N.current[BottomNavItem.Discover.title],
                            icon = {},
                            onClick = {},
                            checked = miscToggles.contains(FEAT_KEY_DISABLE_FEAT_DISCOVER),
                            onCheckedChange = {
                                scope.launch {
                                    if (it) {
                                        pref.miscUiTogglesOff(FEAT_KEY_DISABLE_FEAT_DISCOVER)
                                    } else {
                                        pref.miscUiTogglesOn(FEAT_KEY_DISABLE_FEAT_DISCOVER)
                                    }
                                }
                            })

                    }

                }
            }
        }
    }
}