package com.livejournal.lofe.lofe;

import android.util.Log;

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
}
