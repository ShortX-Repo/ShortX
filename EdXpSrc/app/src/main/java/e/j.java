package e;

import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;
import f.c0;
import f.p0;
import g.o;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class j extends j0.c implements f.l {
    protected static final View.OnTouchListener L = new View.OnTouchListener() { // from class: e.c
        @Override // android.view.View.OnTouchListener
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            return j.Z0(view, motionEvent);
        }
    };
    private g.o E;
    private l F = null;
    private h G = null;
    private boolean H = false;
    private String I = null;
    private String[] J = null;
    private int[] K = null;

    class a extends ListView {
        a(Context context) {
            super(context);
        }

        @Override // android.widget.AbsListView, android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            float axisValue = motionEvent.getAxisValue(9);
            if (axisValue == 100000.0f || axisValue == -100000.0f) {
                try {
                    if (axisValue == 100000.0f) {
                        smoothScrollToPosition(0);
                    } else {
                        smoothScrollToPosition(getCount() - 1);
                    }
                    return true;
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return super.onGenericMotionEvent(motionEvent);
        }
    }

    class b implements SearchView.OnQueryTextListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Context f387a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ SearchView f388b;

        b(Context context, SearchView searchView) {
            this.f387a = context;
            this.f388b = searchView;
        }

        @Override // android.widget.SearchView.OnQueryTextListener
        public boolean onQueryTextChange(String str) {
            if (!str.equals(j.this.I == null ? "" : j.this.I)) {
                j.this.v1();
                j.this.I = str;
                j.this.A1(str);
                if (j.this.F != null) {
                    j.this.F.v(null, 0, 0);
                }
            }
            return false;
        }

        @Override // android.widget.SearchView.OnQueryTextListener
        public boolean onQueryTextSubmit(String str) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.f387a.getSystemService("input_method");
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.f388b.getWindowToken(), 0);
            }
            return false;
        }
    }

    class c implements View.OnAttachStateChangeListener {
        c() {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    public interface d {
        String[] a();
    }

    public static class e implements Parcelable {
        public static final Parcelable.Creator<e> CREATOR = new a();
        public final int A;
        public final String B;
        public final int z;

        class a implements Parcelable.Creator<e> {
            a() {
            }

            @Override // android.os.Parcelable.Creator
            /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
            public e createFromParcel(Parcel parcel) {
                return new e(parcel);
            }

            @Override // android.os.Parcelable.Creator
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public e[] newArray(int i) {
                return new e[i];
            }
        }

        public e(int i, int i2, String str) {
            this.z = i;
            this.A = i2;
            this.B = str;
        }

        protected e(Parcel parcel) {
            this.z = parcel.readInt();
            this.A = parcel.readInt();
            this.B = parcel.readString();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.z);
            parcel.writeInt(this.A);
            parcel.writeString(this.B);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class f extends ViewOnTouchListenerC0005j {
        private final ImageView H;

        public f(Drawable drawable, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            super(charSequence, charSequence2, z);
            ImageView imageView = new ImageView(getContext());
            this.H = imageView;
            imageView.setImageDrawable(drawable);
            addView(imageView, 0, j.this.t().j);
        }

        public f(CharSequence charSequence, CharSequence charSequence2, Drawable drawable, boolean z) {
            super(charSequence, charSequence2, z);
            ImageView imageView = new ImageView(getContext());
            this.H = imageView;
            imageView.setImageDrawable(drawable);
            addView(imageView, 1, j.this.t().j);
        }

        public void setImageColorFilter(int i) {
            this.H.setColorFilter(i);
        }

        public void setImageDrawable(Drawable drawable) {
            this.H.setImageDrawable(drawable);
        }
    }

    protected class g extends k {
        public final ImageView E;

        public g(Drawable drawable, CharSequence charSequence, CharSequence charSequence2) {
            super(charSequence, charSequence2);
            ImageView imageView = new ImageView(getContext());
            this.E = imageView;
            imageView.setImageDrawable(drawable);
            addView(imageView, 0, j.this.t().j);
        }

        public g(CharSequence charSequence, CharSequence charSequence2, Drawable drawable) {
            super(charSequence, charSequence2);
            ImageView imageView = new ImageView(getContext());
            this.E = imageView;
            imageView.setImageDrawable(drawable);
            addView(imageView, j.this.t().j);
        }

        public void setImageDrawable(Drawable drawable) {
            this.E.setImageDrawable(drawable);
        }

        public void setImageResource(int i) {
            this.E.setImageResource(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class h implements Runnable {
        private final Context A;
        private volatile j B;
        private final i z;

        h(i iVar, Context context) {
            this.z = iVar;
            this.A = context;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void c(Object obj) {
            j jVar = this.B;
            if (jVar != null) {
                try {
                    if (obj != null) {
                        jVar.r1(obj);
                    } else {
                        jVar.L();
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void d() {
            j jVar = this.B;
            if (jVar != null) {
                jVar.L();
            }
        }

        public void e(j jVar) {
            this.B = jVar;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                final Object objA = this.z.a(this.A);
                f.h0.a().post(new Runnable() { // from class: e.l
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.c(objA);
                    }
                });
            } catch (Throwable th) {
                f.v.d(th);
                f.h0.a().post(new Runnable() { // from class: e.k
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.d();
                    }
                });
            }
        }
    }

    protected interface i {

        public static class a implements i {
            @Override // e.j.i
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public final List<c0.d> a(Context context) {
                return f.z.K(context).R();
            }
        }

        public static class b implements i {
            @Override // e.j.i
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public final List<c0.e> a(Context context) {
                return f.z.K(context).M();
            }
        }

        public static class c implements i {
            @Override // e.j.i
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public final f.u<String, ArrayList<AppWidgetProviderInfo>> a(Context context) {
                return f.z.K(context).B();
            }
        }

        public static class d implements i {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private final boolean f391a;

            public d(boolean z) {
                this.f391a = z;
            }

            @Override // e.j.i
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public final List<c0.d> a(Context context) {
                return f.z.K(context).Q(this.f391a);
            }
        }

        public static class e implements i {
            @Override // e.j.i
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public List<c0.e> a(Context context) {
                return f.z.K(context).T();
            }
        }

        Object a(Context context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: e.j$j, reason: collision with other inner class name */
    public class ViewOnTouchListenerC0005j extends k implements View.OnTouchListener {
        private final CheckBox E;
        private CompoundButton.OnCheckedChangeListener F;

        public ViewOnTouchListenerC0005j(CharSequence charSequence, CharSequence charSequence2, boolean z) {
            super(charSequence, charSequence2);
            this.F = null;
            CheckBox checkBox = new CheckBox(getContext());
            this.E = checkBox;
            checkBox.setChecked(z);
            checkBox.setFocusable(false);
            checkBox.setOnTouchListener(this);
            addView(checkBox, j.this.t().l);
        }

        public boolean g() {
            return this.E.isChecked();
        }

        public void h() {
            this.E.toggle();
            CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.F;
            if (onCheckedChangeListener != null) {
                CheckBox checkBox = this.E;
                onCheckedChangeListener.onCheckedChanged(checkBox, checkBox.isChecked());
            }
            j.this.z1();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            } else if (actionMasked == 1) {
                this.E.toggle();
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.F;
                if (onCheckedChangeListener != null) {
                    CheckBox checkBox = this.E;
                    onCheckedChangeListener.onCheckedChanged(checkBox, checkBox.isChecked());
                }
                j.this.z1();
            }
            return true;
        }

        public void setChecked(boolean z) {
            if (this.E.isChecked() != z) {
                this.E.setChecked(z);
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.F;
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChanged(this.E, z);
                }
                j.this.z1();
            }
        }

        @Override // e.j.k, android.view.View
        public void setEnabled(boolean z) {
            super.setEnabled(z);
            this.E.setEnabled(z);
        }

        public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            this.F = onCheckedChangeListener;
        }
    }

    protected class k extends LinearLayout {
        private final TextView A;
        private ImageView B;
        private boolean C;
        private final TextView z;

        public k(j jVar, int i, int i2) {
            this(jVar.u(i), jVar.u(i2));
        }

        public k(j jVar, CharSequence charSequence) {
            this(charSequence, (CharSequence) null);
        }

        public k(CharSequence charSequence, CharSequence charSequence2) {
            super(j.this.F.z);
            this.B = null;
            this.C = false;
            j0.d dVarT = j.this.t();
            setLayoutParams(dVarT.n);
            Context context = j.this.F.z;
            setOrientation(0);
            TextView textViewE1 = j.e1(context, charSequence, dVarT.f398g, dVarT.o);
            this.z = textViewE1;
            if (charSequence2 == null) {
                this.A = null;
                addView(textViewE1, dVarT.k);
                return;
            }
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            linearLayout.addView(textViewE1);
            TextView textViewE12 = j.e1(context, charSequence2, dVarT.i, dVarT.p);
            this.A = textViewE12;
            linearLayout.addView(textViewE12);
            addView(linearLayout, dVarT.k);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void d() {
            if (this.B == null) {
                ImageView imageView = new ImageView(getContext());
                this.B = imageView;
                addView(imageView, j.this.t().m);
            }
            setDroppedUnchecked(false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDroppedUnchecked(boolean z) {
            if (this.B != null) {
                this.B.setImageDrawable(j.this.i(z ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
            }
        }

        public void c() {
            j0.d dVarT = j.this.t();
            this.z.setTextColor(dVarT.o);
            TextView textView = this.A;
            if (textView != null) {
                textView.setTextColor(dVarT.p);
            }
        }

        public void e() {
            this.z.setTextColor(-7829368);
            TextView textView = this.A;
            if (textView != null) {
                textView.setTextColor(-7829368);
            }
        }

        @Override // android.view.View
        public void setEnabled(boolean z) {
            if (z == isEnabled()) {
                return;
            }
            super.setEnabled(z);
            if (z) {
                c();
            } else {
                e();
            }
        }

        public void setHighLight(boolean z) {
            if (this.C != z) {
                this.C = z;
                if (z) {
                    setBackgroundColor(1148680055);
                } else {
                    setBackground(null);
                }
            }
        }

        public void setSubText(int i) {
            TextView textView = this.A;
            if (textView != null) {
                textView.setText(i);
            }
        }

        public void setSubText(CharSequence charSequence) {
            TextView textView = this.A;
            if (textView != null) {
                textView.setText(charSequence);
            }
        }

        public void setText(int i) {
            this.z.setText(i);
        }

        public void setText(CharSequence charSequence) {
            this.z.setText(charSequence);
        }
    }

    private class l extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        private int A;
        private View[] B;
        private int C = -1;
        private int D = -1;
        private View[] E = null;
        private int[] F = null;
        private final Context z;

        l(Context context, int i) {
            this.z = context;
            s(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public View l(int i) {
            View[] viewArr;
            if (i < 0 || (viewArr = this.E) == null || i > viewArr.length) {
                return null;
            }
            View view = viewArr[i];
            if (view != null) {
                return view;
            }
            if (j.this.getActivity() == null) {
                return null;
            }
            View[] viewArr2 = this.E;
            View viewI1 = j.this.i1(i);
            viewArr2[i] = viewI1;
            j0.d dVarT = j.this.t();
            viewI1.setPaddingRelative(dVarT.f397f, 0, dVarT.f395d, 0);
            return viewI1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public View m(int i) {
            if (i < 0) {
                return null;
            }
            View[] viewArr = this.B;
            if (i >= viewArr.length) {
                return null;
            }
            if (viewArr[i] == null) {
                viewArr[i] = j.this.h1(i);
                if (j.this.N0(i)) {
                    View[] viewArr2 = this.B;
                    if (viewArr2[i] instanceof k) {
                        ((k) viewArr2[i]).d();
                    }
                }
                j0.d dVarT = j.this.t();
                View view = this.B[i];
                int i2 = dVarT.f395d;
                view.setPadding(i2, 0, i2, 0);
            }
            return this.B[i];
        }

        private void o() {
            if (this.F != null) {
                j.this.J = null;
                j jVar = j.this;
                jVar.A1(jVar.I);
            }
            notifyDataSetChanged();
        }

        private int p(int i) {
            View[] viewArr = this.E;
            if (viewArr == null) {
                int[] iArr = this.F;
                return iArr != null ? iArr[i] : i;
            }
            int[] iArr2 = this.F;
            if (iArr2 == null) {
                int i2 = this.C;
                return i <= i2 ? i : i < (i2 + 1) + viewArr.length ? ((i - i2) - 1) ^ (-1) : i - viewArr.length;
            }
            if (this.D < 0) {
                int iD = p0.d(iArr2, this.C);
                this.D = iD;
                if (iD < 0) {
                    s(this.A);
                    notifyDataSetChanged();
                    throw new IllegalStateException("Set sub position " + this.C + " not in filter indexes.");
                }
            }
            int i3 = this.D;
            if (i <= i3) {
                return this.F[i];
            }
            View[] viewArr2 = this.E;
            return i < (i3 + 1) + viewArr2.length ? ((i - i3) - 1) ^ (-1) : this.F[i - viewArr2.length];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void u(int i, int i2) {
            Bundle bundleH = j.this.h();
            bundleH.putInt("AdaptedListFragment_last_clicked", i);
            bundleH.putInt("AdaptedListFragment_last_clicked_sub", i2);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            int[] iArr = this.F;
            int length = iArr == null ? this.A : iArr.length;
            View[] viewArr = this.E;
            return length + (viewArr != null ? viewArr.length : 0);
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return p(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            try {
                int iP = p(i);
                View viewM = iP >= 0 ? m(iP) : l(iP ^ (-1));
                if (viewM != null) {
                    return viewM;
                }
            } catch (Throwable unused) {
            }
            return new View(this.z);
        }

        public void i(int i) {
            int i2 = this.C;
            if (i2 >= i) {
                this.C = i2 + 1;
                this.D = -1;
            }
            View[] viewArr = this.B;
            if (viewArr.length <= this.A) {
                View[] viewArr2 = new View[viewArr.length + 8];
                this.B = viewArr2;
                System.arraycopy(viewArr, 0, viewArr2, 0, i);
            }
            int i3 = this.A;
            if (i < i3) {
                System.arraycopy(viewArr, i, this.B, i + 1, i3 - i);
            }
            this.B[i] = null;
            this.A++;
            o();
        }

        void j(int i, int i2, boolean z) {
            int iD;
            ListView listViewG0;
            r();
            int[] iArr = this.F;
            if (iArr != null) {
                iD = p0.d(iArr, i);
                if (iD < 0) {
                    f.v.c("Set sub position " + i + " not in filter indexes.");
                    return;
                }
            } else {
                iD = i;
            }
            this.C = i;
            this.D = iD;
            this.E = new View[i2];
            View viewM = m(i);
            if (viewM instanceof k) {
                ((k) viewM).setDroppedUnchecked(true);
            }
            notifyDataSetChanged();
            if (!z || (listViewG0 = j.this.G0()) == null) {
                return;
            }
            int firstVisiblePosition = listViewG0.getFirstVisiblePosition();
            if (iD > firstVisiblePosition) {
                int lastVisiblePosition = listViewG0.getLastVisiblePosition();
                int i3 = i2 + iD + 1;
                if (i3 <= lastVisiblePosition) {
                    return;
                }
                int i4 = (i3 - lastVisiblePosition) + firstVisiblePosition;
                if (i4 <= iD) {
                    iD = i4;
                }
            }
            v(listViewG0, iD, 0);
        }

        void k(int i, int i2) {
            ListView listViewG0 = j.this.G0();
            if (listViewG0 == null) {
                return;
            }
            int firstVisiblePosition = listViewG0.getFirstVisiblePosition();
            int lastVisiblePosition = listViewG0.getLastVisiblePosition();
            if (i2 > 0) {
                i = i + i2 + 1;
            }
            if (i <= firstVisiblePosition || i >= lastVisiblePosition) {
                v(listViewG0, i, 0);
            }
        }

        void n(int i, int i2) {
            if (i == i2) {
                return;
            }
            int i3 = this.C;
            if (i3 >= 0) {
                this.D = -1;
                if (i3 == i) {
                    this.C = i2;
                } else {
                    int iMax = Math.max(i, i2);
                    int iMin = Math.min(i, i2);
                    int i4 = this.C;
                    if (i4 >= iMin && i4 <= iMax) {
                        this.C = i < i2 ? i4 - 1 : i4 + 1;
                    }
                }
            }
            p0.c(this.B, i, i2);
            o();
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            int iP = p(i);
            try {
                if (iP >= 0) {
                    u(iP, -1);
                    j.this.k1(iP);
                } else {
                    int i2 = iP ^ (-1);
                    u(this.C, i2);
                    j.this.o1(i2);
                }
            } catch (Throwable th) {
                j.this.g0(th);
            }
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            int iP = p(i);
            try {
                if (iP >= 0) {
                    u(iP, -1);
                    return j.this.l1(iP);
                }
                int i2 = iP ^ (-1);
                u(this.C, i2);
                return j.this.p1(i2);
            } catch (Throwable th) {
                j.this.g0(th);
                return true;
            }
        }

        public View q(int i) {
            int i2 = this.C;
            if (i2 >= i) {
                this.C = i2 - 1;
                this.D = -1;
            }
            View[] viewArr = this.B;
            View view = viewArr[i];
            int i3 = this.A - 1;
            this.A = i3;
            System.arraycopy(viewArr, i + 1, viewArr, i, i3 - i);
            this.B[this.A] = null;
            o();
            return view;
        }

        void r() {
            ListView listViewG0 = j.this.G0();
            if (listViewG0 != null) {
                listViewG0.setPressed(false);
                listViewG0.jumpDrawablesToCurrentState();
            }
            int i = this.C;
            if (i >= 0) {
                View viewM = m(i);
                if (viewM instanceof k) {
                    ((k) viewM).setDroppedUnchecked(false);
                }
            }
            this.C = -1;
            this.D = -1;
            this.E = null;
            notifyDataSetChanged();
        }

        void s(int i) {
            this.A = i;
            this.B = new View[((i + 8) - 1) & (-8)];
            this.C = -1;
            this.D = -1;
            this.E = null;
            this.F = null;
        }

        void t(int[] iArr) {
            r();
            this.F = iArr;
            notifyDataSetChanged();
        }

        void v(ListView listView, int i, int i2) {
            if (listView == null) {
                listView = j.this.G0();
            }
            if (listView != null) {
                int count = getCount();
                if (i < 0 || i >= count) {
                    return;
                }
                listView.setSelectionFromTop(i, i2);
            }
        }
    }

    private static class m extends FrameLayout {
        public m(Context context) {
            super(context);
        }

        public View a() {
            return getChildAt(0);
        }

        public void b() {
            addView(new ProgressBar(getContext()), new FrameLayout.LayoutParams(-2, -2, 17));
        }

        public void c(View view) {
            removeAllViews();
            addView(view, -1, -1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void A1(String str) {
        if (this.F == null) {
            return;
        }
        if (str.length() == 0) {
            this.F.t(null);
            return;
        }
        if (this.J == null) {
            v1();
            try {
                String[] strArrA = ((d) this).a();
                this.J = strArrA;
                if (strArrA != null) {
                    int i2 = 0;
                    while (true) {
                        String[] strArr = this.J;
                        if (i2 >= strArr.length) {
                            break;
                        }
                        strArr[i2] = strArr[i2].toUpperCase();
                        i2++;
                    }
                }
            } catch (Throwable th) {
                g0(th);
            }
        }
        if (this.J == null) {
            return;
        }
        String upperCase = str.toUpperCase();
        if (this.K == null) {
            this.K = new int[this.J.length];
        }
        int i3 = 0;
        int i4 = 0;
        while (true) {
            String[] strArr2 = this.J;
            if (i3 >= strArr2.length) {
                int[] iArr = new int[i4];
                System.arraycopy(this.K, 0, iArr, 0, i4);
                this.F.t(iArr);
                return;
            } else {
                if (strArr2[i3].contains(upperCase)) {
                    this.K[i4] = i3;
                    i4++;
                }
                i3++;
            }
        }
    }

    private ListView O0(Context context) {
        int iB0 = B0();
        this.F = new l(context, iB0);
        if (this.I == null) {
            this.I = h().getString("AdaptedListFragment_filter_text");
        }
        String str = this.I;
        if (str != null) {
            A1(str);
        }
        a aVar = new a(context);
        aVar.setAdapter((ListAdapter) this.F);
        aVar.setOnItemClickListener(this.F);
        aVar.setOnItemLongClickListener(this.F);
        aVar.setDividerHeight(0);
        int[] intArray = h().getIntArray("AdaptedListFragment_view_state");
        if (intArray != null) {
            this.F.v(aVar, intArray[0], intArray[1]);
            if (iB0 == intArray[2]) {
                n1(intArray[3], intArray[4]);
            }
        }
        return aVar;
    }

    private SearchView P0(Context context, final View view) {
        final SearchView searchView = new SearchView(context);
        if (Build.VERSION.SDK_INT >= 26) {
            searchView.setFocusedByDefault(false);
        }
        Y(searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() { // from class: e.a
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.z.R0(view, searchView, view2);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() { // from class: e.d
            @Override // android.widget.SearchView.OnCloseListener
            public final boolean onClose() {
                return this.f384a.S0(view);
            }
        });
        searchView.setOnQueryTextListener(new b(context, searchView));
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: e.b
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view2, boolean z) {
                j.T0(view2, z);
            }
        });
        searchView.addOnAttachStateChangeListener(new c());
        String string = h().getString("AdaptedListFragment_filter_text");
        this.I = string;
        if (string != null) {
            searchView.setQuery(string, false);
            searchView.setIconified(false);
        }
        if (t().o == -16777216) {
            try {
                int identifier = getResources().getIdentifier("search_button", "id", "android");
                if (identifier > 0) {
                    g.h.a().e(((ImageView) searchView.findViewById(identifier)).getDrawable(), -1);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return searchView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void R0(View view, SearchView searchView, View view2) {
        if (view != null) {
            view.setVisibility(8);
        }
        String str = this.I;
        if (str == null) {
            str = "";
        }
        if (searchView.getQuery().toString().equals(str)) {
            return;
        }
        v1();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean S0(View view) {
        this.I = null;
        if (view != null) {
            view.setVisibility(0);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void T0(View view, boolean z) {
        if (z) {
            return;
        }
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void U0(View view, e eVar) {
        view.setBackground(null);
        this.F.u(eVar.z, -1);
        k1(eVar.z);
        r().y(eVar.B, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void V0(View view, e eVar) {
        view.setBackground(null);
        this.F.u(eVar.z, -1);
        l1(eVar.z);
        r().y(eVar.B, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void W0(View view, e eVar) {
        view.setBackground(null);
        this.F.u(I0(), eVar.z);
        o1(eVar.z);
        r().y(eVar.B, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void X0(View view, e eVar) {
        view.setBackground(null);
        this.F.u(I0(), eVar.z);
        p1(eVar.z);
        r().y(eVar.B, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Y0(ViewOnTouchListenerC0005j viewOnTouchListenerC0005j, e eVar) {
        viewOnTouchListenerC0005j.E.setBackground(null);
        r().y(eVar.B, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean Z0(View view, MotionEvent motionEvent) {
        return true;
    }

    private LinearLayout d1(Drawable drawable, int i2, int i3, CharSequence charSequence) {
        j0.d dVarT = t();
        Context context = this.F.z;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(drawable);
        int i4 = dVarT.f395d;
        imageView.setPadding(i4, i4, i4, i4);
        imageView.setBackgroundColor(1145324612);
        int i5 = dVarT.f395d;
        frameLayout.addView(imageView, i2 + (i5 * 2), i3 + (i5 * 2));
        linearLayout.addView(frameLayout, -1, -2);
        View viewE1 = e1(context, charSequence, dVarT.f398g, dVarT.o);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dVarT.f395d * 2;
        linearLayout.addView(viewE1, layoutParams);
        return linearLayout;
    }

    static TextView e1(Context context, CharSequence charSequence, int i2, int i3) {
        TextView textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(charSequence);
        textView.setTextSize(0, i2);
        textView.setTextColor(i3);
        return textView;
    }

    private void j1(final e eVar) {
        final View viewL0;
        Runnable runnable;
        final View viewL02;
        Runnable runnable2;
        switch (eVar.A) {
            case 0:
                r().y(eVar.B, 500L);
                break;
            case 1:
                z0(eVar.z, -1);
                viewL0 = L0(eVar.z);
                viewL0.setBackgroundColor(q().getColor(R.color.colorPrimaryLight));
                runnable = new Runnable() { // from class: e.g
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.U0(viewL0, eVar);
                    }
                };
                viewL0.postDelayed(runnable, 500L);
                break;
            case 2:
                z0(eVar.z, -1);
                viewL02 = L0(eVar.z);
                viewL02.setBackgroundColor(q().getColor(R.color.colorPrimaryLight));
                runnable2 = new Runnable() { // from class: e.e
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.V0(viewL02, eVar);
                    }
                };
                viewL02.postDelayed(runnable2, 1000L);
                break;
            case 3:
                z0(I0(), eVar.z);
                viewL0 = K0(eVar.z);
                viewL0.setBackgroundColor(q().getColor(R.color.colorPrimaryLight));
                runnable = new Runnable() { // from class: e.f
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.W0(viewL0, eVar);
                    }
                };
                viewL0.postDelayed(runnable, 500L);
                break;
            case 4:
                z0(I0(), eVar.z);
                viewL02 = K0(eVar.z);
                viewL02.setBackgroundColor(q().getColor(R.color.colorPrimaryLight));
                runnable2 = new Runnable() { // from class: e.h
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.X0(viewL02, eVar);
                    }
                };
                viewL02.postDelayed(runnable2, 1000L);
                break;
            case 5:
            case 6:
                z0(eVar.z, -1);
                View viewL03 = L0(eVar.z);
                if (viewL03 instanceof ViewOnTouchListenerC0005j) {
                    final ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = (ViewOnTouchListenerC0005j) viewL03;
                    viewOnTouchListenerC0005j.setChecked(eVar.A == 5);
                    viewOnTouchListenerC0005j.E.setBackgroundColor(q().getColor(R.color.colorPrimaryLight));
                    viewL03.postDelayed(new Runnable() { // from class: e.i
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.z.Y0(viewOnTouchListenerC0005j, eVar);
                        }
                    }, 500L);
                }
                break;
            default:
                A0();
                break;
        }
    }

    private e q1() {
        Bundle bundleO = r().o();
        e[] eVarArr = (e[]) bundleO.getParcelableArray("AdaptedListFragment_auto_data");
        if (eVarArr == null) {
            return null;
        }
        int i2 = bundleO.getInt("AdaptedListFragment_auto_data_index", -1);
        if (i2 < 0 || i2 >= eVarArr.length) {
            A0();
            return null;
        }
        e eVar = eVarArr[i2];
        bundleO.putInt("AdaptedListFragment_auto_data_index", i2 + 1);
        return eVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void r1(Object obj) {
        View view = getView();
        if (view instanceof m) {
            g1(obj);
            ((m) view).c(O0(view.getContext()));
        }
    }

    private void z0(int i2, int i3) {
        l lVar = this.F;
        if (lVar != null) {
            lVar.k(i2, i3);
        }
    }

    protected final void A0() {
        j0 j0VarR = r();
        j0VarR.n();
        Bundle bundleO = j0VarR.o();
        bundleO.remove("AdaptedListFragment_auto_data");
        bundleO.remove("AdaptedListFragment_auto_data_index");
    }

    protected abstract int B0();

    @Override // e.j0.c
    protected final void C() {
        if (!this.H && (this instanceof d)) {
            T(P0(M0(), null));
        }
    }

    protected final Drawable C0(int i2) {
        return this.E.a(i2);
    }

    protected final Drawable D0(a.b bVar) {
        return this.E.d(bVar.m(this.F.z));
    }

    protected final int E0() {
        return h().getInt("AdaptedListFragment_last_clicked", -1);
    }

    protected final int F0() {
        return h().getInt("AdaptedListFragment_last_clicked_sub", -1);
    }

    protected final ListView G0() {
        View view = getView();
        if (view == null) {
            return null;
        }
        if (view instanceof m) {
            view = ((m) view).a();
        }
        if (view instanceof ListView) {
            return (ListView) view;
        }
        return null;
    }

    protected final int H0() {
        return this.F.A;
    }

    protected final int I0() {
        return this.F.C;
    }

    protected final int J0() {
        if (this.F.E != null) {
            return this.F.E.length;
        }
        return 0;
    }

    protected final View K0(int i2) {
        return this.F.l(i2);
    }

    protected final View L0(int i2) {
        return this.F.m(i2);
    }

    protected final Context M0() {
        l lVar = this.F;
        if (lVar != null) {
            return lVar.z;
        }
        Activity activity = getActivity();
        return activity != null ? activity : f();
    }

    protected boolean N0(int i2) {
        return false;
    }

    protected final void Q0(int i2) {
        l lVar = this.F;
        if (lVar != null) {
            lVar.B[i2] = null;
            this.F.notifyDataSetChanged();
        }
    }

    @Override // e.j0.c
    protected void S(int i2, View.OnClickListener onClickListener) {
        Context contextM0 = M0();
        View viewV = v(contextM0, i2, onClickListener);
        if (this instanceof d) {
            SearchView searchViewP0 = P0(contextM0, viewV);
            LinearLayout linearLayout = new LinearLayout(contextM0);
            linearLayout.setOrientation(0);
            linearLayout.addView(searchViewP0, -2, -1);
            linearLayout.addView(viewV, -2, -1);
            T(linearLayout);
        } else {
            T(viewV);
        }
        this.H = true;
    }

    protected final void a1(int i2, int i3) {
        l lVar = this.F;
        if (lVar != null) {
            lVar.n(i2, i3);
        }
    }

    protected g b1() {
        return new g(this.E.b(this.E.c() ? R.drawable.ic_add : R.drawable.ic_add_circle, false), u(R.string.add), (CharSequence) null);
    }

    protected FrameLayout c1(CharSequence charSequence) {
        j0.d dVarT = t();
        TextView textView = new TextView(this.F.z);
        textView.setText(charSequence);
        textView.setTextSize(0, (dVarT.f398g + dVarT.i) / 2.0f);
        textView.setTextColor(dVarT.o);
        textView.setOnTouchListener(L);
        int i2 = dVarT.f395d;
        textView.setPadding(i2, i2, i2, i2);
        FrameLayout frameLayout = new FrameLayout(this.F.z);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 17;
        frameLayout.addView(textView, layoutParams);
        return frameLayout;
    }

    protected LinearLayout f1(AppWidgetProviderInfo appWidgetProviderInfo) {
        Context context = this.F.z;
        int i2 = k().f636a * 2;
        return d1(a.x.k(context, appWidgetProviderInfo), i2, i2, a.x.j(context, appWidgetProviderInfo));
    }

    protected void g1(Object obj) {
    }

    protected abstract View h1(int i2);

    protected View i1(int i2) {
        throw new RuntimeException("Must be implemented if addItem sub items!");
    }

    protected abstract void k1(int i2);

    protected boolean l1(int i2) {
        return false;
    }

    protected i m1() {
        return null;
    }

    protected void n1(int i2, int i3) {
        this.F.j(i2, i3, false);
    }

    protected void o1(int i2) {
    }

    @Override // e.j0.c, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.E = new o.a(q());
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = viewGroup != null ? viewGroup.getContext() : M0();
        i iVarM1 = m1();
        if (iVarM1 == null) {
            return O0(context);
        }
        m mVar = new m(context);
        mVar.b();
        h hVar = new h(iVarM1, f());
        this.G = hVar;
        hVar.e(this);
        f.d.b(this.G);
        Bundle bundleH = h();
        bundleH.remove("AdaptedListFragment_view_state");
        bundleH.remove("AdaptedListFragment_last_clicked");
        bundleH.remove("AdaptedListFragment_last_clicked_sub");
        return mVar;
    }

    @Override // e.j0.c, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        h hVar = this.G;
        if (hVar != null) {
            hVar.e(null);
            this.G = null;
        }
    }

    @Override // e.j0.c, android.app.Fragment
    public void onHiddenChanged(boolean z) {
        String str;
        super.onHiddenChanged(z);
        if (!z || (str = this.I) == null || str.length() <= 0) {
            return;
        }
        h().putString("AdaptedListFragment_filter_text", this.I);
    }

    @Override // e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        View childAt;
        Bundle bundleH = h();
        ListView listViewG0 = G0();
        if (listViewG0 != null && (childAt = listViewG0.getChildAt(0)) != null) {
            bundleH.putIntArray("AdaptedListFragment_view_state", new int[]{listViewG0.getFirstVisiblePosition(), childAt.getTop(), this.F.A, I0(), J0()});
        }
        String str = this.I;
        if (str != null && str.length() > 0) {
            bundleH.putString("AdaptedListFragment_filter_text", this.I);
        }
        super.onSaveInstanceState(bundle);
    }

    protected boolean p1(int i2) {
        return false;
    }

    public final void s1(e[] eVarArr) {
        Bundle bundleO = r().o();
        bundleO.putParcelableArray("AdaptedListFragment_auto_data", eVarArr);
        bundleO.putInt("AdaptedListFragment_auto_data_index", 0);
    }

    protected final void t1() {
        for (View view : this.F.B) {
            if (view != null) {
                ((k) view).setHighLight(false);
            }
        }
    }

    protected final void u1(int i2) {
        l lVar = this.F;
        if (lVar != null) {
            lVar.q(i2);
        }
    }

    protected final void v1() {
        l lVar = this.F;
        if (lVar != null) {
            lVar.r();
        }
    }

    protected final void w0() {
        l lVar = this.F;
        if (lVar != null) {
            x0(lVar.A);
        }
    }

    protected final void w1() {
        Bundle bundleH = h();
        bundleH.remove("AdaptedListFragment_view_state");
        bundleH.remove("AdaptedListFragment_last_clicked");
        bundleH.remove("AdaptedListFragment_last_clicked_sub");
        Fragment fragmentN = n();
        if (fragmentN != null) {
            if (fragmentN instanceof j0.c) {
                ((j0.c) fragmentN).L();
            } else if (fragmentN instanceof j0.b) {
                ((j0.b) fragmentN).dismiss();
            }
        }
        l lVar = this.F;
        if (lVar != null) {
            lVar.s(B0());
            this.F.notifyDataSetChanged();
        }
        this.J = null;
        String str = this.I;
        if (str != null) {
            A1(str);
        }
    }

    protected final void x0(int i2) {
        l lVar = this.F;
        if (lVar != null) {
            lVar.i(i2);
        }
    }

    public final void x1() {
        e eVarQ1 = q1();
        if (eVarQ1 == null || G0() == null) {
            return;
        }
        j1(eVarQ1);
    }

    protected final void y0(int i2, int i3) {
        if (this.F == null) {
            return;
        }
        v1();
        this.F.j(i2, i3, true);
    }

    protected final void y1(int i2, int i3) {
        if (i2 != I0()) {
            y0(i2, i3);
        } else {
            v1();
        }
    }

    protected final void z1() {
        l lVar = this.F;
        if (lVar != null) {
            lVar.notifyDataSetChanged();
        }
    }
}
