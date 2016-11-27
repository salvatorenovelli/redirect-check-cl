package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.InvalidRedirectSpecification;
import com.github.salvatorenovelli.model.RedirectSpecification;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.StreamSupport;

public class RedirectSpecExcelParser implements RedirectSpecificationParser {

    public static final int DEFAULT_STATUS_CODE = 200;
    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecExcelParser.class);
    private final ParsedSpecificationHandler handler;
    private final Workbook wb;
    private final Sheet sheet;

    public RedirectSpecExcelParser(String filename, ParsedSpecificationHandler handler) throws IOException, InvalidFormatException {
        this.wb = WorkbookFactory.create(new FileInputStream(filename));
        this.sheet = wb.getSheetAt(0);
        this.handler = handler;
    }

    @Override
    public int getNumSpecs() {
        return sheet.getPhysicalNumberOfRows();
    }


    public void parse() throws IOException {
        StreamSupport.stream(sheet.spliterator(), false)
                .forEach(this::toRedirectSpecification);
    }

    private void toRedirectSpecification(Row row) {
        try {
            String col1 = extractSourceURI(row);
            String col2 = extractExpectedDestination(row);
            int expectedStatusCode = extractExpectedStatusCode(row);
            handler.handleValidSpec(new RedirectSpecification(col1, col2, expectedStatusCode));
        } catch (Exception e) {
            logger.warn("Unable to parse specification in row {} because:  {}", row.getRowNum(), e.toString());
            handler.handleInvalidSpec(new InvalidRedirectSpecification(row.getRowNum(), e.getMessage()));
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
