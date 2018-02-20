package com.livejournal.lofe.lofe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyUtil {

    // Возвращает текущее время в мс
    public static long getCurTimeMS() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }
}
