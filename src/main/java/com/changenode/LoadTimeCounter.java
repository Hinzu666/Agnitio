package com.changenode;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadTimeCounter {
    private final Thread main;
    private boolean david = false;
    private final SimpleDateFormat sdf = new SimpleDateFormat("s");
    private boolean master = true;
    LoadTimeCounter(ImageView tar, Image i0, Image i1) {
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
            } catch (Exception e) {ErrorHandler.handle(e,ErrorHandler.Severity.MINOR);}
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
