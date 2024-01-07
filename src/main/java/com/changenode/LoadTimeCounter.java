package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LoadTimeCounter {
    private Thread main;
    private long start;
    private boolean david = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("s");
    private boolean master = true;
    LoadTimeCounter(ImageView tar, Image i0, Image i1) {
        Date date = new Date(System.currentTimeMillis());
        start = date.getTime();
        main = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(800);
                    Platform.runLater(() -> {
                        if (david) {
                            david = false;
                            tar.setImage(i1);
                        } else {
                            david = true;
                            tar.setImage(i0);
                        }
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
