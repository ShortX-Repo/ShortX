@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.fact.mapper.getLabelForKeyCode
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


@AndroidEntryPoint
class CombineKeyEventSelectorActivity : BaseSelectorActivity<Action.InjectCombineKeyCode>() {

    @Composable
    override fun BaseSelectorActivity<Action.InjectCombineKeyCode>.SelectorContent(currentData: Action.InjectCombineKeyCode?) {
        CombineKeyEventSelector(currentData)
    }

    @Composable
    private fun CombineKeyEventSelector(currentAction: Action.InjectCombineKeyCode?) {
        val vm = hiltViewModel<CombineKeyEventSelectorViewModel>()
        LaunchedEffect(vm) {
            vm.initFrom(currentAction)
        }
        val state by vm.state.collectAsState()

        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(LocalI18N.current[I18N_R.UI_ACTION_INJECT_COMBINE_KEYCODE]) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    setResult(RESULT_CANCELED)
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val action = Action.InjectCombineKeyCode(
                                        codes = state.keys
                                    )
                                    setResultAndFinish(action)
                                }) {
                                    RemixIcon(remixName = Remix.System.check_line)
                                }

                            },
                        )
                    }) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .padding(paddingValues)
                            .animateContentSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        state.keys.forEachIndexed { index, key ->
                            ListItem(
                                title = getLabelForKeyCode(LocalI18N.current, key),
                                action1 = {
                                    IconButton(onClick = {
                                        vm.removeKeyAt(index)
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                            )
                        }

                        StandardSpacer()


                        val keycodeSelectorState = rememberKeyEventSelectorState(selected = {
                            vm.addKey(it)
                        })
                        KeyEventSelector(state = keycodeSelectorState)
                        androidx.compose.material3.Button(onClick = {
                            keycodeSelectorState.show()
                        }) {
                            Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
                        }
                        StandardSpacer()
                    }
                }
            }
        }
    }
}