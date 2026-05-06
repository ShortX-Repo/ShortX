package e;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class n0 extends j0.b {

    class a extends TimePickerDialog {
        final /* synthetic */ Bundle z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        a(Context context, TimePickerDialog.OnTimeSetListener onTimeSetListener, int i, int i2, boolean z, Bundle bundle) {
            super(context, onTimeSetListener, i, i2, z);
            this.z = bundle;
        }

        @Override // android.app.TimePickerDialog, android.widget.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker timePicker, int i, int i2) {
            this.z.putInt("hour", i);
            this.z.putInt("minute", i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void v(Bundle bundle, TimePicker timePicker, int i, int i2) {
        bundle.putInt("hour", i);
        bundle.putInt("minute", i2);
        o(bundle);
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        final Bundle bundleE = e();
        a aVar = new a(activity, new TimePickerDialog.OnTimeSetListener() { // from class: e.m0
            @Override // android.app.TimePickerDialog.OnTimeSetListener
            public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                this.f399a.v(bundleE, timePicker, i, i2);
            }
        }, bundleE.getInt("hour", 0), bundleE.getInt("minute", 0), true, bundleE);
        aVar.setTitle(bundleE.getCharSequence("title"));
        return aVar;
    }

    public n0 w(CharSequence charSequence, int i, int i2) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putInt("hour", i);
        bundleE.putInt("minute", i2);
        return this;
    }
}
