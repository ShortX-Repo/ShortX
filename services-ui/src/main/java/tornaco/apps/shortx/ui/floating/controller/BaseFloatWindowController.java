package tornaco.apps.shortx.ui.floating.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * <b>Package:</b> com.zh.touchassistant.controller <br>
 * <b>FileName:</b> BaseViewController <br>
 * <b>Create Date:</b> 2018/12/7  下午11:53 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 *
 * @author wally
 */
public abstract class BaseFloatWindowController {
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseFloatWindowController(Context context) {
        this.mContext = context;
    }

    protected LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(mContext);
        }
        return mInflater;
    }

    public abstract View getView();
}