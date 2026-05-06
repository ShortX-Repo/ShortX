package f;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class o extends q implements Parcelable {
    public static final Parcelable.Creator<o> CREATOR = new a();
    public final String E;
    public final long F;
    public final byte[] G;

    class a implements Parcelable.Creator<o> {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public o createFromParcel(Parcel parcel) {
            return new o(parcel);
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public o[] newArray(int i) {
            return new o[i];
        }
    }

    o(Parcel parcel) {
        this.E = parcel.readString();
        this.F = parcel.readLong();
        this.G = parcel.createByteArray();
    }

    public o(String str, long j, byte[] bArr) {
        this.E = str;
        this.F = j;
        this.G = bArr;
    }

    @Override // f.q
    public boolean d() {
        return this.G != null;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // f.q
    public long f() {
        return this.F;
    }

    @Override // f.q
    public byte[] j() {
        byte[] bArr = this.G;
        return bArr != null ? bArr : q.z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.E);
        parcel.writeLong(this.F);
        parcel.writeByteArray(this.G);
    }
}
