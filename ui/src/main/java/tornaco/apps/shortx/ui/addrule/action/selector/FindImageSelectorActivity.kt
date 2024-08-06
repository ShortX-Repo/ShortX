package tornaco.apps.shortx.ui.addrule.action.selector

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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.addrule.ImagePreviewDialog
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.rememberImagePreviewDialogState
import tornaco.apps.shortx.ui.base.Base64ImageIcon
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.SmallSpacer
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
        onBackPressed = {},
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
        }
    }
}

@Composable
fun ImageSourceSelector(
    image: Action.FindImagePosition.ImageSource?,
    selectorMenuTitle: String,
    canSelectScreenshot: Boolean,
    onSelectSrc: (Action.FindImagePosition.ImageSource) -> Unit
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
                        onSelectSrc(Action.FindImagePosition.ImageSource.Base64(base64))
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
        onSelectSrc(Action.FindImagePosition.ImageSource.Path(it))
    }
    TextInputDialog(state = pathInputDialogState)

    val srcMenuState = rememberMenuDialogState<String>(
        title = i18N[selectorMenuTitle],
        menuItems = listOfNotNull(
            if (canSelectScreenshot) MenuDialogItem(
                id = Action.FindImagePosition.ImageSource.Screenshot::class.java.name,
                title = i18N["ui.action.find.image.src.screenshot"]
            ) else null,
            MenuDialogItem(
                id = Action.FindImagePosition.ImageSource.Path::class.java.name,
                title = i18N["ui.action.find.image.src.path"]
            ),
            MenuDialogItem(
                id = Action.FindImagePosition.ImageSource.Base64::class.java.name,
                title = i18N["ui.action.find.image.src.select"]
            ),
        ),
        onItemSelected = { _, id ->
            when (id) {
                Action.FindImagePosition.ImageSource.Screenshot::class.java.name -> {
                    onSelectSrc(Action.FindImagePosition.ImageSource.Screenshot)
                }

                Action.FindImagePosition.ImageSource.Path::class.java.name -> {
                    pathInputDialogState.show()
                }

                Action.FindImagePosition.ImageSource.Base64::class.java.name -> {
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
        is Action.FindImagePosition.ImageSource.Base64 -> {
            ListItem(
                titleLines = listOf(i18N["ui.action.find.image.src.select"]),
                icon = {
                    RemixIcon(remixName = Remix.Media.image_fill)
                },
                onClick = {
                    imagePreviewState.show(image.base64)
                })
            Base64ImageIcon(
                image.base64,
                modifier = Modifier
                    .size(90.dp)
                    .clickable {
                        imagePreviewState.show(image.base64)
                    },
                false
            )
        }

        is Action.FindImagePosition.ImageSource.Path -> {
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

        Action.FindImagePosition.ImageSource.Screenshot -> {
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