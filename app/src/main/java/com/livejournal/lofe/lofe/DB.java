package com.livejournal.lofe.lofe;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import static android.os.Environment.*;
import static com.livejournal.lofe.lofe.MyLog.d;

public class DB {

    private static final String DB_PATH = getExternalStorageDirectory().getAbsolutePath() + "/";
    private static String DB_NAME = "lofe.db";
    private static final int DB_VERSION = 1;
    private static final String RECORD_TABLE = "RECORD";

    public static final String R_COLUMN_ID = "_id";
    public static final String R_COLUMN_TEXT = "text";

    private static final String TAG_TABLE = "TAG";
    public static final String TAG_COLUMN_ID = "_id";
    public static final String TAG_COLUMN_NAME = "name";

    private static final String RECORD_TAG_TABLE = "RECORD_TAG";
    public static final String RECORD_TAG_COLUMN_ID = "_id";
    public static final String RECORD_TAG_COLUMN_RECORD_ID = "record_id";
    public static final String RECORD_TAG_COLUMN_TAG_ID = "tag_id";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    private String[] columns = null;
    private String[] selectionArgs = null;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        d(DB_PATH);
        mDBHelper = new DBHelper(mCtx, DB_PATH + DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        mDB.setForeignKeyConstraintsEnabled(true);
        //MyLog.d("Открытие бд");
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(RECORD_TABLE, null, null, null, null, null, null);
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
        }
        return s;
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
            d("Ярлык таки-удаляется");
        } else {
            assignTag(id_record, id_tag);
            d("Назначение ярлыка таки-происходит");
        }
    }

    // Назначение ярлыка
    public long assignTag(long id_record, long id_tag) {
        ContentValues cv = new ContentValues();
        cv.put(RECORD_TAG_COLUMN_RECORD_ID, id_record);
        cv.put(RECORD_TAG_COLUMN_TAG_ID, id_tag);
        return mDB.insert(RECORD_TAG_TABLE, null, cv);

    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            //MyLog.d("Создание бд");
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
//            MyLog.d("Установили форейн кей");
//        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

