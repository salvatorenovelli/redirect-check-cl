package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class RedirectSpecExcelParserTest {


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
}