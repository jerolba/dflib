package org.dflib;

import org.dflib.agg.SeriesAggregator;
import org.dflib.builder.SeriesByElementBuilder;
import org.dflib.series.ArraySeries;
import org.dflib.series.BooleanArraySeries;
import org.dflib.series.ColumnMappedSeries;
import org.dflib.series.DoubleArraySeries;
import org.dflib.series.DoubleSingleValueSeries;
import org.dflib.series.EmptySeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.IntArraySeries;
import org.dflib.series.IntSingleValueSeries;
import org.dflib.series.LongArraySeries;
import org.dflib.series.LongSingleValueSeries;
import org.dflib.series.OffsetLagSeries;
import org.dflib.series.OffsetLeadSeries;
import org.dflib.series.SingleValueSeries;
import org.dflib.series.TrueSeries;
import org.dflib.sort.SeriesSorter;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * A wrapper around an array of values of a certain type.
 *
 * @param <T>
 */
public interface Series<T> extends Iterable<T> {

    /**
     * @since 0.16
     */
    static <S, T> SeriesByElementBuilder<S, T> byElement(Extractor<S, T> extractor) {
        return new SeriesByElementBuilder<>(extractor);
    }

    /**
     * @since 0.16
     */
    @SafeVarargs
    static <T> Series<T> of(T... data) {
        return data != null && data.length > 0 ? new ArraySeries<>(data) : new EmptySeries<>();
    }

    /**
     * @since 0.16
     */
    static <T> Series<T> ofIterable(Iterable<T> data) {

        return byElement(Extractor.<T>$col())
                .guessCapacity(data)
                .appender()
                .append(data)
                .toSeries();
    }

    /**
     * @since 0.16
     */
    static BooleanSeries ofBool(boolean... bools) {
        return new BooleanArraySeries(bools);
    }

    /**
     * @since 0.16
     */
    static IntSeries ofInt(int... ints) {
        return new IntArraySeries(ints);
    }

    /**
     * @since 0.16
     */
    static DoubleSeries ofDouble(double... doubles) {
        return new DoubleArraySeries(doubles);
    }

    /**
     * @since 0.16
     */
    static LongSeries ofLong(long... longs) {
        return new LongArraySeries(longs);
    }

    /**
     * Returns a Series of the specified size filled with a single value.
     *
     * @since 1.0.0-M19
     */
    static <T> Series<T> ofVal(T value, int size) {
        if (value == null) {
            return new SingleValueSeries<>(null, size);
        } else if (value instanceof Integer) {
            return (Series<T>) new IntSingleValueSeries((int) value, size);
        } else if (value instanceof Long) {
            return (Series<T>) new LongSingleValueSeries((long) value, size);
        } else if (value instanceof Double) {
            return (Series<T>) new DoubleSingleValueSeries((double) value, size);
        } else if (value instanceof Boolean) {
            return (boolean) value ? (Series<T>) new TrueSeries(size) : (Series<T>) new FalseSeries(size);
        } else {
            return new SingleValueSeries<>(value, size);
        }
    }

    /**
     * Returns a "nominal" type of elements this Series object. Since most Series do not carry the type around, this
     * may be the "Object.class" in all cases except for primitive Series. Use a more expensive
     * {@link #getInferredType()} to check the real type of series values.
     *
     * @return the nominal type of values in the series
     * @since 0.6
     */
    Class<?> getNominalType();

    /**
     * Returns the most specific common superclass of the values in this Series object. If all values are null, returns
     * Object.class. First invocation of this method may be quite slow in many cases, as it entails a full scan of the
     * Series values.
     *
     * @return the most specific common superclass of the values in this Series object.
     * @since 0.8
     */
    Class<?> getInferredType();

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * @since 0.16
     */
    default <V> Series<V> map(Exp<V> mapper) {
        return mapper.eval(this);
    }

    /**
     * Creates a new Series with a provided value appended to the end of this Series.
     *
     * @since 0.18
     */
    default Series<?> add(Object value) {
        int s = size();

        Object[] data = new Object[s + 1];
        this.copyTo(data, 0, 0, s);
        data[s] = value;
        return new ArraySeries<>(data);
    }

    /**
     * Converts the Series to another Series of the same length, applying the provided function.
     *
     * @param mapper a function that maps each Series value to some other value
     * @param <V>    value type produced by the mapper
     * @return a Series produced by applying a mapper to this Series
     * @since 0.6
     */
    default <V> Series<V> map(ValueMapper<T, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    /**
     * Converts the Series to a BooleanSeries of the same length, applying the provided function.
     *
     * @since 0.16
     */
    default BooleanSeries mapAsBool(BoolValueMapper<? super T> converter) {
        int len = size();

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = converter.map(get(i));
        }

        return new BooleanArraySeries(data);
    }

    /**
     * Converts the Series to an IntSeries of the same length, applying the provided function.
     *
     * @since 0.16
     */
    default IntSeries mapAsInt(IntValueMapper<? super T> converter) {
        int len = size();

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = converter.map(get(i));
        }

        return new IntArraySeries(data);
    }

    /**
     * Converts the Series to a LongSeries of the same length, applying the provided function.
     *
     * @since 0.16
     */
    default LongSeries mapAsLong(LongValueMapper<? super T> converter) {
        int len = size();
        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = converter.map(get(i));
        }

        return new LongArraySeries(data);
    }

    /**
     * Converts the Series to a DoubleSeries of the same length, applying the provided function.
     *
     * @since 0.16
     */
    default DoubleSeries mapAsDouble(DoubleValueMapper<? super T> converter) {
        int len = size();
        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = converter.map(get(i));
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Casts the Series to a specific type. This is a fast (aka "unsafe") version of cast that does not check that all
     * elements of the Series conform to the provided type. Instead, it relies on user knowledge of the specific type.
     * In case of user errors, this will cause ClassCastExceptions downstream.  One example where this method is useful
     * is when retrieving untyped Series from DataFrames. Applying a cast allows to keep Series transformation
     * invocations "fluent".
     *
     * @see #castAs(Class)
     * @since 0.18
     */
    default <S> Series<S> unsafeCastAs(Class<S> type) {
        return (Series<S>) this;
    }

    /**
     * Casts the Series to a specific type. This is a potentially slow version of cast that checks that all elements
     * of the Series conform to the provided type. One example where this method is useful is when retrieving untyped
     * Series from DataFrames. Applying a cast allows to keep Series transformation invocations "fluent".
     *
     * @see #unsafeCastAs(Class)
     * @since 0.18
     */
    <S> Series<S> castAs(Class<S> type) throws ClassCastException;

    /**
     * @since 0.18
     */
    default BooleanSeries castAsBool() throws ClassCastException {
        throw new ClassCastException("Can't cast to BooleanSeries");
    }

    /**
     * @since 0.18
     */
    default DoubleSeries castAsDouble() throws ClassCastException {
        throw new ClassCastException("Can't cast to DoubleSeries");
    }

    /**
     * @since 0.18
     */
    default IntSeries castAsInt() throws ClassCastException {
        throw new ClassCastException("Can't cast to IntSeries");
    }

    /**
     * @since 0.18
     */
    default LongSeries castAsLong() throws ClassCastException {
        throw new ClassCastException("Can't cast to LongSeries");
    }

    /**
     * A map function over the Series that generates a DataFrame of the same height as the length of the Series,
     * which each row produced by applying the map function to each Series value.
     *
     * @return a new DataFrame built from Series values.
     * @since 0.7
     */
    DataFrame map(Index resultColumns, ValueToRowMapper<T> mapper);

    /**
     * Returns a {@link Series} that contains a range of data from this series.
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @since 1.0.0-M19
     */
    Series<T> range(int fromInclusive, int toExclusive);

    /**
     * @since 0.6
     * @deprecated in favor of {@link #range(int, int)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return range(fromInclusive, toExclusive);
    }

    /**
     * Resolves the Series executing any lazy calculations. If called more than once, the first evaluation result is reused.
     */
    Series<T> materialize();

    Series<T> fillNulls(T value);

    /**
     * @param values a Series to take null replacement values from
     * @return a new Series with nulls replaced with values from another Series with matching positions
     * @since 0.6
     */
    Series<T> fillNullsFromSeries(Series<? extends T> values);

    Series<T> fillNullsBackwards();

    Series<T> fillNullsForward();

    /**
     * Combines this Series with multiple other Series. This is an operation similar to SQL "UNION"
     */
    Series<T> concat(Series<? extends T>... other);

    /**
     * Returns a Series with elements from this Series that are not present in another Series. This is an operation
     * similar to SQL "EXCEPT".
     *
     * @since 1.0.0-M19
     */
    Series<T> diff(Series<? extends T> other);

    /**
     * Returns a Series with elements that are present in this and another Series. This is an operation similar to
     * SQL "INTERSECT".
     *
     * @since 1.0.0-M19
     */
    Series<T> intersect(Series<? extends T> other);

    /**
     * Returns a Series with the first <code>len</code> elements of this Series. If this Series is shorter than the
     * requested length, then the entire Series is returned. If <code>len</code> is negative, instead of returning the
     * leading elements, they are skipped, and the rest of the Series is returned.
     */
    Series<T> head(int len);

    /**
     * Returns a Series with the last <code>len</code> elements of this Series. If this Series is shorter than the
     * requested length, then the entire Series is returned. If <code>len</code> is negative, instead of returning the
     * trailing elements, they are skipped, and the rest of the Series is returned.
     */
    Series<T> tail(int len);

    /**
     * @since 0.11
     */
    Series<T> select(Condition condition);

    default Series<T> select(int... positions) {
        return select(Series.ofInt(positions));
    }

    Series<T> select(IntSeries positions);

    /**
     * @since 0.11
     */
    Series<T> select(ValuePredicate<T> p);

    /**
     * @since 0.11
     */
    Series<T> select(BooleanSeries positions);

    // TODO: can't have "select(boolean...)" as it conflicts with "select(int...)". Should we change to "select(int, int...)" ?

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries index(ValuePredicate<T> predicate);

    /**
     * @since 0.11
     */
    Series<T> sort(Sorter... sorters);

    /**
     * Returns a sorted copy of this Series using provided Comparator.
     *
     * @return sorted copy of this series.
     * @since 0.6
     */
    Series<T> sort(Comparator<? super T> comparator);

    /**
     * Calculates and returns an IntSeries representing element indices from the original Series in the order dictated
     * by the comparator. This operation is useful when we want to sort another Series based on the ordering of this
     * Series.
     * <p>Note that calling "sortIndexInt" on the result of this method produces a Series of sort-aware row numbers.</p>
     *
     * @param comparator value comparator for sorting¬
     * @return an IntSeries representing element indices from the original Series
     * @since 0.8
     */
    default IntSeries sortIndex(Comparator<? super T> comparator) {
        return new SeriesSorter<>(this).sortIndex(comparator);
    }

    /**
     * @param s a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     * @since 0.6
     */
    default BooleanSeries eq(Series<?> s) {
        int len = size();

        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = Objects.equals(get(i), s.get(i));
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @param s a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     * @since 0.6
     */
    default BooleanSeries ne(Series<?> s) {
        int len = size();

        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = !Objects.equals(get(i), s.get(i));
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    BooleanSeries isNull();

    /**
     * @since 0.11
     */
    BooleanSeries isNotNull();

    /**
     * @since 0.18
     */
    BooleanSeries in(Object... values);

    /**
     * @since 0.18
     */
    BooleanSeries notIn(Object... values);

    /**
     * Returns a boolean series indicating whether each original Series position matched the predicate
     *
     * @param predicate match condition
     * @return a BooleanSeries with true/false elements corresponding whether a given position in "this" Series matched
     * the predicate.
     * @since 0.6
     */
    default BooleanSeries locate(ValuePredicate<T> predicate) {

        // even for primitive Series it is slightly faster to implement "locate" directly than delegating
        // to "locateXyz", because the predicate signature requires primitive boxing

        int len = size();
        boolean[] matches = new boolean[len];

        for (int i = 0; i < len; i++) {
            matches[i] = predicate.test(get(i));
        }

        return new BooleanArraySeries(matches);
    }

    /**
     * Replaces values at positions with values from another Series. "with" Series should have the same size as
     * the "positions" Series.
     *
     * @since 1.0.0-M19
     */
    Series<T> replace(IntSeries positions, Series<T> with);

    /**
     * @param condition a BooleanSeries that determines which cells need to be replaced.
     * @param with      a value to replace matching cells with
     * @return a new series with replaced values
     * @since 0.6
     */
    Series<T> replace(BooleanSeries condition, T with);

    /**
     * @param condition a BooleanSeries that determines which cells need not be replaced.
     * @param with      a value to replace non-matching cells with
     * @return a new series with replaced values
     * @since 0.6
     */
    Series<T> replaceNoMatch(BooleanSeries condition, T with);

    /**
     * @return a Series that contains non-repeating values from this Series.
     * @since 0.6
     */
    Series<T> unique();

    /**
     * Returns a 2-column DataFrame that provides counts of distinct values present in the Series. Null values are
     * not included in the counts.
     *
     * @since 0.6
     */
    DataFrame valueCounts();

    /**
     * Groups Series by its values.
     *
     * @since 0.6
     */
    SeriesGroupBy<T> group();

    /**
     * Groups Series, using the provided function to calculate grouping key.
     *
     * @since 0.6
     */
    SeriesGroupBy<T> group(ValueMapper<T, ?> by);

    /**
     * Returns a scalar value that is a result of applying provided aggregator to Series.
     *
     * @since 0.6
     */
    default <R> Series<R> agg(Exp<R> aggregator) {
        return aggregator.eval(this);
    }

    /**
     * Returns a Series, with each value corresponding to the result of aggregation with each aggregator out of the
     * provided array of aggregators.
     *
     * @since 0.6
     */
    default DataFrame aggMultiple(Exp<?>... aggregators) {
        return SeriesAggregator.aggAsDataFrame(this, aggregators);
    }

    /**
     * @since 0.7
     */
    default T first() {
        return size() > 0 ? get(0) : null;
    }

    /**
     * @since 0.18
     */
    default T last() {
        int size = size();
        return size > 0 ? get(size - 1) : null;
    }

    /**
     * @since 0.7
     */
    default String concat(String delimiter) {
        return agg(Exp.$col("").vConcat(delimiter)).get(0);
    }

    /**
     * @since 0.7
     */
    default String concat(String delimiter, String prefix, String suffix) {
        return agg(Exp.$col("").vConcat(delimiter, prefix, suffix)).get(0);
    }

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size. If you are
     * doing sampling in a high concurrency application, consider using {@link #sample(int, Random)}, as this method
     * is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the size of this Series.
     * @return a Series object that is a sample of values from this object
     * @since 0.7
     */
    Series<T> sample(int size);

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the size of this Series.
     * @param random a custom random number generator
     * @return a Series object that is a sample of values from this object
     * @since 0.7
     */
    Series<T> sample(int size, Random random);

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with nulls.
     *
     * @since 0.9
     */
    default Series<T> shift(int offset) {
        return shift(offset, null);
    }

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with the provided filler value.
     *
     * @since 0.9
     */
    default Series<T> shift(int offset, T filler) {
        if (offset > 0) {
            return new OffsetLeadSeries<>(this, offset, filler);
        } else if (offset < 0) {
            return new OffsetLagSeries<>(this, offset, filler);
        } else {
            return this;
        }
    }

    /**
     * @since 0.11
     */
    default <V> Series<V> eval(Exp<V> exp) {
        return exp.eval(this);
    }

    /**
     * @since 0.7
     */
    @Override
    default Iterator<T> iterator() {

        return new Iterator<T>() {

            final int len = Series.this.size();
            int i;

            @Override
            public boolean hasNext() {
                return i < len;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator: " + i);
                }

                return Series.this.get(i++);
            }
        };
    }

    /**
     * Returns a List with this Series values. Returned list is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new List with data from this Series
     * @since 0.7
     */
    default List<T> toList() {
        int len = size();
        Object[] copy = new Object[len];
        copyTo(copy, 0, 0, len);
        return asList((T[]) copy);
    }

    /**
     * Returns a Set with this Series values. Returned set is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new Set with data from this Series
     * @since 0.7
     */
    default Set<T> toSet() {
        int len = size();

        // 1. use a Set with predictable ordering.
        // 2. Start with a set size higher than the default to minimize resizing
        // (have to guess the capacity, as we don't know the Series cardinality)

        int capacity = len < 1000 ? (int) (1 + len / 0.75) : 1300;
        Set<T> set = new LinkedHashSet<>(capacity);
        for (int i = 0; i < len; i++) {
            set.add(get(i));
        }

        return set;
    }

    /**
     * Returns an array with this Series values. Returned array contains a copy of this Series data, so modifying it
     * will not affect the underlying Series.
     *
     * @param a the array into which the elements of this collection are to be stored, if it is big enough; otherwise,
     *          a new array of the same  runtime type is allocated for this purpose. This is similar to
     *          {@link java.util.Collection#toArray(Object[])} behavior.
     * @return a new array with data from this Series
     * @since 0.7
     */
    default T[] toArray(T[] a) {
        int len = size();

        // TODO: wish we could use Series.getType(), but its default implementation returns Object.class, and is useless
        //  here, so we need to rely on the passed array prototype
        T[] copy = a.length < len
                ? (T[]) Array.newInstance(a.getClass().getComponentType(), len)
                : a;

        copyTo(copy, 0, 0, len);
        return copy;
    }
}
