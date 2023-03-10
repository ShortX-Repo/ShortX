package com.squareup.picasso3;

import android.annotation.DrawableRes;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import java.io.File;

public class ContextCompat {
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getDrawable(context, id);
        } else if (Build.VERSION.SDK_INT >= 16) {
            return context.getResources().getDrawable(id);
        } else {
            // Prior to JELLY_BEAN, Resources.getDrawable() would not correctly
            // retrieve the final configuration density when the resource ID
            // is a reference another Drawable resource. As a workaround, try
            // to resolve the drawable reference manually.
            final int resolvedId;
            synchronized (sLock) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
    }

    static class Api21Impl {
        private Api21Impl() {
            // This class is not instantiable.
        }

        static Drawable getDrawable(Context obj, int id) {
            return obj.getDrawable(id);
        }

        static File getNoBackupFilesDir(Context obj) {
            return obj.getNoBackupFilesDir();
        }

        static File getCodeCacheDir(Context obj) {
            return obj.getCodeCacheDir();
        }
    }
}
