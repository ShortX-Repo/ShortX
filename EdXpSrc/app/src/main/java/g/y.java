package g;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public abstract class y extends ListView {
    private final int A;
    private final int B;
    private final Drawable C;
    private final Drawable D;
    private final Drawable E;
    private final int F;
    private final int G;
    private final FrameLayout.LayoutParams H;
    private final FrameLayout.LayoutParams I;
    private final FrameLayout.LayoutParams J;
    private final FrameLayout.LayoutParams K;
    private final FrameLayout.LayoutParams L;
    private final a M;
    private final int z;

    private class a extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnTouchListener {
        private final int A;
        private final Context B;
        private b L;
        private final float z;
        private float C = 0.0f;
        private float D = 0.0f;
        private boolean E = false;
        private boolean F = true;
        private long G = 0;
        private float H = 0.0f;
        private final int[] I = new int[3];
        private int J = 0;
        private final Rect K = new Rect();
        private int M = 0;

        a(Context context) {
            this.B = context;
            this.A = ViewConfiguration.get(context).getScaledMinimumFlingVelocity() * 2;
            this.z = a0.m(context) * 0.15f;
        }

        private void b() {
            b bVar = this.L;
            if (bVar != null) {
                bVar.c(this.M);
                if (this.M == 0) {
                    this.L.b();
                    this.L = null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void c() {
            this.M = 0;
            b bVar = this.L;
            if (bVar != null) {
                bVar.c(0);
                this.L.b();
                this.L = null;
            }
        }

        private void d(int i) {
            b bVar = this.L;
            if (bVar != null) {
                bVar.c(i + this.M);
            }
        }

        private void e(int i) {
            y.this.v(i);
            notifyDataSetChanged();
        }

        private void f(int i, int i2, int i3) {
            b bVar;
            Rect rect = this.K;
            int childCount = y.this.getChildCount() - 1;
            while (true) {
                if (childCount < 0) {
                    bVar = null;
                    break;
                }
                View childAt = y.this.getChildAt(childCount);
                childAt.getHitRect(rect);
                if (rect.contains(i, i2)) {
                    bVar = (b) childAt;
                    break;
                }
                childCount--;
            }
            b bVar2 = this.L;
            if (bVar2 == null) {
                this.M = 0;
            } else if (bVar2 != bVar) {
                c();
            }
            if (bVar != null) {
                bVar.e();
                bVar.c(i3 + this.M);
                this.L = bVar;
            }
        }

        private void g(int i, int i2) {
            int i3;
            if (this.L == null) {
                return;
            }
            int i4 = i + this.M;
            int i5 = (y.this.F * 3) / 4;
            int width = y.this.getWidth() / 2;
            int i6 = 0;
            if ((i4 < i5 && i4 > (-i5)) || ((i2 > (i3 = this.A) && i4 < 0) || (i2 < (-i3) && i4 > 0))) {
                this.L.c(0);
                this.L.b();
                this.L = null;
            } else {
                if (i4 <= width && i4 >= (-width)) {
                    if (i4 > 0) {
                        if (y.this.C != null) {
                            i6 = y.this.F;
                        }
                    } else if (y.this.D != null) {
                        i6 = -y.this.F;
                    }
                    this.M = i6;
                    this.L.c(i6);
                    if (this.M == 0) {
                        this.L.b();
                        this.L = null;
                        return;
                    }
                    return;
                }
                b bVar = this.L;
                this.L = null;
                bVar.c(0);
                bVar.b();
                e(bVar.D);
            }
            this.M = 0;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return y.this.q(this.B);
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
            b bVar = view != null ? (b) view : y.this.new b(this.B);
            try {
                bVar.d(y.this.r(i), i, y.this.s(i));
            } catch (Throwable th) {
                f.v.d(th);
            }
            return bVar;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            try {
                if (view != this.L) {
                    y.this.u(i);
                }
                c();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked;
            try {
                actionMasked = motionEvent.getActionMasked();
            } catch (Throwable th) {
                f.v.d(th);
            }
            if (actionMasked == 0) {
                this.C = motionEvent.getX();
                this.D = motionEvent.getY();
                this.E = false;
                this.F = true;
                this.J = 0;
                for (int length = this.I.length - 1; length >= 0; length--) {
                    this.I[length] = Integer.MAX_VALUE;
                }
                return false;
            }
            if (actionMasked == 1) {
                b bVar = this.L;
                if (bVar != null) {
                    bVar.setPressed(false);
                }
                if (this.F && this.E) {
                    float x = motionEvent.getX() - this.C;
                    int i = 0;
                    for (int i2 : this.I) {
                        if (i2 != Integer.MAX_VALUE) {
                            if (i != 0) {
                                i2 = (i2 + i) / 2;
                            }
                            i = i2;
                        }
                    }
                    g((int) x, i);
                    return true;
                }
                return false;
            }
            if (actionMasked != 2) {
                if (actionMasked == 3) {
                    if (this.F && this.E) {
                        b();
                        this.F = false;
                        this.E = false;
                    }
                    return false;
                }
                if (actionMasked != 5 && actionMasked != 6) {
                    return false;
                }
            }
            if (!this.F) {
                return false;
            }
            float x2 = motionEvent.getX();
            float y = motionEvent.getY();
            float f2 = x2 - this.C;
            float f3 = y - this.D;
            if (this.E) {
                int i3 = this.J + 1;
                this.J = i3;
                int[] iArr = this.I;
                if (i3 >= iArr.length) {
                    this.J = 0;
                }
                iArr[this.J] = (int) (((x2 - this.H) * 1000.0f) / (motionEvent.getEventTime() - this.G));
                this.G = motionEvent.getEventTime();
                this.H = x2;
                d((int) f2);
                return true;
            }
            float f4 = this.z;
            if (f3 <= f4 && f3 >= (-f4)) {
                if (f2 <= f4 && f2 >= (-f4)) {
                    return false;
                }
                this.E = true;
                this.G = motionEvent.getEventTime();
                this.H = x2;
                this.J = 0;
                this.I[0] = 0;
                f((int) this.C, (int) this.D, (int) f2);
                return true;
            }
            this.F = false;
            c();
            return false;
        }
    }

    private class b extends FrameLayout {
        private final FrameLayout A;
        private FrameLayout B;
        private ImageView C;
        private int D;
        private final TextView z;

        class a extends c {
            a(Context context) {
                super(context);
            }

            @Override // g.y.c
            void a() {
                b bVar = b.this;
                y.this.w(bVar.D);
                y.this.M.c();
            }
        }

        /* JADX INFO: renamed from: g.y$b$b, reason: collision with other inner class name */
        class C0012b extends c {
            C0012b(Context context) {
                super(context);
            }

            @Override // g.y.c
            void a() {
                b bVar = b.this;
                y.this.x(bVar.D);
                y.this.M.c();
            }
        }

        public b(Context context) {
            super(context);
            this.B = null;
            this.C = null;
            TextView textView = new TextView(context);
            this.z = textView;
            textView.setGravity(16);
            textView.setSingleLine();
            textView.setTextSize(0, y.this.G);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(y.this.A);
            FrameLayout frameLayout = new FrameLayout(context);
            this.A = frameLayout;
            frameLayout.addView(textView, y.this.H);
            frameLayout.setForeground(a0.l());
            frameLayout.setBackgroundColor(y.this.B);
            addView(frameLayout, y.this.I);
        }

        public void b() {
            setPressed(false);
            FrameLayout frameLayout = this.B;
            if (frameLayout != null) {
                frameLayout.setVisibility(4);
            }
        }

        public void c(int i) {
            this.A.setLeft(i);
            this.A.setRight(i + y.this.getWidth());
        }

        public void d(CharSequence charSequence, int i, boolean z) {
            int i2;
            this.D = i;
            this.z.setText(charSequence);
            ImageView imageView = this.C;
            if (z) {
                if (imageView == null) {
                    ImageView imageView2 = new ImageView(getContext());
                    this.C = imageView2;
                    imageView2.setImageDrawable(y.this.E);
                    this.A.addView(this.C, y.this.L);
                }
                imageView = this.C;
                i2 = 0;
            } else if (imageView == null) {
                return;
            } else {
                i2 = 4;
            }
            imageView.setVisibility(i2);
        }

        public void e() {
            setPressed(true);
            FrameLayout frameLayout = this.B;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                return;
            }
            if (y.this.C == null && y.this.D == null) {
                return;
            }
            Context context = getContext();
            this.B = new FrameLayout(context);
            if (y.this.C != null) {
                ImageView imageView = new ImageView(context);
                imageView.setImageDrawable(new t(y.this.C, null, 0.5f));
                imageView.setOnTouchListener(new a(context));
                this.B.addView(imageView, y.this.J);
            }
            if (y.this.D != null) {
                ImageView imageView2 = new ImageView(context);
                imageView2.setImageDrawable(new t(y.this.D, null, 0.5f));
                imageView2.setOnTouchListener(new C0012b(context));
                this.B.addView(imageView2, y.this.K);
            }
            addView(this.B, 0, y.this.I);
            if (Build.VERSION.SDK_INT < 21) {
                this.B.setBackgroundColor(y.this.z);
            }
        }
    }

    private static abstract class c implements View.OnTouchListener {
        private float A = 0.0f;
        private float B = 0.0f;
        private boolean C = true;
        private final float z;

        c(Context context) {
            float fM = a0.m(context);
            this.z = fM * fM * 0.15f * 0.15f;
        }

        abstract void a();

        /* JADX WARN: Code restructure failed: missing block: B:12:0x0014, code lost:
        
            r4.C = false;
         */
        @Override // android.view.View.OnTouchListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean onTouch(android.view.View r5, android.view.MotionEvent r6) {
            /*
                r4 = this;
                int r5 = r6.getActionMasked()
                r0 = 1
                if (r5 == 0) goto L55
                if (r5 == r0) goto L32
                r1 = 2
                r2 = 0
                if (r5 == r1) goto L17
                r6 = 3
                if (r5 == r6) goto L14
                r6 = 5
                if (r5 == r6) goto L14
                goto L63
            L14:
                r4.C = r2
                goto L63
            L17:
                boolean r5 = r4.C
                if (r5 == 0) goto L63
                float r5 = r4.A
                float r1 = r4.B
                float r3 = r6.getX()
                float r6 = r6.getY()
                float r5 = g.y.h(r5, r1, r3, r6)
                float r6 = r4.z
                int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r5 <= 0) goto L63
                goto L14
            L32:
                boolean r5 = r4.C
                if (r5 == 0) goto L63
                float r5 = r4.A
                float r1 = r4.B
                float r2 = r6.getX()
                float r6 = r6.getY()
                float r5 = g.y.h(r5, r1, r2, r6)
                float r6 = r4.z
                int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r5 >= 0) goto L63
                r4.a()     // Catch: java.lang.Throwable -> L50
                goto L63
            L50:
                r5 = move-exception
                f.v.d(r5)
                goto L63
            L55:
                float r5 = r6.getX()
                r4.A = r5
                float r5 = r6.getY()
                r4.B = r5
                r4.C = r0
            L63:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: g.y.c.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    public y(Context context, Drawable drawable, Drawable drawable2, Drawable drawable3, int i, int i2) {
        int i3;
        super(context);
        this.C = drawable;
        this.D = drawable2;
        this.E = drawable3;
        this.F = i;
        float f2 = i;
        this.G = (int) (0.3f * f2);
        int i4 = (int) (0.275f * f2);
        int i5 = (int) (f2 * 0.225f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2, 16);
        this.H = layoutParams;
        this.I = new FrameLayout.LayoutParams(-1, i);
        this.J = new FrameLayout.LayoutParams(i, i, 8388611);
        this.K = new FrameLayout.LayoutParams(i, i, 8388613);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(i4, i4, 8388661);
        this.L = layoutParams2;
        layoutParams.rightMargin = i5;
        layoutParams.leftMargin = i5;
        int i6 = i5 / 2;
        layoutParams2.rightMargin = i6;
        layoutParams2.topMargin = i6;
        layoutParams2.leftMargin = i6;
        int i7 = i2 | (-16777216);
        this.z = i7;
        int iRed = Color.red(i7);
        int iGreen = Color.green(i7);
        int iBlue = Color.blue(i7);
        h hVarA = h.a();
        if (a0.r(i7)) {
            this.A = -1;
            hVarA.b(drawable);
            hVarA.b(drawable2);
            hVarA.b(drawable3);
            i3 = 6;
        } else {
            this.A = -16777216;
            hVarA.d(drawable);
            hVarA.d(drawable2);
            hVarA.d(drawable3);
            i3 = -6;
        }
        this.B = a0.d(255, iRed, iGreen, iBlue, i3);
        a aVar = new a(context);
        this.M = aVar;
        setAdapter((ListAdapter) aVar);
        setOnItemClickListener(aVar);
        setOnTouchListener(aVar);
        setDividerHeight(0);
        setBackgroundColor(i7);
        setSelector(new ColorDrawable(0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float p(float f2, float f3, float f4, float f5) {
        float f6 = f4 - f2;
        float f7 = f5 - f3;
        return (f6 * f6) + (f7 * f7);
    }

    public final int getItemColor() {
        return this.B;
    }

    @Override // android.view.View
    public final boolean hasWindowFocus() {
        return true;
    }

    protected abstract int q(Context context);

    protected abstract CharSequence r(int i);

    protected abstract boolean s(int i);

    public void t() {
        this.M.c();
        this.M.notifyDataSetChanged();
    }

    protected abstract void u(int i);

    protected abstract void v(int i);

    protected abstract void w(int i);

    protected abstract void x(int i);
}
