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

import static android.os.Environment.getExternalStorageDirectory;
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

//        @Override
//        public Response serve(IHTTPSession session) { // Рабочий первоначальный вариант
//            String msg =
//                "<html>" +
//                    "<head>" +
//                    "</head>" +
//                    "<body>" +
//                        //"<h1>Lofe</h1>\n" +
//                        "<ul>";
//                    //"<div id=\"chart_div\" style=\"width: 900px; height: 500px\"></div>" +
//
//            DB db = new DB(MyApplication.getContext());
//            db.open();
//            Cursor c = db.getAllData(getCurTimeMS());
//            if (c.moveToFirst()) {
//                do {
//                    msg += "<li>" + c.getString(1) + "\n";
//                } while (c.moveToNext());
//            }
//            c.close();
//            db.close();
//            //log("Запрос к серверу");
//                    msg +=
//                    "</body>" +
//                "</html>\n";
////            String msg = "<html><body><h1>Lofe</h1>\n";
//            //Map<String, String> parms = session.getParms();
//            Map<String, List<String>> parms = session.getParameters();
////            if (parms.get("username") == null) {
////                msg += "<form action='?' method='get'>\n";
////                msg += "<p>Your name: <input type='text' name='username'></p>\n";
////                msg += "</form>\n";
////            } else {
////                msg += "<p>Hello, " + parms.get("username") + "!</p>";
////            }
//            return newFixedLengthResponse(msg);
//        }

        @Override
        public Response serve(IHTTPSession session) {

            String filePath = getExternalStorageDirectory().getAbsolutePath() + "/Lofe/www";

//            Method method = session.getMethod();
            String uri = session.getUri();
//            Map<String, String> files = new HashMap<>();
//            SharedPreferences prefs = OpenRAP.getContext().getSharedPreferences(MainActivity.mypreference, MODE_PRIVATE);
//            OpenRAP app = (OpenRAP) OpenRAP.getContext();
//            Storage storage = new Storage(OpenRAP.getContext());
//            String currentpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/";
//            String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/temp/";
//            String ecarpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/ecars_files/";
//            String xcontent = Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/xcontent/";
            //String Endpoint = session.getUri();

            log(uri);

            if (uri.equals("/")) {
                filePath += "/index.html";
                String answer = ""; // Работает криво
                try {
                    FileReader file = new FileReader(filePath); // Open file from SD Card
                    BufferedReader reader = new BufferedReader(file);
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        answer += line + "\r\n";
                    }
                    reader.close();
                } catch (IOException ioe) {
                    Log.w("Httpd", ioe.toString());
                }
                return newFixedLengthResponse(answer);
            } else {
                filePath += uri;
                try {
                    FileInputStream fileInputStream = new FileInputStream(filePath);
                    return newChunkedResponse(Response.Status.OK, getMimeTypeForFile(uri), fileInputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
                }
            }

//            String msg = "<html><head></head><body><ul>";
//            DB db = new DB(MyApplication.getContext());
//            db.open();
//            Cursor c = db.getAllData(getCurTimeMS());
//            if (c.moveToFirst())
//                do msg += "<li>" + c.getString(1) + "\n"; while (c.moveToNext());
//            c.close();
//            db.close();
//            msg += "</body></html>\n";
//            return newFixedLengthResponse(msg);
        }
    }
}
