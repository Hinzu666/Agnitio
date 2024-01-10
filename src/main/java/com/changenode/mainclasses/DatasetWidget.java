package com.changenode.mainclasses;

import com.changenode.interfaces.DatasetWidgetListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class DatasetWidget  {

    private final HBox container;
    private DatasetWidgetListener listener = new DatasetWidgetListener() {
        @Override
        public void onHide() {
            System.out.println("Default listener - please initialize [hide]: "+DatasetWidget.this);
        }
        @Override
        public void onShow() {
            System.out.println("Default listener - please initialize [show]: "+DatasetWidget.this);
        }
    };
    private final Label box;
    private final String identifierColorHex;
    DatasetWidget (String identifierColorHex, String identifierName) {
        this.identifierColorHex = identifierColorHex;
        container = new HBox();
        container.setId("datasetwidgetcontainer");
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setOffsetX(-1);
        dropShadow.setOffsetY(2);
        dropShadow.setColor(Color.valueOf("#00000022"));

        container.setEffect(dropShadow);

        box = new Label("");
        double s = 8;
        box.setMaxSize(s,s);
        box.setMinSize(s,s);

        box.setStyle("-fx-background-color: "+identifierColorHex+"; -fx-background-radius: 1px;");

        Label name = new Label(identifierName);
        name.setId("dw_label");

        addHandler();

        container.setAlignment(Pos.CENTER);
        container.setSpacing(12);
        container.getChildren().addAll(box, name);
    }
    private boolean enabled = true;
    public void setListener(DatasetWidgetListener listener) {
        this.listener = listener;
    }
    private void addHandler() {
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (enabled) {
                    enabled = false;
                    //disable

                    container.setId("datasetwidgetcontainerDisabled");
                    box.setStyle("-fx-background-color: #dadada99; -fx-background-radius: 1px;");

                    listener.onHide();

                } else {
                    enabled = true;
                    //enable

                    container.setId("datasetwidgetcontainer");
                    box.setStyle("-fx-background-color: "+identifierColorHex+"; -fx-background-radius: 1px;");

                    listener.onShow();

                }
            }
        });
    }

    public HBox getNode() {
        return container;
    }
}
