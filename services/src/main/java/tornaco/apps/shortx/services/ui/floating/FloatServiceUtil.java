package tornaco.apps.shortx.services.ui.floating;

/**
 * <b>Package:</b> com.zh.touchassistant.util <br>
 * <b>FileName:</b> FloatServiceUtil <br>
 * <b>Create Date:</b> 2018/12/14  下午11:23 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatServiceUtil {
    public int x = 200;
    public int y = 500;

    public int px = 200;
    public int py = 500;

    public boolean isLeft = false;

    public final FloatViewLiveData floatViewLiveData = new FloatViewLiveData();

    public void openFloatButton() {
        floatViewLiveData.setValue(true);
    }

    public void closeFloatButton() {
        floatViewLiveData.setValue(false);
    }

    public boolean isFloatButtonOpen() {
        return floatViewLiveData.isOpen();
    }

    public void toggleFloatButton() {
        if (isFloatButtonOpen()) {
            closeFloatButton();
        } else {
            openFloatButton();
        }
    }
}