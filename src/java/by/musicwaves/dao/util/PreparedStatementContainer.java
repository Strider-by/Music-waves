package by.musicwaves.dao.util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class PreparedStatementContainer implements java.sql.PreparedStatement
{
    private PreparedStatement preparedStatement;
    private int currentBatchParameterIndex;

    public PreparedStatementContainer()
    { }

    public void wrap(PreparedStatement preparedStatement)
    {
        this.preparedStatement = preparedStatement;
    }

    public PreparedStatement getInnerStatement()
    {
        return preparedStatement;
    }

    // needed when we use addBatch to start parameter numeration from the beginning
    private void resetBatchParameterIndex()
    {
        currentBatchParameterIndex = 0;
    }

    // setters
    public void setNextArray(Array x) throws SQLException
    {
        preparedStatement.setArray(++currentBatchParameterIndex, x);
    }

    public void setNextAsciiStream(InputStream x, int length) throws SQLException
    {
        setAsciiStream(++currentBatchParameterIndex, x, length);
    }

    public void setNextBigDecimal(BigDecimal x) throws SQLException
    {
        setBigDecimal(++currentBatchParameterIndex, x);
    }

    public void setNextBinaryStream(InputStream x, int length) throws SQLException
    {
        setBinaryStream(++currentBatchParameterIndex, x, length);
    }

    public void setNextBlob(InputStream inputStream, long length) throws SQLException
    {
        setBlob(++currentBatchParameterIndex, inputStream, length);
    }

    public void setNextBlob(Blob x) throws SQLException
    {
        setBlob(++currentBatchParameterIndex, x);
    }

    public void setNextBoolean(boolean x) throws SQLException
    {
        setBoolean(++currentBatchParameterIndex, x);
    }

    public void setNextByte(byte x) throws SQLException
    {
        setByte(++currentBatchParameterIndex, x);
    }

    public void setNextBytes(byte[] x) throws SQLException
    {
        setBytes(++currentBatchParameterIndex, x);
    }

    public void setNextCharacterStream(Reader reader, int length) throws SQLException
    {
        setCharacterStream(++currentBatchParameterIndex, reader, length);
    }

    public void setNextClob(Clob x) throws SQLException
    {
        setClob(++currentBatchParameterIndex, x);
    }

    public void setNextDate(Date x) throws SQLException
    {
        setDate(++currentBatchParameterIndex, x);
    }

    public void setNextDate(Date x, Calendar cal) throws SQLException
    {
        setDate(++currentBatchParameterIndex, x, cal);
    }

    public void setNextDouble(double x) throws SQLException
    {
        setDouble(++currentBatchParameterIndex, x);
    }

    public void setNextFloat(float x) throws SQLException
    {
        setFloat(++currentBatchParameterIndex, x);
    }

    public void setNextInt(int x) throws SQLException
    {
        setInt(++currentBatchParameterIndex, x);
    }

    public void setNextLong(long x) throws SQLException
    {
        setLong(++currentBatchParameterIndex, x);
    }

    public void setNextNull(int sqlType) throws SQLException
    {
        setNull(++currentBatchParameterIndex, sqlType);
    }

    public void setNextNull(int sqlType, String arg) throws SQLException
    {
        setNull(++currentBatchParameterIndex, sqlType, arg);
    }

    public void setNextObject(Object parameterObj) throws SQLException
    {
        setObject(++currentBatchParameterIndex, parameterObj);
    }

    public void setNextObject(Object parameterObj, int targetSqlType) throws SQLException
    {
        setObject(++currentBatchParameterIndex, parameterObj, targetSqlType);
    }

    public void setNextObject(Object parameterObj, int targetSqlType, int scale) throws SQLException
    {
        setObject(++currentBatchParameterIndex, parameterObj, targetSqlType, scale);
    }

    public void setNextRef(Ref x) throws SQLException
    {
        setRef(++currentBatchParameterIndex, x);
    }

    public void setNextShort(short x) throws SQLException
    {
        setShort(++currentBatchParameterIndex, x);
    }

    public void setNextString(String x) throws SQLException
    {
        setString(++currentBatchParameterIndex, x);
    }

    public void setNextTime(Time x, Calendar cal) throws SQLException
    {
        setTime(++currentBatchParameterIndex, x, cal);
    }

    public void setNextTime(Time x) throws SQLException
    {
        setTime(++currentBatchParameterIndex, x);
    }

    public void setNextTimestamp(Timestamp x, Calendar cal) throws SQLException
    {
        setTimestamp(++currentBatchParameterIndex, x, cal);
    }

    public void setNextTimestamp(Timestamp x) throws SQLException
    {
        setTimestamp(++currentBatchParameterIndex, x);
    }

    // overrided
    @Override
    public ResultSet executeQuery() throws SQLException
    {
        return preparedStatement.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        return preparedStatement.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException
    {
        preparedStatement.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
        preparedStatement.setBoolean(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException
    {
        preparedStatement.setByte(parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException
    {
        preparedStatement.setShort(parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException
    {
        preparedStatement.setInt(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException
    {
        preparedStatement.setLong(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException
    {
        preparedStatement.setFloat(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException
    {
        preparedStatement.setDouble(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
        preparedStatement.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException
    {
        preparedStatement.setString(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException
    {
        preparedStatement.setBytes(parameterIndex, x
        );
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException
    {
        preparedStatement.setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException
    {
        preparedStatement.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
        preparedStatement.setTimestamp(parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void clearParameters() throws SQLException
    {
        preparedStatement.clearParameters();
        currentBatchParameterIndex = 0;
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x);
    }

    @Override
    public boolean execute() throws SQLException
    {
        return preparedStatement.execute();
    }

    @Override
    public void addBatch() throws SQLException
    {
        preparedStatement.addBatch();
        resetBatchParameterIndex();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException
    {
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
        preparedStatement.setRef(parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
        preparedStatement.setBlob(parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
        preparedStatement.setClob(parameterIndex, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException
    {
        preparedStatement.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return preparedStatement.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
    {
        preparedStatement.setDate(parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
    {
        preparedStatement.setTime(parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
        preparedStatement.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        preparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException
    {
        preparedStatement.setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return preparedStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException
    {
        preparedStatement.setRowId(parameterIndex, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException
    {
        preparedStatement.setNString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
    {
        preparedStatement.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException
    {
        preparedStatement.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        preparedStatement.setClob(parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
    {
        preparedStatement.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        preparedStatement.setNClob(parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
    {
        preparedStatement.setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
    {
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
    {
        preparedStatement.setAsciiStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
    {
        preparedStatement.setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
    {
        preparedStatement.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
    {
        preparedStatement.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException
    {
        preparedStatement.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
    {
        preparedStatement.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException
    {
        preparedStatement.setNClob(parameterIndex, reader);
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public long executeLargeUpdate() throws SQLException
    {
        return preparedStatement.executeLargeUpdate();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException
    {
        return preparedStatement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException
    {
        return preparedStatement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException
    {
        preparedStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException
    {
        return preparedStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException
    {
        preparedStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException
    {
        return preparedStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException
    {
        preparedStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        preparedStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException
    {
        return preparedStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException
    {
        preparedStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException
    {
        preparedStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return preparedStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        preparedStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException
    {
        preparedStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException
    {
        return preparedStatement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException
    {
        return preparedStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException
    {
        return preparedStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException
    {
        return preparedStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException
    {
        preparedStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return preparedStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException
    {
        preparedStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return preparedStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException
    {
        return preparedStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException
    {
        return preparedStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException
    {
        preparedStatement.addBatch(sql);
        currentBatchParameterIndex = 0;
    }

    @Override
    public void clearBatch() throws SQLException
    {
        preparedStatement.clearBatch();
        currentBatchParameterIndex = 0;
    }

    @Override
    public int[] executeBatch() throws SQLException
    {

        return preparedStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return preparedStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException
    {
        return preparedStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException
    {
        return preparedStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
    {
        return preparedStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
    {
        return preparedStatement.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException
    {
        return preparedStatement.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
    {
        return preparedStatement.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException
    {
        return preparedStatement.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException
    {
        return preparedStatement.execute(sql, columnNames
        );
    }

    @Override
    public int getResultSetHoldability() throws SQLException
    {
        return preparedStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return preparedStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException
    {
        preparedStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException
    {
        return preparedStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException
    {
        preparedStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException
    {
        return preparedStatement.isCloseOnCompletion();
    }

    @Override
    public long getLargeUpdateCount() throws SQLException
    {
        return preparedStatement.getLargeUpdateCount();
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException
    {
        preparedStatement.setLargeMaxRows(max);
    }

    @Override
    public long getLargeMaxRows() throws SQLException
    {
        return preparedStatement.getLargeMaxRows();
    }

    @Override
    public long[] executeLargeBatch() throws SQLException
    {

        return preparedStatement.executeLargeBatch();
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException
    {
        return preparedStatement.executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException
    {
        return preparedStatement.executeLargeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException
    {
        return preparedStatement.executeLargeUpdate(sql, columnIndexes);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException
    {
        return preparedStatement.executeLargeUpdate(sql, columnNames);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return preparedStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return preparedStatement.isWrapperFor(iface);
    }

    @Override
    public String toString()
    {
        return preparedStatement.toString();
    }
}
