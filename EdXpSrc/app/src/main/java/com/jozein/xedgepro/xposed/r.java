package com.jozein.xedgepro.xposed;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cc.xposed.R;
import com.jozein.xedgepro.xposed.t3;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import g.l;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class r extends g {
    private static final String X = p2.z + f.l.x + "_clipboard";
    private static final String Y;
    private final List<String> G;
    private final List<String> H;
    private final w1 I;
    private final l0 J;
    private final a.p K;
    private Context L;
    private Handler M;
    private final Runnable N;
    private int O;
    private boolean P;
    private int Q;
    private String R;
    private boolean S;
    private ClipboardManager.OnPrimaryClipChangedListener T;
    private ClipData.Item U;
    private final Runnable V;
    private g.l W;

    class a extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private int f223a = -1;

        a() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                if (this.f223a < 0) {
                    Object[] objArr = methodHookParam.args;
                    int i = 0;
                    if (!(objArr[0] instanceof ClipData)) {
                        i = 1;
                        if (!(objArr[1] instanceof ClipData)) {
                            return;
                        }
                    }
                    this.f223a = i;
                }
                r.this.j0((ClipData) methodHookParam.args[this.f223a]);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class b extends XC_MethodHook {
        b() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if ("android".equals(methodHookParam.args[1])) {
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    class c implements l.e {
        c() {
        }

        @Override // g.l.e
        public void a(Intent intent) {
            r.this.I.n4(intent);
        }

        @Override // g.l.e
        public boolean b(CharSequence charSequence) {
            r.this.N(charSequence);
            return true;
        }
    }

    class d extends t3.b {
        d(Context context) {
            super(context);
        }

        @Override // com.jozein.xedgepro.xposed.t3.c
        public void a() {
            r.this.Q();
        }
    }

    class e extends f {
        final /* synthetic */ o2 O;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        e(Context context, Drawable drawable, Drawable drawable2, Drawable drawable3, int i, int i2, o2 o2Var) {
            super(context, drawable, drawable2, drawable3, i, i2);
            this.O = o2Var;
        }

        @Override // android.widget.AbsListView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.O.c()) {
                return true;
            }
            try {
                return super.onTouchEvent(motionEvent);
            } catch (Throwable th) {
                this.O.b(th);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class f extends g.y {

        class a implements l.e {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            final /* synthetic */ String f227a;

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            final /* synthetic */ int f228b;

            a(String str, int i) {
                this.f227a = str;
                this.f228b = i;
            }

            @Override // g.l.e
            public void a(Intent intent) {
                r.this.I.n4(intent);
            }

            @Override // g.l.e
            public boolean b(CharSequence charSequence) {
                if (charSequence != null && charSequence.length() > 0) {
                    String string = charSequence.toString();
                    if (string.equals(this.f227a)) {
                        return true;
                    }
                    int iIndexOf = r.this.G.indexOf(string);
                    if (iIndexOf < 0) {
                        r.this.G.set(this.f228b, string);
                        r.this.i0();
                    } else if (iIndexOf < this.f228b) {
                        r.this.G.remove(this.f228b);
                        r.this.i0();
                    } else {
                        r.this.G.remove(iIndexOf);
                        r.this.G.set(this.f228b, string);
                        r.this.i0();
                    }
                }
                return true;
            }
        }

        f(Context context, Drawable drawable, Drawable drawable2, Drawable drawable3, int i, int i2) {
            super(context, drawable, drawable2, drawable3, i, i2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void A(String str) {
            r.this.I.o2(str, false);
        }

        @Override // g.y
        protected int q(Context context) {
            if (r.this.g0() <= 0) {
                return 0;
            }
            return r.this.G.size();
        }

        @Override // g.y
        protected CharSequence r(int i) {
            return (CharSequence) r.this.G.get(i);
        }

        @Override // g.y
        protected boolean s(int i) {
            return i < r.this.O;
        }

        @Override // g.y
        protected void u(int i) {
            final String str = (String) r.this.G.get(i);
            if (str != null && str.length() > 0) {
                r.this.M.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.t
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.A(str);
                    }
                }, 200L);
            }
            t3.n().t(20);
        }

        @Override // g.y
        protected void v(int i) {
            if (i < r.this.O) {
                r.this.G.add(r.L(r.this), (String) r.this.G.remove(i));
            } else {
                r.this.G.remove(i);
                if (r.this.G.size() <= 0) {
                    t3.n().t(20);
                }
            }
            r.this.P = true;
        }

        @Override // g.y
        protected void w(int i) {
            t3.n().t(20);
            String str = (String) r(i);
            r.this.T();
            try {
                r rVar = r.this;
                a aVar = new a(str, i);
                final r rVar2 = r.this;
                rVar.S(str, null, aVar, new l.f() { // from class: com.jozein.xedgepro.xposed.s
                    @Override // g.l.f
                    public final void a() {
                        rVar2.Q();
                    }
                });
            } catch (Throwable th) {
                f.v.d(th);
                r.this.Q();
            }
        }

        @Override // g.y
        protected void x(int i) {
            if (i != 0) {
                r.this.G.add(0, (String) r.this.G.remove(i));
                if (i >= r.this.O) {
                    r.K(r.this);
                }
            } else if (r.this.O > 0) {
                return;
            } else {
                r.this.O = 1;
            }
            r.this.P = true;
            t();
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(p2.A);
        sb.append(f.l.r ? "clipboard_pro" : "clipboard");
        Y = sb.toString();
    }

    /* JADX WARN: Illegal instructions before constructor call */
    r(ClassLoader classLoader, w1 w1Var, l0 l0Var, a.p pVar) {
        a aVar;
        String str;
        int i = Build.VERSION.SDK_INT;
        super(i >= 21 ? "com.android.server.clipboard.ClipboardService" : "com.android.server.ClipboardService", classLoader);
        this.G = new ArrayList(16);
        this.H = new ArrayList();
        this.N = new Runnable() { // from class: com.jozein.xedgepro.xposed.o
            @Override // java.lang.Runnable
            public final void run() {
                this.z.n0();
            }
        };
        this.O = 0;
        this.P = false;
        this.Q = 0;
        this.R = Y;
        this.T = null;
        this.U = null;
        this.V = new Runnable() { // from class: com.jozein.xedgepro.xposed.p
            @Override // java.lang.Runnable
            public final void run() {
                this.z.X();
            }
        };
        this.W = null;
        this.I = w1Var;
        this.J = l0Var;
        this.K = pVar;
        try {
            aVar = new a();
        } catch (Throwable th) {
            p2.g(th);
            this.S = false;
        }
        if (i < 33) {
            if (i >= 31) {
                q("setPrimaryClipInternalLocked", aVar);
            } else if (i >= 28) {
                q("setPrimaryClipInternal", aVar);
            } else if (i >= 21) {
                o("setPrimaryClipInternal", aVar);
            } else {
                str = "setPrimaryClip";
            }
            this.S = true;
            if (!this.S || Build.VERSION.SDK_INT < 29) {
            }
            try {
                n("clipboardAccessAllowed", new b());
                return;
            } catch (Throwable th2) {
                p2.g(th2);
                return;
            }
        }
        str = "setPrimaryClipInternalNoClassifyLocked";
        o(str, aVar);
        this.S = true;
        if (this.S) {
        }
    }

    static /* synthetic */ int K(r rVar) {
        int i = rVar.O + 1;
        rVar.O = i;
        return i;
    }

    static /* synthetic */ int L(r rVar) {
        int i = rVar.O - 1;
        rVar.O = i;
        return i;
    }

    private void O(String str) {
        int iIndexOf = this.G.indexOf(str);
        if (iIndexOf >= 0) {
            if (iIndexOf > this.O) {
                this.G.remove(iIndexOf);
                this.G.add(this.O, str);
                return;
            }
            return;
        }
        this.G.add(this.O, str);
        int size = this.G.size();
        if (size > this.K.r()) {
            this.G.remove(size - 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Q() {
        int i = this.Q - 1;
        this.Q = i;
        if (i <= 0) {
            this.Q = 0;
            h0();
            if (this.P) {
                i0();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void T() {
        int i = this.Q + 1;
        this.Q = i;
        if (i > 2) {
            this.Q = 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void W(l.f fVar) {
        this.W = null;
        if (fVar != null) {
            fVar.a();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void X() {
        this.O = 0;
        this.G.clear();
        l0();
        m0();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Y(CharSequence charSequence) {
        N(charSequence.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Z() {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) this.L.getSystemService("clipboard");
            if (clipboardManager != null) {
                j0(clipboardManager.getPrimaryClip());
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void a0(View view) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b0(View view) {
        t3.n().t(20);
        S("", null, new c(), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void c0(FrameLayout frameLayout, FrameLayout frameLayout2, f fVar, View view) {
        int i = this.O;
        if (i == 0) {
            this.G.clear();
            t3.n().u(20, 100L);
        } else {
            if (i == this.G.size()) {
                frameLayout.removeView(frameLayout2);
                return;
            }
            int size = this.G.size();
            int i2 = this.O;
            if (size > i2) {
                List<String> list = this.G;
                list.subList(i2, list.size()).clear();
            }
            frameLayout.removeView(frameLayout2);
            fVar.t();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void d0(int i, final FrameLayout frameLayout, Resources resources, final f fVar, int i2, View view) {
        final FrameLayout frameLayout2 = new FrameLayout(this.L);
        frameLayout2.setBackgroundColor(i);
        Objects.requireNonNull(frameLayout);
        frameLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.jozein.xedgepro.xposed.i
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                frameLayout.removeView(view2);
            }
        });
        ImageView imageView = new ImageView(this.L);
        imageView.setImageDrawable(new g.t(resources.getDrawable(R.drawable.ic_clips_clear_all), null, 0.7f));
        imageView.setBackground(g.a0.o(fVar.getItemColor(), i2 * 0.5f));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.jozein.xedgepro.xposed.l
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.z.c0(frameLayout, frameLayout2, fVar, view2);
            }
        });
        frameLayout2.addView(imageView, new FrameLayout.LayoutParams(i2, i2, 17));
        frameLayout.addView(frameLayout2, -1, -1);
    }

    private void e0() {
        if (g0() <= 0) {
            return;
        }
        String str = Y;
        File file = new File(str);
        if (file.exists()) {
            f0(file);
            File file2 = new File(X);
            if (file2.exists()) {
                file2.delete();
                return;
            }
            return;
        }
        File parentFile = file.getParentFile();
        if ((!parentFile.exists() || !parentFile.canExecute()) && !parentFile.mkdir()) {
            str = X;
        }
        this.R = str;
        File file3 = new File(X);
        if (file3.exists()) {
            f0(file3);
        }
    }

    private void f0(File file) {
        try {
            f.f fVar = new f.f(file, true);
            if (fVar.y()) {
                int iH = fVar.h();
                this.O = fVar.h();
                while (true) {
                    int i = iH - 1;
                    if (iH <= 0) {
                        break;
                    }
                    String strA = fVar.a();
                    if (strA != null && strA.length() > 0) {
                        this.G.add(strA);
                    }
                    iH = i;
                }
            }
            fVar.m();
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int g0() {
        if (this.K.o(31)) {
            return 0;
        }
        return this.K.r();
    }

    private void h0() {
        if (this.H.size() > 0) {
            Iterator<String> it = this.H.iterator();
            while (it.hasNext()) {
                O(it.next());
            }
            this.P = true;
            this.H.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void i0() {
        this.P = true;
        this.M.removeCallbacks(this.N);
        this.M.postDelayed(this.N, 50000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j0(ClipData clipData) {
        if (clipData != null && clipData.getItemCount() > 0) {
            ClipData.Item itemAt = clipData.getItemAt(0);
            ClipData.Item item = this.U;
            if (item != null && item.getText() == itemAt.getText() && this.U.getHtmlText() == itemAt.getHtmlText() && this.U.getIntent() == itemAt.getIntent() && this.U.getUri() == itemAt.getUri() && (Build.VERSION.SDK_INT < 31 || this.U.getTextLinks() == itemAt.getTextLinks())) {
                return;
            }
            this.U = itemAt;
            final CharSequence charSequenceCoerceToText = itemAt.coerceToText(this.L);
            if (charSequenceCoerceToText != null && charSequenceCoerceToText.length() > 0) {
                this.M.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.q
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.Y(charSequenceCoerceToText);
                    }
                });
                return;
            }
        }
        this.U = null;
    }

    private void l0() {
        if (this.T == null) {
            return;
        }
        ((ClipboardManager) this.L.getSystemService("clipboard")).removePrimaryClipChangedListener(this.T);
        this.T = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean n0() {
        h0();
        if (!this.P) {
            return false;
        }
        this.P = false;
        if (g0() <= 0 || this.G.size() <= 0) {
            new File(this.R).delete();
            return false;
        }
        try {
            f.f fVar = new f.f(this.R, true);
            fVar.j();
            fVar.d(this.G.size()).d(this.O);
            Iterator<String> it = this.G.iterator();
            while (it.hasNext()) {
                fVar.c(it.next());
            }
            fVar.B();
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    private void o0() {
        if (this.S || this.T != null || g0() <= 0) {
            return;
        }
        this.T = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: com.jozein.xedgepro.xposed.h
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                this.f128a.Z();
            }
        };
        ClipboardManager clipboardManager = (ClipboardManager) this.L.getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.addPrimaryClipChangedListener(this.T);
        }
    }

    void N(CharSequence charSequence) {
        if (g0() <= 0) {
            return;
        }
        String string = charSequence.toString();
        if (this.Q > 0) {
            this.H.add(0, string);
        } else {
            O(string);
            i0();
        }
    }

    void P(int i) {
        g.l lVar = this.W;
        if (lVar != null) {
            lVar.j(i);
        }
    }

    void R() {
        g.l lVar = this.W;
        if (lVar != null) {
            lVar.o(true);
            this.W = null;
        }
    }

    void S(CharSequence charSequence, CharSequence charSequence2, l.e eVar, final l.f fVar) {
        g.l lVar = this.W;
        if (lVar != null) {
            lVar.o(false);
        }
        g.l lVar2 = new g.l(new u(this.L, this.J), this.K.v(25, -16764873), eVar, new l.f() { // from class: com.jozein.xedgepro.xposed.n
            @Override // g.l.f
            public final void a() {
                this.f164a.W(fVar);
            }
        });
        this.W = lVar2;
        lVar2.u(charSequence, charSequence2, p2.d(this.L).getResources().getDrawable(R.drawable.ic_edit_done));
        if (Build.VERSION.SDK_INT < 30) {
            this.W.j(this.I.X1());
        }
    }

    boolean U(CharSequence charSequence) {
        g.l lVar = this.W;
        return lVar != null && lVar.m(charSequence);
    }

    boolean V() {
        return this.W != null;
    }

    void k0(Context context, Handler handler) {
        this.L = context;
        this.M = handler;
        try {
            e0();
        } catch (Throwable th) {
            f.v.d(th);
        }
        o0();
    }

    boolean m0() {
        Handler handler = this.M;
        if (handler != null) {
            handler.removeCallbacks(this.N);
        }
        return n0();
    }

    void p0() {
        if (this.I.w2()) {
            if (this.L == null) {
                k0(this.I.T1(), this.I.U1());
            }
            t3 t3VarN = t3.n();
            if (t3VarN.p(20)) {
                t3VarN.t(20);
                return;
            }
            g.x xVarC = g.x.c(this.L);
            final int i = (int) (xVarC.f636a * 0.85f);
            int i2 = (int) (i * 0.75f);
            int iV = this.K.v(25, -16764873) | (-16777216);
            final int iC = g.a0.c(iV, g.a0.r(iV) ? 16 : -16);
            final Resources resources = p2.d(this.L).getResources();
            Drawable drawable = resources.getDrawable(R.drawable.ic_clips_add);
            Drawable drawable2 = resources.getDrawable(R.drawable.ic_clips_clear_all);
            g.h hVarA = g.h.a();
            if (g.a0.r(iV)) {
                hVarA.b(drawable);
                hVarA.b(drawable2);
            } else {
                hVarA.d(drawable);
                hVarA.d(drawable2);
            }
            FrameLayout frameLayout = new FrameLayout(this.L);
            frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.jozein.xedgepro.xposed.m
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    r.a0(view);
                }
            });
            frameLayout.setBackgroundColor(iC);
            g.p pVar = new g.p(this.L);
            pVar.setImageDrawable(drawable);
            pVar.setOnClickListener(new View.OnClickListener() { // from class: com.jozein.xedgepro.xposed.j
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.b0(view);
                }
            });
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i2, i2, 8388611);
            int i3 = xVarC.f642g;
            layoutParams.rightMargin = i3;
            layoutParams.leftMargin = i3;
            frameLayout.addView(pVar, layoutParams);
            m3(this.L, frameLayout, iC, i2);
            g.p pVar2 = new g.p(this.L);
            pVar2.setImageDrawable(drawable2);
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(i2, i2, 8388613);
            int i4 = xVarC.f642g;
            layoutParams2.rightMargin = i4;
            layoutParams2.leftMargin = i4;
            frameLayout.addView(pVar2, layoutParams2);
            final d dVar = new d(this.L);
            dVar.addView(frameLayout, -1, -2);
            o2 o2Var = new o2(this.L, 20);
            final e eVar = new e(this.L, resources.getDrawable(R.drawable.ic_clips_edit), resources.getDrawable(R.drawable.ic_clips_sticky), resources.getDrawable(R.drawable.ic_clips_sticky_small), i, iV, o2Var);
            o2Var.a(eVar);
            dVar.setBackgroundColor(eVar.getItemColor());
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, -2, 48);
            layoutParams3.topMargin = i2;
            layoutParams3.bottomMargin = xVarC.f642g;
            dVar.addView(eVar, layoutParams3);
            pVar2.setOnClickListener(new View.OnClickListener() { // from class: com.jozein.xedgepro.xposed.k
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.d0(iC, dVar, resources, eVar, i, view);
                }
            });
            int iQ = eVar.q(this.L);
            if (iQ > 5) {
                iQ = 5;
            }
            int i5 = i2 + (i * iQ) + xVarC.f642g;
            t3VarN.k(dVar, 20, new FrameLayout.LayoutParams(-1, i5, 80));
            T();
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new TranslateAnimation(0.0f, 0.0f, i5 / 2.0f, 0.0f));
            animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            g.a0.z(dVar, animationSet, (long) (((long) (iQ + 1)) * 66 * 0.85f * 0.75f));
            eVar.setSelection(this.O - 1);
        }
    }

    void q0() {
        this.P = true;
        int iG0 = g0();
        if (iG0 <= 0) {
            this.M.postDelayed(this.V, 10000L);
            return;
        }
        this.M.removeCallbacks(this.V);
        int size = this.G.size();
        if (iG0 < size) {
            this.G.subList(iG0, size).clear();
            if (this.O > iG0) {
                this.O = iG0;
            }
            i0();
        }
        o0();
    }

    /* JADX INFO: renamed from: 加载, reason: contains not printable characters */
    private void m3(Context context, FrameLayout frameLayout, int i, int i2) {
        TextView textView = new TextView(context);
        textView.setText("搜索");
        textView.setTextColor(-1);
        textView.setTextSize(24);
        textView.setOnClickListener(new a000000(context, this, this.G));
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2, 17);
        XposedBridge.log("hook剪切板成功");
        frameLayout.addView(textView, layoutParams);
    }
}
