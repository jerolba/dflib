package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DoubleBinarySeriesCondition extends BinarySeriesCondition<Double, Double> {

    private final BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp;

    public DoubleBinarySeriesCondition(
            String opName,
            Exp<Double> left,
            Exp<Double> right,
            BiFunction<Series<Double>, Series<Double>, BooleanSeries> op,
            BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries eval(Series<Double> ls, Series<Double> rs) {
        return (ls instanceof DoubleSeries && rs instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) ls, (DoubleSeries) rs)
                : super.eval(ls, rs);
    }
}
