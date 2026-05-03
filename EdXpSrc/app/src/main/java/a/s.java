package a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;
import cc.xposed.R;
import f.a;
import f.d0;
import f.j;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class s implements f.l, Parcelable {
    public int A;
    public int B;
    public a.b C;
    public String D;
    public boolean z;
    private static final String E = f.l.m + "alarms";
    public static final Parcelable.Creator<s> CREATOR = new a();

    class a implements Parcelable.Creator<s> {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public s createFromParcel(Parcel parcel) {
            d0 d0Var = new d0(parcel);
            d0Var.i();
            s sVarF = s.f(d0Var);
            d0Var.k();
            return sVarF;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public s[] newArray(int i) {
            return new s[i];
        }
    }

    public static class b extends s {
        public int F;
        public int G;

        public b() {
            f.j jVar = new f.j();
            this.z = false;
            this.F = 0;
            this.G = 0;
            this.A = jVar.g();
            this.B = jVar.h();
            this.C = a.b.r();
            this.D = null;
        }

        private b(boolean z, f.s sVar) {
            this.z = z;
            this.F = sVar.h();
            this.G = sVar.h();
            this.A = sVar.h();
            this.B = sVar.h();
            this.C = sVar.g();
            this.D = Uri.decode(sVar.a());
        }

        /* synthetic */ b(boolean z, f.s sVar, a aVar) {
            this(z, sVar);
        }

        @Override // a.s
        protected int c(j.a aVar) {
            if (!this.z || this.F == 0 || this.G == 0) {
                return 268435455;
            }
            int i = (this.A * 60) + this.B;
            int i2 = (aVar.f462f * 60) + aVar.f463g;
            int iB = f.j.b(aVar.f458b, aVar.f459c);
            if ((this.F & (1 << aVar.f459c)) != 0) {
                int i3 = this.G;
                if ((i3 & Integer.MIN_VALUE) != 0) {
                    i3 |= 1 << (iB - 1);
                }
                int i4 = aVar.f460d;
                if (i < i2) {
                    i4++;
                }
                while (i4 < iB) {
                    if (((1 << i4) & i3) != 0) {
                        return (((i4 - aVar.f460d) * 1440) + i) - i2;
                    }
                    i4++;
                }
            }
            int i5 = aVar.f458b;
            int i6 = iB * 1440;
            for (int i7 = 1; i7 < 13; i7++) {
                int i8 = aVar.f459c + i7;
                if (i8 >= 12) {
                    i8 -= 12;
                    i5++;
                }
                int iB2 = f.j.b(i5, i8);
                int i9 = this.G;
                if ((i9 & Integer.MIN_VALUE) != 0) {
                    i9 |= 1 << (iB2 - 1);
                }
                if (((1 << i8) & this.F) != 0) {
                    for (int i10 = 0; i10 < iB2; i10++) {
                        if (((1 << i10) & i9) != 0) {
                            return ((i6 + ((i10 - aVar.f460d) * 1440)) + i) - i2;
                        }
                    }
                }
                i6 += iB2 * 1440;
            }
            return 268435455;
        }

        @Override // a.s
        protected boolean e() {
            return (!this.z || (this.F & 2) == 0 || (this.G & 268435456) == 0) ? false : true;
        }

        @Override // a.s
        protected void g(f.s sVar) {
            sVar.d((this.z ? 1 : 0) | 2).d(this.F).d(this.G).d(this.A).d(this.B).f(this.C).c(Uri.encode(this.D));
        }

        public String toString() {
            return "AsDate { enabled: " + this.z + ", " + Integer.toBinaryString(this.F) + ", " + Integer.toBinaryString(this.G) + ", " + this.A + ":" + this.B + ", " + this.C + ", " + this.D + " }";
        }
    }

    public static class c extends s {
        public int F;

        public c() {
            f.j jVar = new f.j();
            this.z = false;
            this.F = 127;
            this.A = jVar.g();
            this.B = jVar.h();
            this.C = a.b.r();
            this.D = null;
        }

        private c(boolean z, f.s sVar) {
            this.z = z;
            this.F = sVar.h();
            this.A = sVar.h();
            this.B = sVar.h();
            this.C = sVar.g();
            this.D = Uri.decode(sVar.a());
        }

        /* synthetic */ c(boolean z, f.s sVar, a aVar) {
            this(z, sVar);
        }

        @Override // a.s
        protected int c(j.a aVar) {
            if (!this.z || this.F == 0) {
                return 268435455;
            }
            int i = (this.A * 60) + this.B;
            int i2 = (aVar.f462f * 60) + aVar.f463g;
            int i3 = i >= i2 ? 7 : 8;
            for (int i4 = i >= i2 ? 0 : 1; i4 < i3; i4++) {
                int i5 = aVar.f461e + i4;
                if (i5 >= 7) {
                    i5 -= 7;
                }
                if (((1 << i5) & this.F) != 0) {
                    return ((i4 * 1440) + i) - i2;
                }
            }
            return 268435455;
        }

        @Override // a.s
        protected boolean e() {
            return false;
        }

        @Override // a.s
        protected void g(f.s sVar) {
            sVar.d(this.z ? 1 : 0).d(this.F).d(this.A).d(this.B).f(this.C).c(Uri.encode(this.D));
        }

        public String toString() {
            return "AsWeek { enabled: " + this.z + ", " + Integer.toBinaryString(this.F) + ", " + this.A + ":" + this.B + ", " + this.C + ", " + this.D + " }";
        }
    }

    public static abstract class d extends r {
        private static final String D = f.l.k + "ALARMS_CHANGED";
        protected final f.p B;
        ArrayList<s> C;

        d() {
            super(D);
            this.B = new f.p(s.E);
            e();
        }

        d(ArrayList<s> arrayList) {
            super(D);
            this.B = new f.p(s.E);
            this.C = arrayList;
        }

        @Override // a.r
        protected void b(Intent intent) {
            ArrayList<s> parcelableArrayListExtra = intent.getParcelableArrayListExtra("data");
            if (parcelableArrayListExtra != null) {
                this.C = parcelableArrayListExtra;
            }
        }

        public void e() {
            this.C = new ArrayList<>();
            try {
                if (this.B.E()) {
                    ArrayList<s> arrayList = this.C;
                    s sVarF = s.f(this.B);
                    while (true) {
                        arrayList.add(sVarF);
                        if (!this.B.t()) {
                            break;
                        }
                        arrayList = this.C;
                        sVarF = s.f(this.B);
                    }
                }
                this.B.m();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    public static class e extends d implements a.c {
        private Context E = null;
        private Handler F = null;
        private BroadcastReceiver G = null;
        private String H = null;
        private String I = null;
        private a J = null;
        private f.a K = null;

        public interface a {
            void a(a.b bVar);
        }

        private void f() {
            if (this.H == null) {
                try {
                    Context contextCreatePackageContext = this.E.createPackageContext(f.l.j, 2);
                    this.H = contextCreatePackageContext.getString(R.string.next_alarm) + ": ";
                    this.I = contextCreatePackageContext.getString(R.string.no_alarm);
                } catch (Throwable th) {
                    f.v.d(th);
                    this.H = "Next alarm: ";
                    this.I = "No alarm.";
                }
            }
        }

        private void g(j.a aVar, int i) {
            if (this.J != null && i >= 0 && i <= 20) {
                for (s sVar : this.C) {
                    if (sVar.c(aVar) <= i) {
                        this.J.a(sVar.C);
                    }
                }
            }
        }

        private void h(long j) {
            long jCurrentTimeMillis = (System.currentTimeMillis() / 60000) * 60000;
            g(new j.a(j), (int) ((jCurrentTimeMillis - j) / 60000));
            m(new j.a(jCurrentTimeMillis + 60000), false);
        }

        private void i(long j, boolean z) {
            f();
            String strK = k(j);
            f.a aVar = this.K;
            if ((aVar != null ? aVar.b() : 0L) == j) {
                if (z) {
                    Toast.makeText(this.E, this.H + strK, 0).show();
                    return;
                }
                return;
            }
            if (this.K == null) {
                this.K = f.a.c(this.E, this.F, this, 0);
            }
            this.K.d(j);
            String str = this.H + strK;
            if (z) {
                Toast.makeText(this.E, str, 0).show();
            }
        }

        private String k(long j) {
            return DateFormat.getDateTimeInstance().format(new Date(j));
        }

        private void l(boolean z) {
            f.a aVar = this.K;
            if (aVar != null) {
                aVar.a();
                this.K = null;
                if (z) {
                    f();
                    Toast.makeText(this.E, this.I, 0).show();
                }
            }
        }

        private void m(j.a aVar, boolean z) {
            Iterator<s> it = this.C.iterator();
            int i = 268435455;
            while (it.hasNext()) {
                int iC = it.next().c(aVar);
                if (iC < i) {
                    i = iC;
                }
            }
            if (i != 268435455) {
                i(aVar.f457a + (((long) i) * 60000), z);
                return;
            }
            for (s sVar : this.C) {
                if (sVar.e()) {
                    i(f.j.l() + (((((long) sVar.A) * 60) + ((long) sVar.B)) * 60000), z);
                    return;
                }
            }
            l(z);
        }

        @Override // f.a.c
        public void a(f.a aVar, long j) {
            h(j);
        }

        @Override // a.s.d, a.r
        protected void b(Intent intent) {
            ArrayList<s> arrayList = this.C;
            super.b(intent);
            if (arrayList != this.C) {
                n(true);
            }
        }

        @Override // a.r
        public void d(Context context, Handler handler) {
            this.E = context;
            this.F = handler;
            super.d(context, handler);
            n(false);
        }

        @Override // a.s.d
        public void e() {
            super.e();
            n(true);
        }

        public void j(a aVar) {
            this.J = aVar;
        }

        public void n(boolean z) {
            if (this.E != null) {
                try {
                    long jCurrentTimeMillis = System.currentTimeMillis() / 60000;
                    Long.signum(jCurrentTimeMillis);
                    m(new j.a((jCurrentTimeMillis * 60000) + 60000), z);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    public static class f extends d implements Parcelable {
        public static final Parcelable.Creator<f> CREATOR = new a();
        private static f E;

        class a implements Parcelable.Creator<f> {
            a() {
            }

            @Override // android.os.Parcelable.Creator
            /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
            public f createFromParcel(Parcel parcel) {
                d0 d0Var = new d0(parcel);
                d0Var.k();
                int iH = d0Var.h();
                ArrayList arrayList = new ArrayList(iH);
                for (int i = 0; i < iH; i++) {
                    arrayList.add(s.f(d0Var));
                }
                d0Var.k();
                return new f(arrayList, null);
            }

            @Override // android.os.Parcelable.Creator
            /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
            public f[] newArray(int i) {
                return new f[i];
            }
        }

        private f() {
        }

        private f(ArrayList<s> arrayList) {
            super(arrayList);
        }

        /* synthetic */ f(ArrayList arrayList, a aVar) {
            this(arrayList);
        }

        public static f h() {
            f fVar;
            synchronized (f.class) {
                if (E == null) {
                    E = new f();
                }
                fVar = E;
            }
            return fVar;
        }

        public static void k() {
            synchronized (f.class) {
                E = null;
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public void f(s sVar) {
            this.C.add(sVar);
        }

        public s g(int i) {
            return this.C.get(i);
        }

        public void i(int i) {
            ArrayList<s> arrayList = this.C;
            arrayList.add(i + 1, arrayList.remove(i));
        }

        public void j(int i) {
            ArrayList<s> arrayList = this.C;
            arrayList.add(i - 1, arrayList.remove(i));
        }

        public void l(int i) {
            this.C.remove(i);
        }

        public void m(Context context) {
            this.B.j();
            Iterator<s> it = this.C.iterator();
            while (it.hasNext()) {
                it.next().g(this.B);
                this.B.C();
            }
            this.B.n();
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("data", this.C);
            c(context, intent);
        }

        public int n() {
            return this.C.size();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            d0 d0VarD = new d0(parcel).j().d(this.C.size());
            Iterator<s> it = this.C.iterator();
            while (it.hasNext()) {
                it.next().g(d0VarD);
            }
            d0VarD.l();
        }
    }

    static s f(f.s sVar) {
        int iH = sVar.h();
        boolean z = (iH & 1) != 0;
        a aVar = null;
        return (iH & 2) == 0 ? new c(z, sVar, aVar) : new b(z, sVar, aVar);
    }

    public String b() {
        Object objValueOf;
        StringBuilder sb;
        String str;
        StringBuilder sb2 = new StringBuilder();
        int i = this.A;
        if (i < 10) {
            objValueOf = "0" + this.A;
        } else {
            objValueOf = Integer.valueOf(i);
        }
        sb2.append(objValueOf);
        if (this.B < 10) {
            sb = new StringBuilder();
            str = ":0";
        } else {
            sb = new StringBuilder();
            str = ":";
        }
        sb.append(str);
        sb.append(this.B);
        sb2.append(sb.toString());
        return sb2.toString();
    }

    protected abstract int c(j.a aVar);

    public CharSequence d() {
        String str = this.D;
        return (str == null || str.length() <= 0) ? b() : this.D;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected abstract boolean e();

    protected abstract void g(f.s sVar);

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        d0 d0VarJ = new d0(parcel).j();
        g(d0VarJ);
        d0VarJ.l();
    }
}
