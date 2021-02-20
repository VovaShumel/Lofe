package com.livejournal.lofe.lofe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.app.NotificationManagerCompat;

import com.livejournal.lofe.lofe.model.Alarm;
import com.livejournal.lofe.lofe.model.LofeRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static com.livejournal.lofe.lofe.MyUtil.getCurTimeMS;
import static com.livejournal.lofe.lofe.MyUtil.log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String RECORD_TABLE = "RECORD";

    private static final String R_COLUMN_ID = "_id";
    public static final String R_COLUMN_TEXT = "text";
    private static final String R_COLUMN_DATE = "alarm_date";
    private static final String R_COLUMN_PRIORITY = "PRIORITY";
    private static final String R_COLUMN_ALARM_SETTINGS = "alarm_enabled";
    // PRECEDENT_ACTION
    // NEXT_ACTION
    private static final String R_COLUMN_ATTRIBUTES = "attributes";
    private static final String R_COLUMN_CREATION_DATE = "CREATE_DATE";

    private static final String TAG_TABLE = "TAG";
    public static final String TAG_COLUMN_ID = "_id";
    public static final String TAG_COLUMN_NAME = "name";

    private static final String RECORD_TAG_TABLE = "RECORD_TAG";
    public static final String RECORD_TAG_COLUMN_RECORD_ID = "record_id";
    private static final String RECORD_TAG_COLUMN_TAG_ID = "tag_id";

    private static DBHelper sInstance = null;

    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
            sInstance.getWritableDatabase().setForeignKeyConstraintsEnabled(true);
        }
        return sInstance;
    }

    private DBHelper(Context context) {
        super(context, getExternalStorageDirectory().getAbsolutePath() + "/lofe.db", null, 1);
    }

    private static SQLiteDatabase d() {
        return getInstance(MyApplication.getContext()).getWritableDatabase();
    }

    static Cursor GetWithSubstr(String s) {
        return d().rawQuery("SELECT * FROM " + RECORD_TABLE + " WHERE " + R_COLUMN_TEXT + " LIKE '%" + s + "%'", null);
    }

    static void GetTagTable() {
        Cursor c = d().query(TAG_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                MyUtil.log(c.getString(0) + " " + c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
    }

    static void DeleteTag(long id) {
        d().delete(TAG_TABLE, TAG_COLUMN_ID + " = " + id, null);
    }


    static Cursor getRecords(RecordsSortParams sortParams) {
        if (sortParams.byDecPriority) {
            return d().rawQuery("SELECT * FROM " + RECORD_TABLE +
                    " ORDER BY " + R_COLUMN_PRIORITY + " DESC", null);
        }

        if (sortParams.sortByIncTime)
            return d().rawQuery("SELECT * FROM " + RECORD_TABLE +
                    " ORDER BY " + R_COLUMN_DATE, null);

        return null;
    }

    // получить все данные из таблицы DB_TABLE REFACT название метода не соответствует сути, переназвать или переделать вообще
    static Cursor getAllData(long msStartTime) {
        if (msStartTime == 0)
            return d().query(RECORD_TABLE, null, null, null, null, null, null);
        else {
            GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
            calendar.setTimeInMillis(msStartTime);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            msStartTime = calendar.getTimeInMillis();

            return d().rawQuery("SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
                                    " FROM " + RECORD_TABLE +
                                    " WHERE " +
                                    "((" + R_COLUMN_ATTRIBUTES + " & 1) == 0) AND " +
                                    "(" +
                                    RECORD_TABLE + "." + R_COLUMN_DATE + " < " + msStartTime +
                                    " OR " +
                                    RECORD_TABLE + "." + R_COLUMN_DATE + " IS NULL" +
                                    ")" +
                                    " ORDER BY " + R_COLUMN_PRIORITY + " DESC, "
                                                 + R_COLUMN_CREATION_DATE + " DESC", null);
        }
    }

    // получить записи, которым назначены заданные ярлыки
    static Cursor getTagedRecord(long id_tag) {
        return d().rawQuery("SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
                                " FROM " + RECORD_TABLE + ", " + RECORD_TAG_TABLE +
                                " WHERE " +
                                RECORD_TABLE + "." + R_COLUMN_ID + " = " + RECORD_TAG_COLUMN_RECORD_ID +
                                " AND " +
                                RECORD_TAG_COLUMN_TAG_ID + " = " + id_tag + ";", null);
    }

    // получить записи, которым назначены все заданные ярлыки из списка
    static Cursor getTagedRecords(ArrayList<Integer> ids) {
//        String sql = "SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
//              " FROM " + RECORD_TABLE + ", " + RECORD_TAG_TABLE +
//              " WHERE " + RECORD_TABLE + "." + R_COLUMN_ID + " = " + RECORD_TAG_COLUMN_RECORD_ID;

        String sql = "SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
                     " FROM " + RECORD_TABLE + ", " + RECORD_TAG_TABLE +
                     " WHERE " + RECORD_TABLE + "." + R_COLUMN_ID + " = " + RECORD_TAG_COLUMN_RECORD_ID + " AND " +
                                 RECORD_TAG_COLUMN_TAG_ID + " IN (";

        for (int i = 0; i < ids.size(); i++)
            sql += ids.get(i) + ",";

        return d().rawQuery(sql.substring(0, sql.length() - 1) + ") GROUP BY " + RECORD_TAG_COLUMN_RECORD_ID +
                                                                     " HAVING count(" + RECORD_TAG_COLUMN_TAG_ID + ") = " + ids.size(), null);
    }

    // получить список ярлыков, дающих record_id в соответствующем столбце для назначенных ярлыков
    // и NULL, если ярлык не назначен
    // с сортировкой в порядке уменьшения встречаемости ярлыков в других ярлыках
    static Cursor getRecordTags(long id_record) {
//        return d().rawQuery("SELECT " + TAG_TABLE + "." + TAG_COLUMN_ID + ", " + TAG_COLUMN_NAME + ", " + RECORD_TAG_COLUMN_RECORD_ID +
//                                " FROM " + TAG_TABLE +
//                                " LEFT JOIN " + RECORD_TAG_TABLE +
//                                " ON " + RECORD_TAG_COLUMN_TAG_ID + " = " + TAG_TABLE + "." + TAG_COLUMN_ID +
//                                " AND " + RECORD_TAG_COLUMN_RECORD_ID + " = " + id_record, null);

        // Сначала делается выборка данных о всех ярлыках, среди которых отмечены выбранные для данной записи
        // далее выбираются ярлыки по частоте использования
        // и затем делается объединённая выборка по этим с сортировкой по частоте использования ярлыков
        // Подтормаживает, можно ли ускорить?
        return d().rawQuery("SELECT " + TAG_COLUMN_ID + "," + TAG_COLUMN_NAME + "," + RECORD_TAG_COLUMN_RECORD_ID +
                " FROM (SELECT " + TAG_TABLE + "." + TAG_COLUMN_ID + " AS " + TAG_COLUMN_ID + "," + TAG_COLUMN_NAME + "," + RECORD_TAG_COLUMN_RECORD_ID +
                        " FROM " + TAG_TABLE + " LEFT JOIN " + RECORD_TAG_TABLE +
                        " ON " + RECORD_TAG_COLUMN_TAG_ID + "=" + TAG_TABLE + "." + TAG_COLUMN_ID + " AND " +
                                 RECORD_TAG_COLUMN_RECORD_ID + "=" + id_record + ")" +
                " JOIN (SELECT " + RECORD_TAG_COLUMN_TAG_ID + ", count(" + RECORD_TAG_COLUMN_TAG_ID + ") AS tag_id_count " +
                        "FROM " + RECORD_TAG_TABLE + " GROUP BY " + RECORD_TAG_COLUMN_TAG_ID + ")" +
                " ON " + TAG_COLUMN_ID + "=" + RECORD_TAG_COLUMN_TAG_ID +
                " ORDER BY " + RECORD_TAG_COLUMN_RECORD_ID + " DESC," + "tag_id_count DESC", null);
    }

    static LofeRecord getRecord(long id) {
        Cursor c = d().rawQuery("SELECT * FROM " + RECORD_TABLE + " WHERE " + R_COLUMN_ID + " = " + id, null);
        if (c == null) return null;
        c.moveToFirst();
        LofeRecord r = new LofeRecord(id,
                                      c.getString(1),
                                      c.getLong(2),
                                      c.getLong(c.getColumnIndex(R_COLUMN_ALARM_SETTINGS)),
                                      c.getLong(c.getColumnIndex(R_COLUMN_ATTRIBUTES)),
                                      c.getLong(c.getColumnIndex(R_COLUMN_PRIORITY)));
        c.close();
        return r;
    }

    static String getRecordText(long id) {
        String s = "";
        Cursor c = d().query(RECORD_TABLE, new String[] {R_COLUMN_TEXT}, R_COLUMN_ID + " = ?",
                             new String[] {id + ""}, null, null, null);
        if (c != null) {
            if (c.moveToFirst())
                s = c.getString(0);
            c.close();
        }
        return s;
    }

    static long getRecordDate(long id) {
        long l = 0;
        Cursor c = d().query(RECORD_TABLE, new String[] {R_COLUMN_DATE}, R_COLUMN_ID + " = ?",
                             new String[] {id + ""}, null, null, null);
        if (c != null) {
            if (c.moveToFirst())
                l = c.getLong(0);
            c.close();
        }
        return l;
    }

    // добавить запись в RECORD_TABLE
    static long addRecordText(String txt_record) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        cv.put(R_COLUMN_CREATION_DATE, getCurTimeMS());
        return d().insert(RECORD_TABLE, null, cv);
    }

    // Получение всех ярлыков
    static Cursor getAllTag() {
        //return d().query(TAG_TABLE, null, null, null, null, null, null);

        // Выдача ярлыков в порядке частоты их использования, начиная с самого используемого
        return d().rawQuery("SELECT " + TAG_TABLE + "." + TAG_COLUMN_ID + ", " + TAG_COLUMN_NAME +
                            " FROM " + TAG_TABLE +
                            " LEFT JOIN (SELECT " + RECORD_TAG_COLUMN_TAG_ID + ", count(" + RECORD_TAG_COLUMN_TAG_ID + ") AS tag_id_count FROM " +
                                                    RECORD_TAG_TABLE + " GROUP BY " + RECORD_TAG_COLUMN_TAG_ID + ")" +
                            " ON " + RECORD_TAG_COLUMN_TAG_ID + " = " + TAG_TABLE + "." + TAG_COLUMN_ID +
                            " ORDER BY tag_id_count DESC", null);
    }

    // Создать новый ярлык
    static void addTag(String tag_name) {
        ContentValues cv = new ContentValues();
        cv.put(TAG_COLUMN_NAME, tag_name);
        d().insert(TAG_TABLE, null, cv);
    }

    // редактировать запись в RECORD_TABLE
    static void edtRecordText(String txt_record, long id) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        d().update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", new String[] {id + ""});
    }

    static void edtRecordDate(long id, long msDate) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_DATE, msDate);
        d().update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", new String[] {id + ""});
    }

    static void edtRecordPriority(long id, long priority) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_PRIORITY, priority);
        d().update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", new String[] {id + ""});
    }

    // редактировать запись в RECORD_TABLE
    static void edtRecord(LofeRecord record) {
        ContentValues cv = new ContentValues();
        //cv.put(R_COLUMN_TEXT, record.getText());
        cv.put(R_COLUMN_PRIORITY, record.getPriority());
        cv.put(R_COLUMN_ATTRIBUTES, record.getAttributes());
        // TODO добавить остальное
        d().update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", new String[] {Long.toString(record.getId())});
    }

    // добавить запись в RECORD_TABLE
    static void addRec(String txt_record, String txt_date) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        d().insert(RECORD_TABLE, null, cv);
    }

    // удалить запись из RECORD_TABLE
    static void delRec(long id) {
        NotificationManagerCompat.from(MyApplication.getContext()).cancel((int)id); // TODO вроде тут ошибка в том, что при увеличении id записи выше int будут удаляться
                                                                                    // не те уведомления
        d().delete(RECORD_TABLE, R_COLUMN_ID + " = " + id, null);
    }

    // назначить ярлык, если он не назначен, и наоборот
    static void invertTag(long id_record, long id_tag) {
        Cursor c = d().rawQuery("SELECT * " +
                                "FROM " + RECORD_TAG_TABLE +
                                " WHERE" +
                                " record_id = " + id_record +
                                " AND " +
                                " tag_id = " + id_tag + ";", null);

        if (c.getCount() > 0) {
            d().delete(RECORD_TAG_TABLE, RECORD_TAG_COLUMN_RECORD_ID + " = " + id_record + " AND " +
                        RECORD_TAG_COLUMN_TAG_ID + " = " + id_tag, null);
        } else {
            assignTag(id_record, id_tag);
        }
        c.close();
    }

    // Назначение ярлыка
    private static long assignTag(long id_record, long id_tag) {
        ContentValues cv = new ContentValues();
        cv.put(RECORD_TAG_COLUMN_RECORD_ID, id_record);
        cv.put(RECORD_TAG_COLUMN_TAG_ID, id_tag);
        return d().insert(RECORD_TAG_TABLE, null, cv);
    }

    //static Cursor GetCursor(long id, long msStartTime, RecordsSortParams sortParams) {
    static Cursor GetCursor(ArrayList<Integer> ids, long msStartTime, RecordsSortParams sortParams) {
        Cursor cursor;
        if (sortParams != null)
            cursor = getRecords(sortParams);
        else if (ids == null)
            cursor = getAllData(msStartTime);
        else
            //cursor = (id == 0) ? getAllData(msStartTime) : getTagedRecord(id);
            cursor = (ids.size() == 0) ? getAllData(msStartTime) : getTagedRecords(ids);

        return cursor;
    }

    public static List<Alarm> getAlarms() {
        Cursor c = d().rawQuery("SELECT " + R_COLUMN_ID + ", " + R_COLUMN_DATE + " FROM " + RECORD_TABLE +
                                " WHERE " + R_COLUMN_ALARM_SETTINGS + " > 0", null);

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Alarm> alarms = new ArrayList<>(size);

        if (c.moveToFirst()) {
            do {
                Alarm alarm = new Alarm(c.getLong(0), c.getLong(1));
                alarms.add(alarm);
            } while (c.moveToNext());

            c.close();
        }
        return alarms;
    }
}
