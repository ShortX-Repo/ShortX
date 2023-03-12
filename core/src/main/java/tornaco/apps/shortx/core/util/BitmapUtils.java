package tornaco.apps.shortx.core.util;

import android.annotation.DrawableRes;
import android.annotation.Nullable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import java.io.ByteArrayOutputStream;


/**
 * Created by guohao4 on 2017/10/24.
 * Email: Tornaco@163.com
 */

public abstract class BitmapUtils {

    public static byte[] drawableToByteArray(Drawable d, boolean resize) {
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        if (resize) bitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    public static Bitmap getBitmap(AdaptiveIconDrawable adaptiveIconDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(adaptiveIconDrawable.getIntrinsicWidth(),
                adaptiveIconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        adaptiveIconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        adaptiveIconDrawable.draw(canvas);
        return bitmap;
    }

    @Nullable
    public static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
        Drawable drawable = context.getDrawable(drawableResId);
        return getBitmap(context, drawable);
    }

    @Nullable
    public static Bitmap getBitmap(Context context, Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else if (drawable instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable ad = (AdaptiveIconDrawable) drawable;
            return getBitmap(ad);

        } else {
            // https://cs.android.com/android/platform/superproject/+/master:packages/apps/Dialer/java/com/android/dialer/util/DrawableConverter.java?q=drawableToBitmap
            Bitmap bitmap;
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                // Needed for drawables that are just a colour.
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static Bitmap createScaledBitmap(Bitmap in, int newWidth, int newHeight) {
        try {
            return Bitmap.createScaledBitmap(
                    in, newWidth, newHeight, false);
        } catch (Exception e) {
            return in;
        }
    }
}
