@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class BiometricVerifySelectorActivity : BaseSelectorActivity<Action.BiometricVerify>() {
    @Composable
    override fun BaseSelectorActivity<Action.BiometricVerify>.SelectorContent(currentData: Action.BiometricVerify?) {
        BiometricVerifySelector(currentData)
    }
}

@Composable
private fun BaseSelectorActivity<Action.BiometricVerify>.BiometricVerifySelector(currentAction: Action.BiometricVerify?) {
    val vm = hiltViewModel<BiometricVerifySelectorViewModel>()
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
                        title = { Text(LocalI18N.current["ui.action.biometric.verify"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                setResultAndFinish(vm.getAction())
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
                    FieldTitle(
                        modifier = Modifier.fillMaxWidth(),
                        text = LocalI18N.current["ui.action.biometric.verify.title"]
                    )
                    MediumSpacer()
                    ShortXTextField(
                        value = state.title,
                        onValueChange = {
                            vm.updateTitle(it)
                        }, showSymbolButton = true
                    )
                    StandardSpacer()

                    FieldTitle(
                        modifier = Modifier.fillMaxWidth(),
                        text = LocalI18N.current["ui.action.biometric.verify.subtitle"]
                    )
                    MediumSpacer()
                    ShortXTextField(
                        value = state.subtitle,
                        onValueChange = {
                            vm.updateSubtitle(it)
                        }, showSymbolButton = true
                    )
                    StandardSpacer()
                    StandardSpacer()


                    val allowHandler = remember(vm) { vm.allowedActionHandler }
                    val allowState by allowHandler.state.collectAsState()
                    FieldTitle(text = LocalI18N.current["ui.action.biometric.verify.allow.action"])
                    MediumSpacer()
                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        actions = allowState.actions,
                        evaluatingActions = state.evaluatingActions,
                        warnings = emptyMap(),
                        handler = allowHandler.asActionHandler()
                    )
                    StandardSpacer()
                    StandardSpacer()

                    val denyHandler = remember(vm) { vm.deniedActionHandler }
                    val denyState by denyHandler.state.collectAsState()
                    FieldTitle(text = LocalI18N.current["ui.action.biometric.verify.deny.action"])
                    MediumSpacer()
                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        actions = denyState.actions,
                        evaluatingActions = state.evaluatingActions,
                        warnings = emptyMap(),
                        handler = denyHandler.asActionHandler()
                    )
                    StandardSpacer()

                    SectionSpacer()
                    SectionSpacer()
                    SuggestedNavEntry()

                }
            }
        }
    }

}