package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RedirectSpecExcelParser implements RedirectSpecificationParser {

    public static final int DEFAULT_STATUS_CODE = 200;
    private final Workbook wb;

    public RedirectSpecExcelParser(String filename) throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(new FileInputStream(filename));
    }


    @Override
    public List<RedirectSpecification> parse() throws IOException {
        Sheet sheet = wb.getSheetAt(0);
        return StreamSupport.stream(sheet.spliterator(), false)
                .map(this::toRedirectSpecification)
                .collect(Collectors.toList());
    }

    private RedirectSpecification toRedirectSpecification(Row row) {
        String col1 = row.getCell(0).getStringCellValue();
        String col2 = row.getCell(1).getStringCellValue();

        int expectedStatusCode = getExpectedStatusCode(row);

        return new RedirectSpecification(col1, col2, expectedStatusCode);
    }

    private int getExpectedStatusCode(Row row) {
        Cell cell = row.getCell(2);

        if (cell == null || cell.getStringCellValue() == null) {
            return DEFAULT_STATUS_CODE;
        }
        return Integer.parseInt(cell.getStringCellValue());
    }


}
