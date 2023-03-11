package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.Toast;

import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IUI;

public class UIImpl implements IUI {
    private final Context context;
    private final ShortXContext s;

    public UIImpl(Context context, ShortXContext s) {
        this.context = context;
        this.s = s;
    }

    @Override
    public void showShortToast(Object msg) {
        Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLongToast(Object msg) {
        Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog(String title, String msg, String yes) {
        AlertDialog d = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(yes, null).setCancelable(false).create();
        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        d.show();
    }
}
