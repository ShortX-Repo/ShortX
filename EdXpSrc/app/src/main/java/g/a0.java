package g;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import f.p0;

/* JADX INFO: loaded from: classes.dex */
public class a0 implements f.l {
    static final ColorMatrixColorFilter A;
    static final Drawable[] z = new Drawable[0];

    static {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        A = new ColorMatrixColorFilter(colorMatrix);
    }

    public static Bitmap a(a.b bVar, Context context) {
        Drawable drawableM = bVar.m(context);
        return i(drawableM, Math.min(drawableM.getIntrinsicWidth(), 192), Math.min(drawableM.getIntrinsicHeight(), 192));
    }

    public static void b(WindowManager.LayoutParams layoutParams, String str) {
    }

    public static int c(int i, int i2) {
        return d(Color.alpha(i), Color.red(i), Color.green(i), Color.blue(i), i2);
    }

    public static int d(int i, int i2, int i3, int i4, int i5) {
        int i6 = i2 + i5;
        int i7 = i3 + i5;
        int i8 = i4 + i5;
        if (i5 > 0) {
            if (i6 > 255) {
                i6 = 255;
            }
            if (i7 > 255) {
                i7 = 255;
            }
            if (i8 > 255) {
                i8 = 255;
            }
        } else {
            if (i6 < 0) {
                i6 = 0;
            }
            if (i7 < 0) {
                i7 = 0;
            }
            if (i8 < 0) {
                i8 = 0;
            }
        }
        return Color.argb(i, i6, i7, i8);
    }

    public static boolean e(EditText editText, CharSequence charSequence) {
        Editable editableText;
        if (editText != null && charSequence != null && editText.isFocused()) {
            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();
            if (selectionStart >= 0 && selectionEnd >= 0 && (editableText = editText.getEditableText()) != null) {
                editableText.replace(selectionStart, selectionEnd, charSequence);
                return true;
            }
        }
        return false;
    }

    public static int f(int i, int i2) {
        int i3 = (i >> 24) & 255;
        if (i3 == 255) {
            return i;
        }
        if (i3 == 0) {
            return i2;
        }
        int i4 = (i2 >> 24) & 255;
        int i5 = 255 - i3;
        int i6 = 255 - (((255 - i4) * i5) / 255);
        if (i6 == 0) {
            return 0;
        }
        int i7 = i6 * 255;
        return (((((i & 255) * 255) * i3) + (((i2 & 255) * i4) * i5)) / i7) | (i6 << 24) | (((((((i >> 16) & 255) * 255) * i3) + ((((i2 >> 16) & 255) * i4) * i5)) / i7) << 16) | (((((((i >> 8) & 255) * 255) * i3) + ((((i2 >> 8) & 255) * i4) * i5)) / i7) << 8);
    }

    public static int g(int i) {
        return (int) ((i * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static Bitmap h(Drawable drawable) {
        return drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : i(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static Bitmap i(Drawable drawable, int i, int i2) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, i, i2);
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    public static float j() {
        return Resources.getSystem().getDisplayMetrics().xdpi;
    }

    public static Point k(Context context, Point point) {
        if (context != null) {
            try {
                WindowManager windowManager = (WindowManager) context.getSystemService("window");
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getRealSize(point);
                    return point;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        point.x = displayMetrics.widthPixels;
        point.y = displayMetrics.heightPixels;
        return point;
    }

    public static Drawable l() {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(ColorStateList.valueOf(1065320319), null, new ColorDrawable(-1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        ColorDrawable colorDrawable = new ColorDrawable(1065320319);
        ColorDrawable colorDrawable2 = new ColorDrawable(0);
        stateListDrawable.addState(new int[]{R.attr.state_activated}, colorDrawable);
        stateListDrawable.addState(new int[]{R.attr.state_pressed}, colorDrawable);
        stateListDrawable.addState(new int[]{R.attr.state_focused}, colorDrawable);
        stateListDrawable.addState(new int[0], colorDrawable2);
        return stateListDrawable;
    }

    public static float m(Context context) {
        return context.getResources().getDisplayMetrics().xdpi;
    }

    public static Point n(Context context, Point point) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        point.x = displayMetrics.widthPixels;
        point.y = displayMetrics.heightPixels;
        return point;
    }

    public static Drawable o(int i, float f2) {
        int iF = f(1065320319, i);
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(ColorStateList.valueOf(iF), new s(i, f2), new s(-1, f2));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        s sVar = new s(iF, f2);
        s sVar2 = new s(i, f2);
        stateListDrawable.addState(new int[]{R.attr.state_activated}, sVar);
        stateListDrawable.addState(new int[]{R.attr.state_pressed}, sVar);
        stateListDrawable.addState(new int[]{R.attr.state_focused}, sVar);
        stateListDrawable.addState(new int[0], sVar2);
        return stateListDrawable;
    }

    public static int p() {
        return p0.l("status_bar_height");
    }

    public static int q() {
        int iP = p();
        if (iP > 0) {
            return iP;
        }
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) ((displayMetrics.widthPixels + displayMetrics.heightPixels) * 0.025f);
    }

    public static boolean r(int i) {
        return (((i >> 16) & 255) + ((i >> 8) & 255)) + (i & 255) < 384;
    }

    public static boolean s(Drawable drawable) {
        int pixel;
        if (drawable instanceof ColorDrawable) {
            pixel = ((ColorDrawable) drawable).getColor();
        } else {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            drawable.setBounds(0, 0, 1, 1);
            drawable.draw(new Canvas(bitmapCreateBitmap));
            pixel = bitmapCreateBitmap.getPixel(0, 0);
        }
        return r(pixel);
    }

    public static int t(int i) {
        return (int) ((i / Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static Bitmap u(Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f2 = i / width;
        float f3 = i2 / height;
        Matrix matrix = new Matrix();
        if (f2 > f3) {
            matrix.postScale(f2, f2);
            return Bitmap.createBitmap(bitmap, 0, (height - width) / 2, width, width, matrix, false);
        }
        matrix.postScale(f3, f3);
        return Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height, matrix, false);
    }

    public static void v(Drawable drawable, boolean z2) {
        if (drawable != null) {
            if (drawable instanceof g) {
                ((g) drawable).j(z2);
            } else {
                drawable.setColorFilter(z2 ? A : null);
            }
        }
    }

    public static void w(View view, int i, float f2) {
        if (view == null) {
            return;
        }
        view.setBackground(new s(i, f2));
        if (Build.VERSION.SDK_INT >= 21) {
            view.setClipToOutline(true);
            if ((i >>> 24) >= 240) {
                view.setElevation(view.getResources().getDisplayMetrics().density * 4.0f);
            }
        }
    }

    private static void x(View view, Animation animation, long j, String str) {
        animation.setDuration((long) (j * Settings.Global.getFloat(view.getContext().getContentResolver(), str, 1.0f)));
        view.startAnimation(animation);
    }

    public static void y(View view, Animation animation) {
        x(view, animation, 200L, "transition_animation_scale");
    }

    public static void z(View view, Animation animation, long j) {
        if (j > 200) {
            j = 200;
        }
        x(view, animation, j, "window_animation_scale");
    }
}
