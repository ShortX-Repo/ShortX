package tornaco.apps.shortx.ui.addrule.action.selector

import android.graphics.Rect
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.proto.toProtoRect
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
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
        BiometricVerifySelector(currentData)
    }
}


@Composable
private fun BaseSelectorActivity<Action.OcrDetect>.BiometricVerifySelector(currentAction: Action.OcrDetect?) {
    val vm = hiltViewModel<OCRSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.ocr.detect"],
        onBackPressed = {},
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

            when (val currentSrc = state.src) {
                is Action.OcrDetect.RectSrc.FromRect -> {
                    ListItem(
                        icon = {
                            RemixIcon(remixName = Remix.Edit.add_text_two)
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

                Action.OcrDetect.RectSrc.SelectScreenArea -> {
                    ListItem(
                        icon = {
                            RemixIcon(remixName = Remix.Graphics.rectangle)
                        },
                        titleLines = listOf(
                            i18N["ui.action.select.screen.area"],
                            i18N["ui.action.select.screen.area.tip"],
                        )
                    )
                }
            }

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
                ),
                onItemSelected = { _, id ->
                    when (id) {
                        Action.OcrDetect.RectSrc.SelectScreenArea::class.java.name -> {
                            vm.setSrc(Action.OcrDetect.RectSrc.SelectScreenArea)
                        }

                        Action.OcrDetect.RectSrc.FromRect::class.java.name -> {
                            rectInputDialogState.show(
                                Rect(392, 1089, 933, 157).flattenToString()
                            )
                        }
                    }
                }
            )
            MenuDialog(state = srcMenuState)

            Button(onClick = {
                srcMenuState.show()
            }) {
                RemixIcon(remixName = Remix.Edit.edit)
            }
        }
    }
}