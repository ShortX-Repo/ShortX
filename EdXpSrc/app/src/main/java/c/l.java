package c;

import a.b;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j0;
import f.p0;
import g.x;

/* JADX INFO: loaded from: classes.dex */
public class l extends j0.b {
    private BroadcastReceiver C = null;
    private TextView D = null;

    class a implements DialogInterface.OnClickListener {
        a() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            l lVar = l.this;
            lVar.o(lVar.e());
        }
    }

    class b extends BroadcastReceiver {
        b() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int iH;
            f.m mVar = new f.m(intent);
            if (mVar.i() && (iH = mVar.h()) > 0) {
                String str = p0.k(l.this.getActivity(), iH) + '\n' + ((Object) l.this.k(R.string.code)) + iH;
                if (iH == 26) {
                    str = str + "\n" + ((Object) l.this.k(R.string.never_intercept_power_key));
                    l.this.D.setTextColor(-65536);
                } else {
                    l.this.e().putInt("result", iH);
                }
                l.this.D.setText(str);
            }
            mVar.k();
            l.this.B();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void B() {
        b.s0.B(c(), 5);
        if (this.C != null) {
            getActivity().unregisterReceiver(this.C);
            this.C = null;
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        TextView textView = new TextView(activity);
        this.D = textView;
        textView.setText(ApplicationMain.isModuleActivated() ? R.string.click_a_key_to_add : R.string.module_not_activated);
        x xVarG = g();
        this.D.setTextSize(0, xVarG.f638c);
        int i = xVarG.f641f;
        this.D.setPadding(i, i, i, i);
        return new AlertDialog.Builder(activity).setTitle(R.string.add_key).setView(this.D).setPositiveButton(android.R.string.ok, new a()).setNegativeButton(android.R.string.cancel, j0.b.B).create();
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        if (ApplicationMain.isModuleActivated()) {
            B();
        }
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        if (ApplicationMain.isModuleActivated()) {
            this.C = new b();
            getActivity().registerReceiver(this.C, new IntentFilter(b.s0.L));
            b.s0.B(c(), 4);
        }
    }
}
