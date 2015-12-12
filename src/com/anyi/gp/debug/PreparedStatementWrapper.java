package com.anyi.gp.debug;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class PreparedStatementWrapper implements PreparedStatement{
	private Logger logger = Logger.getLogger(PreparedStatementWrapper.class);
	
	private PreparedStatement pst;
	
	public PreparedStatementWrapper(PreparedStatement pst){
		this.pst = pst;
	}
	public void addBatch() throws SQLException {
		pst.addBatch();
	}

	public void clearParameters() throws SQLException {
		pst.clearParameters();
	}

	public boolean execute() throws SQLException {
		return pst.execute();
	}

	public ResultSet executeQuery() throws SQLException {
		return pst.executeQuery();
	}

	public int executeUpdate() throws SQLException {
		return pst.executeUpdate();
	}

	public ResultSetMetaData getMetaData() throws SQLException {		
		return pst.getMetaData();
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {		
		return pst.getParameterMetaData();
	}

	public void setArray(int i, Array x) throws SQLException {
		pst.setArray(i, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		pst.setAsciiStream(parameterIndex, x, length);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		pst.setBigDecimal(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		pst.setBinaryStream(parameterIndex, x, length);
	}

	public void setBlob(int i, Blob x) throws SQLException {
		pst.setBlob(i, x);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		pst.setBoolean(parameterIndex, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		pst.setByte(parameterIndex, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {		
		pst.setBytes(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		pst.setCharacterStream(parameterIndex, reader, length);
	}

	public void setClob(int i, Clob x) throws SQLException {
		pst.setClob(i, x);		
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		pst.setDate(parameterIndex, x);
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		pst.setDate(parameterIndex, x, cal);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		pst.setDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		pst.setFloat(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		pst.setInt(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		pst.setLong(parameterIndex, x);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		pst.setNull(parameterIndex, sqlType);
	}

	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		pst.setNull(paramIndex, sqlType, typeName);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		pst.setObject(parameterIndex, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		pst.setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		pst.setObject(parameterIndex, x, targetSqlType, scale);
	}

	public void setRef(int i, Ref x) throws SQLException {
		pst.setRef(i, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		pst.setShort(parameterIndex, x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		pst.setString(parameterIndex, x);		
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		pst.setTime(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		pst.setTime(parameterIndex, x, cal);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		pst.setTimestamp(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		pst.setTimestamp(parameterIndex, x, cal);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		pst.setURL(parameterIndex, x);
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		pst.setUnicodeStream(parameterIndex, x, length);
	}

	public void addBatch(String sql) throws SQLException {		
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		pst.addBatch(sql);
	}

	public void cancel() throws SQLException {
		pst.cancel();
	}

	public void clearBatch() throws SQLException {
		pst.clearBatch();
	}

	public void clearWarnings() throws SQLException {
		pst.clearWarnings();		
	}

	public void close() throws SQLException {
		pst.close();		
	}

	public boolean execute(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}		
		return false;
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.execute(sql, autoGeneratedKeys);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.execute(sql, columnIndexes);
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.execute(sql, columnNames);
	}

	public int[] executeBatch() throws SQLException {		
		return pst.executeBatch();
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.executeQuery(sql);
	}

	public int executeUpdate(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.executeUpdate(sql);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.executeUpdate(sql, autoGeneratedKeys);
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.executeUpdate(sql, columnIndexes);
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
		return pst.executeUpdate(sql, columnNames);
	}

	public Connection getConnection() throws SQLException {
		//return new ConnectionWrapper(pst.getConnection(), new Exception());
    return pst.getConnection();
	}

	public int getFetchDirection() throws SQLException {
		return pst.getFetchDirection();
	}

	public int getFetchSize() throws SQLException {
		return pst.getFetchSize();
	}

	public ResultSet getGeneratedKeys() throws SQLException {		
		return pst.getGeneratedKeys();
	}

	public int getMaxFieldSize() throws SQLException {
		return pst.getMaxFieldSize();
	}

	public int getMaxRows() throws SQLException {
		return pst.getMaxRows();
	}

	public boolean getMoreResults() throws SQLException {		
		return pst.getMoreResults();
	}

	public boolean getMoreResults(int current) throws SQLException {		
		return pst.getMoreResults(current);
	}

	public int getQueryTimeout() throws SQLException {
		return pst.getQueryTimeout();
	}

	public ResultSet getResultSet() throws SQLException {
		return pst.getResultSet();
	}

	public int getResultSetConcurrency() throws SQLException {		
		return pst.getResultSetConcurrency();
	}

	public int getResultSetHoldability() throws SQLException {
		return pst.getResultSetHoldability();
	}

	public int getResultSetType() throws SQLException {		
		return pst.getResultSetType();
	}

	public int getUpdateCount() throws SQLException {
		return pst.getUpdateCount();
	}

	public SQLWarning getWarnings() throws SQLException {		
		return pst.getWarnings();
	}

	public void setCursorName(String name) throws SQLException {
		pst.setCursorName(name);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		pst.setEscapeProcessing(enable);
	}

	public void setFetchDirection(int direction) throws SQLException {
		pst.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException {
		pst.setFetchSize(rows);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		pst.setMaxFieldSize(max);
	}

	public void setMaxRows(int max) throws SQLException {
		pst.setMaxRows(max);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		pst.setQueryTimeout(seconds);
	}

}
