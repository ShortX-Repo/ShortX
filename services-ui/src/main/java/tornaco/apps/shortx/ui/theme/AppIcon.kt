package tornaco.apps.shortx.ui.theme


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.skydoves.landscapist.glide.GlideImage
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.util.GlideApp


@Composable
internal fun SystemServerAppIcon(modifier: Modifier, pkg: AppPkg) {
    GlideImage(
        modifier = modifier,
        imageModel = { pkg },
        loading = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {}
        },
        // shows an error text if fail to load an image.
        failure = {
            ColoredIcon {
                androidx.compose.material3.Icon(
                    painterResource(id = github.tornaco.shortx.ui.base.R.drawable.ui_ic_baseline_android_24),
                    contentDescription = null
                )
            }
        },

        requestBuilder = {
            GlideApp.with(LocalContext.current).asDrawable()
                .transition(withCrossFade())
        },
    )
}