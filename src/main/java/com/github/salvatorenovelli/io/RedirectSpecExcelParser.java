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

    public RedirectSpecExcelParser(String filename, ParsedSpecificationHandler handler) throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(new FileInputStream(filename));
        this.handler = handler;
    }


    public void parse() throws IOException {
        Sheet sheet = wb.getSheetAt(0);
        StreamSupport.stream(sheet.spliterator(), false)
                .forEach(this::toRedirectSpecification);
    }

    private void toRedirectSpecification(Row row) {
        try {
            String col1 = row.getCell(0).getStringCellValue();
            String col2 = row.getCell(1).getStringCellValue();
            int expectedStatusCode = getExpectedStatusCode(row);
            handler.handleValidSpec(new RedirectSpecification(col1, col2, expectedStatusCode));
        } catch (Exception e) {
            logger.error("Unable to parse specification in row {} because:  {}", row.getRowNum(), e.toString());
            handler.handleInvalidSpec(new InvalidRedirectSpecification(row.getRowNum(),e.toString()));
        }
    }

    private int getExpectedStatusCode(Row row) {
        Cell cell = row.getCell(2);

        if (cell == null || cell.getStringCellValue() == null) {
            return DEFAULT_STATUS_CODE;
        }
        return Integer.parseInt(cell.getStringCellValue());
    }

    private boolean isValid(RedirectSpecification redirectSpecification) {
        return redirectSpecification != null;
    }

    @Override
    public void parse(ParsedSpecificationHandler handler) throws IOException {

    }
}
