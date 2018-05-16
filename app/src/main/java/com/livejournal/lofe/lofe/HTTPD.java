package com.livejournal.lofe.lofe;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import java.io.*;
//import java.util.*;
import java.lang.String;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.livejournal.lofe.lofe.MyUtil.getCurTimeMS;
import static com.livejournal.lofe.lofe.MyUtil.log;

class HTTPD {

    private WebServer server;

    HTTPD() {
        server = new WebServer();
        try {
            server.start();
        } catch(IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");
    }

    void destroy() {
        if (server != null)
            server.stop();
    }

    private class WebServer extends NanoHTTPD {

        WebServer() {
            super(8080);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String msg =
                "<html>" +
                    "<head>" +
                    "</head>" +
                    "<body>" +
                        //"<h1>Lofe</h1>\n" +
                        "<ul>";
                    //"<div id=\"chart_div\" style=\"width: 900px; height: 500px\"></div>" +

            DB db = new DB(MyApplication.getContext());
            db.open();
            Cursor c = db.getAllData(getCurTimeMS());
            if (c.moveToFirst()) {
                do {
                    msg += "<li>" + c.getString(1) + "\n";
                } while (c.moveToNext());
            }
            c.close();
            db.close();
            //log("Запрос к серверу");
                    msg +=
                    "</body>" +
                "</html>\n";
//            String msg = "<html><body><h1>Lofe</h1>\n";
            //Map<String, String> parms = session.getParms();
            Map<String, List<String>> parms = session.getParameters();
//            if (parms.get("username") == null) {
//                msg += "<form action='?' method='get'>\n";
//                msg += "<p>Your name: <input type='text' name='username'></p>\n";
//                msg += "</form>\n";
//            } else {
//                msg += "<p>Hello, " + parms.get("username") + "!</p>";
//            }
            return newFixedLengthResponse(msg);
        }
    }
}
