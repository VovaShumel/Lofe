package com.livejournal.lofe.lofe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

class MyUtil {

    // Возвращает текущее время в мс
    static long getCurTimeMS() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }
}
