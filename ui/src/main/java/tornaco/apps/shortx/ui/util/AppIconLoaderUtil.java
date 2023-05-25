package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import tornaco.apps.shortx.core.ShortXManagerKt;
import tornaco.apps.shortx.core.util.Logger;

class AppIconLoaderUtil {
    private static final Logger LOGGER = new Logger("AppIconLoaderUtil");

    @Nullable
    public static Bitmap loadAppIconBitmapWithIconPack(Context context, String pkgName, int userId) {
        try {
            Bitmap res = ShortXManagerKt.getShortXManager().getAppIcon(pkgName, userId);
            LOGGER.d("loadAppIconBitmapWithIconPack: " + pkgName + ", " + res);
            return res;
        } catch (Throwable e) {
            return null;
        }
    }
}
