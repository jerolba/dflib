package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$bool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BoolColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $bool("a").getColumnName());
        assertEquals("$bool(0)", $bool(0).getColumnName());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $bool("b").getColumnName(df));
        assertEquals("a", $bool(0).getColumnName(df));
    }

    @Test
    public void eval() {
        Condition c = $bool("b");

        BooleanSeries s = Series.ofBool(false, true, true);
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, true);
    }

    @Test
    public void as() {
        Condition c = $bool("b");
        assertEquals("b", c.getColumnName(mock(DataFrame.class)));
        assertEquals("c", c.as("c").getColumnName(mock(DataFrame.class)));
    }
}
