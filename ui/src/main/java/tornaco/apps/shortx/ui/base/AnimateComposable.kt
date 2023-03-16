package tornaco.apps.shortx.ui.base

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

val animSpec = spring<IntOffset>(stiffness = 300f)

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animateComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Start,
                animationSpec = animSpec
            )
        },

        exitTransition = {
            null
        },

        popEnterTransition = {
            null
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.End,
                animationSpec = animSpec
            )
        },
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}