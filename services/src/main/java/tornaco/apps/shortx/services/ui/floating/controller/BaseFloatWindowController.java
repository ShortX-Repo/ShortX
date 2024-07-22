package tornaco.apps.shortx.services.ui.floating.controller;

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
    protected Context context;
    protected LayoutInflater inflater;

    public BaseFloatWindowController(Context context) {
        this.context = context;
    }

    protected LayoutInflater getLayoutInflater() {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        return inflater;
    }

    public abstract View getView();
}