package tornaco.apps.shortx.services.ui.floating.widget;

import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * <b>Package:</b> com.zh.touchassistant.widget <br>
 * <b>FileName:</b> FloatActionButton <br>
 * <b>Create Date:</b> 2018/12/7  下午10:13 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
@SuppressLint("AppCompatCustomView")
public class FloatActionButton extends ImageView {
    public FloatActionButton(Context context) {
        super(context);
    }

    public FloatActionButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatActionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}