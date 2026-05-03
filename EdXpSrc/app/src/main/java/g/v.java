package g;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;

/* JADX INFO: loaded from: classes.dex */
class v extends ActionMode {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final ActionMode.Callback f627a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final EditText f628b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final u f629c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private f f630d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Runnable f631e = new a();

    class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (v.this.f630d != null) {
                    v.this.invalidate();
                    v.this.f630d.v(false);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    v(EditText editText, ActionMode.Callback callback) {
        this.f627a = callback;
        this.f628b = editText;
        this.f629c = new u(editText.getContext());
    }

    ActionMode.Callback b() {
        return this.f627a;
    }

    void c(f fVar) {
        this.f630d = fVar;
    }

    @Override // android.view.ActionMode
    public void finish() {
        try {
            this.f627a.onDestroyActionMode(this);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    @Override // android.view.ActionMode
    public View getCustomView() {
        return null;
    }

    @Override // android.view.ActionMode
    public Menu getMenu() {
        return this.f629c;
    }

    @Override // android.view.ActionMode
    public MenuInflater getMenuInflater() {
        return new MenuInflater(this.f628b.getContext());
    }

    @Override // android.view.ActionMode
    public CharSequence getSubtitle() {
        return null;
    }

    @Override // android.view.ActionMode
    public CharSequence getTitle() {
        return null;
    }

    @Override // android.view.ActionMode
    public void hide(long j) {
        if (j == -1) {
            try {
                j = Build.VERSION.SDK_INT >= 23 ? ViewConfiguration.getDefaultActionModeHideDuration() : 2000L;
            } catch (Throwable th) {
                f.v.d(th);
                return;
            }
        }
        if (j > 3000) {
            j = 3000;
        }
        this.f628b.removeCallbacks(this.f631e);
        if (j <= 0) {
            this.f631e.run();
            return;
        }
        f fVar = this.f630d;
        if (fVar != null) {
            fVar.v(true);
        }
        this.f628b.postDelayed(this.f631e, j);
    }

    @Override // android.view.ActionMode
    public void invalidate() {
        this.f629c.clear();
        this.f627a.onPrepareActionMode(this, this.f629c);
    }

    @Override // android.view.ActionMode
    public void onWindowFocusChanged(boolean z) {
        if (z) {
            return;
        }
        finish();
    }

    @Override // android.view.ActionMode
    public void setCustomView(View view) {
    }

    @Override // android.view.ActionMode
    public void setSubtitle(int i) {
    }

    @Override // android.view.ActionMode
    public void setSubtitle(CharSequence charSequence) {
    }

    @Override // android.view.ActionMode
    public void setTitle(int i) {
    }

    @Override // android.view.ActionMode
    public void setTitle(CharSequence charSequence) {
    }
}
