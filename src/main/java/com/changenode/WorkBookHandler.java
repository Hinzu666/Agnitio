package com.changenode;

import javafx.scene.chart.XYChart;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class WorkBookHandler {
    private final String PATH;
    private Sheet sheet;
    private FileInputStream fis;
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
        fis = new FileInputStream(PATH);
        Workbook wb = new XSSFWorkbook(fis);
        fis.close();
        return wb;
    }
    public DataPackage extract() throws IOException, NotOfficeXmlFileException {
        Workbook book = getObject();

        sheet = book.getSheetAt(0);
        FormulaEvaluator formulaEvaluator = book.getCreationHelper().createFormulaEvaluator();

        int rowCount = sheet.getPhysicalNumberOfRows();

        Double[] xAxisArray = new Double[rowCount];
        Double[] yAxisArray = new Double[rowCount];

        populate();

        //TODO: magic
        return null;
    }
}
