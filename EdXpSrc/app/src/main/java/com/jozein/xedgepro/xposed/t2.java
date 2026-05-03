package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
class t2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final a.p f231a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Context f232b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private int f233c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private int f234d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private int f235e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private AbsListView.LayoutParams f236f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private AbsListView.LayoutParams f237g;
    private FrameLayout.LayoutParams h;
    private FrameLayout.LayoutParams i;
    private FrameLayout.LayoutParams j;
    private int k = -1;
    private int l = -1;

    class a extends FrameLayout {
        final /* synthetic */ c z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        a(Context context, c cVar) {
            super(context);
            this.z = cVar;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == 1) {
                this.z.b();
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            View childAt = getChildAt(0);
            return childAt != null ? childAt.onGenericMotionEvent(motionEvent) : super.onGenericMotionEvent(motionEvent);
        }
    }

    class b extends GridView {
        final /* synthetic */ o2 z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        b(Context context, o2 o2Var) {
            super(context);
            this.z = o2Var;
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return true;
        }

        @Override // android.widget.AbsListView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.z.c()) {
                return true;
            }
            try {
                return super.onTouchEvent(motionEvent);
            } catch (Throwable th) {
                this.z.b(th);
                t2.this.k = -1;
                return true;
            }
        }
    }

    private class c extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        private final Drawable[] A;
        private final CharSequence[] B;
        private boolean C = false;
        private final e z;

        c(e eVar, boolean z) {
            int count = 0;
            this.z = eVar;
            try {
                count = eVar.getCount();
            } catch (Throwable th) {
                f.v.d(th);
            }
            this.A = new Drawable[count];
            this.B = z ? new CharSequence[count] : null;
        }

        void b() {
            if (this.C) {
                this.C = false;
                try {
                    t2.this.j();
                    this.z.f();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.A.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            d dVar;
            boolean z = this.B != null;
            Drawable[] drawableArr = this.A;
            if (drawableArr[i] == null) {
                try {
                    drawableArr[i] = this.z.c(i);
                    if (z) {
                        this.B[i] = this.z.d(i);
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            if (view == null) {
                dVar = t2.this.new d(viewGroup.getContext(), this.B != null);
                t2 t2Var = t2.this;
                dVar.setLayoutParams(z ? t2Var.f237g : t2Var.f236f);
            } else {
                dVar = (d) view;
            }
            dVar.z.setImageDrawable(this.A[i]);
            if (z) {
                dVar.A.setText(this.B[i]);
            }
            return dVar;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            try {
                this.z.e(i, view);
            } catch (Throwable th) {
                f.v.d(th);
            }
            t2.this.j();
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            try {
                if (this.z.b(i)) {
                    this.C = true;
                    return true;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
            t2.this.j();
            return false;
        }
    }

    private class d extends FrameLayout {
        final TextView A;
        final ImageView z;

        d(Context context, boolean z) {
            super(context);
            if (!z) {
                ImageView imageView = new ImageView(context);
                this.z = imageView;
                addView(imageView, t2.this.h);
                this.A = null;
                return;
            }
            ImageView imageView2 = new ImageView(context);
            this.z = imageView2;
            addView(imageView2, t2.this.i);
            TextView textView = new TextView(context);
            this.A = textView;
            textView.setTextSize(0, t2.this.f235e);
            textView.setTextColor(t2.this.l);
            textView.setPadding(t2.this.f235e / 4, 0, t2.this.f235e / 4, 0);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            addView(textView, t2.this.j);
        }
    }

    interface e {
        boolean a();

        boolean b(int i);

        Drawable c(int i);

        CharSequence d(int i);

        void e(int i, View view);

        void f();

        int getCount();
    }

    t2(w1 w1Var) {
        this.f231a = w1Var.b2();
        this.f232b = w1Var.T1();
    }

    private void k() {
        g.x xVarB = g.x.b(this.f232b);
        float fV = this.f231a.v(28, 100) / 100.0f;
        int i = (int) ((xVarB.f636a * fV) + 0.5f);
        this.f233c = i;
        this.f235e = (int) ((xVarB.f639d * fV) + 0.5f);
        this.f234d = i;
        int i2 = (int) ((xVarB.f637b * fV) + 0.5f);
        this.f236f = new AbsListView.LayoutParams(-1, this.f233c);
        this.h = new FrameLayout.LayoutParams(i2, i2, 17);
        int i3 = (int) (((this.f233c - i2) - this.f235e) * 0.3f);
        this.f237g = new AbsListView.LayoutParams(-1, this.f234d);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i2, i2, 49);
        this.i = layoutParams;
        layoutParams.topMargin = i3;
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-2, -2, 81);
        this.j = layoutParams2;
        layoutParams2.bottomMargin = i3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void m(GridView gridView) {
        int width = gridView.getWidth() / this.f233c;
        if (width > 0) {
            gridView.setNumColumns(width);
        }
    }

    void j() {
        t3.n().t(18);
        this.k = -1;
    }

    boolean l(int i) {
        return i == this.k && t3.n().p(18);
    }

    /* JADX WARN: Removed duplicated region for block: B:81:0x01bd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void n(com.jozein.xedgepro.xposed.t2.e r22, int r23, int r24, int r25) {
        /*
            Method dump skipped, instruction units count: 452
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.t2.n(com.jozein.xedgepro.xposed.t2$e, int, int, int):void");
    }

    void o(e eVar, int i) {
        n(eVar, 2, 0, i);
    }
}
