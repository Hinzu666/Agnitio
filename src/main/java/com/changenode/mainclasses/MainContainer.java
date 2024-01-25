package com.changenode.mainclasses;

import com.changenode.*;
import com.changenode.interfaces.*;
import com.github.javafaker.Cat;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.poi.ss.formula.functions.T;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/*TODO:
   resize,
   fix autoupdate "feature"; thread update frequency change, load first data
 */
public class MainContainer extends Application {
    private static DataPackage dataPackage;
    private static LogHandler logHandler;
    public static void main(String[] args, DataPackage data) {
        System.setProperty("prism.lcdtext", "false");
        stage = new Stage();
        dataPackage = data;
        logHandler = new LogHandler(new Label());
        dataHandler = new DataHandler(logHandler);

        buildContainer(stage);
        //refresh(); TODO: refresh
    }

    @Override
    public void start(Stage stage) {}
    private static final boolean[] autoUpdate = {true};
    private static long refreshRate = 1800000;
    private static DataHandler dataHandler;
    private static double preferredWindowWidth = 1500.d;
    private static double preferredWindowHeight = 900.d;
    private static Label statusLabelAU;
    private static boolean drawerOpen = false;
    private static final double[] originalPosition = new double[2];
    private static ArrayList<DatasetWidget> datasetWidgets = new ArrayList<>();
    private static Label statusLabel;
    public static DropShadow dropShadow;
    private static RangeSelectorWidget rangeSelectorWidget;
    private static LineChart<String,Number> lineChart;
    private static void buildContainer(Stage stage) {
        getPreferences();

        dataHandler.setErrorInterface(new ErrorInterface() {
            @Override
            public void onCatch(Exception exception, int process) {
                //TODO: catch
            }
        });

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
        imcont.setOnMouseClicked(event -> getDataAndRebuild());

        AnchorPane.setBottomAnchor(containerRB, 15.0);
        AnchorPane.setRightAnchor(containerRB, 15.0);

        //XYChart.Series[] chart = startChart();

        RangeSelectorWidget rsw = new RangeSelectorWidget(300, new RangeSelectorInterface() {
            @Override
            public void onRangeChangeRequested(int from, int to) { // Returns index
                rerangeAndRebuild(from, to);
            }
        }, new String[]{"one","two","three"});
        rsw.setEffect(dropShadow);
        rangeSelectorWidget = rsw;

        containerRB.getChildren().addAll(rsw, imcont, autoUpdateContainer);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setId("xaxis");
        xAxis.setLabel("Week #");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("(%)");

        lineChart =
                new LineChart<>(xAxis, yAxis);

        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(false);
        lineChart.setId("chart");

        lineChart.getXAxis().autoRangingProperty().addListener((observable, oldValue, newValue) -> {
            logHandler.log("X-axis loaded");
        });

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

        statusLabel = new Label();
        statusLabel.setId("statusLabel");

        logHandler.setTarget(statusLabel);

        AnchorPane.setLeftAnchor(statusLabel, 15.0);
        AnchorPane.setBottomAnchor(statusLabel, 15.0);

        pane.getChildren().addAll(moveRegion, cornerUL, ivLogo, containerRB, lineChart, statusLabel);
        setOnClose();
        stage.show();

        /* -----------------------------------------------*/

        buildChart(dataPackage);
        updateRangeWidget();
        startAutoUpdateThread();

        /* -----------------------------------------------*/
    }
    private static Stage stage;
    private static String[] colors = {"#757ED4", "#75AED4", "#75D49C", "#D475AD", "#D4B075", "#D47575", "#8c7ed4 "};

    private static void getDataAndRebuild() {
        logHandler.log("~getDataAndRebuild()");
        dataHandler.setThreadInterface(() -> {
            dataPackage = dataHandler.getPackage();
            buildChart(dataHandler.getPackage());
            updateRangeWidget();
        });
        dataHandler.load(new Label());
    }
    private static void updateRangeWidget() {
        ArrayList<XYChart.Series> list = dataPackage.getData();
        if (list.size() < 2) {
            return;
        }
        XYChart.Series ser = list.get(1);
        String[] cats = new String[ser.getData().size()];
        int n = 0;
        for (Object data : ser.getData()) {
            XYChart.Data d = (XYChart.Data) data;
            cats[n] = String.valueOf(d.getXValue());
            n++;
        }

        rangeSelectorWidget.setCategories(cats);
    }
    private static void rerangeAndRebuild(int from, int to) {
        logHandler.log("~rerangeAndRebuild()");
        dataPackage.setRange(from, to);
        buildChart(dataPackage);
    }
    private static void buildChart(DataPackage dataPackage) {
        logHandler.log("~buildChart()");
        for (DatasetWidget dw : datasetWidgets) {
            Platform.runLater(() -> {
                datasetWidgetContainer.getChildren().remove(dw.getNode());
            });
        }
        datasetWidgets.clear();
        Platform.runLater(() -> {
            lineChart.getData().clear();
        });

        int colorCursor = 0;

        for (ArrayList<XYChart.Series> pair : dataPackage.getDataPairs()) {
            handleDataPair(pair, colors[colorCursor%colors.length]);
            colorCursor++;
        }

        logHandler.log("Data updated @ "+LogHandler.sdf_short.format(new Date(System.currentTimeMillis()).getTime()));
    }
    private static void handleDataPair(ArrayList<XYChart.Series> pair, String identifierColor) {
        if (pair.size() != 2) {
            logHandler.log("Discarding data-pair - invalid size: "+pair.size());
            return;
        }
        XYChart.Series real = pair.get(0);
        XYChart.Series model = pair.get(1);
        logHandler.log("Data-pair: "+model.getName() + " & "+real.getName());

        Platform.runLater(() -> {
            lineChart.getData().addAll(model, real);

            DatasetWidget dw = new DatasetWidget(identifierColor, real.getName());
            dw.setListener(new DatasetWidgetListener() {
                @Override
                public void onHide() {
                    model.getNode().setVisible(false);
                    real.getNode().setVisible(false);
                }

                @Override
                public void onShow(boolean master) {
                    if (!master) {
                        model.getNode().setVisible(true);
                        real.getNode().setVisible(true);
                    } else {
                        hideAllBut(dw);
                    }
                }
            });
            datasetWidgetContainer.getChildren().add(dw.getNode());
            datasetWidgets.add(dw);

            model.getNode().setStyle("-fx-stroke: "+identifierColor+";" +
                    "-fx-stroke-dash-array: 5 10;");
            real.getNode().setStyle("-fx-stroke: "+identifierColor+";");
        });
    }
    private static void hideAllBut(DatasetWidget dw) {
        for (DatasetWidget widget : datasetWidgets) {
            if (!widget.equals(dw)) {
                Platform.runLater(widget::hide);
            } else {
                Platform.runLater(widget::show);
            }
        }
    }
    private static void setOnClose() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                fh.saveDimensionsToJSON(stage.getWidth(), stage.getHeight());
                fh.saveToJSON("autoRefreshRate", refreshRate);
            } catch (IOException | ParseException e) {
                ErrorHandler.handle(e, ErrorHandler.Severity.MODERATE);
            }
        }));
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
    private static HBox datasetWidgetContainer;
    private static HBox buildCornerUL() {
        HBox container = new HBox();
        datasetWidgetContainer = container;

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

        container.getChildren().addAll(gp);
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
                boolean result = fh.deleteEverything(); //TODO: make sure xlsx is closed first
                LogHandler logger = new LogHandler(statusLabel);
                logger.log("File deletion status: "+result, true);
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
                refreshRate = (long) val * 60 * 1000;
                //refresh(); TODO: refresh
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
            Object result = fh.getFromJSON("autoRefreshRate");
            if (result != null) {
                refreshRate = (long) result;
            }
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
                        Platform.runLater(() -> {
                            //TODO: refresh
                        });
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handle(e, ErrorHandler.Severity.HIGH);
            }
        });
        t.start();
    }

}
