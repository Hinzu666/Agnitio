package com.changenode;

import com.changenode.interfaces.SetupInterface;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class SetupHandler {

    private SetupInterface onsetupcompletedh;
    private final LogHandler logger;
    private String inputData;
    private final TextField tf;
    private final Button btn;

    void addOnComplete(SetupInterface inter) {
        onsetupcompletedh = inter;
    }

    SetupHandler (TextField tf, Button confb, LogHandler logger, ImageView originalView) {
        this.logger = logger;
        this.tf = tf;
        btn = confb;
        this.originalView = originalView;
        ImageView warnView = new ImageView(new Image("icons/warning_FILL0_wght400_GRAD0_opsz24.png"));
        warnView.setFitHeight(22);
        warnView.setFitWidth(22);

        doneView = new ImageView(new Image("icons/done_FILL0_wght400_GRAD0_opsz24.png"));
        doneView.setFitWidth(22);
        doneView.setFitHeight(22);


        confb.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleButtonPress();
            }
        });
    }

    private void handleButtonPress() {
        btn.setDisable(true);
        Thread t = new Thread(() -> {
            ButtonLoadAnimator bla = new ButtonLoadAnimator(btn);
            bla.start();

            String data = tf.getText();

            logger.log("Validating URL");

            if (data.length() < 10) {
                notifyEmpty();
                logger.log("Please provide a valid link");

                bla.requestStopAndLoad(originalView);
                Platform.runLater(() -> {
                    btn.setDisable(false);
                });
                return;
            }

            if (!isValidURL(data)) {
                notifyEmpty();
                logger.log("Invalid URL");

                bla.requestStopAndLoad(originalView);
                Platform.runLater(() -> {
                    btn.setDisable(false);
                });
                return;
            }

            logger.log("URL accepted - saving");

            FileHandler fh = new FileHandler("src/main/resources/data/userdata.JSON");
            try {
                fh.saveLinkToJSON(data);
            } catch (IOException | ParseException ioe) {
                ErrorHandler.handle(ioe, ErrorHandler.Severity.CRITICAL);
            }

            bla.requestStopAndLoad(doneView);

            if (onsetupcompletedh != null) {
                onsetupcompletedh.onSetupCompleted();
            }

        });
        t.start();
    }

    private boolean isValidURL(String link)  {
        try {
            new URL(link).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private void notifyEmpty() {
        Thread t = new Thread(() -> {
            for (int n = 0; n < 5; n++) {
                try {
                    Platform.runLater(() -> {
                        tf.setId("linktfnotify");
                    });
                    Thread.sleep(100);
                    Platform.runLater(() -> {
                        tf.setId("linktf");
                    });
                    Thread.sleep(100);
                } catch (Exception e) {
                    ErrorHandler.handle(e, ErrorHandler.Severity.IGNORE);
                }
            }
        });
        t.start();
    }

    private final ImageView originalView;
    private final ImageView doneView;
}
