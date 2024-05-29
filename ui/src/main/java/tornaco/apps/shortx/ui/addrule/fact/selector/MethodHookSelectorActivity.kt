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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.settings.log.hook.MethodHookLogActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class MethodHookSelectorActivity : BaseSelectorActivity<Fact.MethodHook>() {

    @Composable
    override fun BaseSelectorActivity<Fact.MethodHook>.SelectorContent(currentData: Fact.MethodHook?) {
        CombineKeyEventSelector(currentData)
    }

    @Composable
    private fun CombineKeyEventSelector(currentFact: Fact.MethodHook?) {
        val context = LocalContext.current
        val vm = hiltViewModel<MethodHookSelectorViewModel>()
        LaunchedEffect(vm) {
            vm.initFrom(currentFact)
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
                            title = { Text(LocalI18N.current["Method hook"]) },
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
                                    MethodHookLogActivity.Starter.start(context)
                                }) {
                                    RemixIcon(remixName = Remix.Document.file_text_line)
                                }

                                IconButton(onClick = {
                                    setResultAndFinish(state.toFact())
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

                        FieldTitle(text = LocalI18N.current["Package name"])
                        SmallSpacer()
                        ShortXTextField(
                            value = state.packageName,
                            onValueChange = { vm.setPackageName(it) })
                        StandardSpacer()

                        FieldTitle(text = LocalI18N.current["Class name"])
                        SmallSpacer()
                        ShortXTextField(
                            value = state.className,
                            onValueChange = { vm.setClassName(it) })
                        StandardSpacer()


                        FieldTitle(text = LocalI18N.current["Method name"])
                        SmallSpacer()
                        ShortXTextField(
                            value = state.methodName,
                            onValueChange = { vm.setMethodName(it) })
                        StandardSpacer()


                    }
                }
            }
        }
    }
}