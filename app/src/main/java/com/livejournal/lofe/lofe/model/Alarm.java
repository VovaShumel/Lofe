package com.livejournal.lofe.lofe.model;

public class Alarm {
    private final long id;
    private long time;
    private Boolean isEnabled;

    // TODO вроде как правильно будет потом этот объект держать внутри LofeRecord. А может, и нет

    public Alarm(long id, long time) {
        this.id = id;
        this.time = time;
        this.isEnabled = true;
    }

    public Alarm(long id, long time, Boolean isEnabled) {
        this.id = id;
        this.time = time;
        this.isEnabled = isEnabled;
    }

    public long getId() {
        return id;
    }

    public int notificationId() {
        final long id = getId();
        return (int) (id^(id>>>32));
    }

    public long getTime() {
        return time;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }
}
