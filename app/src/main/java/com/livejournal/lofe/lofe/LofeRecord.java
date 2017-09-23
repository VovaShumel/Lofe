package com.livejournal.lofe.lofe;

public class LofeRecord {
    private static String text;

    LofeRecord(String _text) {
        text = _text;
    }

    static String getText() {
        return text;
    }
}
