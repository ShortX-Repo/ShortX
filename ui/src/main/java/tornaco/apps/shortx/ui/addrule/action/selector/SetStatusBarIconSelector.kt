@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class SetStatusBarIconSelectorActivity : BaseSelectorActivity<Action.SetStatusBarIcon>() {
    @Composable
    override fun BaseSelectorActivity<Action.SetStatusBarIcon>.SelectorContent(currentData: Action.SetStatusBarIcon?) {
        SetStatusBarIconSelector(currentData)
    }
}

@Composable
private fun SetStatusBarIconSelectorActivity.SetStatusBarIconSelector(currentAction: Action.SetStatusBarIcon?) {
    val vm = viewModel<SetStatusBarIconSelectorViewModel>()
    LaunchedEffect(vm) {
        currentAction?.let { vm.initFrom(it) }
    }
    val state by vm.state.collectAsState()

    ShortXTheme {
        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(LocalI18N.current[I18N_R.UI_ACTION_SET_STATUSBAR_ICON]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            cancelAndFinish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            setResultAndFinish(
                                Action.SetStatusBarIcon(
                                    slot = state.slot.fallbackOnEmpty { "ShortX" },
                                    icon = state.icon
                                )
                            )
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
                TipRow(text = LocalI18N.current[I18N_R.UI_ACTION_SET_STATUSBAR_ICON_SLOT_SUMMARY])
                SmallSpacer()
                ShortXTextField(modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = LocalI18N.current[I18N_R.UI_ACTION_SET_STATUSBAR_ICON_SLOT])
                    },
                    value = state.slot,
                    onValueChange = {
                        vm.updateSlot(it)
                    })
                StandardSpacer()
                StandardSpacer()

                FieldTitle(text = "Icon")
                val context = LocalContext.current
                val iconLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        val icon = result.popupSelectorResult<MDIcon>()
                        icon?.let {
                            vm.updateIcon(it.iconNameOrBase64)
                        }
                    }
                )

                IconButton(onClick = {
                    iconLauncher.launch(
                        BaseSelectorActivity.newIntent(
                            context,
                            null,
                            MDIconSelectorActivity::class.java
                        )
                    )
                }) {
                    RemixIcon(state.icon)
                }
            }
        }
    }
}

