package tornaco.apps.shortx.ui.addrule.action.selector.cv

import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.FindPointsByColorOutputType
import tornaco.apps.shortx.core.proto.toProtoRect
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.FindPointsByColorConfig
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.gesture.ScreenRectPickerActivity
import tornaco.apps.shortx.ui.util.toComposeColor
import tornaco.apps.shortx.core.proto.common.Rect as ProtoRect
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class FindColorSelectorActivity : BaseSelectorActivity<FindPointsByColorConfig>() {
    companion object {
        const val EXTRA_SHOW_OUTPUT_TYPE = "find_color.show_output_type"
    }

    @Composable
    override fun BaseSelectorActivity<FindPointsByColorConfig>.SelectorContent(currentData: FindPointsByColorConfig?) {
        Selector(
            currentAction = currentData,
            showOutputTypeSelector = intent.getBooleanExtra(EXTRA_SHOW_OUTPUT_TYPE, false),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<FindPointsByColorConfig>.Selector(
    currentAction: FindPointsByColorConfig?,
    showOutputTypeSelector: Boolean,
) {
    val vm = hiltViewModel<FindColorSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    val screenRectPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val rect = result.data?.getSerializableExtra("rect") as ProtoRect?
            if (rect != null) {
                vm.setRect(rect)
            }
        }
    }

    ShortXAppBarScaffold(
        title = LocalI18N.current[I18N_R.UI_ACTION_FIND_COLOR],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(vm.toActionOrNull())
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    )
    {
        Column(Modifier.padding(horizontal = 16.dp)) {
            val i18N = LocalI18N.current
            val context = LocalContext.current

            FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_FIND_COLOR_IMAGE])
            SmallSpacer()
            ImageSourceSelector(state.image, i18N[I18N_R.UI_ACTION_FIND_COLOR_IMAGE], true) {
                vm.setImage(it)
            }

            SectionSpacer()

            if (showOutputTypeSelector) {
                FieldTitle(text = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TYPE])
                SmallSpacer()
                val outputTypeMenuState = rememberMenuDialogState<String>(
                    title = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TYPE],
                    menuItems = listOf(
                        MenuDialogItem(
                            id = FindPointsByColorOutputType.FindPointsByColorOutputType_Text.name,
                            title = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TEXT],
                            summary = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TEXT_SUMMARY],
                        ),
                        MenuDialogItem(
                            id = FindPointsByColorOutputType.FindPointsByColorOutputType_Json.name,
                            title = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_JSON],
                            summary = i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_JSON_SUMMARY],
                        ),
                    ),
                    onItemSelected = { _, id ->
                        when (id) {
                            FindPointsByColorOutputType.FindPointsByColorOutputType_Text.name -> {
                                vm.setOutputType(FindPointsByColorOutputType.FindPointsByColorOutputType_Text)
                            }

                            FindPointsByColorOutputType.FindPointsByColorOutputType_Json.name -> {
                                vm.setOutputType(FindPointsByColorOutputType.FindPointsByColorOutputType_Json)
                            }
                        }
                    }
                )
                MenuDialog(state = outputTypeMenuState)

                ListItem(
                    icon = {
                        ColoredIcon {
                            RemixIcon(remixName = Remix.Document.file_text_line)
                        }
                    },
                    titleLines = listOf(
                        i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TYPE],
                        when (state.outputType) {
                            FindPointsByColorOutputType.FindPointsByColorOutputType_Text ->
                                i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TEXT]

                            else -> i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_JSON]
                        },
                        when (state.outputType) {
                            FindPointsByColorOutputType.FindPointsByColorOutputType_Text ->
                                i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_TEXT_SUMMARY]

                            else -> i18N[I18N_R.UI_ACTION_FIND_COLOR_OUTPUT_JSON_SUMMARY]
                        },
                    ),
                    onClick = { outputTypeMenuState.show() }
                )

                StandardSpacer()
            }

            FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_FIND_COLOR_COLOR])
            SmallSpacer()
            val colorPickerState = remember(state) {
                ColorPickerDialogState(
                    colorInt = state.color,
                    onColorChanged = {
                        vm.setColor(it)
                    })
            }
            ColorPickerDialog(state = colorPickerState)
            ShortXCard(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(64.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(20.dp)
                    ),
                backgroundColor = state.color.toComposeColor(),
                onClick = {
                    colorPickerState.show()
                }
            ) {

            }

            SectionSpacer()
            FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_FIND_COLOR_RECT])
            SmallSpacer()

            // Rect输入框
            var rectInputText by remember(state.rect) {
                mutableStateOf(
                    if (state.rect == ProtoRect.getDefaultInstance()) {
                        ""
                    } else {
                        "${state.rect.left} ${state.rect.top} ${state.rect.right} ${state.rect.bottom}"
                    }
                )
            }

            fun parseRectFromInput(): ProtoRect? {
                return runCatching { Rect.unflattenFromString(rectInputText) }.getOrNull()
                    ?.toProtoRect()
            }

            ShortXTextField(
                value = rectInputText,
                onValueChange = {
                    rectInputText = it
                    val parsedRect = parseRectFromInput()
                    if (parsedRect != null) {
                        vm.setRect(parsedRect)
                    } else if (it.isEmpty()) {
                        vm.setRect(ProtoRect.getDefaultInstance())
                    }
                },
                label = {
                    Text(
                        i18N[I18N_R.UI_ACTION_FIND_COLOR_RECT_INPUT_LABEL]
                    )
                },
                placeholder = { Text("例如: 100 200 300 400，留空表示全屏") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = rectInputText.isNotEmpty() && parseRectFromInput() == null
            )

            if (rectInputText.isNotEmpty() && parseRectFromInput() == null) {
                Text(
                    text = i18N[I18N_R.UI_ACTION_FIND_COLOR_RECT_INPUT_ERROR],
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            SmallSpacer()

            // 添加选择器按钮
            TextButton(
                onClick = {
                    screenRectPickerLauncher.launch(
                        ScreenRectPickerActivity.start(context)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(i18N[I18N_R.UI_ACTION_FIND_COLOR_RECT_PICKER])
            }

            SmallSpacer()
            OutlinedButton(
                onClick = {
                    composeLogger.d("showScreenRectHint ${state.rect}")
                    shortXManager.showScreenRectHint(state.rect)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(i18N[I18N_R.UI_FACT_EDGE_GESTURE_SHOW_SELECTED_AREA])
            }
        }
    }
}