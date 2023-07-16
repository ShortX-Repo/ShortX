package tornaco.apps.shortx.core.res;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import tornaco.apps.shortx.core.BuildProp;
import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.core.util.PkgUtils;

public class AppResources {

    private static final Logger LOGGER = new Logger("AppResources");
    private static final boolean DEBUG = BuildProp.APP_IS_DEBUG;

    private static final Map<Object, String> S_STRING_RES_CACHE = new HashMap<>();
    private static final Map<Object, String[]> S_STRING_ARRAY_RES_CACHE = new HashMap<>();

    private final Context context;
    private final String appPackageName;

    public AppResources(Context context, String appPackageName) {
        this.context = context;
        this.appPackageName = appPackageName;
    }

    public Context getContext() {
        return context;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public Bitmap getBitmap(String resName) {
        if (!PkgUtils.isPkgInstalled(this.context, appPackageName)) {
            return BitmapFactory.decodeResource(this.context.getResources(), android.R.drawable.stat_sys_warning);
        }
        if (DEBUG) {
            LOGGER.d("getBitmap, resName: " + resName);
        }
        try {
            Context appContext = getAppContext();
            if (appContext != null) {
                Resources res = appContext.getResources();
                if (DEBUG) {
                    LOGGER.d("getBitmap, res: " + res);
                }
                if (res != null) {
                    int id = res.getIdentifier(resName, "drawable", appPackageName);
                    if (DEBUG) {
                        LOGGER.d("getBitmap, id: " + id);
                    }
                    if (id > 0) {
                        Bitmap bitmap = BitmapFactory.decodeResource(res, id);
                        if (DEBUG) {
                            LOGGER.d("getBitmap, bitmap: " + bitmap);
                        }
                        if (bitmap != null) {
                            return bitmap;
                        } else {
                            return BitmapFactory.decodeResource(this.context.getResources(), android.R.drawable.stat_sys_warning);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.e("Fail getBitmap: " + Log.getStackTraceString(e));
        }
        return null;
    }

    public Drawable getDrawable(String resName) {
        if (!PkgUtils.isPkgInstalled(this.context, appPackageName)) {
            return null;
        }
        if (DEBUG) {
            LOGGER.d("getDrawable, resName: " + resName);
        }
        try {
            Context appContext = getAppContext();
            if (appContext != null) {
                Resources res = appContext.getResources();
                if (DEBUG) {
                    LOGGER.d("getDrawable, res: " + res);
                }
                if (res != null) {
                    int id = res.getIdentifier(resName, "drawable", appPackageName);
                    if (DEBUG) {
                        LOGGER.d("getDrawable, id: " + id);
                    }
                    if (id > 0) {
                        Drawable drawable = appContext.getDrawable(id);
                        LOGGER.d("getDrawable, drawable: " + drawable);
                        return drawable;
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.e("Fail getDrawable: " + Log.getStackTraceString(e));
        }
        return null;
    }

    public Icon getIcon(String resName) {
        return getIcon(resName, null);
    }

    public Icon getIcon(String resName, Transform<Bitmap> bitmapTransform) {
        if (!PkgUtils.isPkgInstalled(this.context, appPackageName)) {
            return Icon.createWithResource(this.context.getResources(), android.R.drawable.stat_sys_warning);
        }
        if (DEBUG) {
            LOGGER.d("getIcon, resName: " + resName);
        }
        try {
            Context appContext = getAppContext();
            if (appContext != null) {
                Resources res = appContext.getResources();
                if (DEBUG) {
                    LOGGER.d("getIcon, res: " + res);
                }
                if (res != null) {
                    int id = res.getIdentifier(resName, "drawable", appPackageName);
                    if (DEBUG) {
                        LOGGER.d("getIcon, id: " + id);
                    }
                    if (id > 0) {
                        Icon ic = null;
                        // Create with res directly.
                        if (bitmapTransform == null) {
                            ic = Icon.createWithResource(res, id);
                        } else {
                            // Create bitmap and transform.
                            try {
                                Bitmap icb = BitmapFactory.decodeResource(res, id);
                                if (icb != null) {
                                    icb = bitmapTransform.onTransform(icb);
                                    if (icb != null) {
                                        ic = Icon.createWithBitmap(icb);
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.e("getIcon, bitmap transform err: " + e);
                            } finally {
                                // Back to res.
                                if (ic == null) {
                                    ic = Icon.createWithResource(res, id);
                                }
                            }
                        }
                        if (DEBUG) {
                            LOGGER.d("getIcon, ic: " + ic);
                        }
                        if (ic != null) {
                            return ic;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.e("Fail getIcon: " + Log.getStackTraceString(e));
        }
        return Icon.createWithResource(getContext(), android.R.drawable.stat_sys_warning);
    }

    public Context getAppContext() {
        Context context = getContext();
        if (context == null) {
            LOGGER.e("Context is null!!!");
            return null;
        }
        try {
            return context.createPackageContext(appPackageName, CONTEXT_IGNORE_SECURITY);
        } catch (Throwable e) {
            LOGGER.e("Fail createPackageContext: " + Log.getStackTraceString(e));
        }
        return null;
    }

    public String[] getStringArray(String resName) {
        if (!PkgUtils.isPkgInstalled(this.context, appPackageName)) {
            if (S_STRING_ARRAY_RES_CACHE.containsKey(resName)) {
                String[] cached = S_STRING_ARRAY_RES_CACHE.get(resName);
                if (cached != null) {
                    return cached;
                }
            }
            return new String[0];
        }
        Context context = getContext();
        if (context == null) {
            LOGGER.e("Context is null!!!");
            return new String[0];
        }
        try {
            Context appContext =
                    context.createPackageContext(appPackageName, CONTEXT_IGNORE_SECURITY);
            Resources res = appContext.getResources();
            int id = res.getIdentifier(resName, "array", appPackageName);
            LOGGER.d("getStringArray get id: " + id + ", for res: " + resName);
            if (id != 0) {
                String[] stringArr = res.getStringArray(id);
                S_STRING_ARRAY_RES_CACHE.put(resName, stringArr);
                return stringArr;
            }
        } catch (Throwable e) {
            LOGGER.e("Fail createPackageContext: " + Log.getStackTraceString(e));
        }
        return new String[0];
    }

    public String getString(String resName, Object... args) {
        if (!PkgUtils.isPkgInstalled(this.context, appPackageName)) {
            // Return cache.
            String cachedString = S_STRING_RES_CACHE.get(resName);
            if (cachedString != null) {
                return String.format(cachedString, args);
            }
            return resName;
        }
        Context context = getContext();
        if (context == null) {
            LOGGER.e("Context is null!!!");
            return null;
        }
        try {
            Context appContext =
                    context.createPackageContext(appPackageName, CONTEXT_IGNORE_SECURITY);
            Resources res = appContext.getResources();
            int id = res.getIdentifier(resName, "string", appPackageName);
            LOGGER.d("getString get id: " + id + ", for res: " + resName);
            if (id != 0) {
                String string = res.getString(id, args);
                S_STRING_RES_CACHE.put(resName, string);
                return string;
            }
        } catch (Throwable e) {
            LOGGER.e("Fail createPackageContext: " + Log.getStackTraceString(e));
        }
        return null;
    }

    public interface Transform<T> {
        T onTransform(T in) throws Exception;
    }
}
