package org.sakaiproject.assignment.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.sakaiproject.util.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Abstracts away writing to a CSV or Excel file.
 */
public abstract class SpreadsheetExporter {

    enum Type {CSV, EXCEL};

    /**
     * Factory method for getting a spreadsheet exporter.
     * @param type The type of spreadsheet.
     * @param title The title of the spreadsheet.
     * @param gradeType The grade type being exported.
     * @return A new SpreadsheetExporter that can be used.
     */
    public static SpreadsheetExporter getInstance(Type type, String title, String gradeType) {
        if (Type.CSV.equals(type)) {
            return new CsvExporter(title, gradeType);
        } else if (Type.EXCEL.equals(type)) {
            return new ExcelExporter(title, gradeType);
        } else {
            throw new IllegalArgumentException("Unsupported type: "+ type);
        }
    }

    /**
     * Add a header row to the spreadsheet.
     * @param values The values of the cells.
     * @return The current spreadsheet exporter to allow chaining of method calls.
     */
    public abstract SpreadsheetExporter addHeader(String... values);

    /**
     * Add a normal row to the spreadsheet.
     * @param values The values of the cells.
     * @return The current spreadsheet exporter to allow chaining of method calls.
     */
    public abstract SpreadsheetExporter addRow(String... values);

    /**
     * Writes the spreadsheet contents to a stream. We don't close the stream after writing.
     * @param outputStream The output stream to write the content to.
     * @throws IOException If there is a problem writing the stream.
     */
    public abstract void write(OutputStream outputStream) throws IOException;

    /**
     * The standard file extension for this type of spreadsheet.
     * @return A file extension (eg. csv).
     */
    public abstract String getFileExtension();

}

class CsvExporter extends SpreadsheetExporter {

    private final ByteArrayOutputStream gradesBAOS;
    private final CSVWriter gradesBuffer;

    CsvExporter(String title, String gradeType) {
        gradesBAOS = new ByteArrayOutputStream();
        gradesBuffer = new CSVWriter(new OutputStreamWriter(gradesBAOS));
        addRow(title, gradeType);
        addRow("");
    }


    @Override
    public SpreadsheetExporter addHeader(String... values) {
        gradesBuffer.writeNext(values);
        return this;
    }

    @Override
    public SpreadsheetExporter addRow(String... values) {
        gradesBuffer.writeNext(values);
        return this;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        gradesBuffer.close();
        outputStream.write(gradesBAOS.toByteArray());
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }
}

class ExcelExporter extends SpreadsheetExporter {

    private final HSSFWorkbook gradesWorkbook;
    private final HSSFSheet dataSheet;
    private int rowCount = 0;

    ExcelExporter(String title, String gradeType) {
        gradesWorkbook = new HSSFWorkbook();
        dataSheet = gradesWorkbook.createSheet(Validator.escapeZipEntry(title));
    }

    private HSSFCellStyle createHeaderStyle(){
        //TO-DO read style information from sakai.properties
        HSSFFont font = gradesWorkbook.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(IndexedColors.PLUM.getIndex());
        font.setBold(true);
        HSSFCellStyle cellStyle = gradesWorkbook.createCellStyle();
        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public SpreadsheetExporter addHeader(String... values) {
        HSSFCellStyle cellStyle = createHeaderStyle();
        HSSFRow headerRow = dataSheet.createRow(rowCount++);
        for (int i = 0; i < values.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(values[i]));
        }
        return this;
    }

    @Override
    public SpreadsheetExporter addRow(String... values) {
        HSSFRow dataRow = dataSheet.createRow(rowCount++);
        for (int i = 0; i < values.length; i++) {
            HSSFCell cell = dataRow.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(values[i]);
        }
        return this;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        gradesWorkbook.write(outputStream);
    }

    @Override
    public String getFileExtension() {
        return "xls";
    }
}

