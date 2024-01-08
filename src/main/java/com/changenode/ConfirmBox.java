package com.changenode;

import com.changenode.mainclasses.MainContainer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfirmBox {
    //TODO: this
    public ConfirmBox() {
        Stage stage = new Stage();
        AnchorPane ap = new AnchorPane();
        Scene scene = new Scene(ap, 630, 400);
        scene.getStylesheets().add(MainContainer.class.getResource("/confirm_style.css").toExternalForm());

        RadialGradient radialGradient = new RadialGradient(
                0, 0, 0.5, 0.65, 0.5, true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.valueOf("#2D1E25")),
                new Stop(1, Color.valueOf("#692434"))
        );

        BackgroundFill backgroundFill = new BackgroundFill(radialGradient, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY);
        Background background = new Background(backgroundFill);

        Label attention = new Label("Attention!");
        attention.setId("attention");
        attention.setAlignment(Pos.CENTER);

        AnchorPane.setTopAnchor(attention, 65.0);
        AnchorPane.setRightAnchor(attention, 0.0);
        AnchorPane.setLeftAnchor(attention, 0.0);

        Text explanation = new Text("You are bout to delete all user data. The next time this application is launched, you will be prompted to enter a new project link. This action is irreversible. Do you wish to continue?");
        explanation.setId("explanation");
        explanation.setWrappingWidth(630-75-75);
        explanation.setTextAlignment(TextAlignment.CENTER);
        explanation.setFill(Color.valueOf("#dadada"));
        explanation.setFont(Font.font("Arial", 20));

        AnchorPane.setTopAnchor(explanation, 120.0);
        AnchorPane.setLeftAnchor(explanation, 75.0);
        AnchorPane.setRightAnchor(explanation, 75.0);

        HBox midcont = new HBox();
        Image im = new Image("icons/warning_FILL0_wght400_GRAD0_opsz24.png");
        ImageView iv = new ImageView(im);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setFitWidth(80);

        midcont.setAlignment(Pos.CENTER);
        midcont.setSpacing(20);

        Label cancelb = new Label("Cancel");
        cancelb.setAlignment(Pos.CENTER);
        cancelb.setId("cancelb");
        cancelb.setMinWidth(150);

        Label continueb = new Label("Continue");
        continueb.setAlignment(Pos.CENTER);
        continueb.setId("continueb");
        continueb.setMinWidth(150);

        midcont.getChildren().addAll(cancelb, iv, continueb);

        AnchorPane.setTopAnchor(midcont, ((double)(400/2)-40+50));
        AnchorPane.setLeftAnchor(midcont, 50.0);
        AnchorPane.setRightAnchor(midcont, 50.0);

        ap.getChildren().addAll(attention, explanation, midcont);

        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        ap.setBackground(background);
    }
}
