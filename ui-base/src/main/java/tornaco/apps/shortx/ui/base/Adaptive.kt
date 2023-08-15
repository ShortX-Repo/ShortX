package tornaco.apps.shortx.ui.base

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("UI.Adaptive")

val adaptiveCardSizeMin get() = 160.dp
val adaptiveLauncherIconSizeMin get() = 82.dp

@Composable
fun dynamicStaggeredGridCells(): StaggeredGridCells {
    val context = LocalContext.current
    val isTablet = remember { isTabletDevice(context) }
    logger.d("isTablet: $isTablet")
    return if (isTablet) {
        StaggeredGridCells.Adaptive(minSize = adaptiveCardSizeMin)
    } else {
        StaggeredGridCells.Fixed(2)
    }
}


private fun isTabletDevice(context: Context): Boolean {
    val screenLayout = context.resources.configuration.screenLayout
    val screenSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
}