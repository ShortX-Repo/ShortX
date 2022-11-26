package tornaco.apps.shortx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.enro.annotations.NavigationComponent
import dev.enro.core.controller.NavigationApplication
import dev.enro.core.controller.createNavigationController
import dev.enro.core.plugins.EnroLogger
import tornaco.apps.shortx.core.util.Logger

@NavigationComponent
@HiltAndroidApp
class ShortXApp : Application(), NavigationApplication {
    private val logger = Logger("ShortXApp")

    override val navigationController = createNavigationController {
        plugin(EnroLogger())
    }

    override fun onCreate() {
        super.onCreate()
        logger.d("onCreate.")
    }
}