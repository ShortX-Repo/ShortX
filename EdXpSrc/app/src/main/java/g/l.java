package g;

import a.b;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassifier;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import cc.xposed.R;
import f.o0;
import f.p0;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class l extends FrameLayout {
    private static CharSequence L;
    private final e A;
    private final f B;
    private int C;
    private boolean D;
    private FrameLayout E;
    private FrameLayout.LayoutParams F;
    private EditText G;
    private v H;
    private g.f I;
    private int J;
    private List<ResolveInfo> K;
    private final int z;

    class a extends FrameLayout {
        a(Context context) {
            super(context);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            super.onTouchEvent(motionEvent);
            return true;
        }
    }

    class b extends EditText {
        b(Context context) {
            super(context);
        }

        @Override // android.widget.TextView
        public TextClassifier getTextClassifier() {
            return Build.VERSION.SDK_INT >= 26 ? TextClassifier.NO_OP : super.getTextClassifier();
        }

        @Override // android.view.View
        public ActionMode startActionMode(ActionMode.Callback callback) {
            return l.this.n(callback);
        }

        @Override // android.view.View
        public ActionMode startActionMode(ActionMode.Callback callback, int i) {
            return l.this.n(callback);
        }
    }

    private final class c implements ActionMode.Callback {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final ActionMode.Callback f563a;

        c(ActionMode.Callback callback) {
            this.f563a = callback;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return l.this.s(menuItem.getItemId());
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            this.f563a.onCreateActionMode(actionMode, menu);
            menu.clear();
            l.this.p();
            l.this.t(menu);
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            this.f563a.onDestroyActionMode(actionMode);
            if (l.this.I != null) {
                l.this.I.u();
                l.this.I = null;
            }
            l.this.H = null;
            l.this.K = null;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (l.this.I == null) {
                return true;
            }
            l.this.I.w(l.this.t(menu));
            return true;
        }
    }

    private static final class d extends ContextWrapper {
        public d(Context context) {
            super(context);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Context getApplicationContext() {
            Context applicationContext = super.getApplicationContext();
            return applicationContext != null ? applicationContext : getBaseContext();
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String str) {
            return "window".equals(str) ? new g((WindowManager) super.getSystemService(str)) : super.getSystemService(str);
        }
    }

    public interface e {
        void a(Intent intent);

        boolean b(CharSequence charSequence);
    }

    public interface f {
        void a();
    }

    private static final class g implements WindowManager {
        private final WindowManager z;

        g(WindowManager windowManager) {
            this.z = windowManager;
        }

        private static boolean a(ViewGroup.LayoutParams layoutParams) {
            if (!(layoutParams instanceof WindowManager.LayoutParams)) {
                return true;
            }
            WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) layoutParams;
            if (layoutParams2.type != 1002) {
                return true;
            }
            layoutParams2.type = 2002;
            layoutParams2.flags |= 256;
            return true;
        }

        @Override // android.view.ViewManager
        public void addView(View view, ViewGroup.LayoutParams layoutParams) {
            if (a(layoutParams)) {
                this.z.addView(view, layoutParams);
            }
        }

        @Override // android.view.WindowManager
        public Display getDefaultDisplay() {
            return this.z.getDefaultDisplay();
        }

        @Override // android.view.ViewManager
        public void removeView(View view) {
            this.z.removeView(view);
        }

        @Override // android.view.WindowManager
        public void removeViewImmediate(View view) {
            this.z.removeViewImmediate(view);
        }

        @Override // android.view.ViewManager
        public void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams) {
            if (a(layoutParams)) {
                this.z.updateViewLayout(view, layoutParams);
            }
        }
    }

    public l(Context context, int i, e eVar, f fVar) {
        super(Build.VERSION.SDK_INT >= 30 ? new d(context) : context);
        this.C = 0;
        this.D = false;
        this.E = null;
        this.F = null;
        this.G = null;
        this.H = null;
        this.I = null;
        this.J = 0;
        this.K = null;
        this.z = i;
        this.A = eVar;
        this.B = fVar;
    }

    private ActionMode.Callback A(ActionMode.Callback callback) {
        return new c(callback);
    }

    private CharSequence getShareText() {
        String strN = p0.n("share");
        return strN != null ? strN : getContext().getText(R.string.share);
    }

    private void k() {
        try {
            v vVar = this.H;
            if (vVar != null) {
                vVar.finish();
                this.H = null;
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
                if (inputMethodManager == null || !inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0)) {
                    o(false);
                }
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private boolean l() {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
        return clipboardManager != null && clipboardManager.hasPrimaryClip();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ActionMode n(ActionMode.Callback callback) {
        if (callback == null) {
            return null;
        }
        try {
            v vVar = this.H;
            if (vVar != null) {
                vVar.finish();
            }
            ActionMode.Callback callbackA = A(callback);
            v vVar2 = new v(this.G, callbackA);
            if (callbackA.onCreateActionMode(vVar2, vVar2.getMenu())) {
                g.f fVar = new g.f(this, this.G, vVar2, this.z);
                this.I = fVar;
                vVar2.c(fVar);
                this.H = vVar2;
            } else {
                this.H = null;
            }
            return this.H;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void p() {
        String str;
        Context context = getContext();
        List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.PROCESS_TEXT").setType("text/plain"), 0);
        if (listQueryIntentActivities.size() > 0) {
            this.K = new ArrayList(4);
            for (ResolveInfo resolveInfo : listQueryIntentActivities) {
                if (!f.l.j.equals(resolveInfo.activityInfo.packageName)) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    if (!activityInfo.exported || ((str = activityInfo.permission) != null && context.checkCallingOrSelfPermission(str) != 0)) {
                    }
                }
                this.K.add(resolveInfo);
                if (this.K.size() >= 4) {
                    return;
                }
            }
        }
    }

    private static boolean q(String str) {
        return (str.startsWith("http://") || str.startsWith("https://")) && Patterns.WEB_URL.matcher(str).matches();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void r(View view) {
        e eVar = this.A;
        if (eVar != null) {
            try {
                if (!eVar.b(this.G.getText())) {
                    return;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        o(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean s(int i) {
        try {
            switch (i) {
                case 268435713:
                    y();
                    break;
                case 268435714:
                    x();
                    break;
                default:
                    if (i >= 268435715 && i < 268435719) {
                        w(this.K.get(i - 268435715));
                    }
                    break;
            }
            return true;
        } catch (Throwable th) {
            z(th.toString());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean t(Menu menu) {
        int i;
        EditText editText = this.G;
        if (editText == null) {
            return false;
        }
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = this.G.getSelectionEnd();
        int length = this.G.length();
        boolean zHasSelection = this.G.hasSelection();
        Resources system = Resources.getSystem();
        if (zHasSelection) {
            menu.add(0, android.R.id.cut, 1, system.getText(android.R.string.cut));
            menu.add(0, android.R.id.copy, 2, system.getText(android.R.string.copy));
            i = 3;
        } else {
            i = 0;
        }
        if (l()) {
            menu.add(0, android.R.id.paste, 4, system.getText(android.R.string.paste));
            i |= 4;
        }
        if (length > 0 && (selectionStart != 0 || selectionEnd != length)) {
            menu.add(0, android.R.id.selectAll, 8, system.getText(android.R.string.selectAll));
            i |= 8;
        }
        if (zHasSelection) {
            menu.add(0, 268435713, 16, getShareText());
            menu.add(0, 268435714, 32, system.getText(android.R.string.search_go));
            i |= 48;
            List<ResolveInfo> list = this.K;
            if (list != null && list.size() > 0) {
                int iMin = Math.min(this.K.size(), 4);
                for (int i2 = 0; i2 < iMin; i2++) {
                    try {
                        menu.add(0, 268435715 + i2, 64, this.K.get(i2).loadLabel(getContext().getPackageManager()));
                        i |= 64;
                    } catch (Throwable th) {
                        f.v.d(th);
                    }
                }
            }
        }
        if (this.J == i) {
            return false;
        }
        this.J = i;
        return true;
    }

    private void v(Intent intent) {
        intent.addFlags(268435456);
        e eVar = this.A;
        if (eVar != null) {
            eVar.a(intent);
        } else {
            getContext().startActivity(intent);
        }
        o(true);
    }

    private void w(ResolveInfo resolveInfo) {
        Intent intentPutExtra = new Intent("android.intent.action.PROCESS_TEXT").setType("text/plain").putExtra("android.intent.extra.PROCESS_TEXT", getSelectedText().trim()).putExtra("android.intent.extra.PROCESS_TEXT_READONLY", true);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        v(intentPutExtra.setClassName(activityInfo.packageName, activityInfo.name));
    }

    private void x() {
        String strTrim = getSelectedText().trim();
        v(q(strTrim) ? new Intent("android.intent.action.VIEW", Uri.parse(strTrim)) : new Intent("android.intent.action.WEB_SEARCH").putExtra("query", strTrim));
    }

    private void y() {
        v(Intent.createChooser(new Intent("android.intent.action.SEND").setType("text/plain").putExtra("android.intent.extra.TEXT", getSelectedText()).addFlags(268435456), null));
    }

    private void z(String str) {
        try {
            if (o0.d() == 0) {
                Toast.makeText(getContext(), str, 1).show();
            } else {
                a.b.t(getContext(), new b.u2(str));
            }
        } catch (Throwable unused) {
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.isCanceled() || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
            return super.dispatchKeyEvent(keyEvent);
        }
        k();
        return true;
    }

    public String getSelectedText() {
        String string = this.G.getText().toString();
        int selectionStart = this.G.getSelectionStart();
        int selectionEnd = this.G.getSelectionEnd();
        return string.substring(Math.max(0, Math.min(selectionStart, selectionEnd)), Math.max(0, Math.max(selectionStart, selectionEnd)));
    }

    public void j(int i) {
        if (!this.D || Build.VERSION.SDK_INT >= 30) {
            return;
        }
        if (i <= 0) {
            this.F.height = -1;
        } else {
            int i2 = a0.n(getContext(), new Point()).y / 4;
            if (i < i2) {
                i = i2;
            }
            this.F.height = i + this.C;
        }
        this.E.setLayoutParams(this.F);
    }

    public boolean m(CharSequence charSequence) {
        return a0.e(this.G, charSequence);
    }

    public void o(boolean z) {
        if (this.D) {
            if (z) {
                try {
                    L = this.G.getText();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            this.G.clearFocus();
            Context context = getContext();
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService("input_method");
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
            v vVar = this.H;
            if (vVar != null) {
                vVar.finish();
                this.H = null;
            }
            ((WindowManager) context.getSystemService("window")).removeView(this);
            f fVar = this.B;
            if (fVar != null) {
                fVar.a();
            }
            this.D = false;
        }
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                Insets insets = windowInsets.getInsets(WindowInsets.Type.ime() | WindowInsets.Type.systemBars());
                FrameLayout.LayoutParams layoutParams = this.F;
                layoutParams.leftMargin = insets.left;
                layoutParams.rightMargin = insets.right;
                layoutParams.topMargin = Math.max(insets.top - this.C, 0);
                this.F.bottomMargin = Math.max(insets.bottom - this.C, 0);
                this.E.setLayoutParams(this.F);
                return WindowInsets.CONSUMED;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            super.onLayout(z, i, i2, i3, i4);
        } catch (Throwable unused) {
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0) {
            return true;
        }
        k();
        return true;
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        v vVar = this.H;
        if (vVar != null) {
            vVar.onWindowFocusChanged(z);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return n(callback);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback, int i) {
        return n(callback);
    }

    public void u(CharSequence charSequence, CharSequence charSequence2, Drawable drawable) {
        String str;
        if (this.D) {
            f.v.c("showing");
            o(false);
        }
        if (charSequence == null || charSequence.length() == 0) {
            CharSequence charSequence3 = L;
            str = (charSequence3 == null || charSequence3.length() <= 0) ? "" : L;
        } else {
            str = charSequence;
        }
        L = null;
        try {
            Context context = getContext();
            int i = a0.r(this.z) ? -1 : -16777216;
            x xVarC = x.c(context);
            int i2 = xVarC.f637b;
            int i3 = xVarC.f640e;
            this.C = xVarC.f641f * 2;
            a aVar = new a(context);
            aVar.setBackgroundColor(this.z);
            b bVar = new b(context);
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 29) {
                bVar.setForceDarkAllowed(false);
            }
            bVar.setBackground(new j(bVar, i3 / 8));
            bVar.setTextColor(i);
            bVar.setTextSize(0, i3);
            bVar.setInputType(131073);
            bVar.setText(str);
            bVar.setHint(charSequence2);
            bVar.setSelection(str.length());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
            layoutParams.setMarginStart(this.C);
            layoutParams.setMarginEnd(this.C + i2);
            int i5 = this.C;
            layoutParams.bottomMargin = i5;
            layoutParams.topMargin = i5;
            aVar.addView(bVar, layoutParams);
            p pVar = new p(context);
            t tVar = new t(drawable, null, 0.75f);
            h.a().e(tVar, (-16777216) | bVar.getHighlightColor());
            pVar.setImageDrawable(tVar);
            pVar.setBackground(a0.l());
            pVar.setOnClickListener(new View.OnClickListener() { // from class: g.k
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.r(view);
                }
            });
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(i2, i2, 8388629);
            layoutParams2.setMarginEnd(this.C);
            aVar.addView(pVar, layoutParams2);
            FrameLayout frameLayout = new FrameLayout(context);
            this.E = frameLayout;
            frameLayout.setPadding(0, a0.q() - layoutParams.topMargin, 0, 0);
            this.E.addView(aVar, new FrameLayout.LayoutParams(-1, -2, 80));
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, -1, 48);
            this.F = layoutParams3;
            layoutParams3.bottomMargin = this.C;
            addView(this.E, layoutParams3);
            WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams(-1, -1, 2002, i4 >= 30 ? 16777472 : 16778496, 1);
            layoutParams4.softInputMode = 48;
            if (i4 >= 28) {
                layoutParams4.layoutInDisplayCutoutMode = 1;
            }
            a0.b(layoutParams4, "Editor");
            this.G = bVar;
            ((WindowManager) context.getSystemService("window")).addView(this, layoutParams4);
            this.D = true;
        } catch (Throwable th) {
            f.v.d(th);
        }
    }
}
