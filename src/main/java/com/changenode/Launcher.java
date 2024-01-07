package com.changenode;


import com.changenode.interfaces.ErrorInterface;
import com.changenode.interfaces.SetupInterface;
import com.changenode.interfaces.ThreadInterface;
import com.changenode.mainclasses.MainContainer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//TODO: logger
public class Launcher extends Application {
    private static boolean setupCompleted;

    public static void main(String[] args) {
        Launcher.args = args;
        prelaunch();
        launch(args);
    }
    private static void prelaunch() {
        PreLaunchHandler plh = new PreLaunchHandler();
        try {
             setupCompleted = plh.notMyFirstTimeAroundHere();
        } catch (Exception e) {
            //TODO: handle
            e.printStackTrace();
        }
    }
    private Stage stage;
    private LogHandler logger;
    private Scene scn;

    private void load() {
        VBox vb = new VBox();
        vb.setId("root");
        vb.setAlignment(Pos.TOP_CENTER);

        scn = new Scene(vb, 450, 450);
        scn.getStylesheets().add("launcher_style.css");

        Label title = new Label("Agnitio");
        title.setId("title");
        title.setPadding(new Insets(20, 0, 0, 0));

        Label subtitle = new Label("Data visualisation");
        subtitle.setId("subtitle");
        subtitle.setPadding(new Insets(-12, 0, 0, 0));

        Label time = new Label("0 s");
        time.setId("timelab");
        time.setPadding(new Insets(75, 0, 0, 0));

        Label statusBar = new Label("Contacting sharepoint");
        statusBar.setId("status-bar");
        statusBar.setAlignment(Pos.CENTER);
        logger = new LogHandler(statusBar);
        //statusBar.setPrefWidth(300);

        Label addr_title = new Label("URL address:");
        addr_title.setId("addrlab");
        addr_title.setPadding(new Insets(10, 0, 0, 0));
        Label addr = new Label("UNINITIALIZED");
        addr.setId("addrlab");

        VBox addr_box = new VBox();
        addr_box.setAlignment(Pos.CENTER);
        addr_box.getChildren().addAll(statusBar, addr_title, addr);
        addr_box.setPadding(new Insets(130, 0, 0, 0));

        vb.getChildren().addAll(title, subtitle, time, addr_box);
        scn.setFill(Color.TRANSPARENT);

        stage.setTitle("Agnitio");
        stage.setScene(scn);
        stage.show();

        LoadTimeCounter ltc = new LoadTimeCounter(time);
        ltc.start();

        DataHandler dh = new DataHandler(logger);
        dh.setThreadInterface(new ThreadInterface() {
            @Override
            public void onNotify() {
                Platform.runLater(() -> {
                    moveToMain();
                });
            }
        });
        dh.setErrorInterface(new ErrorInterface() {
            @Override
            public void onCatch(Exception exception, int process) {
                //TODO: handle
            }
        });

        dh.load(addr);

    }
    private static String[] args;
    private void moveToMain() { //TODO: close everything
        stage.close();
        scn = null;
        stage = null;
        MainContainer.main(new String[]{}); //TODO: revisit if this is actually a good idea
    }
    private void setup() {
        VBox vb = new VBox();
        vb.setId("root");
        vb.setAlignment(Pos.TOP_CENTER);

        scn = new Scene(vb, 450, 450);
        scn.getStylesheets().add("launcher_style.css");

        Label title = new Label("Agnitio");
        title.setId("title");
        title.setPadding(new Insets(20, 0, 0, 0));

        Label subtitle = new Label("Data visualisation");
        subtitle.setId("subtitle");
        subtitle.setPadding(new Insets(-12, 0, 0, 0));

        VBox div0 = new VBox();
        div0.setSpacing(3);
        div0.setMaxWidth(360);
        div0.setPadding(new Insets(40, 0, 0, 0));

        VBox div01_left = new VBox();
        div01_left.setAlignment(Pos.CENTER_LEFT);
        Label hintlab = new Label("Sharepoint link");
        hintlab.setId("hintlab");
        div01_left.getChildren().add(hintlab);

        HBox div02 = new HBox();
        div02.setSpacing(8);
        TextField linkField = new TextField();
        linkField.setId("linktf");
        linkField.setPrefHeight(40);
        linkField.setPrefWidth(312);

        Button confirmButton = new Button();
        confirmButton.setId("confirmbutton");
        confirmButton.setPrefHeight(40);
        confirmButton.setMaxHeight(40);
        confirmButton.setPrefWidth(40);
        confirmButton.setMaxWidth(40);
        Image img = new Image("icons/subdirectory_arrow_right_FILL0_wght400_GRAD0_opsz24.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(22);
        view.setFitWidth(22);
        confirmButton.setGraphic(view);

        div02.getChildren().addAll(linkField, confirmButton);
        div0.getChildren().addAll(div01_left, div02);

        Label statusBar = new Label("Contacting sharepoint");
        statusBar.setId("status-bar-undec");
        statusBar.setPadding(new Insets(210, 0, 0, 0));

        logger = new LogHandler(statusBar);
        logger.log("Please provide a project link");

        SetupHandler sh = new SetupHandler(linkField, confirmButton, logger, (ImageView)confirmButton.getGraphic());
        sh.addOnComplete(new SetupInterface() {
            @Override
            public void onSetupCompleted() {
                logger.log(" - Redirecting...", false, true);
                try {
                    Thread.sleep(400);
                } catch (Exception ignore) {} //Not important
                Platform.runLater(() -> {
                    load();
                });
            }
        });

        vb.getChildren().addAll(title, subtitle, div0, statusBar);
        scn.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Agnitio");
        stage.setScene(scn);
        stage.show();
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        if (!setupCompleted) {
            setup();
        } else {
            stage.initStyle(StageStyle.TRANSPARENT);
            load();
        }
    }

}
