package g;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import f.d0;

/* JADX INFO: loaded from: classes.dex */
public class q implements Parcelable {
    public final int A;
    public final b[] B;
    public final int z;
    private static final MotionEvent.PointerProperties[] C = {new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties()};
    private static final MotionEvent.PointerCoords[] D = {new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords()};
    public static final Parcelable.Creator<q> CREATOR = new a();

    class a implements Parcelable.Creator<q> {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public q createFromParcel(Parcel parcel) {
            d0 d0Var = new d0(parcel);
            d0Var.i();
            q qVar = new q(d0Var);
            d0Var.k();
            return qVar;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public q[] newArray(int i) {
            return new q[i];
        }
    }

    public static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public int f576a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public int f577b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public int f578c;

        public b(int i, float f2, float f3) {
            this.f576a = i;
            this.f577b = (int) f2;
            this.f578c = (int) f3;
        }
    }

    public q(MotionEvent motionEvent, long j) {
        this.z = (int) (motionEvent.getEventTime() - j);
        this.A = motionEvent.getAction();
        int pointerCount = motionEvent.getPointerCount();
        this.B = new b[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            this.B[i] = new b(motionEvent.getPointerId(i), motionEvent.getX(i), motionEvent.getY(i));
        }
    }

    public q(f.s sVar) {
        this.z = sVar.h();
        this.A = sVar.h();
        int iH = sVar.h();
        this.B = new b[iH];
        for (int i = 0; i < iH; i++) {
            this.B[i] = new b(sVar.h(), sVar.h(), sVar.h());
        }
    }

    public MotionEvent a(long j, long j2) {
        int i = 0;
        while (true) {
            b[] bVarArr = this.B;
            if (i >= bVarArr.length) {
                return MotionEvent.obtain(j2, j + ((long) this.z), this.A, bVarArr.length, C, D, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
            }
            MotionEvent.PointerProperties[] pointerPropertiesArr = C;
            pointerPropertiesArr[i].id = bVarArr[i].f576a;
            pointerPropertiesArr[i].toolType = 1;
            MotionEvent.PointerCoords[] pointerCoordsArr = D;
            pointerCoordsArr[i].x = bVarArr[i].f577b;
            pointerCoordsArr[i].y = bVarArr[i].f578c;
            i++;
        }
    }

    public void b(f.s sVar) {
        sVar.d(this.z).d(this.A).d(this.B.length);
        for (b bVar : this.B) {
            sVar.d(bVar.f576a).d(bVar.f577b).d(bVar.f578c);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        d0 d0VarJ = new d0(parcel).j();
        b(d0VarJ);
        d0VarJ.l();
    }
}
