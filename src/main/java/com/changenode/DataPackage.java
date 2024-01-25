package com.changenode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class DataPackage {
    private ArrayList<XYChart.Series> data = new ArrayList<>();
    private ArrayList<ArrayList<XYChart.Series>> dataPairs = new ArrayList<>();
    private int dataPairCursorLen = 0;
    DataPackage () {}

    public void setRange(int from, int to) {

        rebuildPairListAsClone();

        for (ArrayList<XYChart.Series> pair : dataPairs) {
            resizeDataPair(pair, from, to);
        }

    }
    private void resizeDataPair(ArrayList<XYChart.Series> pair, int from, int to) {
        for (XYChart.Series ser : pair) {
            ObservableList<XYChart.Data> seriesData = ser.getData();
            resizeObservableList(seriesData, from, to);
        }
    }
    private void resizeObservableList(ObservableList<XYChart.Data> seriesData, int from, int to) {
        int size = seriesData.size();
        if (size == 0) {
            return;
        }
        if (to == from) {
            return;
        }
        if (to < size) {
            seriesData.subList(to+1, size).clear();
        }
        if (from > 0 && from < size) {
            seriesData.subList(0, from).clear();
        }
        if (from >= size) {
            seriesData.clear();
        }
    }

    public void addSeries(XYChart.Series ser) {
        addDataToPair(ser);
        data.add(ser);
    }
    private void rebuildPairListAsClone() {
        dataPairs.clear();
        dataPairCursorLen = 0;

        for (XYChart.Series ser : data) {
            XYChart.Series clone = new XYChart.Series<>(FXCollections.observableArrayList(ser.getData()));
            clone.setName(ser.getName());
            addDataToPair(clone);
        }
    }
    private void addDataToPair(XYChart.Series ser) {
        int dataPairLen = dataPairs.size();

        if (dataPairCursorLen == 0) {
            ArrayList<XYChart.Series> cursor = new ArrayList<>();
            cursor.add(ser);
            dataPairs.add(cursor);
            dataPairCursorLen++;
        } else {
            dataPairs.get(dataPairLen-1).add(ser);
            dataPairCursorLen = 0;
        }
    }
    public ArrayList<XYChart.Series> getData() {
        return data;
    }

    public ArrayList<ArrayList<XYChart.Series>> getDataPairs() {
        return dataPairs;
    }

}
