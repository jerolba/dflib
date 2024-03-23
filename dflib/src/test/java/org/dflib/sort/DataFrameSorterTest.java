package org.dflib.sort;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrameSorterTest {

    @Test
    public void sort() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries sortIndex = DataFrameSorter.sort(Comparators.of(df.getColumn(1), true), df.height());
        new IntSeriesAsserts(sortIndex).expectData(3, 0, 4, 1, 2);
    }
}
