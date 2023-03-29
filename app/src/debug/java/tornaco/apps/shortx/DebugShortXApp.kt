package tornaco.apps.shortx

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.net.Uri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.shortcutManager
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

        shortcutManager.dynamicShortcuts = mutableListOf(
            ShortcutInfo.Builder(applicationContext, "demo")
                .setShortLabel("Demo")
                .setIcon(
                    Icon.createWithResource(
                        applicationContext,
                        R.drawable.ic_baseline_developer_mode_24
                    )
                )
                .setIntent(Intent("tornaco.apps.shortx.action.demo"))
                .build()
        )
    }
}