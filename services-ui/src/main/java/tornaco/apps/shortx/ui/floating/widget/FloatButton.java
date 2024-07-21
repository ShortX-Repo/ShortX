package tornaco.apps.shortx.ui.floating.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * <b>Package:</b> com.zh.touchassistant.widget <br>
 * <b>FileName:</b> FloatButton <br>
 * <b>Create Date:</b> 2018/12/7  下午10:10 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
@SuppressLint("AppCompatCustomView")
public class FloatButton extends ImageView {
    public FloatButton(Context context) {
        super(context);
    }

    public FloatButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}