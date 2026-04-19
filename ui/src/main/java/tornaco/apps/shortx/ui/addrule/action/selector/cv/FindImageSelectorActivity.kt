package tornaco.apps.shortx.ui.addrule.action.selector.cv

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.action.FindImagePositionOutputType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.addrule.ImagePreviewDialog
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.ImageSource
import tornaco.apps.shortx.ui.addrule.action.selector.PhotoSelectorActivity
import tornaco.apps.shortx.ui.addrule.rememberImagePreviewDialogState
import tornaco.apps.shortx.ui.base.Base64ImageIcon
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState

@AndroidEntryPoint
class FindImageSelectorActivity : BaseSelectorActivity<Action.FindImagePosition>() {
    @Composable
    override fun BaseSelectorActivity<Action.FindImagePosition>.SelectorContent(currentData: Action.FindImagePosition?) {
        Selector(currentAction = currentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.FindImagePosition>.Selector(currentAction: Action.FindImagePosition?) {
    val vm = hiltViewModel<FindImageSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }

    val state by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.find.image"],
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                vm.toActionOrNull()?.let {
                    setResultAndFinish(it)
                }
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    )
    {
        Column(Modifier.padding(horizontal = 16.dp)) {
            val i18N = LocalI18N.current

            FieldTitle(text = LocalI18N.current["ui.action.find.image.image"])
            SmallSpacer()
            ImageSourceSelector(state.image, i18N["ui.action.find.image.image"], true) {
                vm.setImage(it)
            }

            SectionSpacer()

            FieldTitle(text = LocalI18N.current["ui.action.find.image.template"])
            SmallSpacer()
            ImageSourceSelector(state.template, i18N["ui.action.find.image.template"], false) {
                vm.setTemplate(it)
            }

            StandardSpacer()

            FieldTitle(text = i18N["ui.action.find.image.output.type"])
            SmallSpacer()
            val outputTypeMenuState = rememberMenuDialogState<String>(
                title = i18N["ui.action.find.image.output.type"],
                menuItems = listOf(
                    MenuDialogItem(
                        id = FindImagePositionOutputType.FindImagePositionOutputType_Text.name,
                        title = i18N["ui.action.find.image.output.text"],
                        summary = i18N["ui.action.find.image.output.text.summary"],
                    ),
                    MenuDialogItem(
                        id = FindImagePositionOutputType.FindImagePositionOutputType_Json.name,
                        title = i18N["ui.action.find.image.output.json"],
                        summary = i18N["ui.action.find.image.output.json.summary"],
                    ),
                ),
                onItemSelected = { _, id ->
                    when (id) {
                        FindImagePositionOutputType.FindImagePositionOutputType_Text.name -> {
                            vm.setOutputType(FindImagePositionOutputType.FindImagePositionOutputType_Text)
                        }

                        FindImagePositionOutputType.FindImagePositionOutputType_Json.name -> {
                            vm.setOutputType(FindImagePositionOutputType.FindImagePositionOutputType_Json)
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
                    i18N["ui.action.find.image.output.type"],
                    when (state.outputType) {
                        FindImagePositionOutputType.FindImagePositionOutputType_Text ->
                            i18N["ui.action.find.image.output.text"]

                        else -> i18N["ui.action.find.image.output.json"]
                    },
                    when (state.outputType) {
                        FindImagePositionOutputType.FindImagePositionOutputType_Text ->
                            i18N["ui.action.find.image.output.text.summary"]

                        else -> i18N["ui.action.find.image.output.json.summary"]
                    },
                ),
                onClick = { outputTypeMenuState.show() }
            )
        }
    }
}

@Composable
fun ImageSourceSelector(
    image: ImageSource?,
    selectorMenuTitle: String,
    canSelectScreenshot: Boolean,
    onSelectSrc: (ImageSource) -> Unit
) {
    val i18N = LocalI18N.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = result.data?.data
            uri?.let {
                scope.launch {
                    runCatching {
                        val base64 =
                            context.contentResolver.openInputStream(uri).use { rawStream ->
                                BitmapUtils.bitmapToBase64(BitmapFactory.decodeStream(rawStream))
                            }
                        onSelectSrc(ImageSource.Base64(base64))
                    }.onFailure {
                        TextDisplayActivity.start(context, "OOPS! WTF!", it.stackTraceToString())
                    }
                }
            }
        }
    )

    val pathInputDialogState = rememberTextInputState(
        title = i18N["ui.action.find.image.src.path"],
        message = null
    ) {
        onSelectSrc(ImageSource.Path(it))
    }
    TextInputDialog(state = pathInputDialogState)

    val srcMenuState = rememberMenuDialogState<String>(
        title = i18N[selectorMenuTitle],
        menuItems = listOfNotNull(
            if (canSelectScreenshot) MenuDialogItem(
                id = ImageSource.Screenshot::class.java.name,
                title = i18N["ui.action.find.image.src.screenshot"]
            ) else null,
            MenuDialogItem(
                id = ImageSource.Path::class.java.name,
                title = i18N["ui.action.find.image.src.path"]
            ),
            MenuDialogItem(
                id = ImageSource.Base64::class.java.name,
                title = i18N["ui.action.find.image.src.select"]
            ),
        ),
        onItemSelected = { _, id ->
            when (id) {
                ImageSource.Screenshot::class.java.name -> {
                    onSelectSrc(ImageSource.Screenshot)
                }

                ImageSource.Path::class.java.name -> {
                    pathInputDialogState.show()
                }

                ImageSource.Base64::class.java.name -> {
                    selectPhotoLauncher.launch(
                        Intent(context, PhotoSelectorActivity::class.java)
                    )
                }
            }
        }
    )
    MenuDialog(state = srcMenuState)

    val imagePreviewState = rememberImagePreviewDialogState {
        selectPhotoLauncher.launch(
            Intent(context, PhotoSelectorActivity::class.java)
        )
    }
    ImagePreviewDialog(state = imagePreviewState)

    when (image) {
        is ImageSource.Base64 -> {
            ListItem(
                titleLines = listOf(i18N["ui.action.find.image.src.select"]),
                icon = {
                    RemixIcon(remixName = Remix.Media.image_fill)
                },
                onClick = {
                    imagePreviewState.show(image.base64)
                })
            Base64ImageIcon(
                base64Str = image.base64,
                modifier = Modifier
                    .size(90.dp)
                    .clickable {
                        imagePreviewState.show(image.base64)
                    },
                clipToCircleShape = false
            )
        }

        is ImageSource.Path -> {
            ListItem(
                titleLines = listOf(
                    i18N["ui.action.find.image.src.path"],
                    image.path
                ),
                icon = {
                    RemixIcon(remixName = Remix.Document.file_fill)
                },
                onClick = {
                    pathInputDialogState.show(image.path)
                }
            )
        }

        ImageSource.Screenshot -> {
            ListItem(
                titleLines = listOf(i18N["ui.action.find.image.src.screenshot"]),
                icon = {
                    RemixIcon(remixName = Remix.Device.smartphone_fill)
                },
                onClick = {
                    srcMenuState.show()
                }
            )
        }

        else -> {
            ListItem(
                titleLines = listOf(i18N["ui.common.unselected"]),
                onClick = {
                    srcMenuState.show()
                })
        }
    }

    Button(onClick = {
        srcMenuState.show()
    }) {
        Text(text = i18N["ui.common.edit"])
    }
}