package com.livejournal.lofe.lofe;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.os.Environment.*;
import static com.livejournal.lofe.lofe.MyLog.d;

public class DB {

    private static final String DB_PATH = getExternalStorageDirectory().getAbsolutePath() + "/";
    private static String DB_NAME = "lofe.db";
    private static final int DB_VERSION = 1;
    private static final String RECORD_TABLE = "RECORD";

    private static final String R_COLUMN_ID = "_id";
    public static final String R_COLUMN_TEXT = "text";
    private static final String R_COLUMN_DATE = "alarm_date";
    // PRECEDENT_ACTION
    // NEXT_ACTION

    private static final String TAG_TABLE = "TAG";
    public static final String TAG_COLUMN_ID = "_id";
    public static final String TAG_COLUMN_NAME = "name";

    private static final String RECORD_TAG_TABLE = "RECORD_TAG";
    public static final String RECORD_TAG_COLUMN_RECORD_ID = "record_id";
    private static final String RECORD_TAG_COLUMN_TAG_ID = "tag_id";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    private String[] columns = null;
    private String[] selectionArgs = null;

    DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_PATH + DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        mDB.setForeignKeyConstraintsEnabled(true);
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    public void AddColumn(String tableName, String newColumnName) {
        String upgradeQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + newColumnName + " NUMERIC;";
        mDB.execSQL(upgradeQuery);
    }

    public void GetColumnNames() {
        Cursor c = mDB.rawQuery("PRAGMA table_info(" + RECORD_TABLE + ")", null);
        if (c.moveToFirst()) {
            do {
                MyUtil.log("name: " + c.getString(1) + " type: " + c.getString(2));
            } while (c.moveToNext());
        }
        c.close();
    }

    void GetWithSubstr(String s) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + RECORD_TABLE +
                                     " WHERE " + R_COLUMN_TEXT + " LIKE '%" + s + "%'", null);
        if (c.moveToFirst()) {
            do {
                MyUtil.log(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void GetTagTable() {
        Cursor c = mDB.query(TAG_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                MyUtil.log(c.getString(0) + " " + c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
    }

    void DeleteTag(long id) {
        mDB.delete(TAG_TABLE, TAG_COLUMN_ID + " = " + id, null);
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(long msStartTime) {
        if (msStartTime == 0)
            return mDB.query(RECORD_TABLE, null, null, null, null, null, null);
        else {
            GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
            calendar.setTimeInMillis(msStartTime);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            msStartTime = calendar.getTimeInMillis();


            MyUtil.log("{");
            MyLog.d("Загрузка за прошедшие сутки от " + msStartTime);
            MyUtil.log(new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(msStartTime)));
            MyUtil.log("}");

            String query = "SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
                    " FROM " + RECORD_TABLE +
                    " WHERE " +
                    RECORD_TABLE + "." + R_COLUMN_DATE + " < " + msStartTime +
                    " OR " +
                    RECORD_TABLE + "." + R_COLUMN_DATE + " IS NULL;";
            return mDB.rawQuery(query, null);
        }
    }

    // получить записи, которым назначены заданные ярлыки
    public Cursor getTagedRecord(long id_tag) {
        String query = "SELECT " + RECORD_TABLE + "." + R_COLUMN_ID + ", " + R_COLUMN_TEXT +
                " FROM " + RECORD_TABLE + ", " + RECORD_TAG_TABLE +
                " WHERE " +
                RECORD_TABLE + "." + R_COLUMN_ID + " = " + RECORD_TAG_COLUMN_RECORD_ID +
                " AND " +
                RECORD_TAG_COLUMN_TAG_ID + " = " + id_tag + ";";
        return mDB.rawQuery(query, null);
    }

    // получить список ярлыков, дающих record_id в соответствующем столбце для назначенных ярлыков
    // и NULL, если ярлык не назначен
    public Cursor getRecordTags(long id_record) {
        String query = "SELECT " + TAG_TABLE + "." + TAG_COLUMN_ID + ", " + TAG_COLUMN_NAME + ", " + RECORD_TAG_COLUMN_RECORD_ID +
                " FROM " + TAG_TABLE +
                " LEFT JOIN " + RECORD_TAG_TABLE +
                " ON " + RECORD_TAG_COLUMN_TAG_ID + " = " + TAG_TABLE + "." + TAG_COLUMN_ID +
                " AND " + RECORD_TAG_COLUMN_RECORD_ID + " = " + id_record;
        return mDB.rawQuery(query, null);
    }

    public String getRecordText(long id) {
        String s = "";
        columns = new String[] {R_COLUMN_TEXT};
        selectionArgs = new String[] {id + ""};
        Cursor c = mDB.query(RECORD_TABLE, columns, R_COLUMN_ID + " = ?", selectionArgs, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                s = c.getString(0);
            }
            c.close();
        }
        return s;
    }

    public long getRecordDate(long id) {
        long l = 0;
        columns = new String[] {R_COLUMN_DATE};
        selectionArgs = new String[] {id + ""};
        Cursor c = mDB.query(RECORD_TABLE, columns, R_COLUMN_ID + " = ?", selectionArgs, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                l = c.getLong(0);
            }
            c.close();
        }
        return l;
    }

//    public Cursor getTagsDataByID(long id) {
//
//
//        //String query = mDB
//
//
//        //Cursor c = mDB.query(R_T_TABLE, )
//    }

    // добавить запись в RECORD_TABLE
    public long addRecordText(String txt_record) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        return mDB.insert(RECORD_TABLE, null, cv);
    }

    // Получение всех ярлыков
    public Cursor getAllTag() {
        return mDB.query(TAG_TABLE, null, null, null, null, null, null);
    }

    // Создать новый ярлык
    public void addTag(String tag_name) {
        ContentValues cv = new ContentValues();
        cv.put(TAG_COLUMN_NAME, tag_name);
        mDB.insert(TAG_TABLE, null, cv);
    }

    // редактировать запись в RECORD_TABLE
    public void edtRecordText(String txt_record, long id) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        selectionArgs = new String[] {id + ""};
        mDB.update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", selectionArgs);
    }


    public void edtRecordDate(long id, long msDate) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_DATE, msDate);
        selectionArgs = new String[] {id + ""};
        mDB.update(RECORD_TABLE, cv, R_COLUMN_ID + " = ?", selectionArgs);
    }

    // добавить запись в RECORD_TABLE
    public void addRec(String txt_record, String txt_date) {
        ContentValues cv = new ContentValues();
        cv.put(R_COLUMN_TEXT, txt_record);
        mDB.insert(RECORD_TABLE, null, cv);
    }

    // удалить запись из RECORD_TABLE
    public void delRec(long id) {
        mDB.delete(RECORD_TABLE, R_COLUMN_ID + " = " + id, null);
    }

    // назначить ярлык, если он не назначен, и наоборот
    public void invertTag(long id_record, long id_tag) {
        String query;
        query = "SELECT * " +
                "FROM " + RECORD_TAG_TABLE +
                " WHERE" +
                " record_id = " + id_record +
                " AND " +
                " tag_id = " + id_tag + ";";
        Cursor c = mDB.rawQuery(query, null);

        //if (c != null) {
        if (c.getCount() > 0) {
            mDB.delete(RECORD_TAG_TABLE, RECORD_TAG_COLUMN_RECORD_ID + " = " + id_record + " AND " +
                                         RECORD_TAG_COLUMN_TAG_ID + " = " + id_tag, null);
        } else {
            assignTag(id_record, id_tag);
        }
        c.close();
    }

    // Назначение ярлыка
    private long assignTag(long id_record, long id_tag) {
        ContentValues cv = new ContentValues();
        cv.put(RECORD_TAG_COLUMN_RECORD_ID, id_record);
        cv.put(RECORD_TAG_COLUMN_TAG_ID, id_tag);
        return mDB.insert(RECORD_TAG_TABLE, null, cv);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL("PRAGMA foreign_keys=ON;");
//            db.execSQL(DB_CREATE);
//
//            ContentValues cv = new ContentValues();
//            for (int i = 1; i < 5; i++) {
//                cv.put(COLUMN_TXT, "sometext " + i);
//                cv.put(COLUMN_IMG, R.drawable.ic_launcher);
//                db.insert(DB_TABLE, null, cv);
//            }
        }

//        @Override
//        public void onConfigured(SQLiteDatabase db) {
//            //db.execSQL("PRAGMA foreign_keys=ON;");
//            db.setForeignKeyConstraintsEnabled(true);
//        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

