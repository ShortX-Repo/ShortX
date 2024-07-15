package tornaco.apps.shortx.ui.addrule.action.selector

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard

@AndroidEntryPoint
class PhotoSelectorActivity : BaseActivity() {
    @Composable
    override fun Content() {
        val selectedImages: MutableState<List<Uri>> = remember {
            mutableStateOf(emptyList())
        }
        ShortXAppBarScaffold(title = "Select photo",
            onBackPressed = {
                setResult(RESULT_CANCELED)
                finish()
            },
            actions = {
                IconButton(
                    enabled = selectedImages.value.isNotEmpty(),
                    onClick = {
                        setResult(RESULT_OK, Intent().apply {
                            setData(selectedImages.value[0])
                        })
                        finish()
                    }) {
                    RemixIcon(remixName = Remix.System.check_line)
                }
            }) {
            PhotoSelectorView(selectedImages)
        }
    }
}

@Composable
fun PhotoSelectorView(selectedImages: MutableState<List<Uri>>, maxSelectionCount: Int = 1) {
    val buttonText = if (maxSelectionCount > 1) {
        "Select up to $maxSelectionCount photos"
    } else {
        "Select a photo"
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImages.value = listOfNotNull(uri) }
    )

    // I will start this off by saying that I am still learning Android development:
    // We are tricking the multiple photos picker here which is probably not the best way,
    // if you know of a better way to implement this feature drop a comment and let me know
    // how to improve this design
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = if (maxSelectionCount > 1) {
                maxSelectionCount
            } else {
                2
            }
        ),
        onResult = { uris -> selectedImages.value = uris }
    )

    fun launchPhotoPicker() {
        if (maxSelectionCount > 1) {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            launchPhotoPicker()
        }) {
            Text(buttonText)
        }

        ImageLayoutView(selectedImages = selectedImages.value)
    }
}

@Composable
fun ImageLayoutView(selectedImages: List<Uri?>) {
    ShortXCard {
        LazyRow {
            items(selectedImages) { uri ->
                com.skydoves.landscapist.glide.GlideImage(
                    imageModel = { uri },
                )
            }
        }
    }
}