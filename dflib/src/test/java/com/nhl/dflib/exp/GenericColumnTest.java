package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.nhl.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GenericColumnTest {

    @Test
    public void testCastAsStr() {
        StrExp str = $col(0).castAsStr();
        Series<Object> s = Series.forData("a", null, LocalDate.of(2021, 1, 2));
        new SeriesAsserts(str.eval(s)).expectData("a", null, "2021-01-02");
    }

    @Test
    public void testGetColumnName() {
        assertEquals("a", $col("a").getColumnName());
        assertEquals("$col(0)", $col(0).getColumnName());
    }

    @Test
    public void testName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $col("b").getColumnName(df));
        assertEquals("a", $col(0).getColumnName(df));
    }

    @Test
    public void testAs() {
        Exp<?> e = $col("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testIsNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(false, false, true, false);
    }

    @Test
    public void testIsNotNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNotNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false, true);
    }
}
