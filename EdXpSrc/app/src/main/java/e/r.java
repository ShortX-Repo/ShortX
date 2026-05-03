package e;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class r extends j0.c {
    private void h0() {
        j0(true);
    }

    private void i0() {
        j0(false);
    }

    private void j0(boolean z) {
        Window window;
        WindowInsetsController windowInsetsController;
        Activity activity = getActivity();
        if (activity == null || (window = activity.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        int i = attributes.flags;
        attributes.flags = z ? i | 1024 : i & (-1025);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 28) {
            attributes.layoutInDisplayCutoutMode = z ? 1 : 0;
        }
        window.setAttributes(attributes);
        if (i2 < 30 || (windowInsetsController = window.getDecorView().getWindowInsetsController()) == null) {
            return;
        }
        if (z) {
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        } else {
            windowInsetsController.show(WindowInsets.Type.systemBars());
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        h0();
    }

    @Override // e.j0.c
    protected void I() {
        super.I();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        i0();
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        if (view != null) {
            view.setSystemUiVisibility(5638);
        }
    }
}
