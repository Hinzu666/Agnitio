package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHandler {
    private final Label lab;
    LogHandler (Label label) {
        lab = label;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("[dd.MM HH:mm:ss:SSSS] ");
    private String timeFormatD() {
        Date d = new Date(System.currentTimeMillis());
        return sdf.format(d.getTime());
    }
    void log (String data) {
        System.out.println(timeFormatD() + data);
        sett(data);
    }
    void log (String data, boolean format, boolean append) {
        String stamp = timeFormatD();
        System.out.println(stamp + data);

        String pref = "";
        if (format) {
            pref = stamp;
        }

        if (append) {
            String ex = lab.getText();
            sett(pref + ex + data);
        } else {
            sett(pref + data);
        }
    }
    void log (String data, boolean format) {
        String formattedData = timeFormatD() + data;
        System.out.println(formattedData);
        if (format) {
            sett(formattedData);
        } else {
            sett(data);
        }
    }


    private void sett(String dat) {
        Platform.runLater(() -> {
            lab.setText(dat);
        });
    }
}
