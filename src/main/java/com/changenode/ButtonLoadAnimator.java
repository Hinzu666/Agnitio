package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.text.View;



public class ButtonLoadAnimator {
    private Button btn;
    private Thread main;

    private ImageView up, down, original;
    private boolean master = true;
    ButtonLoadAnimator (Button btn) {

        original = (ImageView) btn.getGraphic();

        up = new ImageView(new Image("icons/hourglass_top_FILL0_wght400_GRAD0_opsz24.png"));
        up.setFitWidth(22);
        up.setFitHeight(22);
        down = new ImageView(new Image("icons/hourglass_bottom_FILL0_wght400_GRAD0_opsz24.png"));
        down.setFitWidth(22);
        down.setFitHeight(22);

        this.btn = btn;
        main = new Thread(() -> {
            try {
                while (master) {
                    glassDown();

                    if (!master) {break;}
                    Thread.sleep(600);
                    if (!master) {break;}

                    glassUp();

                    if (!master) {break;}
                    Thread.sleep(600);
                    if (!master) {break;}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void start() {
        main.start();
    }
    public void requestStopAndLoad (ImageView imageView) {
        master = false;
        Platform.runLater(() -> {
            btn.setGraphic(imageView);
        });
    }

    private void glassDown() {
        Platform.runLater(() -> {
            btn.setGraphic(down);
        });
    }
    private void glassUp() {
        Platform.runLater(() -> {
            btn.setGraphic(up);
        });
    }

}
