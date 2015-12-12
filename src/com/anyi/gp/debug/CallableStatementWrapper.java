package com.anyi.gp.debug;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

public class CallableStatementWrapper implements CallableStatement{
  private Logger logger = Logger.getLogger(CallableStatementWrapper.class);
  
  private CallableStatement stmt;
  
  public CallableStatementWrapper(CallableStatement stmt){
    this.stmt = stmt;
  }
  
  public Array getArray(int i) throws SQLException {
    return stmt.getArray(i);
  }

  public Array getArray(String parameterName) throws SQLException {
    return stmt.getArray(parameterName);
  }

  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    return stmt.getBigDecimal(parameterIndex);
  }

  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    return stmt.getBigDecimal(parameterName);
  }

  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    return stmt.getBigDecimal(parameterIndex, scale);
  }

  public Blob getBlob(int i) throws SQLException {
    return stmt.getBlob(i);
  }

  public Blob getBlob(String parameterName) throws SQLException {
    return stmt.getBlob(parameterName);
  }

  public boolean getBoolean(int parameterIndex) throws SQLException {
    return stmt.getBoolean(parameterIndex);
  }

  public boolean getBoolean(String parameterName) throws SQLException {
    return stmt.getBoolean(parameterName);
  }

  public byte getByte(int parameterIndex) throws SQLException {    
    return stmt.getByte(parameterIndex);
  }

  public byte getByte(String parameterName) throws SQLException {
    return stmt.getByte(parameterName);
  }

  public byte[] getBytes(int parameterIndex) throws SQLException {
    return stmt.getBytes(parameterIndex);
  }

  public byte[] getBytes(String parameterName) throws SQLException {    
    return stmt.getBytes(parameterName);
  }

  public Clob getClob(int i) throws SQLException {    
    return stmt.getClob(i);
  }

  public Clob getClob(String parameterName) throws SQLException { 
    return stmt.getClob(parameterName);
  }

  public Date getDate(int parameterIndex) throws SQLException { 
    return stmt.getDate(parameterIndex);
  }

  public Date getDate(String parameterName) throws SQLException { 
    return stmt.getDate(parameterName);
  }

  public Date getDate(int parameterIndex, Calendar cal) throws SQLException { 
    return stmt.getDate(parameterIndex, cal);
  }

  public Date getDate(String parameterName, Calendar cal) throws SQLException { 
    return stmt.getDate(parameterName, cal);
  }

  public double getDouble(int parameterIndex) throws SQLException {
    return stmt.getDouble(parameterIndex);
  }

  public double getDouble(String parameterName) throws SQLException { 
    return stmt.getDouble(parameterName);
  }

  public float getFloat(int parameterIndex) throws SQLException {
    return stmt.getFloat(parameterIndex);
  }

  public float getFloat(String parameterName) throws SQLException {    
    return stmt.getFloat(parameterName);
  }

  public int getInt(int parameterIndex) throws SQLException {
    return stmt.getInt(parameterIndex);
  }

  public int getInt(String parameterName) throws SQLException {
    return stmt.getInt(parameterName);
  }

  public long getLong(int parameterIndex) throws SQLException { 
    return stmt.getLong(parameterIndex);
  }

  public long getLong(String parameterName) throws SQLException {
    return stmt.getLong(parameterName);
  }

  public Object getObject(int parameterIndex) throws SQLException {
    return stmt.getObject(parameterIndex);
  }

  public Object getObject(String parameterName) throws SQLException {
    return stmt.getObject(parameterName);
  }

  public Object getObject(int i, Map map) throws SQLException {
    return stmt.getObject(i, map);
  }

  public Object getObject(String parameterName, Map map) throws SQLException {
    return stmt.getObject(parameterName, map);
  }

  public Ref getRef(int i) throws SQLException {
    return stmt.getRef(i);
  }

  public Ref getRef(String parameterName) throws SQLException {    
    return stmt.getRef(parameterName);
  }

  public short getShort(int parameterIndex) throws SQLException {    
    return stmt.getShort(parameterIndex);
  }

  public short getShort(String parameterName) throws SQLException {
    return stmt.getShort(parameterName);
  }

  public String getString(int parameterIndex) throws SQLException {
    return stmt.getString(parameterIndex);
  }

  public String getString(String parameterName) throws SQLException {    
    return stmt.getString(parameterName);
  }

  public Time getTime(int parameterIndex) throws SQLException {
    return stmt.getTime(parameterIndex);
  }

  public Time getTime(String parameterName) throws SQLException {
    return stmt.getTime(parameterName);
  }

  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    return stmt.getTime(parameterIndex, cal);
  }

  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    return stmt.getTime(parameterName, cal);
  }

  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    return stmt.getTimestamp(parameterIndex);
  }

  public Timestamp getTimestamp(String parameterName) throws SQLException {
    return stmt.getTimestamp(parameterName);
  }

  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    return stmt.getTimestamp(parameterIndex, cal);
  }

  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {    
    return stmt.getTimestamp(parameterName, cal);
  }

  public URL getURL(int parameterIndex) throws SQLException {
    return stmt.getURL(parameterIndex);
  }

  public URL getURL(String parameterName) throws SQLException {
    return stmt.getURL(parameterName);
  }

  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    stmt.registerOutParameter(parameterIndex, sqlType); 
  }

  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    stmt.registerOutParameter(parameterName, sqlType);
  }

  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    stmt.registerOutParameter(parameterIndex, sqlType, scale);
  }

  public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
    stmt.registerOutParameter(paramIndex, sqlType, typeName); 
  }

  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
    stmt.registerOutParameter(parameterName, sqlType, scale);
  }

  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
    stmt.registerOutParameter(parameterName, sqlType, typeName);
  }

  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    stmt.setAsciiStream(parameterName, x, length);    
  }

  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    stmt.setBigDecimal(parameterName, x);
  }

  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    stmt.setBinaryStream(parameterName, x, length);    
  }

  public void setBoolean(String parameterName, boolean x) throws SQLException {
    stmt.setBoolean(parameterName, x);
  }

  public void setByte(String parameterName, byte x) throws SQLException {
    stmt.setByte(parameterName, x);
  }

  public void setBytes(String parameterName, byte[] x) throws SQLException {    
    stmt.setBytes(parameterName, x);
  }

  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
    stmt.setCharacterStream(parameterName, reader, length);
  }

  public void setDate(String parameterName, Date x) throws SQLException {
    stmt.setDate(parameterName, x);
  }

  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    stmt.setDate(parameterName, x, cal);
  }

  public void setDouble(String parameterName, double x) throws SQLException {
    stmt.setDouble(parameterName, x);    
  }

  public void setFloat(String parameterName, float x) throws SQLException {
    stmt.setFloat(parameterName, x);
  }

  public void setInt(String parameterName, int x) throws SQLException {
    stmt.setInt(parameterName, x);
  }

  public void setLong(String parameterName, long x) throws SQLException {
    stmt.setLong(parameterName, x);    
  }

  public void setNull(String parameterName, int sqlType) throws SQLException {
    stmt.setNull(parameterName, sqlType);
  }

  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    stmt.setNull(parameterName, sqlType);    
  }

  public void setObject(String parameterName, Object x) throws SQLException {
    stmt.setObject(parameterName, x);
  }

  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    stmt.setObject(parameterName, x, targetSqlType);    
  }

  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    stmt.setObject(parameterName, x, targetSqlType, scale);   
  }

  public void setShort(String parameterName, short x) throws SQLException {
    stmt.setShort(parameterName, x);
  }

  public void setString(String parameterName, String x) throws SQLException {
    stmt.setString(parameterName, x);
  }

  public void setTime(String parameterName, Time x) throws SQLException {
    stmt.setTime(parameterName, x);
  }

  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    stmt.setTime(parameterName, x, cal);    
  }

  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    stmt.setTimestamp(parameterName, x);
  }

  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    stmt.setTimestamp(parameterName, x, cal);
  }

  public void setURL(String parameterName, URL val) throws SQLException {
    stmt.setURL(parameterName, val);
  }

  public boolean wasNull() throws SQLException {    
    return stmt.wasNull();
  }

  public void addBatch() throws SQLException {
    stmt.addBatch();
  }

  public void clearParameters() throws SQLException {
    stmt.clearParameters(); 
  }

  public boolean execute() throws SQLException {
    return stmt.execute();
  }

  public ResultSet executeQuery() throws SQLException { 
    return stmt.executeQuery();
  }

  public int executeUpdate() throws SQLException {    
    return stmt.executeUpdate();
  }

  public ResultSetMetaData getMetaData() throws SQLException {
    return stmt.getMetaData();
  }

  public ParameterMetaData getParameterMetaData() throws SQLException {
    return stmt.getParameterMetaData();
  }

  public void setArray(int arg0, Array arg1) throws SQLException {
    stmt.setArray(arg0, arg1);
  }

  public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {    
    stmt.setAsciiStream(arg0, arg1, arg2);
  }

  public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException { 
    stmt.setBigDecimal(arg0, arg1);
  }

  public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
    stmt.setBinaryStream(arg0, arg1, arg2);    
  }

  public void setBlob(int arg0, Blob arg1) throws SQLException {
    stmt.setBlob(arg0, arg1);
  }

  public void setBoolean(int arg0, boolean arg1) throws SQLException {
    stmt.setBoolean(arg0, arg1);
  }

  public void setByte(int arg0, byte arg1) throws SQLException {
    stmt.setByte(arg0, arg1);
  }

  public void setBytes(int arg0, byte[] arg1) throws SQLException {
    stmt.setBytes(arg0, arg1);    
  }

  public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
    stmt.setCharacterStream(arg0, arg1, arg2);
  }

  public void setClob(int arg0, Clob arg1) throws SQLException {
    stmt.setClob(arg0, arg1); 
  }

  public void setDate(int arg0, Date arg1) throws SQLException {
    stmt.setDate(arg0, arg1); 
  }

  public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
    stmt.setDate(arg0, arg1, arg2); 
  }

  public void setDouble(int arg0, double arg1) throws SQLException {
    stmt.setDouble(arg0, arg1);    
  }

  public void setFloat(int arg0, float arg1) throws SQLException {
    stmt.setFloat(arg0, arg1);
  }

  public void setInt(int arg0, int arg1) throws SQLException {
    stmt.setInt(arg0, arg1);    
  }

  public void setLong(int arg0, long arg1) throws SQLException {
    stmt.setLong(arg0, arg1);
  }

  public void setNull(int arg0, int arg1) throws SQLException {
    stmt.setNull(arg0, arg1);
  }

  public void setNull(int arg0, int arg1, String arg2) throws SQLException {
    stmt.setNull(arg0, arg1);    
  }

  public void setObject(int arg0, Object arg1) throws SQLException {
    stmt.setObject(arg0, arg1);
  }

  public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
    stmt.setObject(arg0, arg1, arg2);
  }

  public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
    stmt.setObject(arg0, arg1, arg2, arg3); 
  }

  public void setRef(int arg0, Ref arg1) throws SQLException {
    stmt.setRef(arg0, arg1);    
  }

  public void setShort(int arg0, short arg1) throws SQLException {
    stmt.setShort(arg0, arg1);    
  }

  public void setString(int arg0, String arg1) throws SQLException {
    stmt.setString(arg0, arg1);
  }

  public void setTime(int arg0, Time arg1) throws SQLException {
    stmt.setTime(arg0, arg1);
  }

  public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
    stmt.setTime(arg0, arg1, arg2);
  }

  public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {    
    stmt.setTimestamp(arg0, arg1);
  }

  public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
    stmt.setTimestamp(arg0, arg1, arg2);    
  }

  public void setURL(int arg0, URL arg1) throws SQLException {    
    stmt.setURL(arg0, arg1);
  }

  public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {
    stmt.setUnicodeStream(arg0, arg1, arg2);
  }

  public void addBatch(String sql) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    stmt.addBatch(sql);
  }

  public void cancel() throws SQLException {
    stmt.cancel();
  }

  public void clearBatch() throws SQLException {
    stmt.clearBatch();    
  }

  public void clearWarnings() throws SQLException {
    stmt.clearWarnings();
  }

  public void close() throws SQLException {
    stmt.close();
  }

  public boolean execute(String sql) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.execute(sql);
  }

  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.execute(sql, autoGeneratedKeys);
  }

  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.execute(sql, columnIndexes);
  }

  public boolean execute(String sql, String[] columnNames) throws SQLException {    
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.execute(sql, columnNames);
  }

  public int[] executeBatch() throws SQLException {    
    return stmt.executeBatch();
  }

  public ResultSet executeQuery(String sql) throws SQLException {  
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.executeQuery(sql);
  }

  public int executeUpdate(String sql) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.executeUpdate(sql);
  }

  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {    
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.executeUpdate(sql, autoGeneratedKeys);
  }

  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {    
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.executeUpdate(sql, columnIndexes);
  }

  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    if(logger.isDebugEnabled()){
      logger.debug(sql);
    }
    return stmt.executeUpdate(sql, columnNames);
  }

  public Connection getConnection() throws SQLException {
    //return new ConnectionWrapper(stmt.getConnection(), new Exception());
    return stmt.getConnection();
  }

  public int getFetchDirection() throws SQLException {
    return stmt.getFetchDirection();
  }

  public int getFetchSize() throws SQLException {
    return stmt.getFetchSize();
  }

  public ResultSet getGeneratedKeys() throws SQLException {
    return stmt.getGeneratedKeys();
  }

  public int getMaxFieldSize() throws SQLException {    
    return stmt.getMaxFieldSize();
  }

  public int getMaxRows() throws SQLException {
    return stmt.getMaxRows();
  }

  public boolean getMoreResults() throws SQLException {
    return stmt.getMoreResults();
  }

  public boolean getMoreResults(int current) throws SQLException {
    return stmt.getMoreResults(current);
  }

  public int getQueryTimeout() throws SQLException {    
    return stmt.getQueryTimeout();
  }

  public ResultSet getResultSet() throws SQLException {
    return stmt.getResultSet();
  }

  public int getResultSetConcurrency() throws SQLException {    
    return stmt.getResultSetConcurrency();
  }

  public int getResultSetHoldability() throws SQLException {
    return stmt.getResultSetHoldability();
  }

  public int getResultSetType() throws SQLException {    
    return stmt.getResultSetType();
  }

  public int getUpdateCount() throws SQLException {
    return stmt.getUpdateCount();
  }

  public SQLWarning getWarnings() throws SQLException {
    return stmt.getWarnings();
  }

  public void setCursorName(String name) throws SQLException {    
    stmt.setCursorName(name);
  }

  public void setEscapeProcessing(boolean enable) throws SQLException {
    stmt.setEscapeProcessing(enable);
  }

  public void setFetchDirection(int direction) throws SQLException {    
    stmt.setFetchDirection(direction);
  }

  public void setFetchSize(int rows) throws SQLException {
    stmt.setFetchSize(rows);
  }

  public void setMaxFieldSize(int max) throws SQLException {
    stmt.setMaxFieldSize(max);
  }

  public void setMaxRows(int max) throws SQLException {
    stmt.setMaxRows(max);
  }

  public void setQueryTimeout(int seconds) throws SQLException {
    stmt.setQueryTimeout(seconds);
  }

}
