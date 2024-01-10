package com.changenode.mainclasses;

import com.changenode.DataPackage;
import com.changenode.ErrorHandler;
import com.changenode.FileHandler;
import com.changenode.interfaces.DrawerInterface;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.parser.ParseException;

import java.io.IOException;
//TODO: resize, get refresh rate from josn
public class MainContainer extends Application {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        stage = new Stage();
        buildContainer(stage);
    }

    @Override
    public void start(Stage stage) {}
    private static final boolean[] autoUpdate = {true};
    private static long refreshRate = 1800000;
    private static double preferredWindowWidth = 1500.d;
    private static double preferredWindowHeight = 900.d;
    private static Label statusLabelAU;
    private static boolean drawerOpen = false;
    private static final double[] originalPosition = new double[2];
    public static DropShadow dropShadow;
    private static void buildContainer(Stage stage) {
        getPreferences();

        AnchorPane pane = new AnchorPane();

        dropShadow = new DropShadow();
        dropShadow.setColor(Color.valueOf("#00000022"));
        dropShadow.setRadius(2);
        dropShadow.setOffsetX(-1);
        dropShadow.setOffsetY(2);

        pane.setId("pane");
        Scene scene = new Scene(pane, preferredWindowWidth, preferredWindowHeight);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        scene.getStylesheets().add(MainContainer.class.getResource("/main_style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        HBox cornerUL = buildCornerUL();
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
        autoUpdateContainer.setEffect(dropShadow);
        autoUpdateContainer.setSpacing(8);
        autoUpdateContainer.setAlignment(Pos.CENTER);
        autoUpdateContainer.setId("aucontainer");
        Label staticLabelAU = new Label("Auto update: ");
        staticLabelAU.setId("autoupdatelab");

        statusLabelAU = new Label("ON");
        statusLabelAU.setId("statusLabelAU_ON");

        autoUpdateContainer.getChildren().addAll(staticLabelAU, statusLabelAU);
        autoUpdateContainer.setOnMouseClicked(event -> handleAUClick());

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
        imcont.setOnMouseClicked(event -> refresh());

        containerRB.getChildren().addAll(imcont, autoUpdateContainer);
        AnchorPane.setBottomAnchor(containerRB, 15.0);
        AnchorPane.setRightAnchor(containerRB, 15.0);

        //XYChart.Series[] chart = startChart();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis, yAxis);

        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setId("chart");

        AnchorPane.setTopAnchor(lineChart, 59.0);
        AnchorPane.setBottomAnchor(lineChart,50.0);
        AnchorPane.setLeftAnchor(lineChart, 15.0);
        AnchorPane.setRightAnchor(lineChart, 15.0);

        Region moveRegion = new Region();
        moveRegion.setMinHeight(75);
        AnchorPane.setTopAnchor(moveRegion, 0.0);
        AnchorPane.setRightAnchor(moveRegion, 0.0);
        AnchorPane.setLeftAnchor(moveRegion, 0.0);

        moveRegion.setOnMousePressed(event -> {
            originalPosition[0] = event.getSceneX();
            originalPosition[1] = event.getSceneY();
        });
        moveRegion.setOnMouseDragged(event -> Platform.runLater(() -> {
            stage.setX((event.getScreenX() - originalPosition[0]));
            stage.setY((event.getScreenY() - originalPosition[1]));
        }));

        pane.getChildren().addAll(moveRegion, cornerUL, ivLogo, containerRB, lineChart);
        setOnClose();
        stage.show();

        /* -----------------------------------------------*/

        startAutoUpdateThread();

        /* -----------------------------------------------*/
    }
    private DataPackage data;
    private static Stage stage;
    private static void refresh() {
        //TODO: reload data
    }
    private static void setOnClose() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                fh.saveDimensionsToJSON(stage.getWidth(), stage.getHeight());
            } catch (IOException | ParseException e) {
                ErrorHandler.handle(e, ErrorHandler.Severity.MINOR);
            }
        }));
    }
    private XYChart.Series[] startChart() {
        XYChart.Series[] ret = new XYChart.Series[data.size];
        for (int n = 0; n < data.size; n++) {

        }
        return ret;
    }
    private static void handleAUClick() {
        Label label = statusLabelAU;
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
    private static HBox buildCornerUL() {
        HBox container = new HBox();

        //four boxes
        int s = 7;
        GridPane gp = new GridPane();
        Label[] sqrs = new Label[4];
        int n = 0;
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col <= 1; col++) {
                Label label = new Label("");
                label.setId("whitebox");

                label.setEffect(dropShadow);

                sqrs[n] = label;

                label.setMinSize(s, s);
                label.setMaxSize(s, s);
                GridPane.setRowIndex(label, row);
                GridPane.setColumnIndex(label, col);
                gp.getChildren().add(label);
                n++;
            }
        }
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setMaxHeight(s*2 + 5);
        gp.setPadding(new Insets(0, 20, 0, 0));
        gp.setId("drawerbutton");

        gp.setOnMouseClicked(event -> {
            if (drawerOpen) {
                drawerOpen = false;
            } else {
                openDrawer(gp);
                drawerOpen = true;
            }
        });
        gp.setOnMouseEntered(event -> {
            for (Label sq : sqrs) {
                Platform.runLater(() -> sq.setId("whiteboxhover"));
            }
        });
        gp.setOnMouseExited(event -> {
            for (Label sq : sqrs) {
                Platform.runLater(() -> sq.setId("whitebox"));
            }
        });

        DatasetWidget dw = new DatasetWidget("#75D49C", "Dataset 1"); //TODO: actually generate right amount
        DatasetWidget dw1 = new DatasetWidget("#757ED4", "Dataset 2");

        container.getChildren().addAll(gp, dw.getNode(), dw1.getNode());
        return container;
    }
    private static void openDrawer(GridPane gp) {
        DrawerWidget drawerWidget = new DrawerWidget(stage, gp.localToScreen(0, 0).getX(), gp.localToScreen(0, 0).getY(), autoUpdate[0], refreshRate,new DrawerInterface() {
            @Override
            public void onExitRequested() {
                stage.close();
                active = false;
                System.exit(0);
            }

            @Override
            public void onResetAll() {
                fh.deleteEverything();
            }

            @Override
            public void onChangeARState(boolean state) {
                handleAUClick();
            }

            @Override
            public void onDrawerClose() {
                drawerOpen = false;
            }

            @Override
            public void onRefreshRateChange(int val) {
                refreshRate = (long) val * 60 * 1000; //TODO: write to file
                refresh();
            }
        });

    }
    private static FileHandler fh;
    private static void getPreferences() {
        try {
            fh = new FileHandler("src/main/resources/data/userdata.JSON");
            double[] dimensions = fh.getDimensionsFromJSON();
            preferredWindowWidth = dimensions[0];
            preferredWindowHeight = dimensions[1];
        } catch (IOException | ParseException ioe) {
            ErrorHandler.handle(ioe, ErrorHandler.Severity.MINOR);
        }
    }
    private static boolean active = true;
    private static void startAutoUpdateThread() {
        Thread t = new Thread(() -> {
            try {
                while (active) {
                    Thread.sleep(refreshRate);
                    if (autoUpdate[0]) {
                        refresh();
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handle(e, ErrorHandler.Severity.HIGH);
            }
        });
        t.start();
    }

}
