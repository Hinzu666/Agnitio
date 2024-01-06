package com.changenode.mainclasses;

import com.changenode.DataPackage;
import com.changenode.FileHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.formula.functions.T;
import org.json.simple.parser.ParseException;

import java.io.IOException;
//TODO: resize
public class MainContainer extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    /*MainContainer(String[] args) {
        launch(args);
    }
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("prism.lcdtext", "false");
        buildContainer(stage);
    }
    private final boolean[] autoUpdate = {true};
    private final long refreshRate = 1800000;
    private double preferredWindowWidth = 1500.d, preferredWindowHeight = 900.d;
    private void buildContainer(Stage stage) {
        getPreferences();
        AnchorPane pane = new AnchorPane();
        pane.setId("pane");
        Scene scene = new Scene(pane, preferredWindowWidth, preferredWindowHeight);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        scene.getStylesheets().add(MainContainer.class.getResource("/main_style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        HBox cornerUL = startCornerUL();
        cornerUL.setAlignment(Pos.CENTER_LEFT);
        cornerUL.setSpacing(12);
        AnchorPane.setTopAnchor(cornerUL, 15.0);
        AnchorPane.setLeftAnchor(cornerUL, 15.0);

        Image im = new Image("graphics/Nokia_2023.png");
        ImageView ivLogo = new ImageView(im);
        ivLogo.setPreserveRatio(true);
        ivLogo.setFitHeight(19);

        AnchorPane.setTopAnchor(ivLogo, 15.0);
        AnchorPane.setRightAnchor(ivLogo, 15.0);

        HBox containerRB = new HBox();
        containerRB.setAlignment(Pos.CENTER);
        containerRB.setSpacing(12);

        HBox autoUpdateContainer = new HBox();
        autoUpdateContainer.setSpacing(8);
        autoUpdateContainer.setAlignment(Pos.CENTER);
        autoUpdateContainer.setId("aucontainer");
        Label staticLabelAU = new Label("Auto update: ");
        staticLabelAU.setId("autoupdatelab");

        Label statusLabelAU = new Label("ON");
        statusLabelAU.setId("statusLabelAU_ON");

        autoUpdateContainer.getChildren().addAll(staticLabelAU, statusLabelAU);
        autoUpdateContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleAUClick(statusLabelAU);
            }
        });

        HBox imcont = new HBox();
        imcont.setId("refreshicon");
        imcont.setAlignment(Pos.CENTER);

        Image reloadImg = new Image("icons/sync_FILL0_wght400_GRAD0_opsz24.png");
        ImageView reloadImgV = new ImageView(reloadImg);
        reloadImgV.setSmooth(true);
        reloadImgV.setPreserveRatio(true);
        reloadImgV.setFitWidth(20);
        reloadImgV.setId("refreshicon");
        imcont.getChildren().addAll(reloadImgV);
        imcont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refresh();
            }
        });

        containerRB.getChildren().addAll(imcont, autoUpdateContainer);
        AnchorPane.setBottomAnchor(containerRB, 15.0);
        AnchorPane.setRightAnchor(containerRB, 15.0);

        //XYChart.Series[] chart = startChart();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setId("chart");

        AnchorPane.setTopAnchor(lineChart, 59.0);
        AnchorPane.setBottomAnchor(lineChart,50.0);
        AnchorPane.setLeftAnchor(lineChart, 15.0);
        AnchorPane.setRightAnchor(lineChart, 15.0);

        pane.getChildren().addAll(cornerUL, ivLogo, containerRB, lineChart);
        stage.show();

        startAutoUpdateThread();
    }
    private DataPackage data;
    private void refresh() {

    }
    private XYChart.Series[] startChart() {
        XYChart.Series[] ret = new XYChart.Series[data.size];
        for (int n = 0; n < data.size; n++) {

        }
        return ret;
    }
    private void handleAUClick(Label label) {
        if (autoUpdate[0]) {
            autoUpdate[0] = false;
            label.setText("OFF");
            label.setId("statusLabelAU_OFF");
        } else {
            autoUpdate[0] = true;
            label.setText("ON");
            label.setId("statusLabelAU_ON");
        }
    }
    private HBox startCornerUL() {
        HBox container = new HBox();

        //four boxes
        int s = 7;
        GridPane gp = new GridPane();
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col <= 1; col++) {
                Label label = new Label("");
                label.setId("whitebox");

                label.setMinSize(s, s);
                label.setMaxSize(s, s);
                GridPane.setRowIndex(label, row);
                GridPane.setColumnIndex(label, col);
                gp.getChildren().add(label);
            }
        }
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setMaxHeight(s*2 + 5);
        gp.setPadding(new Insets(0, 20, 0, 0));

        //TODO: handle button press gp

        DatasetWidget dw = new DatasetWidget("#75D49C", "Dataset 1"); //TODO: actually generate right amount
        DatasetWidget dw1 = new DatasetWidget("#757ED4", "Dataset 2");

        container.getChildren().addAll(gp, dw.getNode(), dw1.getNode());
        return container;
    }

    private void getPreferences() {
        try {
            FileHandler fh = new FileHandler("src/main/resources/data/userdata.JSON");
            double[] dimensions = fh.getDimensionsFromJSON();
            preferredWindowWidth = dimensions[0];
            preferredWindowHeight = dimensions[1];
        } catch (IOException ioe) {
            ioe.printStackTrace(); //TODO: handle
        } catch (ParseException pe) {
            pe.printStackTrace(); //TODO: handle
        }
    }

    private void startAutoUpdateThread() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(refreshRate);
                    if (autoUpdate[0]) {
                        refresh();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

}
//TODO: save preferences on exit