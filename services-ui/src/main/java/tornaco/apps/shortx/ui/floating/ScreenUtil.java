package tornaco.apps.shortx.ui.floating;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * <b>Package:</b> com.zh.touchassistant.util <br>
 * <b>FileName:</b> ScreenUtil <br>
 * <b>Create Date:</b> 2018/12/7  下午12:22 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class ScreenUtil {
    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 屏幕宽度和指定比率计算位置
     *
     * @param ratio 比率
     */
    public static int getPointFromScreenWidthRatio(Context context, float ratio) {
        return (int) (getScreenWidth(context) * ratio);
    }

    /**
     * 判断坐标点是否在屏幕左边
     *
     * @param x 点的x坐标
     */
    public static boolean isScreenLeft(Context context, int x) {
        int halfScreenWidth = getScreenWidth(context) / 2;
        return x <= halfScreenWidth;
    }

    public static int getPointFromScreenHeightRatio(Context context, float ratio) {
        return (int) (getScreenHeight(context) * ratio);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static int spToPixel(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}