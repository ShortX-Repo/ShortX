package tornaco.apps.shortx.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;

import com.elvishew.xlog.XLog;

import tornaco.apps.shortx.core.res.AppResources;
import tornaco.apps.shortx.core.util.Logger;

public final class AppGlobals {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static Context appContext;
    @SuppressLint("StaticFieldLeak")
    public static AppResources appResources;

    private AppGlobals() {
    }

    public static void setContext(Context context) {
        XLog.w("AppGlobals.setContext: %s", context);
        AppGlobals.context = context;
    }

    public static void setAppResources(AppResources appResources) {
        XLog.w("AppGlobals.setAppResources: %s", appResources);
        AppGlobals.appResources = appResources;
        AppGlobals.appContext = appResources.getAppContext();
    }

    public static Context getContext() {
        if (AppGlobals.context == null) {
            // Fetching ActivityThread on the main thread is no longer required on API 18+
            // See: https://cs.android.com/android/platform/frameworks/base/+/66a017b63461a22842b3678c9520f803d5ddadfc
            try {
                Context c = (Context) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null);
                AppGlobals.context = getContextImpl(c);
            } catch (Exception e) {
                // Shall never happen
                Logger.Companion.getNameless().e(e, "getContext");
            }
        }
        return AppGlobals.context;
    }

    private static Context getContextImpl(Context context) {
        while (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return context;
    }
}
