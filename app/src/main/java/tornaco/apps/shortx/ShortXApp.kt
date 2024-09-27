package tornaco.apps.shortx

import android.app.Application
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.EntryPoint
import tornaco.apps.shortx.ui.FlavorEntryPoint
import tornaco.apps.shortx.ui.analytics.Event
import tornaco.apps.shortx.ui.analytics.analytics
import tornaco.apps.shortx.ui.settings.pref.Pref

@HiltAndroidApp
class ShortXApp : Application() {
    private val logger = Logger("ShortXApp")

    override fun onCreate() {
        super.onCreate()
        initLogger(this)
        logger.d("onCreate.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        installFakeService()

        val initStartTimeMillis = System.currentTimeMillis()
        EntryPoint.onCreate(this)
        FlavorEntryPoint.onCreate(this)

        analytics.logEvent(Event.AppColdStart)

        logger.w("EntryPoint.onCreate taken ${System.currentTimeMillis() - initStartTimeMillis}ms")
    }

    private fun installFakeService() {
        runBlocking {
            val shouldInstall = Pref(applicationContext).installFakeServiceFlow.first()
            if (shouldInstall) {
                logger.w("Installing fake service.")
                FakeServiceInstaller(applicationContext).install().apply {
                    ShortXManagerNative.attachLocalService(this)
                }
            }
        }
    }
}