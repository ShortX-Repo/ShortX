package tornaco.apps.shortx.services.ui.floating;

import android.content.Context;
import android.view.View;

import java.util.HashMap;

public class FloatWindowManager {
    private Context mContext;
    private HashMap<String, FloatWindow> mMapping;

    public FloatWindowManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private void ensureInitMapping() {
        if (mMapping == null) {
            mMapping = new HashMap<>();
        }
    }

    /**
     * 创建悬浮窗
     *
     * @param view   悬浮的布局
     * @param tag    查找悬浮窗时使用的标记
     * @param option 可选参数
     */
    public void makeFloatWindow(View view, String tag, FloatWindowOption option) {
        ensureInitMapping();
        FloatWindow floatWindow = new FloatWindow(mContext, tag, view, option);
        mMapping.put(tag, floatWindow);
    }

    public FloatWindow getFloatWindow(String tag) {
        ensureInitMapping();
        return mMapping.get(tag);
    }
}