package com.changenode;

import com.changenode.interfaces.DrawerInterface;
import com.changenode.mainclasses.MainContainer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//TODO: make sure only one drawer open, add shutdown hook
public class DrawerWidget {
    private DrawerInterface drawerInterface;
    public DrawerWidget(double x, double y, DrawerInterface drawerInterface) {
        this.drawerInterface = drawerInterface;
        build(x, y);
    }
    private void build(double x, double y) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setColor(Color.valueOf("#00000022"));
        dropShadow.setOffsetX(-1);
        dropShadow.setOffsetY(2);

        Stage stage = new Stage();
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

        ap.getChildren().addAll(titlelab, resetcont, exitbcont);

        stage.setX(x + 40);
        stage.setY(y - 10);
        scne.setFill(Color.TRANSPARENT);
        stage.setScene(scne);
        stage.show();
    }
    private boolean confirmReset() {
        ConfirmBox cb = new ConfirmBox();

        return false;
    }
}
