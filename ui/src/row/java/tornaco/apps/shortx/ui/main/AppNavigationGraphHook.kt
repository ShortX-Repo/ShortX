package tornaco.apps.shortx.ui.main

import androidx.navigation.NavGraphBuilder
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.row.util.PreHomeScreen

enum class RowRoute {
    PreHome,
}

fun NavGraphBuilder.appNavGraphHook() {
    animateComposable(RowRoute.PreHome.name) {
        PreHomeScreen()
    }
}