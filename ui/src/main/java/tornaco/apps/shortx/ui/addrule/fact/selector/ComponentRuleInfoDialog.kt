package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer

class ComponentRuleInfoDialogState(
    val ruleLabel: String,
    val ruleUrl: String,
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberComponentRuleInfoDialogState(
    ruleLabel: String,
    ruleUrl: String,
): ComponentRuleInfoDialogState {
    return remember {
        ComponentRuleInfoDialogState(ruleLabel, ruleUrl)
    }
}

@Composable
fun ComponentRuleInfoDialog(state: ComponentRuleInfoDialogState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.ruleLabel)
        }, buttons = {
            TextButton(onClick = {
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            val vm = hiltViewModel<ComponentRuleInfoDialogViewModel>()
            LaunchedEffect(vm) {
                vm.load(state.ruleUrl)
            }
            val vmState by vm.state.collectAsState()
            when (val infoState = vmState.infoState) {
                is LoadState.Error -> {
                    Box(Modifier.padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        LoadError(Modifier.fillMaxWidth())
                    }
                }

                LoadState.Loading -> {
                    Box(Modifier.padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        Loading(Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Success -> {
                    Column {
                        MediumTitle(text = infoState.data.team ?: "")
                        Text(text = infoState.data.description)
                        StandardSpacer()
                        StandardSpacer()


                        if (infoState.data.contributors.isNotEmpty()) {
                            Column {
                                MediumTitle(text = "Contributors")
                                Row {
                                    infoState.data.contributors.forEach {
                                        OutlineBadge(text = it)
                                        StandardSpacer()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}