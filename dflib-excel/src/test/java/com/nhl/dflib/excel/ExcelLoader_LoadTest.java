package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExcelLoader_LoadTest {

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void testFromFile(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(file);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void testFromStringFilePath(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(file.getPath());
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void testFromPath(String source) throws URISyntaxException {

        Path path = Paths.get(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(path);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void testFromStream(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            Map<String, DataFrame> data = new ExcelLoader().load(in);
            assertEquals(1, data.size());
            DataFrame df = data.get("Sheet1");
            assertNotNull(df);

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromStreamMultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            Map<String, DataFrame> data = new ExcelLoader().load(in);

            // the ordering of keys in the map must match the order in Excel
            List<String> keys = data.keySet().stream().collect(Collectors.toList());
            assertEquals(asList("S2", "S0", "Some Sheet", "S1"), keys);

            new DataFrameAsserts(data.get("S1"), "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");

            new DataFrameAsserts(data.get("S2"), "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }

    @Test
    public void testSparse() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet-sparse.xlsx")) {
            DataFrame df = new ExcelLoader().load(in).get("Sheet1");

            new DataFrameAsserts(df, "B", "C", "D", "E", "F", "G")
                    .expectHeight(4)
                    .expectRow(0, 1d, 2d, null, null, 3d, null)
                    .expectRow(1, null, 4d, 5d, null, null, 6d)
                    .expectRow(2, null, null, null, null, null, null)
                    .expectRow(3, null, 3d, 8d, null, 7d, null);
        }
    }
}
