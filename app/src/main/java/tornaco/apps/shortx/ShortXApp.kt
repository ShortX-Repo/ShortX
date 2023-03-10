package tornaco.apps.shortx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.EntryPoint

@HiltAndroidApp
class ShortXApp : Application() {
    private val logger = Logger("ShortXApp")

    override fun onCreate() {
        super.onCreate()
        logger.d("onCreate.")
        EntryPoint.onCreate(this)
    }
}