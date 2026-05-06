package g;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.xposed.R;
import f.p0;

/* JADX INFO: loaded from: classes.dex */
class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f531a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final FrameLayout f532b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final EditText f533c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final v f534d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f535e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f536f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final int f537g;
    private final int h;
    private final LinearLayout.LayoutParams i;
    private final LinearLayout.LayoutParams j;
    private final LinearLayout.LayoutParams k;
    private final int l;
    private final int m;
    private final int n;
    private a q;
    private final Rect o = new Rect();
    private final int[] p = new int[2];
    private boolean r = false;
    private int s = 4;

    class a extends LinearLayout {
        private ViewTreeObserver.OnDrawListener A;
        private final int B;
        private final FrameLayout.LayoutParams z;

        a(boolean z, int i, int i2) {
            super(f.this.f531a);
            this.B = i2;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2, 51);
            this.z = layoutParams;
            layoutParams.leftMargin = i;
            layoutParams.topMargin = -5000;
            setOrientation(!z ? 1 : 0);
            a0.w(this, f.this.n, f.this.f536f / 2.0f);
            o();
            q();
            f.this.f532b.addView(this, layoutParams);
            this.A = new ViewTreeObserver.OnDrawListener() { // from class: g.e
                @Override // android.view.ViewTreeObserver.OnDrawListener
                public final void onDraw() {
                    this.f530a.j();
                }
            };
            f.this.f533c.getViewTreeObserver().addOnDrawListener(this.A);
            setOnClickListener(new View.OnClickListener() { // from class: g.c
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.k(view);
                }
            });
        }

        private View f(int i, View.OnClickListener onClickListener) {
            Context context = getContext();
            ImageView imageView = new ImageView(context);
            Drawable drawable = context.getResources().getDrawable(i);
            if (a0.r(f.this.f535e)) {
                h.a().d(drawable);
            } else {
                h.a().b(drawable);
            }
            imageView.setImageDrawable(drawable);
            FrameLayout frameLayout = new FrameLayout(context);
            int i2 = f.this.f537g / 2;
            frameLayout.addView(imageView, new FrameLayout.LayoutParams(i2, i2, 17));
            frameLayout.setBackground(a0.l());
            frameLayout.setOnClickListener(onClickListener);
            return frameLayout;
        }

        private View g(final MenuItem menuItem) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(0, f.this.f536f);
            textView.setTextColor(f.this.f535e);
            textView.setTypeface(Typeface.defaultFromStyle(1));
            textView.setText(menuItem.getTitle());
            textView.setGravity(16);
            textView.setPadding(f.this.h, 0, f.this.h, 0);
            textView.setBackground(a0.l());
            textView.setLayoutParams(f.this.i);
            textView.setOnClickListener(new View.OnClickListener() { // from class: g.d
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.i(menuItem, view);
                }
            });
            return textView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void i(MenuItem menuItem, View view) {
            try {
                f.this.f534d.b().onActionItemClicked(f.this.f534d, menuItem);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void j() {
            try {
                if (Build.VERSION.SDK_INT < 23) {
                    f.this.f534d.invalidate();
                }
                r();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void k(View view) {
            if (getOrientation() == 1) {
                n(true, -1);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void l(View view) {
            n(false, view.getLeft() < getLeft() / 2 ? 0 : getRight());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void m(View view) {
            n(true, -1);
        }

        private void n(boolean z, int i) {
            h();
            f fVar = f.this;
            fVar.q = fVar.new a(z, this.z.leftMargin, i);
        }

        private void p() {
            Menu menu = f.this.f534d.getMenu();
            int size = menu.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    i = size;
                    break;
                } else if (menu.getItem(i).getItemId() >= 268435713) {
                    break;
                } else {
                    i++;
                }
            }
            int iMin = Math.min(i, f.this.m);
            for (int i2 = 0; i2 < iMin; i2++) {
                addView(g(menu.getItem(i2)), f.this.i);
            }
            f.this.s = iMin;
            if (size > iMin) {
                addView(f(R.drawable.ic_painter_more, new View.OnClickListener() { // from class: g.a
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.z.l(view);
                    }
                }), f.this.k);
            }
        }

        private boolean q() {
            boolean z;
            int i;
            if (f.this.r) {
                return false;
            }
            FrameLayout.LayoutParams layoutParams = this.z;
            int i2 = layoutParams.topMargin;
            int i3 = layoutParams.leftMargin;
            int height = getHeight();
            int width = getWidth();
            Rect rect = f.this.o;
            int[] iArr = f.this.p;
            f.this.f533c.getFocusedRect(rect);
            f.this.f533c.getLocationInWindow(iArr);
            int scrollY = rect.top - f.this.f533c.getScrollY();
            int scrollY2 = rect.bottom - f.this.f533c.getScrollY();
            if (width <= 0 || scrollY > f.this.f533c.getHeight() - f.this.f533c.getCompoundPaddingBottom() || scrollY2 < f.this.f533c.getCompoundPaddingTop()) {
                setVisibility(4);
                return false;
            }
            if (!f.this.r) {
                setVisibility(0);
            }
            if (scrollY < 0) {
                scrollY = 0;
            }
            if (rect.left < 0) {
                rect.left = 0;
            }
            int i4 = f.this.h / 2;
            int height2 = f.this.f533c.getHeight() + iArr[1];
            if (getOrientation() == 0) {
                this.z.leftMargin = p0.e((iArr[0] + rect.centerX()) - (width / 2), f.this.h, (f.this.f532b.getWidth() - width) - f.this.h);
            } else {
                int i5 = this.B;
                if (i5 > 0) {
                    this.z.leftMargin = i5 - width;
                }
            }
            if (this.z.leftMargin > rect.right + iArr[0] + f.this.h || this.z.leftMargin + width < (rect.left + iArr[0]) - f.this.h) {
                z = true;
                i = ((scrollY + iArr[1]) - f.this.f537g) - i4;
                if (i + height > height2 - i4) {
                    i = (height2 - height) - i4;
                } else if (i < f.this.l) {
                    i = f.this.l + i4;
                }
            } else {
                z = true;
                i = ((scrollY + iArr[1]) - height) - i4;
                if (i < f.this.l) {
                    i = iArr[1] + scrollY2 + f.this.h;
                    if (height + i > height2 - i4) {
                        i = f.this.l + i4;
                    }
                    z = true;
                }
            }
            FrameLayout.LayoutParams layoutParams2 = this.z;
            layoutParams2.topMargin = i;
            if (i2 == i && i3 == layoutParams2.leftMargin) {
                return false;
            }
            return z;
        }

        private void s() {
            Menu menu = f.this.f534d.getMenu();
            int size = menu.size();
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            int i = f.this.f537g;
            boolean z = size - f.this.s > 1;
            View view = null;
            for (int i2 = f.this.s; i2 < size; i2++) {
                View viewG = g(menu.getItem(i2));
                if (z) {
                    viewG.measure(iMakeMeasureSpec, iMakeMeasureSpec);
                    int measuredWidth = viewG.getMeasuredWidth();
                    if (measuredWidth > i) {
                        view = viewG;
                        i = measuredWidth;
                    }
                }
                addView(viewG, f.this.i);
            }
            if (z) {
                for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                    View childAt = getChildAt(childCount);
                    if (childAt != view) {
                        childAt.setLayoutParams(f.this.j);
                    }
                }
            }
            addView(f(R.drawable.ic_edit_back, new View.OnClickListener() { // from class: g.b
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.z.m(view2);
                }
            }), f.this.k);
        }

        void h() {
            try {
                f.this.f533c.getViewTreeObserver().removeOnDrawListener(this.A);
                this.A = null;
                f.this.f532b.removeView(this);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        void o() {
            try {
                removeAllViews();
                if (getOrientation() == 1) {
                    s();
                } else {
                    p();
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        void r() {
            try {
                if (q()) {
                    setLayoutParams(this.z);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    f(FrameLayout frameLayout, EditText editText, v vVar, int i) {
        Context context = frameLayout.getContext();
        this.f531a = context;
        this.f532b = frameLayout;
        this.f533c = editText;
        this.f534d = vVar;
        if (a0.r(i)) {
            this.n = a0.c(i, 32);
            this.f535e = -1;
        } else {
            this.n = a0.c(i, -32);
            this.f535e = -16777216;
        }
        x xVarC = x.c(context);
        int i2 = xVarC.f640e;
        this.f536f = i2;
        int i3 = xVarC.f642g;
        this.h = i3;
        int i4 = (i3 * 2) + i2;
        this.f537g = i4;
        this.i = new LinearLayout.LayoutParams(-2, i4);
        this.j = new LinearLayout.LayoutParams(-1, i4);
        this.k = new LinearLayout.LayoutParams(i4, i4);
        this.l = a0.q();
        this.m = frameLayout.getWidth() / i2 >= 33 ? 5 : 4;
        this.q = new a(true, 0, -1);
    }

    void u() {
        this.q.h();
    }

    void v(boolean z) {
        this.r = z;
        this.q.setVisibility(z ? 4 : 0);
    }

    void w(boolean z) {
        if (z) {
            this.q.o();
        }
    }
}
