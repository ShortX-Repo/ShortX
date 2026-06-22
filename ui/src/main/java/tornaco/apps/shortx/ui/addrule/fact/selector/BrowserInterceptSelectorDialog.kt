@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.i18n.I18N_R
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody

class BrowserInterceptSelectorState(
    val onSelected: (Fact.BrowserIntercept) -> Unit
) : CommonDialogState() {
    var openUrlAfterTrigger by mutableStateOf(false)
    var openUrlBrowserPkg by mutableStateOf("")

    fun show(current: Fact.BrowserIntercept? = null) {
        this.openUrlAfterTrigger = current?.openUrlAfterTrigger ?: false
        this.openUrlBrowserPkg = current?.openUrlBrowserPkg ?: ""
        super.show()
    }
}

@Composable
fun rememberBrowserInterceptSelectorState(
    onSelected: (Fact.BrowserIntercept) -> Unit
): BrowserInterceptSelectorState {
    return remember {
        BrowserInterceptSelectorState(onSelected)
    }
}

@Composable
fun BrowserInterceptSelectorDialog(state: BrowserInterceptSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            BrowserInterceptSelector(state)
        }
    }
}

@Composable
private fun BrowserInterceptSelector(state: BrowserInterceptSelectorState) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = {
                    Text(
                        text = i18N.get(
                            key = I18N_R.UI_FACT_BROWSER_INTERCEPT,
                            fallback = "Browser link intercept"
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { state.dismiss() }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.onSelected(
                            Fact.BrowserIntercept(
                                openUrlAfterTrigger = state.openUrlAfterTrigger,
                                openUrlBrowserPkg = state.openUrlBrowserPkg
                            )
                        )
                        state.dismiss()
                    }) {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                    }
                }
            )
        }
    ) { paddingValues ->
        val singleAppSelectorState = rememberSingleAppPkgSelectorState {
            state.openUrlBrowserPkg = it.pkg.pkgName
        }
        SingleAppSelectorDialog(state = singleAppSelectorState)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                title = i18N.get(
                    key = I18N_R.UI_FACT_BROWSER_INTERCEPT_AUTO_OPEN,
                    fallback = "Open URL after trigger"
                ),
                checked = state.openUrlAfterTrigger,
                onClick = { state.openUrlAfterTrigger = !state.openUrlAfterTrigger },
                onCheckedChange = { state.openUrlAfterTrigger = it }
            )

            AnimatedVisibility(visible = state.openUrlAfterTrigger) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    StandardSpacer()

                    ShortXTextField(
                        value = state.openUrlBrowserPkg,
                        onValueChange = { state.openUrlBrowserPkg = it },
                        label = {
                            Text(
                                i18N.get(
                                    key = I18N_R.UI_FACT_BROWSER_INTERCEPT_BROWSER_PKG,
                                    fallback = "Browser package name (optional, blank = system default)"
                                )
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { singleAppSelectorState.show() }) {
                                RemixIcon(remixName = Remix.System.search_2_line)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            StandardSpacer()

            TipBody(
                text = i18N.get(
                    key = I18N_R.UI_FACT_BROWSER_INTERCEPT_HINT,
                    fallback = "If you disable this option, you can also add an \"Open browser link\" action in the rule's action list to open the corresponding URL."
                )
            )
        }
    }
}
