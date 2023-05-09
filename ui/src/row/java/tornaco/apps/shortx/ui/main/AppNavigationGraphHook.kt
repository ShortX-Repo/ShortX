package tornaco.apps.shortx.ui.main

import androidx.navigation.NavGraphBuilder
import tornaco.apps.shortx.ui.base.animateComposable

enum class RowRoute {
    PreHome,
}

fun NavGraphBuilder.appNavGraphHook() {
    animateComposable(RowRoute.PreHome.name) {
        PreHomeScreen()
    }
}