package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.squareup.picasso3.CropCircleTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.util.Logger

@Composable
fun ColoredIcon(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    padding: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = CircleShape,
    onClick: (() -> Unit)? = null,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .size(size)
            .clip(shape)
            .thenIf(Modifier.clickable {
                onClick?.invoke()
            }, onClick != null)
            .background(color = color)
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            icon()
        }
    }
}

@Composable
fun Icon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        modifier = modifier,
        imageVector = imageVector,
        tint = tint,
        contentDescription = "Icon"
    )
}

@Composable
fun RemixIcon(
    remixName: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val context = LocalContext.current

    val isBase64Icon = remixName.isBase64Image()
    if (isBase64Icon) {
        Base64ImageIcon(remixName, modifier)
    } else {
        val resId = remember(remixName) {
            context.remixResId(remixName)
        }
        androidx.compose.material3.Icon(
            modifier = modifier,
            painter = runCatching {
                painterResource(
                    id = resId
                )
            }.getOrElse {
                Logger("RemixIcon").e(it, "Fail on: $remixName")
                painterResource(id = github.tornaco.shortx.icons.R.drawable.ic_remix_bug_line)
            },
            tint = tint,
            contentDescription = "Icon"
        )
    }
}

@Composable
fun Base64ImageIcon(
    base64Str: String,
    modifier: Modifier = Modifier,
    clipToCircleShape: Boolean = true
) {
    val imageBitmap: State<ImageBitmap?> =
        rememberImageBitmapFromBase64(base64Str, clipToCircleShape)
    imageBitmap.value?.let {
        Box(
            modifier = modifier.thenIf(Modifier.clip(CircleShape), clipToCircleShape)
        ) {
            Image(
                bitmap = it,
                contentDescription = "Base64 Image",
                modifier = Modifier.fillMaxSize(),
            )
        }

    } ?: run {
        // You can show a placeholder or a progress indicator here
    }
}

@Composable
fun AppImageIcon(
    app: String,
    modifier: Modifier = Modifier
) {
    val imageBitmap: State<ImageBitmap?> =
        rememberImageBitmapFromAppIcon(app)
    imageBitmap.value?.let {
        Box(
            modifier = modifier
        ) {
            Image(
                bitmap = it,
                contentDescription = "App icon Image",
                modifier = Modifier.fillMaxSize(),
            )
        }

    } ?: run {
        // You can show a placeholder or a progress indicator here
    }
}

@Composable
fun rememberImageBitmapFromBase64(
    base64Str: String,
    clipToCircleShape: Boolean
): State<ImageBitmap?> {
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(base64Str) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = kotlin.runCatching {
                    PicassoInitializer.get().load("base64://${base64Str}")
                        .apply {
                            if (clipToCircleShape) {
                                transform(cropCircleTransformation)
                            }
                        }
                        .get()
                }.getOrElse {
                    Logger.nameless.e(it)
                    null
                }
                bitmap?.let {
                    bitmapState.value = it.asImageBitmap()
                }
            }
        }
    }
    return bitmapState
}


@Composable
fun rememberImageBitmapFromAppIcon(
    app: String
): State<ImageBitmap?> {
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(app) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = kotlin.runCatching {
                    PicassoInitializer.get().load(app)
                        .get()
                }.getOrElse {
                    Logger.nameless.e(it)
                    null
                }
                bitmap?.let {
                    bitmapState.value = it.asImageBitmap()
                }
            }
        }
    }
    return bitmapState
}

private val cropCircleTransformation = CropCircleTransformation()