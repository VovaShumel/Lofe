package com.livejournal.lofe.lofe;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordsSortParams implements Parcelable {

    boolean sortByIncTime = false;

    public RecordsSortParams(boolean sortByIncTime) {
        this.sortByIncTime = sortByIncTime;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(sortByIncTime ? 1 : 0);
    }

    public static final Parcelable.Creator<RecordsSortParams> CREATOR = new Parcelable.Creator<RecordsSortParams>() {
        public RecordsSortParams createFromParcel(Parcel in) {
            return new RecordsSortParams(in);
        }

        public RecordsSortParams[] newArray(int size) {
            return new RecordsSortParams[size];
        }
    };

    private RecordsSortParams(Parcel parcel) {
        sortByIncTime = parcel.readInt() > 0;
    }
}