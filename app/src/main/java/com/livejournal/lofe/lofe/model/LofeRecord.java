package com.livejournal.lofe.lofe.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class LofeRecord implements Parcelable {

    private static final long MASK_ALARM_ENABLED = 1;

    private static final long MASK_IS_NEED = 1;

    private LofeRecord(Parcel in) {
        id = in.readLong();
        text = in.readString();
        time = in.readLong();
        alarmSettings = in.readLong();
        //allDays = in.readSparseBooleanArray();
    }

    public static final Creator<LofeRecord> CREATOR = new Creator<LofeRecord>() {
        @Override
        public LofeRecord createFromParcel(Parcel in) {
            return new LofeRecord(in);
        }

        @Override
        public LofeRecord[] newArray(int size) {
            return new LofeRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(text);
        parcel.writeLong(time);
        //parcel.writeSparseBooleanArray(allDays);
        parcel.writeLong(alarmSettings);
    }

//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({MON,TUES,WED,THURS,FRI,SAT,SUN})
//    @interface Days{}
//    public static final int MON = 1;
//    public static final int TUES = 2;
//    public static final int WED = 3;
//    public static final int THURS = 4;
//    public static final int FRI = 5;
//    public static final int SAT = 6;
//    public static final int SUN = 7;
//
//    private static final long NO_ID = -1;

    private long id;
    private String text;
    private long time;
    //private SparseBooleanArray allDays;
    //private boolean isAlarmEnabled;
    private long alarmSettings;
    private long attributes;

    public LofeRecord(long id, String text, long time, long alarmSettings) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.alarmSettings = alarmSettings;
    }

    public LofeRecord(long id, String text, long time, long alarmSettings, long attributes) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.alarmSettings = alarmSettings;
        this.attributes = attributes;
    }

    public LofeRecord(long id, String text, long time) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.alarmSettings = 0;
    }

    public LofeRecord(long time) {
        this.id = 0;
        this.text = "";
        this.time = time;
        this.alarmSettings = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public long getAttributes() {
        return attributes;
    }

    public void setIsAlarmEnabled(boolean isAlarmEnabled) {
        if (isAlarmEnabled)
            alarmSettings |= MASK_ALARM_ENABLED;
        else
            alarmSettings &= ~(MASK_ALARM_ENABLED);
    }

    public boolean isAlarmEnabled() {
        return (alarmSettings & MASK_ALARM_ENABLED) > 0;
    }

    public Alarm getAlarm() {
        return new Alarm(getId(), getTime(), isAlarmEnabled());
    }

    public boolean isNeed() {
        return (attributes & MASK_IS_NEED) > 0;
    }

    @Override
    public String toString() {
//        return "Alarm{" +
//                "id=" + id +
//                ", time=" + time +
//                ", label='" + label + '\'' +
//                ", allDays=" + allDays +
//                ", isEnabled=" + isEnabled +
//                '}';
        return "Record{" +
                "id=" + id +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", alarmSettings=" + alarmSettings +
                '}';
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id^(id>>>32));
        result = 31 * result + (int) (time^(time>>>32));
        result = 31 * result + text.hashCode();
//        for(int i = 0; i < allDays.size(); i++) {
//            result = 31 * result + (allDays.valueAt(i)? 1 : 0);
//        }
        return result;
    }

//    private static SparseBooleanArray buildDaysArray(@Days int... days) {
//
//        final SparseBooleanArray array = buildBaseDaysArray();
//
//        for (@Days int day : days) {
//            array.append(day, true);
//        }
//
//        return array;
//
//    }
//
//    private static SparseBooleanArray buildBaseDaysArray() {
//
//        final int numDays = 7;
//
//        final SparseBooleanArray array = new SparseBooleanArray(numDays);
//
//        array.put(MON, false);
//        array.put(TUES, false);
//        array.put(WED, false);
//        array.put(THURS, false);
//        array.put(FRI, false);
//        array.put(SAT, false);
//        array.put(SUN, false);
//
//        return array;
//
//    }

}
