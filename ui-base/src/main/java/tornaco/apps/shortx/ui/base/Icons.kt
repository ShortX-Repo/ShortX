package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.Remix.allFlat
import tornaco.apps.shortx.core.util.Logger

@Composable
fun ColoredIcon(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    padding: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (() -> Unit)? = null,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
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

@SuppressLint("DiscouragedApi")
fun Context.remixResId(remixName: String): Int {
    return resources.getIdentifier(
        allFlat[remixName] ?: allFlat[Remix.Development.bug_line],
        "drawable",
        packageName
    )
}