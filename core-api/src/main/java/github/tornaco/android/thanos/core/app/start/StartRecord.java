package github.tornaco.android.thanos.core.app.start;

import android.os.Parcel;
import android.os.Parcelable;

public final class StartRecord implements Parcelable {
    protected StartRecord(Parcel in) {
    }

    public static final Creator<StartRecord> CREATOR = new Creator<StartRecord>() {
        @Override
        public StartRecord createFromParcel(Parcel in) {
            return new StartRecord(in);
        }

        @Override
        public StartRecord[] newArray(int size) {
            return new StartRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getStability() {
        return Parcelable.super.getStability();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
