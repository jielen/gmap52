// $Id: DBHelper.java,v 1.3 2008/03/12 08:36:33 liubo Exp $

package com.kingdrive.workflow.db;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** 与数据库操作相关的一些辅助方法 */
public class DBHelper {

   private static final Logger logger = Logger.getLogger(DBHelper.class);
  /**
   * 返回数据集的最大行数，被 resultSetToList, queryToList 用到
   *
   * @see #resultSetToList
   * @see #queryToList
   */
  public static int MAX_ROW_COUNT = 10000; // _hd

  public static int executeUpdate(String sql, Object[] params) throws SQLException {
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      return executeUpdate(conn,sql,params);
    } catch (SQLException e) {
      throw e;
    } finally {
      closeConnection(conn);
    }
  }
  
  public static int executeUpdate(Connection conn, String sql, Object[] params)
    throws SQLException {
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(sql);
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);
      }
      return stmt.executeUpdate();
    } finally {
      closeConnection(null, stmt, null);
    }
  }
  public static void setStatementParameters(PreparedStatement pst, Object[] params)
  throws SQLException {
  for (int i = 0; i < params.length; ++i) {
    Object obj = params[i];
    if (null == obj) {
      pst.setNull(i + 1, Types.CHAR); 
    } 
    else if(obj instanceof java.sql.Date){
      pst.setDate(i + 1, (java.sql.Date)obj);
    }
    else if(obj instanceof java.sql.Timestamp){
      pst.setTimestamp(i + 1, (java.sql.Timestamp)obj);
    }
    else{
      pst.setObject(i + 1, obj);
    }
  }
}
  
  public static void closeConnection(Connection conn) {
    try {
      if (null != conn && !conn.isClosed()) {
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
    try {
      if (null != rs) {
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      if (null != stmt) {
        stmt.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      if (null != conn) {
        if (!conn.isClosed())
          conn.close();
      }
    } catch (SQLException e) {

      e.printStackTrace();
    }
  }
  
  public static List queryToList(Connection conn, String sql, Object[] params) {
    List resList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.prepareStatement(sql);
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);
      }
      rs = stmt.executeQuery();
      while(rs.next()) {
        Map result = new HashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; ++i) {
          result.put(rsmd.getColumnName(i), rs.getString(i));
        }
        resList.add(result);
        return resList;
      }
    } catch (SQLException e) {
      logger.error(sql, e);
      throw new RuntimeException(e);
    } finally {
      closeConnection(null, stmt, rs);
    }
    return null;    
  }
  
  public static Object queryOneValue(String sql, Object[] params){
    Connection conn = null;
    try{
      conn = ConnectionFactory.getConnection();
      return queryOneValue(conn, sql, params);
    }catch(SQLException e){
      logger.error(e);
      throw new RuntimeException(e);
    }finally{
      closeConnection(conn);
    }
  }

  /**
   * 执行指定的查询并返回结果的第一个字段值
   * 
   * @param conn
   * @param sql
   * @param params
   * @return
   */
  public static Object queryOneValue(Connection conn, String sql, Object[] params) throws SQLException{
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try{
      stmt = conn.prepareStatement(sql);
      if(null != params && params.length > 0){
        setStatementParameters(stmt, params);
      }
      rs = stmt.executeQuery();
      if(rs.next()){
        return rs.getObject(1);
      }
    }finally{
      closeConnection(null, stmt, rs);
    }
    return null;
  }
  
}
