package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import tornaco.apps.shortx.core.util.ApkUtils;
import tornaco.apps.shortx.core.util.UserHandleUtils;

class AppIconLoaderUtil {

    @Nullable
    public static Bitmap loadAppIconBitmapWithIconPack(Context context, String pkgName, int userId) {
        try {
            Drawable icon = ApkUtils.loadIconByPkgName(context, pkgName);
            Drawable badged = context.getPackageManager().getUserBadgedIcon(icon, UserHandleUtils.INSTANCE.userIdToUserHandle(userId));
            return BitmapUtil.getBitmap(context, badged);
        } catch (Throwable e) {
            return null;
        }
    }
}
