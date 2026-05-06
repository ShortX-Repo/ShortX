package g;

import android.R;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
public class m implements f.l {
    private final WindowManager.LayoutParams A;
    private View B = null;
    private final WindowManager z;

    public m(Context context) {
        this.z = (WindowManager) context.getSystemService("window");
        int i = Build.VERSION.SDK_INT;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(i >= 26 ? 2038 : i < 24 ? 2005 : 2002, R.drawable.ic_media_route_connecting_light_05_mtrl, 1);
        this.A = layoutParams;
        a0.b(layoutParams, "Floating");
    }

    public void a(int i, int i2) {
        View view = this.B;
        if (view != null) {
            WindowManager.LayoutParams layoutParams = this.A;
            layoutParams.x = i;
            layoutParams.y = i2;
            this.z.updateViewLayout(view, layoutParams);
        }
    }

    public void b() {
        View view = this.B;
        if (view != null) {
            this.z.removeView(view);
            this.B = null;
        }
    }

    public void c(int i, int i2) {
        View view = this.B;
        if (view != null) {
            WindowManager.LayoutParams layoutParams = this.A;
            layoutParams.width = i;
            layoutParams.height = i2;
            this.z.updateViewLayout(view, layoutParams);
        }
    }

    public void d(View view, int i, int i2, int i3, int i4) {
        b();
        WindowManager.LayoutParams layoutParams = this.A;
        layoutParams.width = i;
        layoutParams.height = i2;
        layoutParams.x = i3;
        layoutParams.y = i4;
        this.z.addView(view, layoutParams);
        this.B = view;
    }
}
