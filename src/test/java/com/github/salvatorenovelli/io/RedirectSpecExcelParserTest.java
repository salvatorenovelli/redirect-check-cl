package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.InvalidRedirectSpecification;
import com.github.salvatorenovelli.model.RedirectSpecification;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RedirectSpecExcelParserTest {


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    private List<RedirectSpecification> validSpec = new ArrayList<>();
    private List<InvalidRedirectSpecification> invalidSpec = new ArrayList<>();

    private ParsedSpecificationHandler handler = new ParsedSpecificationHandler() {
        @Override
        public void handleValidSpec(RedirectSpecification spec) {
            validSpec.add(validSpec.size(), spec);
        }

        @Override
        public void handleInvalidSpec(InvalidRedirectSpecification spec) {
            invalidSpec.add(invalidSpec.size(), spec);
        }
    };


    @Test
    public void shouldAcceptOptionalExpectedStatusCode() throws Exception {

        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1", "1234")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(validSpec, hasSize(1));
        assertThat(validSpec.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(validSpec.get(0).getExpectedDestination(), is("ExpectedDestination1"));
        assertThat(validSpec.get(0).getExpectedStatusCode(), is(1234));

    }

    @Test
    public void specificationWithoutExpectedDestinationShouldBeConsideredAsInvalid() throws Exception {
        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1", "1234")
                .withRow("sourceURI")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(validSpec, hasSize(1));
        assertThat(invalidSpec, hasSize(1));
    }

    @Test
    public void whenNoStatusCodeIsSpecifiedShouldDefaultTo200() throws Exception {

        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(validSpec, hasSize(1));
        assertThat(validSpec.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(validSpec.get(0).getExpectedDestination(), is("ExpectedDestination1"));
        assertThat(validSpec.get(0).getExpectedStatusCode(), is(200));

    }

    @Test
    public void shouldBeAbleToParseXls() throws Exception {


        String filename = givenAnExcelFile()
                .withRow("SourceURI1", "ExpectedDestination1")
                .withRow("SourceURI2", "ExpectedDestination2")
                .get();

        RedirectSpecExcelParser sut = new RedirectSpecExcelParser(filename, handler);
        sut.parse();

        assertThat(validSpec, hasSize(2));
        assertThat(validSpec.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(validSpec.get(0).getExpectedDestination(), is("ExpectedDestination1"));

        assertThat(validSpec.get(1).getSourceURI(), is("SourceURI2"));
        assertThat(validSpec.get(1).getExpectedDestination(), is("ExpectedDestination2"));
    }

    @Test
    public void shouldBeAbleToParseXlsx() throws Exception {


        String filename = givenAnExcelXFile()
                .withRow("SourceURI1", "ExpectedDestination1")
                .withRow("SourceURI2", "ExpectedDestination2")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(validSpec, hasSize(2));
        assertThat(validSpec.get(0).getSourceURI(), is("SourceURI1"));
        assertThat(validSpec.get(0).getExpectedDestination(), is("ExpectedDestination1"));

        assertThat(validSpec.get(1).getSourceURI(), is("SourceURI2"));
        assertThat(validSpec.get(1).getExpectedDestination(), is("ExpectedDestination2"));
    }

    @Test
    public void parserReturnMeaningfulErrorWhenSourceUriIsMissing() throws Exception {
        String filename = givenAnExcelXFile()
                .withRow("", "ExpectedDestination1")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(invalidSpec, hasSize(1));
        assertThat(invalidSpec.get(0).getErrorMessage(), containsString("'Source URI' parameter is invalid or missing"));

    }

    @Test
    public void parserReturnMeaningfulErrorWhenExpectedUriIsNull() throws Exception {
        String filename = givenAnExcelXFile()
                .withRow("SourceUri")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(invalidSpec, hasSize(1));
        assertThat(invalidSpec.get(0).getErrorMessage(), containsString("'Expected Destination' parameter is invalid or missing"));
    }

    @Test
    public void parserReturnMeaningfulErrorWhenExpectedUriIsEmpty() throws Exception {
        String filename = givenAnExcelXFile()
                .withRow("SourceUri", "")
                .get();

        new RedirectSpecExcelParser(filename, handler).parse();

        assertThat(invalidSpec, hasSize(1));
        assertThat(invalidSpec.get(0).getErrorMessage(), containsString("'Expected Destination' parameter is invalid or missing"));
    }


    @Test
    public void shouldReturnTheNumberOfRows() throws Exception {

        int NUM_ROWS = 10;

        String filename = givenAnExcelFileWithRows(NUM_ROWS);

        RedirectSpecExcelParser parser = new RedirectSpecExcelParser(filename, handler);
        assertThat(parser.getNumSpecs(), is(NUM_ROWS));

    }


    @Test
    public void hiddenSheetsShouldNotBeConsidered() throws Exception {
        final String filename = givenAnExcelFile()
                .withAnHiddenSheetAsFirstSheet()
                .withRow("SourceURI1", "ExpectedDestination1")
                .get();

        RedirectSpecExcelParser parser = new RedirectSpecExcelParser(filename, handler);
        assertThat(parser.getNumSpecs(), is(1));
    }

    private String givenAnExcelFileWithRows(int NUM_ROWS) throws IOException {
        ExcelTestFileBuilder excelTestFileBuilder = givenAnExcelFile();
        for (int i = 0; i < NUM_ROWS; i++) {
            excelTestFileBuilder.withRow("SourceURI" + i, "ExpectedDestination" + i);
        }
        return excelTestFileBuilder.get();
    }

    private ExcelTestFileBuilder givenAnExcelXFile() {
        return new ExcelTestFileBuilder(true);
    }

    private ExcelTestFileBuilder givenAnExcelFile() {
        return new ExcelTestFileBuilder(false);
    }

    class ExcelTestFileBuilder {

        private final Workbook workbook;
        private final Sheet sheet;
        private int curRowNumber = 0;

        ExcelTestFileBuilder(boolean xslsx) {
            if (xslsx) {
                this.workbook = new XSSFWorkbook();
            } else {
                this.workbook = new HSSFWorkbook();
            }

            this.sheet = workbook.createSheet("Test sheet");
        }

        ExcelTestFileBuilder withAnHiddenSheetAsFirstSheet() {
            String hiddenSheetName = "HiddenSheet";
            Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);
            workbook.setSheetOrder(hiddenSheetName, 0);
            workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
            return this;
        }

        ExcelTestFileBuilder withRow(String... values) {
            Row row = sheet.createRow(curRowNumber++);
            int cellNumber = 0;
            for (String value : values) {
                row.createCell(cellNumber++).setCellValue(value);
            }
            return this;
        }

        String get() throws IOException {
            File file = temporaryFolder.newFile();
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            return file.getPath();
        }
    }
}