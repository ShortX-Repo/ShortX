package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.content.Context
import tornaco.apps.shortx.core.util.Logger

object GlobalSettings {
    private const val STORE_FILE_NAME = "global_settings_shortx.txt"
    private const val KEY_GESTURE_ENABLED = "gesture"

    private val logger = Logger("GlobalSettings")

    // java.lang.UnsatisfiedLinkError: dlopen failed:
    // couldn't map "/data/app/tornaco.apps.shortx-6nJ5nMnybZooF0oXnBorZA==/base.apk!/lib/arm64-v8a/libservices.so" segment 0: Permission denied
    // [ 2025-05-15T19:47:01.764    10052:  2845:  2845 I/LSPosed-Bridge  ] ShortXSystemUi ShortXHookUtil - [main] afterHookedMethod-class com.android.systemui.SystemUIApplication-onCreate --- java.lang.UnsatisfiedLinkError: dlopen failed: library "/system/lib64/libservices.so" needed or dlopened by "/apex/com.android.art/lib64/libnativeloader.so" is not accessible for the namespace "clns-6"
    //	at java.lang.Runtime.loadLibrary0(Runtime.java:1082)
    //	at java.lang.Runtime.loadLibrary0(Runtime.java:1003)
    //	at java.lang.System.loadLibrary(System.java:1661)
    //	at tornaco.apps.shortx.j.KVStore.<clinit>(Unknown Source:9)
    //	at djbwc.tzuqhs.kae.etz.P40.<clinit>(Unknown Source:16)
    //	at tornaco.apps.shortx.services.xposed.hooks.hook.systemui.HookAppKt.hookApp$lambda$8$lambda$7(Unknown Source:82)
    //	at tornaco.apps.shortx.services.xposed.hooks.hook.systemui.HookAppKt.OooO0o(Unknown Source:0)
    //	at djbwc.tzuqhs.kae.etz.Za0.OooOOO0(Unknown Source:6)
    //	at djbwc.tzuqhs.kae.etz.ob0$OooO0O0.afterHookedMethod(Unknown Source:11)
    //	at de.robv.android.xposed.XC_MethodHook.callAfterHookedMethod(Unknown Source:0)
    //	at H.callback(Unknown Source:347)
    //	at LSPHooker_.onCreate(Unknown Source:8)
    //	at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1316)
    //	at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7848)
    //	at android.app.ActivityThread.-$$Nest$mhandleBindApplication(Unknown Source:0)
    //	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2486)
    //	at android.os.Handler.dispatchMessage(Handler.java:106)
    //	at android.os.Looper.loopOnce(Looper.java:230)
    //	at android.os.Looper.loop(Looper.java:319)
    //	at android.app.ActivityThread.main(ActivityThread.java:9063)
    //	at java.lang.reflect.Method.invoke(Native Method)
    //	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:588)
    //	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1103)
    fun init(context: Context) {
        logger.w("Init.FAKE")
    }

    var isEdgeGestureEnabled: Boolean = false
}