@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addda


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.UnderlineSmallText
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

class NewFuncParameterState(
    val onSelected: (FuncParameter) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var defaultValue by mutableStateOf("")
    var comments by mutableStateOf("")
    var isRequired by mutableStateOf(false)

    fun show(current: FuncParameter? = null) {
        this.name = current?.name ?: ""
        this.defaultValue = current?.defaultValue ?: ""
        this.comments = current?.comments ?: ""
        this.isRequired = current?.isRequired ?: false

        show()
    }
}

@Composable
fun rememberNewFuncParameterState(
    onSelected: (FuncParameter) -> Unit
): NewFuncParameterState {
    return remember {
        NewFuncParameterState(onSelected)
    }
}

@Composable
fun NewFunctionParameterDialog(state: NewFuncParameterState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier, topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = LocalI18N.current["ui.da.params"]) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    state.dismiss()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (state.name.isNotBlank() && (!state.isRequired || state.defaultValue.isNotEmpty())) {
                                        state.onSelected(
                                            FuncParameter.newBuilder()
                                                .setName(state.name)
                                                .setDefaultValue(state.defaultValue)
                                                .setComments(state.comments)
                                                .setIsRequired(state.isRequired)
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
                        ClickOutsideClearFocusBox {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(paddingValues)
                                    .padding(16.dp)
                            ) {
                                val context = LocalContext.current
                                val i18N = LocalI18N.current

                                FieldTitle(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = LocalI18N.current["ui.da.params.name"]
                                )
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    value = state.name,
                                    showSymbolButton = false,
                                    onValueChange = {
                                        state.name = it
                                    })
                                SmallSpacer()
                                TextButton(onClick = {
                                    ClipboardUtils.copyToClipboard(
                                        context,
                                        "arg",
                                        "$FUNC_PARAM_REF_PREFIX${state.name}"
                                    )
                                    Toast.makeText(
                                        context,
                                        i18N["ui.common.copied.to.clipboard"],
                                        Toast.LENGTH_LONG
                                    ).show()
                                }) {
                                    UnderlineSmallText(
                                        text = LocalI18N.current["ui.da.params.access.example", mapOf(
                                            "arg" to "$FUNC_PARAM_REF_PREFIX${state.name}"
                                        )]
                                    )
                                }

                                FieldTitle(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = LocalI18N.current["ui.da.params.default.value"]
                                )
                                MediumSpacer()
                                ShortXTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 120.dp)
                                        .verticalScroll(rememberScrollState()),
                                    value = state.defaultValue,
                                    showSymbolButton = false,
                                    onValueChange = {
                                        state.defaultValue = it
                                    })

                                LargeSpacer()

                                FieldTitle(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .heightIn(max = 120.dp)
                                        .verticalScroll(rememberScrollState()),
                                    text = LocalI18N.current["ui.common.note"]
                                )
                                MediumSpacer()
                                ShortXTextField(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    value = state.comments,
                                    showSymbolButton = false,
                                    onValueChange = {
                                        state.comments = it
                                    })

                                LargeSpacer()

                                // isRequired
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FieldTitle(text = LocalI18N.current["ui.da.params.is.required"])
                                    ShortXSwitch(checked = state.isRequired, onCheckedChange = {
                                        state.isRequired = it
                                    })
                                }
                            }
                        }

                    }
                }

            }
        }
    }
}