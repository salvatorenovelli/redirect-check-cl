package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RedirectSpecExcelParser implements RedirectSpecificationParser {

    public static final int DEFAULT_STATUS_CODE = 200;
    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecExcelParser.class);
    private final Workbook wb;

    public RedirectSpecExcelParser(String filename) throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(new FileInputStream(filename));
    }


    @Override
    public List<RedirectSpecification> parse() throws IOException {
        Sheet sheet = wb.getSheetAt(0);
        return StreamSupport.stream(sheet.spliterator(), false)
                .map(this::toRedirectSpecification)
                .filter(this::isValid)
                .collect(Collectors.toList());
    }

    private RedirectSpecification toRedirectSpecification(Row row) {
        try {
            String col1 = row.getCell(0).getStringCellValue();
            String col2 = row.getCell(1).getStringCellValue();
            int expectedStatusCode = getExpectedStatusCode(row);
            return new RedirectSpecification(col1, col2, expectedStatusCode);
        } catch (Exception e) {
            logger.error("Unable to parse specification in row {} because:  {}", row.getRowNum(), e.toString());
        }
        return null;
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

}
