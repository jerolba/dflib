package com.nhl.dflib.builder;

/**
 * Assembles a DataFrame from a sequence of Object arrays. Supports source transformations, including generation of
 * primitive columns, etc.
 *
 * @since 0.16
 */
public class DataFrameArrayAppender extends DataFrameAppender<Object[]> {

    protected DataFrameArrayAppender(DataFrameAppenderSink<Object[]> sink) {
        super(sink);
    }

    /**
     * Appends a single row, extracting data from the supplied array vararg.
     */
    @Override
    public DataFrameArrayAppender append(Object... rowSource) {
        super.append(rowSource);
        return this;
    }
}
