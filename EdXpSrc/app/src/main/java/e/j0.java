package e;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j0;
import f.p0;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class j0 extends Activity {
    private static final int H = View.generateViewId();
    private static Context I = null;
    private static Resources J = null;
    private static Toast K = null;
    private static final SparseArray<Object> L = new SparseArray<>();
    private int z = 0;
    private Bundle A = null;
    private FrameLayout B = null;
    private long C = 0;
    private d D = null;
    private FrameLayout E = null;
    private TextView F = null;
    private boolean G = false;

    class a extends FrameLayout {
        a(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 4) {
                if (keyCode != 23) {
                    return super.dispatchKeyEvent(keyEvent);
                }
                j0.this.w();
                return true;
            }
            j0.this.n();
            Fragment fragmentFindFragmentById = j0.this.getFragmentManager().findFragmentById(j0.H);
            if (fragmentFindFragmentById instanceof j) {
                ((j) fragmentFindFragmentById).A0();
            }
            return true;
        }
    }

    public static class b extends DialogFragment {
        protected static final DialogInterface.OnClickListener B = new DialogInterface.OnClickListener() { // from class: e.k0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                j0.b.l(dialogInterface, i);
            }
        };
        private d A;
        private Bundle z;

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void l(DialogInterface dialogInterface, int i) {
        }

        protected Context c() {
            return j0.I;
        }

        protected ApplicationMain d() {
            return (ApplicationMain) getActivity().getApplication();
        }

        protected Bundle e() {
            if (this.z == null) {
                this.z = new Bundle();
            }
            return this.z;
        }

        protected final Drawable f(int i) {
            Drawable drawable = j0.J.getDrawable(i);
            g.h.a().h(drawable, j0.J);
            return drawable;
        }

        protected final g.x g() {
            return j().f392a;
        }

        protected c h() {
            Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(p0.h(getTag()));
            if (fragmentFindFragmentByTag instanceof c) {
                return (c) fragmentFindFragmentByTag;
            }
            return null;
        }

        protected j0 i() {
            return (j0) getActivity();
        }

        final d j() {
            if (this.A == null) {
                this.A = i().D;
            }
            return this.A;
        }

        protected final CharSequence k(int i) {
            return j0.J.getText(i);
        }

        protected g.p m(Drawable drawable) {
            Context activity = getActivity();
            if (activity == null) {
                activity = j0.I;
            }
            g.p pVar = new g.p(activity);
            pVar.setImageDrawable(drawable);
            return pVar;
        }

        protected void n() {
            dismiss();
        }

        protected void o(Bundle bundle) {
            c cVarH = h();
            if (cVarH != null) {
                cVarH.K(bundle);
            }
        }

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (bundle == null || this.z != null) {
                return;
            }
            this.z = bundle.getBundle("ElemDialogFragment_bundle");
        }

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            Bundle bundle2 = this.z;
            if (bundle2 != null) {
                bundle.putBundle("ElemDialogFragment_bundle", bundle2);
            }
            super.onSaveInstanceState(bundle);
        }

        protected void p(String str, int i) {
            Bundle bundle = new Bundle(1);
            bundle.putInt(str, i);
            o(bundle);
        }

        protected void q(String str, Parcelable parcelable) {
            Bundle bundle = new Bundle(1);
            bundle.putParcelable(str, parcelable);
            o(bundle);
        }

        protected final void r(View view) {
            j0.x(view, e());
        }

        public void s(int i) {
            t(k(i));
        }

        public void t(CharSequence charSequence) {
            j0.z(charSequence);
        }
    }

    public static class c extends Fragment {
        private d C;
        private Bundle z = null;
        private boolean A = false;
        private boolean B = false;
        private View D = null;

        /* JADX INFO: Access modifiers changed from: private */
        public void K(final Bundle bundle) {
            View view = getView();
            Runnable runnable = new Runnable() { // from class: e.l0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.x(bundle);
                }
            };
            if (view != null) {
                view.post(runnable);
            } else {
                f.h0.a().post(runnable);
            }
        }

        private boolean Q(View view) {
            if (view.requestFocus(33)) {
                return true;
            }
            if (view.isFocusableInTouchMode()) {
                return false;
            }
            view.setFocusableInTouchMode(true);
            boolean zRequestFocus = view.requestFocus(33);
            view.setFocusableInTouchMode(false);
            return zRequestFocus;
        }

        private boolean R() {
            View view = this.D;
            if (view != null && !view.isFocused()) {
                if (this.D.isFocusable()) {
                    return Q(this.D);
                }
                View view2 = this.D;
                if (view2 instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view2;
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View childAt = viewGroup.getChildAt(childCount);
                        if (childAt.isFocusable()) {
                            return Q(childAt);
                        }
                    }
                }
            }
            return false;
        }

        private boolean e() {
            j0 j0VarR = r();
            long j = j0VarR.C;
            long jUptimeMillis = SystemClock.uptimeMillis();
            j0VarR.C = jUptimeMillis;
            return jUptimeMillis - j > 500;
        }

        private boolean w() {
            View view = this.D;
            if (view != null && view.isFocused()) {
                return true;
            }
            View view2 = this.D;
            if (!(view2 instanceof ViewGroup)) {
                return false;
            }
            ViewGroup viewGroup = (ViewGroup) view2;
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                if (viewGroup.getChildAt(childCount).isFocused()) {
                    return true;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void x(Bundle bundle) {
            J(bundle, h().getInt("ElemFragment_request_code"));
        }

        protected void A() {
            View view;
            if ("0".equals(getTag()) || (view = getView()) == null) {
                return;
            }
            g.a0.y(view, new TranslateAnimation(0.0f, view.getWidth(), 0.0f, 0.0f));
        }

        protected void B() {
            a0(h().getCharSequence("ElemFragment_subtitle"));
            T(null);
        }

        protected void C() {
        }

        protected boolean D(int i, KeyEvent keyEvent) {
            return false;
        }

        protected boolean E(int i, KeyEvent keyEvent) {
            j0 j0VarR;
            if (this.D != null && ((i == 20 || i == 82) && !keyEvent.isCanceled())) {
                if (w()) {
                    if (i == 20 && (j0VarR = r()) != null) {
                        j0VarR.B.requestFocus(130);
                        return true;
                    }
                } else if (i == 82) {
                    return R();
                }
            }
            return false;
        }

        protected void F(Intent intent) {
        }

        protected void G() {
            if (Build.VERSION.SDK_INT < 28) {
                A();
            }
            I();
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null && "1".equals(getTag())) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            Z(null);
        }

        protected void H() {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null && "1".equals(getTag())) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            z(getView());
            c cVarP = p();
            if (cVarP != null) {
                cVarP.I();
            }
        }

        protected void I() {
            if (this.D != null) {
                try {
                    ActionBar actionBar = getActivity().getActionBar();
                    if (actionBar != null && actionBar.getCustomView() == this.D) {
                        actionBar.setCustomView((View) null);
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
                this.D = null;
            }
        }

        protected void J(Bundle bundle, int i) {
            f.v.c("Unhandled result.");
        }

        protected final void L() {
            InputMethodManager inputMethodManager;
            this.B = true;
            try {
                if ("0".equals(getTag())) {
                    getActivity().finish();
                } else {
                    getFragmentManager().beginTransaction().remove(this).show(p()).commit();
                }
                if (this.D == null || (inputMethodManager = (InputMethodManager) j0.I.getSystemService("input_method")) == null) {
                    return;
                }
                inputMethodManager.hideSoftInputFromWindow(this.D.getWindowToken(), 0);
            } catch (Throwable th) {
                g0(th);
            }
        }

        protected final void M(b bVar) {
            N(bVar, -1);
        }

        protected final void N(b bVar, int i) {
            d dVar = this.C;
            if (dVar != null) {
                bVar.A = dVar;
            }
            try {
                if (e()) {
                    h().putInt("ElemFragment_request_code", i);
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager == null) {
                        return;
                    }
                    fragmentManager.beginTransaction().add(bVar, p0.o(getTag())).commit();
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        protected final void O(c cVar) {
            P(cVar, -1);
        }

        protected final void P(c cVar, int i) {
            d dVar = this.C;
            if (dVar != null) {
                cVar.C = dVar;
            }
            try {
                if (e()) {
                    h().putInt("ElemFragment_request_code", i);
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager == null) {
                        return;
                    }
                    fragmentManager.beginTransaction().add(j0.H, cVar, p0.o(getTag())).hide(this).commit();
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        protected void S(int i, View.OnClickListener onClickListener) {
            if (this.B) {
                return;
            }
            Context activity = getActivity();
            if (activity == null) {
                activity = j0.I;
            }
            T(v(activity, i, onClickListener));
        }

        protected final void T(View view) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                if (view == null) {
                    actionBar.setCustomView((View) null);
                } else {
                    g.x xVarL = l();
                    ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(-2, -1, 8388629);
                    layoutParams.rightMargin = xVarL.f641f;
                    actionBar.setCustomView(view, layoutParams);
                    this.D = view;
                }
                actionBar.setDisplayShowCustomEnabled(true);
            }
        }

        protected void U(Bundle bundle) {
            c cVarP = p();
            if (cVarP != null) {
                cVarP.K(bundle);
            }
        }

        protected void V(String str, int i) {
            Bundle bundle = new Bundle(1);
            bundle.putInt(str, i);
            U(bundle);
        }

        protected void W(String str, Parcelable parcelable) {
            Bundle bundle = new Bundle(1);
            bundle.putParcelable(str, parcelable);
            U(bundle);
        }

        protected void X(String str, boolean z) {
            Bundle bundle = new Bundle(1);
            bundle.putBoolean(str, z);
            U(bundle);
        }

        protected final void Y(View view) {
            j0.x(view, h());
        }

        protected final void Z(Object obj) {
            int i;
            if (obj == null) {
                Bundle bundle = this.z;
                if (bundle == null || (i = bundle.getInt("BaseActivity_fragment_id", 0)) == 0) {
                    return;
                }
                j0.L.remove(i);
                return;
            }
            Bundle bundleH = h();
            int iHashCode = bundleH.getInt("BaseActivity_fragment_id", 0);
            if (iHashCode == 0) {
                iHashCode = hashCode();
                bundleH.putInt("BaseActivity_fragment_id", iHashCode);
            }
            j0.L.append(iHashCode, obj);
        }

        protected final void a0(CharSequence charSequence) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(charSequence);
            }
        }

        public final c b0(CharSequence charSequence) {
            h().putCharSequence("ElemFragment_subtitle", charSequence);
            return this;
        }

        protected final void c0(int i) {
            d0(u(i));
        }

        protected final void d0(CharSequence charSequence) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(charSequence);
            }
        }

        public final void e0(int i) {
            j0.z(u(i));
        }

        protected final Context f() {
            return j0.I;
        }

        public final void f0(CharSequence charSequence) {
            j0.z(charSequence);
        }

        protected ApplicationMain g() {
            return (ApplicationMain) getActivity().getApplication();
        }

        public final void g0(Throwable th) {
            String message = th.getMessage();
            if (message == null) {
                message = th.toString();
            }
            f0(message);
            f.v.d(th);
        }

        protected final Bundle h() {
            if (this.z == null) {
                this.z = new Bundle();
            }
            return this.z;
        }

        protected final Drawable i(int i) {
            Drawable drawable = j0.J.getDrawable(i);
            g.h.a().h(drawable, j0.J);
            return drawable;
        }

        protected final Drawable j(int i) {
            Drawable drawable = j0.J.getDrawable(i);
            g.h.a().b(drawable);
            return drawable;
        }

        protected final g.x k() {
            return t().f393b;
        }

        protected final g.x l() {
            return t().f392a;
        }

        protected final <E> List<E> m(Class<E> cls) {
            Object objS = s();
            if (!(objS instanceof List)) {
                return null;
            }
            List<E> list = (List) objS;
            if (list.size() <= 0 || !cls.isInstance(list.get(0))) {
                return null;
            }
            return list;
        }

        protected final Fragment n() {
            try {
                return getFragmentManager().findFragmentByTag(p0.o(getTag()));
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        protected final CharSequence o() {
            return h().getCharSequence("ElemFragment_subtitle");
        }

        @Override // android.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (bundle == null) {
                this.A = false;
                return;
            }
            if (this.z == null) {
                this.z = bundle.getBundle("ElemFragment_bundle");
            }
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null && "1".equals(getTag())) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            if (bundle.getBoolean("ElemFragment_hidden", false)) {
                getFragmentManager().beginTransaction().hide(this).commit();
            }
            this.A = true;
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            if (Build.VERSION.SDK_INT < 28 || this.A) {
                return;
            }
            G();
        }

        @Override // android.app.Fragment
        public void onHiddenChanged(boolean z) {
            super.onHiddenChanged(z);
            if (z || this.B) {
                return;
            }
            B();
            C();
        }

        @Override // android.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            Bundle bundle2 = this.z;
            if (bundle2 != null) {
                bundle.putBundle("ElemFragment_bundle", bundle2);
            }
            this.A = true;
            if (isHidden()) {
                bundle.putBoolean("ElemFragment_hidden", true);
            } else {
                bundle.remove("ElemFragment_hidden");
            }
        }

        @Override // android.app.Fragment
        public void onStart() {
            super.onStart();
            if (!this.A) {
                H();
            }
            if (!isHidden()) {
                B();
                C();
            }
            this.A = false;
        }

        @Override // android.app.Fragment
        public void onStop() {
            super.onStop();
            if (Build.VERSION.SDK_INT >= 28 || this.A) {
                return;
            }
            G();
        }

        protected final c p() {
            try {
                Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(p0.h(getTag()));
                if (fragmentFindFragmentByTag instanceof c) {
                    return (c) fragmentFindFragmentByTag;
                }
                return null;
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        protected final Resources q() {
            return j0.J;
        }

        protected final j0 r() {
            return (j0) getActivity();
        }

        protected final Object s() {
            int i;
            Bundle bundle = this.z;
            if (bundle == null || (i = bundle.getInt("BaseActivity_fragment_id", 0)) == 0) {
                return null;
            }
            return j0.L.get(i);
        }

        final d t() {
            if (this.C == null) {
                this.C = r().D;
            }
            return this.C;
        }

        protected final CharSequence u(int i) {
            return j0.J.getText(i);
        }

        View v(Context context, int i, View.OnClickListener onClickListener) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setFocusable(true);
            frameLayout.setOnClickListener(onClickListener);
            frameLayout.setBackground(g.a0.l());
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(i);
            int i2 = (int) (l().f637b * 0.75f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i2, i2, 17);
            int i3 = l().f641f / 2;
            layoutParams.rightMargin = i3;
            layoutParams.leftMargin = i3;
            frameLayout.addView(imageView, layoutParams);
            return frameLayout;
        }

        protected void y() {
            L();
        }

        protected void z(View view) {
            if (view == null || "0".equals(getTag())) {
                return;
            }
            g.a0.y(view, new TranslateAnimation(getActivity().getWindow().getDecorView().getWidth(), 0.0f, 0.0f, 0.0f));
        }
    }

    static class d {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final g.x f392a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final g.x f393b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final g.x f394c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        final int f395d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        final int f396e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        final int f397f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        final int f398g;
        final int h;
        final int i;
        final LinearLayout.LayoutParams j;
        final LinearLayout.LayoutParams k;
        final LinearLayout.LayoutParams l;
        final LinearLayout.LayoutParams m;
        final AbsListView.LayoutParams n;
        int o;
        int p;

        d(Context context, boolean z) {
            int i;
            g.x xVarC = g.x.c(context);
            this.f392a = xVarC;
            this.f393b = g.x.b(context);
            g.x xVarA = g.x.a(context);
            this.f394c = xVarA;
            xVarC = a.m.b().a(4) != 2 ? xVarA : xVarC;
            int i2 = xVarC.f641f;
            this.f395d = i2;
            int i3 = xVarC.f642g;
            this.f396e = i3;
            this.f397f = (int) (i3 * 1.5f);
            this.f398g = xVarC.f638c;
            this.h = xVarC.f640e;
            this.i = xVarC.f639d;
            int i4 = xVarC.f637b;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i4, i4);
            this.j = layoutParams;
            layoutParams.gravity = 16;
            layoutParams.rightMargin = i2;
            layoutParams.leftMargin = i2;
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2, 1.0f);
            this.k = layoutParams2;
            layoutParams2.gravity = 16;
            layoutParams2.rightMargin = i2;
            layoutParams2.leftMargin = i2;
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, xVarC.f636a);
            this.l = layoutParams3;
            layoutParams3.gravity = 16;
            int i5 = xVarC.f636a;
            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(i5 / 2, i5 / 2);
            this.m = layoutParams4;
            layoutParams4.gravity = 16;
            this.n = new AbsListView.LayoutParams(-1, xVarC.f636a);
            if (z) {
                this.o = -1;
                i = -3355444;
            } else {
                this.o = -16777216;
                i = -12303292;
            }
            this.p = i;
        }
    }

    private int q() {
        int iA = a.m.b().a(1);
        return iA != 1 ? iA != 2 ? iA != 3 ? (iA == 4 && s()) ? R.style.AppThemeDark : R.style.AppTheme : R.style.AppThemeBlack : R.style.AppThemeLightGray : R.style.AppThemeDark;
    }

    private boolean r(boolean z) {
        if (Build.VERSION.SDK_INT < 29) {
            try {
                return g.a0.s(getWindow().getDecorView().getBackground());
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return z;
    }

    private boolean s() {
        return (getResources().getConfiguration().uiMode & 48) == 32;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void t(View view) {
        w();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void u() {
        this.G = true;
        TextView textView = this.F;
        if (textView != null) {
            textView.requestFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void w() {
        if (this.G) {
            Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
            if (fragmentFindFragmentById instanceof j) {
                ((j) fragmentFindFragmentById).x1();
            }
            this.G = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void x(View view, Bundle bundle) {
        if (view == null || bundle == null) {
            return;
        }
        int iGenerateViewId = bundle.getInt("BaseActivity_view_id", -1);
        if (iGenerateViewId == -1) {
            iGenerateViewId = View.generateViewId();
        }
        view.setId(iGenerateViewId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void z(CharSequence charSequence) {
        Toast toast = K;
        if (toast == null) {
            K = Toast.makeText(I, charSequence, 0);
        } else if (Build.VERSION.SDK_INT >= 24) {
            toast.cancel();
            K = Toast.makeText(I, charSequence, 0);
        } else {
            toast.setText(charSequence);
        }
        K.show();
    }

    public void n() {
        if (this.E != null) {
            getWindowManager().removeView(this.E);
            this.E = null;
            this.F = null;
        }
    }

    public Bundle o() {
        if (this.A == null) {
            this.A = new Bundle();
        }
        return this.A;
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
        if (fragmentFindFragmentById instanceof c) {
            ((c) fragmentFindFragmentById).y();
            return;
        }
        if (fragmentFindFragmentById instanceof b) {
            ((b) fragmentFindFragmentById).n();
            return;
        }
        if (fragmentFindFragmentById == null) {
            f.v.d(new RuntimeException("Null pointer of fragment."));
        } else {
            f.v.d(new RuntimeException("Invalid fragment type: " + fragmentFindFragmentById.getClass().getName()));
        }
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        if (I == null) {
            Context applicationContext = getApplicationContext();
            I = applicationContext;
            J = applicationContext.getResources();
        }
        if (this.z == 0) {
            int iQ = q();
            this.z = iQ;
            super.setTheme(iQ);
        }
        int i = this.z;
        this.D = new d(this, r(i == R.style.AppThemeDark || i == R.style.AppThemeBlack || (i == R.style.AppThemeAuto && s())));
        if (this.A == null && bundle != null) {
            this.A = bundle.getBundle("BaseActivity_bundle");
        }
        super.onCreate(bundle);
        FrameLayout frameLayout = new FrameLayout(this);
        this.B = frameLayout;
        int i2 = H;
        frameLayout.setId(i2);
        setContentView(this.B);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(i2) == null) {
            c cVarV = v();
            if (cVarV == null) {
                finish();
            } else {
                cVarV.C = this.D;
                fragmentManager.beginTransaction().add(i2, cVarV, "0").commit();
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        n();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
        return ((fragmentFindFragmentById instanceof c) && ((c) fragmentFindFragmentById).D(i, keyEvent)) || super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
        if ((fragmentFindFragmentById instanceof c) && ((c) fragmentFindFragmentById).E(i, keyEvent)) {
            return true;
        }
        if (this.E == null || !(i == 23 || i == 66)) {
            return super.onKeyUp(i, keyEvent);
        }
        w();
        return true;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
        if ((fragmentFindFragmentById instanceof c) && "0".equals(fragmentFindFragmentById.getTag())) {
            ((c) fragmentFindFragmentById).F(intent);
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        Fragment fragmentFindFragmentById = getFragmentManager().findFragmentById(H);
        if (!(fragmentFindFragmentById instanceof c) || "0".equals(fragmentFindFragmentById.getTag())) {
            return true;
        }
        ((c) fragmentFindFragmentById).y();
        return true;
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Bundle bundle2 = this.A;
        if (bundle2 != null) {
            bundle.putBundle("BaseActivity_bundle", bundle2);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        ActionBar actionBar;
        super.onStart();
        if (Build.VERSION.SDK_INT >= 21 || (actionBar = getActionBar()) == null) {
            return;
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }

    protected final g.x p() {
        return this.D.f392a;
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        int iQ = q();
        this.z = iQ;
        super.setTheme(iQ);
    }

    protected abstract c v();

    public void y(String str, long j) {
        if (this.E == null) {
            a aVar = new a(this);
            this.E = aVar;
            aVar.setKeepScreenOn(true);
            this.E.setFocusable(true);
            TextView textView = new TextView(this);
            this.F = textView;
            textView.setTextColor(-1);
            this.F.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            g.x xVarP = p();
            this.F.setTextSize(0, xVarP.f638c);
            int i = xVarP.f641f;
            this.F.setPadding(i, i, i, i);
            this.F.setOnClickListener(new View.OnClickListener() { // from class: e.h0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.t(view);
                }
            });
            this.E.addView(this.F, new FrameLayout.LayoutParams(-1, -2, 80));
            getWindowManager().addView(this.E, new WindowManager.LayoutParams(1000, 1024, -3));
        }
        if (str == null) {
            str = "";
        }
        this.F.setText(str + "\n\n" + ((Object) getText(R.string.click_to_continue)));
        this.G = false;
        this.F.postDelayed(new Runnable() { // from class: e.i0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.u();
            }
        }, j + 100);
    }
}
