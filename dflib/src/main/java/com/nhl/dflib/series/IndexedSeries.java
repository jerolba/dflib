package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Objects;

public class IndexedSeries<T> implements Series<T> {

    private Series<T> source;
    private Series<Integer> includePositions;

    private Series<T> materialized;

    public IndexedSeries(Series<T> source, Series<Integer> includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions.size();
    }

    @Override
    public T get(int index) {
        return getMaterialized().get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        getMaterialized().copyTo(to, fromOffset, toOffset, len);
    }

    protected Series<T> getMaterialized() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected ArraySeries doMaterialize() {

        int h = includePositions.size();

        Object[] data = new Object[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.get(i);

            // skipped positions (index < 0) are found in joins
            data[i] = index < 0 ? null : source.get(index);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new ArraySeries(data);
    }
}
