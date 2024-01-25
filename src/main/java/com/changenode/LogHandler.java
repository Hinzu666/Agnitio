package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHandler {
    private Label lab;
    public LogHandler(Label label) {
        lab = label;
    }

    public static final SimpleDateFormat sdf = new SimpleDateFormat("[dd.MM HH:mm:ss:SSSS] ");
    public static final SimpleDateFormat sdf_short = new SimpleDateFormat("[dd.MM HH:mm] ");
    private String timeFormatD() {
        Date d = new Date(System.currentTimeMillis());
        return sdf.format(d.getTime());
    }
    public void log(String data) {
        System.out.println(timeFormatD() + data);
        sett(data);
    }
    public void setTarget(Label lab) {
        this.lab = lab;
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
    public void log(String data, boolean format) {
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
