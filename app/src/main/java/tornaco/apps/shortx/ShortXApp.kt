package tornaco.apps.shortx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.enro.annotations.NavigationComponent
import dev.enro.core.NavigationAnimation
import dev.enro.core.controller.NavigationApplication
import dev.enro.core.controller.createNavigationController
import dev.enro.core.plugins.EnroLogger
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.main.MainActivity

@NavigationComponent
@HiltAndroidApp
class ShortXApp : Application(), NavigationApplication {
    private val logger = Logger("ShortXApp")

    override val navigationController = createNavigationController {
        plugin(EnroLogger())

        override<MainActivity, Any> {
            animation {
                NavigationAnimation.Resource(
                    R.anim.enter_end_slide,
                    R.anim.exit_end_slide,
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        logger.d("onCreate.")
    }
}