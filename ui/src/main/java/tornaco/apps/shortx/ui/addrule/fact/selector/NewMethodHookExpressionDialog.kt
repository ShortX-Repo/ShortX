@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.context.ContextDataMapping.asAccessExample
import tornaco.apps.shortx.core.proto.fact.MethodHookExpressions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.UnderlineSmallText
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.core.i18n.I18N_R

class NewMethodHookExpressionState(
    val onSelected: (MethodHookExpressions) -> Unit
) : CommonDialogState() {
    var contextKey by mutableStateOf("")
    var expression by mutableStateOf("")

    fun show(current: MethodHookExpressions? = null) {
        this.contextKey = current?.contextKey ?: ""
        this.expression = current?.expressionMVEL ?: ""

        show()
    }
}

@Composable
fun rememberNewMethodHookExpressionState(
    onSelected: (MethodHookExpressions) -> Unit
): NewMethodHookExpressionState {
    return remember {
        NewMethodHookExpressionState(onSelected)
    }
}

@Composable
fun NewMethodHookExpressionDialog(state: NewMethodHookExpressionState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier, topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = "Expression") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    state.dismiss()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (state.contextKey.isNotBlank() && state.expression.isNotBlank()) {
                                        state.onSelected(
                                            MethodHookExpressions.newBuilder()
                                                .setContextKey(state.contextKey)
                                                .setExpressionMVEL(state.expression)
                                                .build()
                                        )
                                        state.dismiss()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Done"
                                    )
                                }
                            },
                        )
                    }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            val context = LocalContext.current
                            val i18N = LocalI18N.current

                            FieldTitle(
                                modifier = Modifier.padding(vertical = 12.dp),
                                text = "Context data key"
                            )
                            SmallSpacer()
                            ShortXTextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = state.contextKey,
                                showSymbolButton = false,
                                onValueChange = {
                                    state.contextKey = it
                                })
                            SmallSpacer()
                            TextButton(onClick = {
                                ClipboardUtils.copyToClipboard(
                                    context,
                                    "key",
                                    "{${state.contextKey}}"
                                )
                                Toast.makeText(
                                    context,
                                    i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                                    Toast.LENGTH_LONG
                                ).show()
                            }) {
                                UnderlineSmallText(
                                    text = LocalI18N.current[I18N_R.UI_CONTEXT_DATA_EXAMPLE, mapOf(
                                        "eg" to state.contextKey.asAccessExample()
                                    )]
                                )
                            }

                            FieldTitle(
                                modifier = Modifier.padding(vertical = 12.dp),
                                text = "MVEL expression"
                            )
                            MediumSpacer()
                            ShortXTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                                    .verticalScroll(rememberScrollState()),
                                value = state.expression,
                                showSymbolButton = true,
                                onValueChange = {
                                    state.expression = it
                                })

                            LargeSpacer()
                        }
                    }
                }

            }
        }
    }
}
