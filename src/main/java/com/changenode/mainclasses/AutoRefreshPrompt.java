package com.changenode.mainclasses;

import com.changenode.interfaces.DrawerInterface;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.formula.functions.T;

public class AutoRefreshPrompt {
    private static TextField tf;
    private static DropShadow dropShadow;
    public static void query(DrawerInterface drawerInterface) {
        Stage stage = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane, 400, 200);
        scene.getStylesheets().add(MainContainer.class.getResource("/auto_refresh_prompt_style.css").toExternalForm());

        dropShadow = new DropShadow();
        dropShadow.setOffsetX(-1);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(2);
        dropShadow.setColor(Color.valueOf("#00000022"));

        Label title = new Label("Auto refresh rate");
        title.setId("titleLabel");

        AnchorPane.setLeftAnchor(title, 15.0);
        AnchorPane.setTopAnchor(title, 15.0);

        Label desc = new Label("Set the rate at which your project data is re-downloaded and visualized automatically");
        desc.setWrapText(true);
        desc.setMaxWidth(380.0);
        desc.setId("medLabel");

        AnchorPane.setTopAnchor(desc, 35.0);
        AnchorPane.setLeftAnchor(desc, 15.0);

        HBox row0 = new HBox();
        row0.setSpacing(8);
        tf = new TextField();
        tf.setPromptText("Minutes");
        tf.setId("textField");
        tf.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        Button accept = new Button("Accept");
        accept.setId("button");
        accept.setOnMouseClicked(event -> {
            int value = Integer.parseInt(tf.getText());
            drawerInterface.onRefreshRateChange(value);
            stage.close();
        });

        HBox.setHgrow(accept, Priority.ALWAYS);
        HBox.setHgrow(tf, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(row0, 15.0);
        AnchorPane.setRightAnchor(row0, 15.0);
        AnchorPane.setTopAnchor(row0, 95.0);

        row0.getChildren().addAll(tf, accept);

        HBox row1 = new HBox();
        row1.setSpacing(8);

        Button discard = new Button("Discard");
        discard.setId("buttonDisc");
        discard.setEffect(dropShadow);
        discard.setOnMouseClicked(event -> {
            stage.close();
        });

        row1.getChildren().addAll(addSuggestion("30 min", 30), addSuggestion("1 h", 60), addSuggestion("2 h", 120), addSuggestion("4h", 240),discard);

        AnchorPane.setBottomAnchor(row1, 15.0);
        AnchorPane.setLeftAnchor(row1, 15.0);

        anchorPane.getChildren().addAll(title, desc, row0, row1);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    private static Button addSuggestion (String suggestion, int value) {

        Button ret = new Button(suggestion);
        ret.setId("buttonSug");
        ret.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                tf.setText(String.valueOf(value));
            });
        });
        ret.setEffect(dropShadow);

        return ret;
    }
}
