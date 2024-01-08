package com.changenode.mainclasses;

import com.changenode.DataPackage;
import com.changenode.DrawerWidget;
import com.changenode.FileHandler;
import com.changenode.interfaces.DatasetWidgetListener;
import com.changenode.interfaces.DrawerInterface;
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
        System.setProperty("prism.lcdtext", "false");
        stage = new Stage();
        buildContainer(stage);
    }

    @Override
    public void start(Stage stage) throws Exception {}
    private static final boolean[] autoUpdate = {true};
    private static final long refreshRate = 1800000;
    private static double preferredWindowWidth = 1500.d;
    private static double preferredWindowHeight = 900.d;
    private static void buildContainer(Stage stage) {
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
        setOnClose();
        stage.show();

        /* -----------------------------------------------*/

        startAutoUpdateThread();

        /* -----------------------------------------------*/
    }
    private DataPackage data;
    private static void refresh() {
        //TODO: this
    }
    private static void setOnClose() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //TODO: save prefs
        }));
    }
    private XYChart.Series[] startChart() {
        XYChart.Series[] ret = new XYChart.Series[data.size];
        for (int n = 0; n < data.size; n++) {

        }
        return ret;
    }
    private static void handleAUClick(Label label) {
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
    private static Stage stage;
    private static HBox startCornerUL() {
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

        gp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openDrawer(gp);
            }
        });
        gp.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Label sq : sqrs) {
                    Platform.runLater(() -> {
                        sq.setId("whiteboxhover");
                    });
                }
            }
        });
        gp.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Label sq : sqrs) {
                    Platform.runLater(() -> {
                        sq.setId("whitebox");
                    });
                }
            }
        });

        DatasetWidget dw = new DatasetWidget("#75D49C", "Dataset 1"); //TODO: actually generate right amount
        DatasetWidget dw1 = new DatasetWidget("#757ED4", "Dataset 2");

        container.getChildren().addAll(gp, dw.getNode(), dw1.getNode());
        return container;
    }

    private static void openDrawer(GridPane gp) {
        DrawerWidget dw = new DrawerWidget(gp.localToScreen(0, 0).getX(), gp.localToScreen(0, 0).getY(), new DrawerInterface() {
            @Override
            public void onExitRequested() {
                stage.close();
                active = false;
                System.exit(0);
            }

            @Override
            public void onResetAll() {

            }
        });

    }

    private static void getPreferences() {
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
                e.printStackTrace();
            }
        });
        t.start();
    }

}
