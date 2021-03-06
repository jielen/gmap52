package com.anyi.gp.debug;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

public class ConnectionWrapper implements Connection {

  private Connection conn = null;
  
  private Exception stack = null;
  
  private Logger logger = Logger.getLogger(ConnectionWrapper.class);
  
  public ConnectionWrapper(Connection conn,Exception stack){
    this.conn = conn;
    this.stack = stack;
  }
  
  protected void finalize() throws Throwable{
    super.finalize();
    if (!conn.isClosed()){
      logger.error("时间：" + new Date(System.currentTimeMillis()).toString() 
        + "检测未关闭的连接：",stack);
    }
  }

  public void clearWarnings() throws SQLException {
    conn.clearWarnings();
  }

  public void close() throws SQLException {
    conn.close();
  }

  public void commit() throws SQLException {
    conn.commit();
  }

  public Statement createStatement() throws SQLException {
    return new StatementWrapper(conn.createStatement());
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new StatementWrapper(conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    return new StatementWrapper(conn.createStatement(resultSetType, resultSetConcurrency));
  }

  public boolean getAutoCommit() throws SQLException {
    return conn.getAutoCommit();
  }

  public String getCatalog() throws SQLException {
    return conn.getCatalog();
  }

  public int getHoldability() throws SQLException {
    return conn.getHoldability();
  }

  public DatabaseMetaData getMetaData() throws SQLException {
    return conn.getMetaData();
  }

  public int getTransactionIsolation() throws SQLException {
    return conn.getTransactionIsolation();
  }

  public Map getTypeMap() throws SQLException {
    return conn.getTypeMap();
  }

  public SQLWarning getWarnings() throws SQLException {
    return conn.getWarnings();
  }

  public boolean isClosed() throws SQLException {
    return conn.isClosed();
  }

  public boolean isReadOnly() throws SQLException {
    return conn.isReadOnly();
  }

  public String nativeSQL(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
    return conn.nativeSQL(sql);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new CallableStatementWrapper(
      conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new CallableStatementWrapper(
      conn.prepareCall(sql, resultSetType, resultSetConcurrency));
  }

  public CallableStatement prepareCall(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new CallableStatementWrapper(conn.prepareCall(sql));
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
  }

  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql, autoGeneratedKeys));
  }

  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql, columnIndexes));
  }

  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql, columnNames));
  }

  public PreparedStatement prepareStatement(String sql) throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug(sql);
		}
  	return new PreparedStatementWrapper(conn.prepareStatement(sql));
  }

  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    conn.releaseSavepoint(savepoint);
  }

  public void rollback() throws SQLException {
    conn.rollback();
  }

  public void rollback(Savepoint savepoint) throws SQLException {
    conn.rollback(savepoint);
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException {
    conn.setAutoCommit(autoCommit);
  }

  public void setCatalog(String catalog) throws SQLException {
    conn.setCatalog(catalog);
  }

  public void setHoldability(int holdability) throws SQLException {
    conn.setHoldability(holdability);
  }

  public void setReadOnly(boolean readOnly) throws SQLException {
    conn.setReadOnly(readOnly);
  }

  public Savepoint setSavepoint() throws SQLException {
    return conn.setSavepoint();
  }

  public Savepoint setSavepoint(String name) throws SQLException {
    return conn.setSavepoint(name);
  }

  public void setTransactionIsolation(int level) throws SQLException {
    conn.setTransactionIsolation(level);
  }

  public void setTypeMap(Map arg0) throws SQLException {
    conn.setTypeMap(arg0);
  }

}
