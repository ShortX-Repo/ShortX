package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.Build

// Android 17.
val isSupportedAndroidVersion: Boolean get() = Build.VERSION.SDK_INT <= 37