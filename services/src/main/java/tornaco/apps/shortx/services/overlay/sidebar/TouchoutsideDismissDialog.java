package tornaco.apps.shortx.services.overlay.sidebar;

import android.annotation.NonNull;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;

public class TouchoutsideDismissDialog extends Dialog implements DialogInterface {
    private final Runnable dismiss;

    public TouchoutsideDismissDialog(Context context, Runnable dismiss) {
        super(context);
        this.dismiss = dismiss;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isShowing()) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss.run();
                return true;
            }
        }
        return false;
    }
}