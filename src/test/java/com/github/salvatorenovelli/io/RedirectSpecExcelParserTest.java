package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class RedirectSpecExcelParserTest {


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void shouldAcceptOptionalExpectedStatusCode() throws Exception {

        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1", "1234")
                .get();

        List<RedirectSpecification> parsedSpecs = new RedirectSpecExcelParser(filename).parse();

        assertThat(parsedSpecs, hasSize(1));
        assertThat(parsedSpecs.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(parsedSpecs.get(0).getExpectedDestination(), is("ExpectedDestination1"));
        assertThat(parsedSpecs.get(0).getExpectedStatusCode(), is(1234));

    }

    @Test
    public void specificationWithoutExpectedDestinationShouldBeSkipped() throws Exception {
        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1", "1234")
                .withRow("sourceURI")
                .get();

        List<RedirectSpecification> parsedSpecs = new RedirectSpecExcelParser(filename).parse();

        assertThat(parsedSpecs, hasSize(1));
    }

    @Test
    public void whenNoStatusCodeIsSpecifiedShouldDefaultTo200() throws Exception {

        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1")
                .get();

        List<RedirectSpecification> parsedSpecs = new RedirectSpecExcelParser(filename).parse();

        assertThat(parsedSpecs, hasSize(1));
        assertThat(parsedSpecs.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(parsedSpecs.get(0).getExpectedDestination(), is("ExpectedDestination1"));
        assertThat(parsedSpecs.get(0).getExpectedStatusCode(), is(200));

    }

    @Test
    public void shouldBeAbleToParseXls() throws Exception {

        RedirectSpecExcelParser sut = new RedirectSpecExcelParser(getExcelTestFile("2_rows_2_column.xls"));
        List<RedirectSpecification> parse = sut.parse();

        assertThat(parse, hasSize(2));
        assertThat(parse.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(parse.get(0).getExpectedDestination(), is("ExpectedDestination1"));

        assertThat(parse.get(1).getSourceURI(), is("SourceURI2"));
        assertThat(parse.get(1).getExpectedDestination(), is("ExpectedDestination2"));
    }

    @Test
    public void shouldBeAbleToParseXlsx() throws Exception {

        RedirectSpecExcelParser sut = new RedirectSpecExcelParser(getExcelTestFile("2_rows_2_column.xlsx"));
        List<RedirectSpecification> parse = sut.parse();

        assertThat(parse, hasSize(2));
        assertThat(parse.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(parse.get(0).getExpectedDestination(), is("ExpectedDestination1"));

        assertThat(parse.get(1).getSourceURI(), is("SourceURI2"));
        assertThat(parse.get(1).getExpectedDestination(), is("ExpectedDestination2"));
    }


    private String getExcelTestFile(String name) {
        return RedirectSpecification.class.getResource("/excel/" + name).getFile();
    }

    private ExcelTestFileBuilder givenAnExcelFile() {
        return new ExcelTestFileBuilder();
    }


    class ExcelTestFileBuilder {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Test sheet");
        private int curRowNumber = 0;

        public ExcelTestFileBuilder withRow(String... values) {
            HSSFRow row = sheet.createRow(curRowNumber++);
            int cellNumber = 0;
            for (String value : values) {
                row.createCell(cellNumber++).setCellValue(value);
            }
            return this;
        }

        public String get() throws IOException {
            File file = temporaryFolder.newFile();
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            return file.getPath();
        }
    }
}