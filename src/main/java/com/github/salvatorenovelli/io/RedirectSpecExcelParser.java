package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.InvalidRedirectSpecification;
import com.github.salvatorenovelli.model.RedirectSpecification;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class RedirectSpecExcelParser implements RedirectSpecificationParser {

    public static final int DEFAULT_STATUS_CODE = 200;
    public static final boolean NON_PARALLEL = false;
    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecExcelParser.class);
    private final ParsedSpecificationHandler handler;
    private final Workbook wb;
    private final Sheet sheet;

    public RedirectSpecExcelParser(String filename, ParsedSpecificationHandler handler) throws IOException, InvalidFormatException {
        this.wb = WorkbookFactory.create(new FileInputStream(filename));
        this.sheet = getFirstVisibleSheet();
        this.handler = handler;
    }

    private Sheet getFirstVisibleSheet() {
        final Optional<Sheet> first = StreamSupport.stream(wb.spliterator(), NON_PARALLEL)
                .filter(sheet -> !(wb.isSheetHidden(wb.getSheetIndex(sheet)) || wb.isSheetVeryHidden(wb.getSheetIndex(sheet))))
                .findFirst();
        //A workbook without a visible sheet is impossible to create with Microsoft Excel or via APIs (but I'll leave the check there just in case I'm missing something)
        return first.orElseThrow(() -> new RuntimeException("The workbook looks empty!"));
    }

    @Override
    public int getNumSpecs() {
        return sheet.getPhysicalNumberOfRows();
    }


    public void parse() throws IOException {
        StreamSupport.stream(sheet.spliterator(), NON_PARALLEL)
                .forEach(this::toRedirectSpecification);
    }

    private void toRedirectSpecification(Row row) {
        try {
            String col1 = extractSourceURI(row);
            String col2 = extractExpectedDestination(row);
            int expectedStatusCode = extractExpectedStatusCode(row);
            handler.handleValidSpec(new RedirectSpecification(row.getRowNum() + 1, col1, col2, expectedStatusCode));
        } catch (Exception e) {
            logger.warn("Unable to parse specification in row {} because:  {}", row.getRowNum(), e.toString());
            handler.handleInvalidSpec(new InvalidRedirectSpecification(row.getRowNum() + 1, e.getMessage()));
        }
    }

    private String extractSourceURI(Row row) {
        Cell cell = extractCell(row, 0, "Source URI");
        return cell.getStringCellValue();
    }

    private String extractExpectedDestination(Row row) {
        Cell cell = extractCell(row, 1, "Expected Destination");
        return cell.getStringCellValue();
    }

    private int extractExpectedStatusCode(Row row) {
        Cell cell = row.getCell(2);
        if (cell == null || cell.getStringCellValue() == null) {
            return DEFAULT_STATUS_CODE;
        }
        return Integer.parseInt(cell.getStringCellValue());
    }

    private Cell extractCell(Row row, int i, String cellName) {
        Cell cell = row.getCell(i);
        if (cell == null || cell.getStringCellValue().length() == 0) {
            throw new IllegalArgumentException("'" + cellName + "' parameter is invalid or missing.");
        }
        return cell;
    }


}
