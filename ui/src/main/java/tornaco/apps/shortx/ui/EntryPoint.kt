package tornaco.apps.shortx.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import lang3.RandomStringUtils
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.shortcutManager
import tornaco.apps.shortx.ui.base.I18NCache
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.*

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        initDeviceId(context)
        I18NCache.init(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        runBlocking {
            val shouldInstall = Pref(context).installFakeServiceFlow.first()
            if (shouldInstall) {
                logger.w("Installing fake service.")
                FakeServiceInstaller(context).install().apply {
                    ShortXManagerNative.shortXInjection = this
                }
            }
        }

        context.shortcutManager.dynamicShortcuts = mutableListOf(
            ShortcutInfo.Builder(context, "demo")
                .setShortLabel("Demos")
                .setIcon(
                    Icon.createWithResource(
                        context,
                        R.drawable.ui_ic_baseline_developer_mode_24
                    )
                )
                .setIntent(Intent("tornaco.apps.shortx.action.demo"))
                .build()
        )
    }

    fun initDeviceId(context: Context) {
        if (!TextUtils.isEmpty(getDeviceId(context))) {
            return
        }
        val id: String = generateDeviceId(context)

        logger.d("generateDeviceId: $id")
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("did", id)
            .apply()
    }

    fun getDeviceId(context: Context?): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString("did", "N/A").fallbackOnEmpty("N/A")
    }

    private fun getAndroidId(context: Context): String {
        return try {
            Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Throwable) {
            logger.e(e, "Error getAndroidId")
            ""
        }
    }

    private fun generateDeviceId(context: Context): String {
        return getAndroidId(context) + "_" + RandomStringUtils.randomAlphanumeric(12) + "_" + UUID.randomUUID()
            .toString().hashCode()
    }
}