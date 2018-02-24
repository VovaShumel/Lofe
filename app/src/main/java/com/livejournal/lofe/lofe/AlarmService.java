package com.livejournal.lofe.lofe;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// Сервис, который постоянно висит в памяти и запускает установленные будильники
// или отображает уведомления

public class AlarmService extends Service {
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
