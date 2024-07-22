package tornaco.apps.shortx.services.ui.floating;

import android.content.Context;

/**
 * <b>Package:</b> com.zh.touchassistant.util <br>
 * <b>FileName:</b> FloatServiceUtil <br>
 * <b>Create Date:</b> 2018/12/14  下午11:23 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatServiceUtil {
    public static int x = 200;
    public static int y = 500;

    public static int px = 200;
    public static int py = 500;

    public static FloatViewLiveData floatViewLiveData = new FloatViewLiveData();

    private FloatServiceUtil() {
    }

    public static int getFloatButtonX(Context context) {
        return x;
    }

    public static int getFloatButtonY(Context context) {
        return y;
    }

    public static int getFloatPanelX() {
        return px;
    }

    public static int getFloatPanelY() {
        return py;
    }


    public static void openFloatButton() {
        floatViewLiveData.setValue(true);
    }

    public static void closeFloatButton() {
        floatViewLiveData.setValue(false);
    }

    public static boolean isFloatButtonOpen() {
        return floatViewLiveData.isOpen();
    }

    public static void toggleFloatButton() {
        if (isFloatButtonOpen()) {
            closeFloatButton();
        } else {
            openFloatButton();
        }
    }
}