package tornaco.apps.shortx.services.xposed.hooks.hook

import android.net.Uri
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object SettingsProviderHook {
    private val logger = Logger("ShortXHook.SettingProviderHook")

    fun hookPutGetString() = runCatching {
        // android.provider.Settings$NameValueCache
        val nameValueCacheClass = Reflections.findClass(
            "android.provider.Settings\$NameValueCache",
            null
        )
        logger.d("hookPutGetString, nameValueCacheClass: $nameValueCacheClass")
        afterMethod(nameValueCacheClass, "putStringForUser") {
            val shortX = ShortXManagerNative.shortX
            if (shortX != null && shortX.isSettingsCatcherEnabled) {
                val contentUri: Uri =
                    Reflections.getObjectField(it.thisObject, "mUri") as Uri
                val name = it.args[1].toString()
                val value = it.result?.toString() ?: ""
                logger.d("putStringForUser: $contentUri $name $value")
                shortX.onWriteSettings("${contentUri}/$name", value)
            }
        }

        afterMethod(nameValueCacheClass, "getStringForUser") {
            // getStringForUser(ContentResolver cr, String name, final int userHandle)
            //  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY(settings) + "/system");
            //  private final Uri mUri;
            val shortX = ShortXManagerNative.shortX
            if (shortX != null && shortX.isSettingsCatcherEnabled) {
                val contentUri: Uri =
                    Reflections.getObjectField(it.thisObject, "mUri") as Uri
                val name = it.args[1].toString()
                val value = it.result?.toString() ?: ""
                logger.d("getStringForUser: $contentUri $name $value")
                shortX.onReadSettings("${contentUri}/$name", value)
            }
        }
    }.onFailure {
        logger.e(it, "hookPutGetString")
    }
}

