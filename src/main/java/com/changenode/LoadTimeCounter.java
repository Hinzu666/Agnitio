package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LoadTimeCounter {
    private Thread main;
    private long start;
    private SimpleDateFormat sdf = new SimpleDateFormat("s");
    private boolean master = true;
    LoadTimeCounter(Label tar) {
        Date date = new Date(System.currentTimeMillis());
        start = date.getTime();
        main = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(500);
                    long cur = new Date(System.currentTimeMillis()).getTime();
                    long elap = cur - start;
                    Platform.runLater(() -> {
                        tar.setText(sdf.format(elap) + " s");
                    });
                }
            } catch (Exception e) {e.printStackTrace();}
        });
    }

    public void start() {
        master = true;
        main.start();
    }

    public void requestStop() {
        master = false;
    }
}
