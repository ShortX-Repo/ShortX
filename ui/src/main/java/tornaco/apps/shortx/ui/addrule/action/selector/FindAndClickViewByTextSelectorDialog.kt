package tornaco.apps.shortx.ui.addrule.action.selector


import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.FindTextMethod
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.proto.toProtoRect
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import javax.inject.Inject

@AndroidEntryPoint
class FindAndClickViewByTextSelectorActivity :
    BaseSelectorActivity<Action.FindAndClickViewByText>() {
    @Composable
    override fun BaseSelectorActivity<Action.FindAndClickViewByText>.SelectorContent(currentData: Action.FindAndClickViewByText?) {
        val vm = hiltViewModel<FindAndClickViewByTextSelectorVM>()
        LaunchedEffect(vm) {
            vm.initFrom(currentData)
        }

        val state by vm.state.collectAsState()

        ShortXAppBarScaffold(
            title = LocalI18N.current["ui.action.find.click.view.by.text"],
            onBackPressed = {},
            actions = {
                IconButton(onClick = {
                    setResultAndFinish(
                        Action.FindAndClickViewByText(
                            id = state.id,
                            text = state.text,
                            isRegex = state.isRegex,
                            timeout = state.timeout,
                            rect = state.rect,
                            method = state.method
                        )
                    )
                }) {
                    Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                FieldTitle(
                    text = LocalI18N.current["ui.action.find.click.view.by.text.text"]
                )
                TinySpacer()
                ShortXTextField(modifier = Modifier.fillMaxWidth(),
                    value = state.text,
                    onValueChange = {
                        vm.setText(it)
                    })
                StandardSpacer()

                FieldTitle(
                    text = LocalI18N.current["ui.action.find.click.view.by.text.is.regex"]
                )
                TinySpacer()
                ShortXSwitch(
                    checked = state.isRegex,
                    onCheckedChange = { vm.setIsRegex(it) })
                StandardSpacer()


                val methodMenuState = rememberMenuDialogState<String>(
                    title = LocalI18N.current["ui.action.find.text.method"],
                    menuItems = listOf(
                        FindTextMethod.FTM_UI_AUTO,
                        FindTextMethod.FTM_OCR,
                        FindTextMethod.FTM_UI_AUTO_OCR,
                    ).map {
                        MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
                    },
                    onItemSelected = { _, id ->
                        val method = FindTextMethod.valueOf(id)
                        vm.setMethod(method)
                    }
                )
                MenuDialog(state = methodMenuState)
                Button(onClick = { methodMenuState.show() }) {
                    Text(text = LocalI18N.current["ui.action.find.text.method"] + " ${LocalI18N.current[state.method.labelKey()]}")
                }
                StandardSpacer()

                AnimatedVisibility(visible = state.method != FindTextMethod.FTM_OCR) {
                    Column {
                        FieldTitle(
                            text = LocalI18N.current["ui.action.find.click.view.by.text.timeout"]
                        )
                        TipBody(LocalI18N.current["ui.action.find.click.view.by.text.timeout.description"])
                        TinySpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.timeout.toString(),
                            onValueChange = {
                                if (it.isDigitsOnly()) {
                                    vm.setTimeout(kotlin.runCatching { it.toLong() }
                                        .getOrElse { 0 })
                                }
                            },
                            showSymbolButton = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        StandardSpacer()
                    }
                }

                AnimatedVisibility(visible = state.method != FindTextMethod.FTM_UI_AUTO) {
                    Column {
                        FieldTitle(
                            text = LocalI18N.current["ui.action.rect.src.rect"]
                        )
                        TipBody(LocalI18N.current["ui.action.rect.src.rect.format"])
                        TinySpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.rect.toAndroidRect().flattenToString(),
                            onValueChange = {
                                val checked =
                                    android.graphics.Rect.unflattenFromString(it)?.toProtoRect()
                                checked?.let {
                                    vm.setRect(checked)
                                }
                            },
                        )
                        StandardSpacer()
                    }
                }

            }
        }
    }
}


@HiltViewModel
class FindAndClickViewByTextSelectorVM @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FindAndClickViewByTextSelectorState>(
        context, {
            FindAndClickViewByTextSelectorState()
        }
    ) {
    fun initFrom(settings: Action.FindAndClickViewByText?) {
        logger.d("initFrom: $settings")

        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    text = it.text,
                    isRegex = it.isRegex,
                    timeout = it.timeout,
                    rect = it.rect,
                    method = it.method
                )
            }
        }
    }

    fun setText(it: String) {
        updateState {
            copy(text = it)
        }
    }

    fun setIsRegex(it: Boolean) {
        updateState {
            copy(isRegex = it)
        }
    }

    fun setTimeout(timeout: Long) {
        updateState {
            copy(timeout = timeout)
        }
    }

    fun setRect(checked: Rect) {
        updateState {
            copy(rect = checked)
        }
    }

    fun setMethod(method: FindTextMethod) {
        updateState {
            copy(method = method)
        }
    }

}

data class FindAndClickViewByTextSelectorState(
    val id: String = defaultNewActionId(),
    val text: String = "",
    val isRegex: Boolean = false,
    val timeout: Long = 3000,
    val rect: Rect = Rect.getDefaultInstance(),
    val method: FindTextMethod = FindTextMethod.FTM_UI_AUTO
)