package tornaco.apps.shortx

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.ui.settings.pref.Pref

class DebugShortXApp : ShortXApp() {
    override fun onCreate() {
        super.onCreate()

        runBlocking {
            val shouldInstall = Pref(this@DebugShortXApp).installFakeServiceFlow.first()
            if (shouldInstall) {
                logger.w("Install fake service.")
                FakeServiceInstaller(this@DebugShortXApp).install().apply {
                    ShortXManagerNative.shortXInjection = this
                }
            }
        }
    }
}