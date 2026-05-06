package a;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import com.jozein.xedgepro.service.BinderService;
import f.h0;
import f.o0;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface g extends IInterface {

    public static final class a implements ServiceConnection, Runnable {
        private static volatile a F;
        private final Handler A = h0.b();
        private final List<Runnable> B = new ArrayList();
        private final List<Pair<String, Parcelable>> C = new ArrayList();
        private boolean D = false;
        private g E = null;
        private final Context z;

        public a(Context context) {
            this.z = context;
        }

        private boolean a() {
            synchronized (this) {
                this.A.removeCallbacks(this);
                if (this.D) {
                    return true;
                }
                boolean zG = BinderService.a.g(this.z, this);
                this.D = zG;
                if (!zG) {
                    f.v.c("Failed to bind service!");
                    if (o0.d() == 0) {
                        BinderService.y(this.z, c());
                    }
                }
                return zG;
            }
        }

        private Runnable[] b() {
            Runnable[] runnableArr;
            synchronized (this.B) {
                int size = this.B.size();
                runnableArr = size > 0 ? (Runnable[]) this.B.toArray(new Runnable[size]) : null;
                this.B.clear();
            }
            return runnableArr;
        }

        private Pair<String, Parcelable>[] c() {
            Pair<String, Parcelable>[] pairArr;
            synchronized (this.C) {
                int size = this.C.size();
                pairArr = size > 0 ? (Pair[]) this.C.toArray(new Pair[size]) : null;
                this.C.clear();
            }
            return pairArr;
        }

        private synchronized g e() {
            IBinder iBinderAsBinder;
            this.A.removeCallbacks(this);
            g gVar = this.E;
            if (gVar != null && (iBinderAsBinder = gVar.asBinder()) != null && iBinderAsBinder.isBinderAlive()) {
                return this.E;
            }
            this.E = null;
            this.D = false;
            return null;
        }

        public static a f(Context context) {
            if (F == null) {
                synchronized (a.class) {
                    if (F == null) {
                        F = new a(context);
                    }
                }
            }
            return F;
        }

        private void i() {
            this.A.postDelayed(this, 120000L);
        }

        public boolean d(String str, Parcelable parcelable) {
            g gVarE = e();
            if (gVarE != null) {
                gVarE.b(str, parcelable);
                i();
                return true;
            }
            synchronized (this.C) {
                this.C.add(new Pair<>(str, parcelable));
            }
            return a();
        }

        public Parcelable g(String str) {
            g gVarE = e();
            Parcelable parcelableA = gVarE != null ? gVarE.a(str) : null;
            i();
            return parcelableA;
        }

        public void h(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (e() != null) {
                try {
                    runnable.run();
                } finally {
                    i();
                }
            } else {
                synchronized (this.B) {
                    this.B.add(runnable);
                }
                a();
            }
        }

        @Override // android.content.ServiceConnection
        public synchronized void onBindingDied(ComponentName componentName) {
            this.E = null;
            this.D = false;
        }

        @Override // android.content.ServiceConnection
        public synchronized void onNullBinding(ComponentName componentName) {
            this.E = null;
            this.D = false;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                g gVarD = b.d(iBinder);
                this.E = gVarD;
                if (gVarD != null) {
                    Pair<String, Parcelable>[] pairArrC = c();
                    if (pairArrC != null) {
                        for (Pair<String, Parcelable> pair : pairArrC) {
                            gVarD.b((String) pair.first, (Parcelable) pair.second);
                        }
                    }
                    Runnable[] runnableArrB = b();
                    if (runnableArrB != null) {
                        for (Runnable runnable : runnableArrB) {
                            try {
                                runnable.run();
                            } catch (Throwable th) {
                                f.v.d(th);
                            }
                        }
                    }
                }
            } catch (Throwable th2) {
                f.v.d(th2);
            }
            i();
        }

        @Override // android.content.ServiceConnection
        public synchronized void onServiceDisconnected(ComponentName componentName) {
            this.E = null;
            this.D = false;
        }

        @Override // java.lang.Runnable
        public synchronized void run() {
            if (this.D) {
                this.D = false;
                this.E = null;
                try {
                    this.z.unbindService(this);
                } catch (Throwable th) {
                    f.v.c(th.toString());
                }
            }
        }
    }

    public static abstract class b extends Binder implements g {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final String f7a = f.l.j + ".BinderInterface";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private static int f8b = -1;

        static final class a implements g {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private final IBinder f9a;

            a(IBinder iBinder) {
                this.f9a = iBinder;
            }

            @Override // a.g
            public Parcelable a(String str) {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(b.f7a);
                    parcelObtain.writeString(str);
                } finally {
                    try {
                    } finally {
                    }
                }
                if (this.f9a.transact(1, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    return parcelObtain2.readParcelable(a.class.getClassLoader());
                }
                f.v.c("Failed to read " + str);
                parcelObtain2.recycle();
                parcelObtain.recycle();
                return null;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.f9a;
            }

            @Override // a.g
            public void b(String str, Parcelable parcelable) {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(b.f7a);
                    parcelObtain.writeString(str);
                    parcelObtain.writeParcelable(parcelable, 0);
                    if (!this.f9a.transact(2, parcelObtain, null, 1)) {
                        f.v.c("Failed to commit: " + str);
                    }
                } finally {
                    try {
                    } finally {
                    }
                }
            }
        }

        protected b() {
            attachInterface(this, f7a);
        }

        public static g d(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(f7a);
            return iInterfaceQueryLocalInterface instanceof g ? (g) iInterfaceQueryLocalInterface : new a(iBinder);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        protected int e() {
            if (f8b < 0) {
                try {
                    f8b = f.r.l(f.l.l);
                } catch (Throwable th) {
                    f.v.d(th);
                    f8b = 0;
                }
            }
            return f8b;
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            int callingUid = Binder.getCallingUid();
            int iE = e();
            if (callingUid >= 10000 && iE > 0 && iE != callingUid) {
                f.v.c("Unsupported caller uid: " + callingUid);
                return false;
            }
            if (i == 1) {
                parcel.enforceInterface(f7a);
                try {
                    Parcelable parcelableA = a(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeParcelable(parcelableA, 0);
                    return true;
                } catch (Throwable th) {
                    f.v.d(th);
                    return false;
                }
            }
            if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(f7a);
                return true;
            }
            parcel.enforceInterface(f7a);
            try {
                b(parcel.readString(), parcel.readParcelable(getClass().getClassLoader()));
                if (parcel2 != null) {
                    parcel2.writeNoException();
                }
                return true;
            } catch (Throwable th2) {
                f.v.d(th2);
                return false;
            }
        }
    }

    Parcelable a(String str);

    void b(String str, Parcelable parcelable);
}
