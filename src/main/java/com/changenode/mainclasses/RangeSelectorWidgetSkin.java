package com.changenode.mainclasses;


import com.changenode.interfaces.RangeSelectorInterface;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class RangeSelectorWidgetSkin extends SkinBase<RangeSelectorWidget> {
    double[] args;
    private double left = 0;
    private int leftValue = 0;
    private double right = 0;
    private int rightValue = 0;
    private RangeSelectorInterface listener;
    private String[] cats;
    public RangeSelectorWidgetSkin(RangeSelectorWidget control, double[] args, RangeSelectorInterface listener, String[] cats) {
        super(control);
        this.args = args;
        this.cats = cats;
        this.listener = listener;
        initialize();
    }

    public void setCategories(String[] cats) {
        this.cats = cats;
        recalc();
    }
    private double sliderAreaHeight, sliderAreaWidth, spacing;
    private Label leftLabel, rightLabel;
    private void recalc() {
        spacing = (sliderAreaWidth - 10) / (cats.length-1);

        rightValue = (int) Math.round(right/spacing);

        if (leftLabel != null) {
            Platform.runLater(() -> {
                leftLabel.setText(cats[leftValue]);
            });
        }
        if (rightLabel != null) {
            Platform.runLater(() -> {
                rightLabel.setText(cats[rightValue]);
            });
        }
    }
    private void initialize() {
        sliderAreaHeight = 24;
        sliderAreaWidth = 250;

        spacing = sliderAreaWidth / (cats.length-1);

        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(0,12,0,12));
        root.setSpacing(12);
        root.setStyle("-fx-background-color: #303137;" +
                "-fx-background-radius: 2.5px;");

        AnchorPane sliderArea = new AnchorPane();
        sliderArea.setMinSize(sliderAreaWidth, sliderAreaHeight);
        sliderArea.setMaxSize(sliderAreaWidth, sliderAreaHeight);

        leftLabel = new Label("43");
        leftLabel.setStyle("-fx-text-fill: #dadada;" +
                "    -fx-font-family: \"Arial Narrow\";" +
                "    -fx-font-smoothing-type: gray;" +
                "    -fx-font-size: 14px;");

        rightLabel = new Label("25");
        rightLabel.setStyle("-fx-text-fill: #dadada;" +
                "    -fx-font-family: \"Arial Narrow\";" +
                "    -fx-font-smoothing-type: gray;" +
                "    -fx-font-size: 14px;");

        Rectangle rangeRectangle = new Rectangle(20,3,Color.valueOf("#dadada"));

        AnchorPane.setTopAnchor(rangeRectangle, (sliderAreaHeight-3)/2);
        AnchorPane.setBottomAnchor(rangeRectangle, (sliderAreaHeight-3)/2);
        AnchorPane.setLeftAnchor(rangeRectangle, 0d);
        AnchorPane.setRightAnchor(rangeRectangle, 0d);

        Rectangle sliderLeft = createThumb(sliderAreaHeight);
        AnchorPane.setLeftAnchor(sliderLeft, 0d);

        sliderLeft.setOnMouseDragged(event -> {
            double start = sliderArea.localToScene(0, 0).getX();
            double max = sliderAreaWidth - sliderLeft.getWidth();
            double val = event.getSceneX()-start;
            if (val < 0) {
                val = 0;
            } else if (val > max) {
                val = max;
            }
            if (val > right) {
                val = right;
            }
            left = val;
            AnchorPane.setLeftAnchor(sliderLeft, val);
            AnchorPane.setLeftAnchor(rangeRectangle, val);
            rangeRectangle.setWidth(right - left);

            leftValue = (int) Math.round(left/spacing);
            leftLabel.setText(cats[leftValue]);
        });

        Rectangle sliderRight = createThumb(sliderAreaHeight);

        sliderRight.setOnMouseDragged(event -> {
            double start = sliderArea.localToScene(0, 0).getX();
            double max = sliderAreaWidth - sliderRight.getWidth();
            double val = event.getSceneX()-start;
            if (val < 0) {
                val = 0;
            } else if (val > max) {
                val = max;
            }
            if (val < left) {
                val = left;
            }
            right = val;
            AnchorPane.setLeftAnchor(sliderRight, val);
            rangeRectangle.setWidth(right - left);

            rightValue = (int) Math.round(right/spacing);
            rightLabel.setText(cats[rightValue]);
        });

        sliderRight.setOnMouseReleased(event -> {
            listener.onRangeChangeRequested(leftValue, rightValue);
        });
        sliderLeft.setOnMouseReleased(event -> {
            listener.onRangeChangeRequested(leftValue, rightValue);
        });

        right = sliderAreaWidth-sliderRight.getWidth();
        rangeRectangle.setWidth(right - left);

        leftValue = (int) Math.round(left/spacing);
        leftLabel.setText(cats[leftValue]);
        rightValue = (int) Math.round(right/spacing);
        rightLabel.setText(cats[rightValue]);

        AnchorPane.setLeftAnchor(sliderRight, sliderAreaWidth-sliderRight.getWidth()-1);

        Rectangle sliderRail = new Rectangle(sliderAreaWidth, 3, Color.valueOf("#dadada4d"));
        AnchorPane.setBottomAnchor(sliderRail, (sliderAreaHeight-5)/2);
        AnchorPane.setTopAnchor(sliderRail, (sliderAreaHeight-5)/2);
        AnchorPane.setRightAnchor(sliderRail, 0d);
        AnchorPane.setLeftAnchor(sliderRail, 0d);

        sliderArea.getChildren().addAll(sliderRail, rangeRectangle, sliderLeft, sliderRight);

        root.getChildren().addAll(leftLabel, sliderArea, rightLabel);

        getChildren().add(root);

    }
    private Rectangle createThumb(double sliderAreaHeight) {
        Rectangle thumb = new Rectangle(10,10, Color.valueOf("#75D49C"));
        thumb.setCursor(Cursor.H_RESIZE);
        thumb.setArcHeight(4);
        thumb.setArcWidth(4);
        thumb.setStrokeWidth(2);
        thumb.setStrokeType(StrokeType.INSIDE);
        thumb.setOnMouseEntered(event -> {
            thumb.setStroke(Paint.valueOf("#dadada"));
        });
        thumb.setOnMouseExited(event -> {
            thumb.setStroke(Paint.valueOf("#00000000"));
        });
        AnchorPane.setTopAnchor(thumb, (sliderAreaHeight-thumb.getHeight())/2);
        AnchorPane.setBottomAnchor(thumb, (sliderAreaHeight-thumb.getHeight())/2);
        return thumb;
    }
}