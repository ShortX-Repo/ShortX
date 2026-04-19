package tornaco.apps.shortx.ui.addrule.action.selector

import android.graphics.Rect
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import tornaco.apps.shortx.core.proto.action.OcrDetectOutputType
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.proto.toProtoRect
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState

@AndroidEntryPoint
class OCRSelectorActivity : BaseSelectorActivity<Action.OcrDetect>() {
    @Composable
    override fun BaseSelectorActivity<Action.OcrDetect>.SelectorContent(currentData: Action.OcrDetect?) {
        Selector(currentData)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.OcrDetect>.Selector(currentAction: Action.OcrDetect?) {
    val vm = hiltViewModel<OCRSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.ocr.detect"],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(vm.getAction())
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    )
    {
        Column(Modifier.padding(horizontal = 16.dp)) {
            FieldTitle(text = LocalI18N.current["ui.action.rect.src"])
            SmallSpacer()

            val context = LocalContext.current
            val i18N = LocalI18N.current
            val showRectFormatError = {
                Toast.makeText(
                    context,
                    i18N["ui.action.rect.src.rect.format.error"],
                    Toast.LENGTH_SHORT
                ).show()
            }

            val rectInputDialogState = rememberTextInputState(
                title = i18N["ui.action.rect.src.rect"],
                message = i18N["ui.action.rect.src.rect.format"]
            ) {
                val rect = kotlin.runCatching {
                    Rect.unflattenFromString(it)
                }.getOrElse {
                    showRectFormatError()
                    null
                }
                rect?.let {
                    vm.setSrc(Action.OcrDetect.RectSrc.FromRect(rect = it.toProtoRect()))
                }
            }
            TextInputDialog(state = rectInputDialogState)

            val pathInputDialogState = rememberTextInputState(
                title = i18N["ui.action.rect.src.path"],
                message = null
            ) {
                vm.setSrc(Action.OcrDetect.RectSrc.FromPath(path = it))
            }
            TextInputDialog(state = pathInputDialogState)

            when (val currentSrc = state.src) {
                is Action.OcrDetect.RectSrc.FromRect -> {
                    ListItem(
                        icon = {
                            ColoredIcon {
                                RemixIcon(remixName = Remix.Edit.add_text_two)
                            }
                        },
                        titleLines = listOf(
                            i18N["ui.action.rect.src.rect"],
                            currentSrc.rect.toAndroidRect().flattenToString()
                        ),
                        onClick = {
                            rectInputDialogState.show(
                                currentSrc.rect.toAndroidRect().flattenToString()
                            )
                        }
                    )
                }

                is Action.OcrDetect.RectSrc.FromPath -> {
                    ListItem(
                        icon = {
                            ColoredIcon {
                                RemixIcon(remixName = Remix.Office.image_files)
                            }
                        },
                        titleLines = listOf(
                            i18N["ui.action.rect.src.path"],
                            currentSrc.path
                        ),
                        onClick = {
                            pathInputDialogState.show(
                                currentSrc.path
                            )
                        }
                    )
                }

                Action.OcrDetect.RectSrc.SelectScreenArea -> {
                    ListItem(
                        icon = {
                            ColoredIcon {
                                RemixIcon(remixName = Remix.Graphics.rectangle)
                            }
                        },
                        titleLines = listOf(
                            i18N["ui.action.select.screen.area"],
                            i18N["ui.action.select.screen.area.tip"],
                        )
                    )
                }

                Action.OcrDetect.RectSrc.FullScreen -> {
                    ListItem(
                        icon = {
                            ColoredIcon {
                                RemixIcon(remixName = Remix.Device.device_fill)
                            }
                        },
                        titleLines = listOf(
                            i18N["ui.action.rect.src.fullscreen"],
                        )
                    )
                }
            }

            StandardSpacer()

            FieldTitle(text = i18N["ui.action.ocr.output.type"])
            SmallSpacer()
            val outputTypeMenuState = rememberMenuDialogState<String>(
                title = i18N["ui.action.ocr.output.type"],
                menuItems = listOf(
                    MenuDialogItem(
                        id = OcrDetectOutputType.OcrDetectOutputType_Text.name,
                        title = i18N["ui.action.ocr.output.text"],
                        summary = i18N["ui.action.ocr.output.text.summary"],
                    ),
                    MenuDialogItem(
                        id = OcrDetectOutputType.OcrDetectOutputType_Json.name,
                        title = i18N["ui.action.ocr.output.json"],
                        summary = i18N["ui.action.ocr.output.json.summary"],
                    ),
                ),
                onItemSelected = { _, id ->
                    when (id) {
                        OcrDetectOutputType.OcrDetectOutputType_Text.name -> {
                            vm.setOutputType(OcrDetectOutputType.OcrDetectOutputType_Text)
                        }

                        OcrDetectOutputType.OcrDetectOutputType_Json.name -> {
                            vm.setOutputType(OcrDetectOutputType.OcrDetectOutputType_Json)
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
                    i18N["ui.action.ocr.output.type"],
                    when (state.outputType) {
                        OcrDetectOutputType.OcrDetectOutputType_Text ->
                            i18N["ui.action.ocr.output.text"]

                        else -> i18N["ui.action.ocr.output.json"]
                    },
                    when (state.outputType) {
                        OcrDetectOutputType.OcrDetectOutputType_Text ->
                            i18N["ui.action.ocr.output.text.summary"]

                        else -> i18N["ui.action.ocr.output.json.summary"]
                    },
                ),
                onClick = { outputTypeMenuState.show() }
            )

            StandardSpacer()

            val srcMenuState = rememberMenuDialogState<String>(
                title = i18N["ui.action.rect.src"],
                menuItems = listOf(
                    MenuDialogItem(
                        id = Action.OcrDetect.RectSrc.SelectScreenArea::class.java.name,
                        title = i18N["ui.action.rect.src.select.screen"]
                    ),
                    MenuDialogItem(
                        id = Action.OcrDetect.RectSrc.FromRect::class.java.name,
                        title = i18N["ui.action.rect.src.rect"]
                    ),
                    MenuDialogItem(
                        id = Action.OcrDetect.RectSrc.FromPath::class.java.name,
                        title = i18N["ui.action.rect.src.path"]
                    ),
                    MenuDialogItem(
                        id = Action.OcrDetect.RectSrc.FullScreen::class.java.name,
                        title = i18N["ui.action.rect.src.fullscreen"]
                    ),
                ),
                onItemSelected = { _, id ->
                    when (id) {
                        Action.OcrDetect.RectSrc.SelectScreenArea::class.java.name -> {
                            vm.setSrc(Action.OcrDetect.RectSrc.SelectScreenArea)
                        }

                        Action.OcrDetect.RectSrc.FullScreen::class.java.name -> {
                            vm.setSrc(Action.OcrDetect.RectSrc.FullScreen)
                        }

                        Action.OcrDetect.RectSrc.FromRect::class.java.name -> {
                            rectInputDialogState.show()
                        }

                        Action.OcrDetect.RectSrc.FromPath::class.java.name -> {
                            pathInputDialogState.show()
                        }
                    }
                }
            )
            MenuDialog(state = srcMenuState)

            Button(onClick = {
                srcMenuState.show()
            }) {
                Text(text = i18N["ui.common.edit"])
            }
        }
    }
}