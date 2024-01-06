package com.changenode;

import javafx.scene.chart.XYChart;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public class WorkBookHandler {
    private String PATH;
    private FormulaEvaluator formulaEvaluator;
    private Sheet sheet;
    WorkBookHandler (String PATH) {
        this.PATH = PATH;
    }
    //series.getData().add(new XYChart.Data<>(convertExcelDateToString(x.get(n)), y.get(n)));
    XYChart.Series<Double, Double> chartSeries = new XYChart.Series<>();
    private void populate() {
        Iterator<Row> rowIterator = sheet.rowIterator();

        int rowN = 0;
        while (rowIterator.hasNext()) {
            handleRow(rowIterator.next(), rowN);

            rowN++;
        }
    }
    private void handleRow(Row row, int n) {

        Iterator<Cell> iterator = row.cellIterator();



    }
    private Workbook getObject() throws IOException {
        FileInputStream fis = new FileInputStream(PATH);
        Workbook wb = new XSSFWorkbook(fis);
        return wb;
    }
    private Double[] xAxisArray, yAxisArray;
    public DataPackage extract() throws IOException, NotOfficeXmlFileException {
        Workbook book = getObject();

        sheet = book.getSheetAt(0);
        formulaEvaluator = book.getCreationHelper().createFormulaEvaluator();

        int rowCount = sheet.getPhysicalNumberOfRows();

        xAxisArray = new Double[rowCount];
        yAxisArray = new Double[rowCount];

        populate();

        //TODO: magic
        return null;
    }
}
