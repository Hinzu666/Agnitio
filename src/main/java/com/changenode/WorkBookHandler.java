package com.changenode;

import javafx.scene.chart.XYChart;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class WorkBookHandler {
    private final String PATH;
    private Sheet sheet;
    private FileInputStream fis;
    public DataPackage dataPackage;
    WorkBookHandler (String PATH) {
        this.PATH = PATH;
        dataPackage = new DataPackage();
    }
    //series.getData().add(new XYChart.Data<>(convertExcelDateToString(x.get(n)), y.get(n)));

    private ArrayList<String> xAxisData = new ArrayList<>();

    private void populate() {
        Iterator<Row> rowIterator = sheet.rowIterator();

        int rowN = 0;
        while (rowIterator.hasNext()) {
            if (rowN == 1) { //TODO: add more checks that correct row
                handleRowX(rowIterator.next());
            } else if (rowN > 1) {
                handleRowData(rowIterator.next());
            } else {
                rowIterator.next();
            }


            rowN++;
        }
    }
    private void handleRowX(Row row) {
        Iterator<Cell> iterator = row.cellIterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            CellType ct = cell.getCellType();

            if (ct == CellType.NUMERIC) {
                xAxisData.add(String.valueOf(Math.round(cell.getNumericCellValue())));
            }
        }
    }
    private void handleRowData(Row row) {

        Iterator<Cell> iterator = row.cellIterator();
        XYChart.Series series = new XYChart.Series();
        int n = 0;
        boolean nostop = true;
        while (iterator.hasNext() && nostop) {
            Cell cell = iterator.next();
            CellType ct = cell.getCellType();
            if (ct == CellType.STRING) {
                series.setName(cell.getStringCellValue());
            } else if (ct == CellType.BLANK || ct == CellType._NONE) {
                nostop = false;

            } else if (ct == CellType.NUMERIC) {
                double value = cell.getNumericCellValue();
                series.getData().add(new XYChart.Data<>(xAxisData.get(n), value*100));
                n++;
            }
        }

        if (!series.getData().isEmpty()) {
            dataPackage.addSeries(series);
        }

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
