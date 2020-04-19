package com.livejournal.lofe.lofe;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

class MyUtil {

    // Возвращает текущее время в мс для текущей локали
    static long getCurTimeMS() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }

    static void log(String s) {
        Log.d("DEBUG", s);
    }

    static String cursorToString(Cursor crs) {
        JSONArray arr = new JSONArray();
        crs.moveToFirst();
        while (!crs.isAfterLast()) {
            int nColumns = crs.getColumnCount();
            JSONObject row = new JSONObject();
            for (int i = 0 ; i < nColumns ; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    try {
                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:    row.put(colName, crs.getBlob(i).toString()) ; break;
                            case Cursor.FIELD_TYPE_FLOAT:   row.put(colName, crs.getDouble(i))          ; break;
                            case Cursor.FIELD_TYPE_INTEGER: row.put(colName, crs.getLong(i))            ; break;
                            case Cursor.FIELD_TYPE_NULL:    row.put(colName, null)                ; break;
                            case Cursor.FIELD_TYPE_STRING:  row.put(colName, crs.getString(i))          ; break;
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            arr.put(row);
            if (!crs.moveToNext())
                break;
        }
        crs.close(); // close the cursor
        return arr.toString();
    }
}
