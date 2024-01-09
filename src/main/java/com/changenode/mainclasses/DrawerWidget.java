package com.changenode.mainclasses;

import com.changenode.interfaces.DrawerInterface;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.StringReader;

//TODO: make sure only one drawer open, add shutdown hook
public class DrawerWidget {
    private DrawerInterface drawerInterface;
    public DrawerWidget(Stage root, double x, double y, boolean autoRefreshState, DrawerInterface drawerInterface) {
        arStat = autoRefreshState;
        this.drawerInterface = drawerInterface;
        build(x, y, root);
    }
    private boolean arStat = false;
    private Stage stage;
    private void build(double x, double y, Stage root) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setColor(Color.valueOf("#00000022"));
        dropShadow.setOffsetX(-1);
        dropShadow.setOffsetY(2);

        stage = new Stage();

        stage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                System.out.println("close");
                stage.close();  // or primaryStage.hide();
                drawerInterface.onDrawerClose();
            }
        });

        stage.initOwner(root);

        AnchorPane ap = new AnchorPane();
        Scene scne = new Scene(ap, 300, 480);
        scne.getStylesheets().add(MainContainer.class.getResource("/drawer_style.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);

        Label titlelab = new Label("All the settings\nyou are getting");
        titlelab.setId("title");
        titlelab.setAlignment(Pos.CENTER);

        AnchorPane.setTopAnchor(titlelab, 10.0);
        AnchorPane.setRightAnchor(titlelab, 10.0);
        AnchorPane.setLeftAnchor(titlelab, 10.0);

        HBox resetcont = new HBox();
        resetcont.setEffect(dropShadow);
        resetcont.setId("resetcont");
        resetcont.setAlignment(Pos.CENTER);
        Label resetlab = new Label("Reset all");
        resetlab.setId("resetlab");
        resetlab.setMinHeight(35);
        resetcont.getChildren().addAll(resetlab);
        resetcont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (confirmReset()) {
                    drawerInterface.onResetAll();
                }
            }
        });

        AnchorPane.setLeftAnchor(resetcont, 10.0);
        AnchorPane.setBottomAnchor(resetcont, 10.0);
        AnchorPane.setRightAnchor(resetcont, 54.0);

        HBox exitbcont = new HBox();
        exitbcont.setEffect(dropShadow);
        exitbcont.setMinSize(35, 35);
        exitbcont.setAlignment(Pos.CENTER);
        exitbcont.setId("exitbcont");
        Image exitim = new Image("icons/logout_FILL0_wght400_GRAD0_opsz24.png");
        Image exitimDark = new Image("icons/logout_FILL0_wght400_GRAD0_opsz24_dark.png");
        ImageView exitiv = new ImageView(exitim);
        exitiv.setPreserveRatio(true);
        exitiv.setFitWidth(25);
        exitbcont.getChildren().addAll(exitiv);
        exitbcont.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(() -> {
                    exitiv.setImage(exitimDark);
                });
            }
        });
        exitbcont.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(() -> {
                    exitiv.setImage(exitim);
                });
            }
        });
        exitbcont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawerInterface.onExitRequested();
                stage.close();
            }
        });

        AnchorPane.setRightAnchor(exitbcont, 10.0);
        AnchorPane.setBottomAnchor(exitbcont, 10.0);

        VBox content = buildContent();

        AnchorPane.setTopAnchor(content, 75.0);
        AnchorPane.setRightAnchor(content, 10.0);
        AnchorPane.setLeftAnchor(content, 10.0);

        ap.getChildren().addAll(titlelab, resetcont, exitbcont, content);

        stage.setX(x + 40);
        stage.setY(y - 10);
        scne.setFill(Color.TRANSPARENT);
        stage.setScene(scne);
        stage.show();
    }
    private VBox buildContent() {
        VBox cont = new VBox();

        HBox row0 = new HBox();
        AnchorPane autoRefreshCont = new AnchorPane();
        autoRefreshCont.setId("arcont");
        Label autRefreshLab = new Label("Auto refresh");
        autRefreshLab.setId("arl");
        Label autRefreshLabState = new Label();
        if (arStat) {
            autRefreshLabState.setId("arls_ON");
            autRefreshLabState.setText("ON");
        } else {
            autRefreshLabState.setId("arls_OFF");
            autRefreshLabState.setText("OFF");
        }


        AnchorPane.setLeftAnchor(autRefreshLab, 3.0);
        AnchorPane.setTopAnchor(autRefreshLab, 0.0);
        AnchorPane.setBottomAnchor(autRefreshLab, 0.0);

        AnchorPane.setRightAnchor(autRefreshLabState, 3.0);
        AnchorPane.setTopAnchor(autRefreshLabState, 0.0);
        AnchorPane.setBottomAnchor(autRefreshLabState, 0.0);

        autoRefreshCont.getChildren().addAll(autRefreshLab, autRefreshLabState);
        autoRefreshCont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(() -> {
                    if (arStat) {
                        arStat = false;
                        autRefreshLabState.setId("arls_OFF");
                        autRefreshLabState.setText("OFF");
                        drawerInterface.onChangeARState(false);
                    } else {
                        arStat = true;
                        autRefreshLabState.setId("arls_ON");
                        autRefreshLabState.setText("ON");
                        drawerInterface.onChangeARState(true);
                    }
                });
            }
        });

        AnchorPane manualRefreshCont = new AnchorPane();
        manualRefreshCont.setId("arcont"); //same as in auto refresh
        Label refreshLab = new Label("Manual refresh");
        refreshLab.setId("arl"); // same as in auto refresh
        Image rfrshim = new Image("icons/sync_FILL0_wght400_GRAD0_opsz24.png");
        ImageView rfrshimv = new ImageView(rfrshim);
        rfrshimv.setPreserveRatio(true);
        rfrshimv.setFitWidth(20);

        AnchorPane.setLeftAnchor(refreshLab,3.0);
        AnchorPane.setTopAnchor(refreshLab,0.0);
        AnchorPane.setBottomAnchor(refreshLab,0.0);

        AnchorPane.setRightAnchor(rfrshimv,3.0);
        AnchorPane.setTopAnchor(rfrshimv,0.0);
        AnchorPane.setBottomAnchor(rfrshimv,0.0);

        manualRefreshCont.getChildren().addAll(refreshLab, rfrshimv);

        HBox.setHgrow(manualRefreshCont, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(autoRefreshCont, javafx.scene.layout.Priority.ALWAYS);

        row0.setSpacing(6);
        row0.getChildren().addAll(autoRefreshCont, manualRefreshCont);

        cont.getChildren().addAll(row0);
        return cont;
    }
    private boolean confirmReset() {
        ConfirmBox cb = new ConfirmBox(drawerInterface);
        return false;
    }
}
