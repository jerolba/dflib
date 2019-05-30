package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;

public class TableLoader {

    protected JdbcConnector connector;
    protected int maxRows;
    private String tableName;
    private String[] columns;

    public TableLoader(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.tableName = tableName;
    }

    public TableLoader includeColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    // TODO: limit without sorting may return unpredictable data.. should we allow to specify a sort column?
    public TableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public DataFrame load() {
        return new SqlLoader(connector, buildSql())
                .maxRows(maxRows)
                .load();
    }

    protected String buildSql() {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        String columns = buildColumnsSql();
        String name = connector.quoteIdentifier(tableName);
        return "select " + columns + " from " + name;
    }

    protected String buildColumnsSql() {

        if (this.columns == null || columns.length == 0) {
            return "*";
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }

            buf.append(connector.quoteIdentifier(columns[i]));
        }

        return buf.toString();
    }
}
