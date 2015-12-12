package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.*;

public class ExecutorSourceBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(ExecutorSourceBean.class);

  public ExecutorSourceBean() {
  }

  public void delete(int nodeId, String executor, int source)
      throws SQLException {
    String sql = "delete from WF_EXECUTOR_SOURCE where NODE_ID=? and EXECUTOR=? and SOURCE=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      st.setString(2, executor);
      st.setInt(3, source);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn,st,null);
    }
  }

  public ArrayList find() throws SQLException {
    return find(-1, -1);
  }

  public ArrayList find(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select NODE_ID, EXECUTOR, SOURCE, RESPONSIBILITY from WF_EXECUTOR_SOURCE";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0) {
        st.setFetchSize(theEnd);
      }

      rs = st.executeQuery();
      if (theBegin > 1) {
        rs.absolute(theBegin - 1);
      }
      while (rs.next()) {
        list.add(parseResultSet(rs));
        if (rs.getRow() == theEnd) {
          break;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ExecutorSourceModel findByKey(int nodeId,
      String executor, int source) throws SQLException {
    ExecutorSourceModel model = new ExecutorSourceModel();
    String sql = "select NODE_ID, EXECUTOR, SOURCE, RESPONSIBILITY from WF_EXECUTOR_SOURCE where NODE_ID=? and EXECUTOR=? and SOURCE=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      st.setString(2, executor);
      st.setInt(3, source);
      rs = st.executeQuery();
      if (rs.next()) {
        model = parseResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return model;
  }

  public int insert(ExecutorSourceModel model)
      throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID",  model.getNodeId(), strList,valList);
      }
    if (model.getExecutorModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR",  convertSQL(model.getExecutor()), strList,valList);
      }
    if (model.getSourceModifyFlag()) {
      StringUtil.makeDynaParam("SOURCE",  model.getSource(), strList,valList);
       }
    if (model.getResponsibilityModifyFlag()) {
      StringUtil.makeDynaParam("RESPONSIBILITY",model.getResponsibility(), strList,valList);
       }
    String insertString = "";
    String valsString = "";
    int length = strList.size();
    if (length == 0) {
      insertString = null;
      valsString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        insertString += strList.get(i) + ",";
        valsString += "?,";
      }
      insertString = insertString.substring(0, insertString.length() - 1);
      valsString = valsString.substring(0, valsString.length() - 1);
    }
    sql = "insert into WF_EXECUTOR_SOURCE(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(ExecutorSourceModel model)
      throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getResponsibilityModifyFlag()) {
      StringUtil.makeDynaParam("RESPONSIBILITY = ",model.getResponsibility(), strList,valList);
       }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getNodeId());
    valList.add(convertSQL(model.getExecutor()));
    valList.add(model.getSource());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_EXECUTOR_SOURCE set " + updateString + " where NODE_ID=? and EXECUTOR = ? and SOURCE = ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getSourceListByNode(int nodeId)
      throws SQLException {
    return getSourceListByNode(-1, -1, nodeId);
  }

  public ArrayList getSourceListByNode(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_executor_source where node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0) {
        st.setFetchSize(theEnd);
      }
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      rs = st.executeQuery();
      if (theBegin > 1) {
        rs.absolute(theBegin - 1);
      }
      while (rs.next()) {
        list.add(parseResultSet(rs));
        if (rs.getRow() == theEnd) {
          break;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public void removeByTemplate(int templateId)
      throws SQLException {
    String sql = "select a.NODE_ID,a.EXECUTOR,a.SOURCE from wf_executor_source a, wf_node b where a.node_id = b.node_id and b.template_id = ?";
    List resList = new ArrayList();
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      List delList = DBHelper.queryToList(conn,sql,new String[]{templateId+ ""});
      for (int i = 0; i < resList.size(); i++) {
        Map record = (Map) resList.get(i);
        resList.add(new Object[]{record.get("SOURCE"),record.get("NODE_ID"),record.get("EXECUTOR")});
      }
      for (int i = 0; i < delList.size(); i++) {
        String delSql = "delete from WF_EXECUTOR_SOURCE where NODE_ID= ? and EXECUTOR=? and SOURCE=?";
        DBHelper.executeUpdate(conn, delSql, (Object[])delList.get(i));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,null,null);
    }
  }

  public void removeByNode(int nodeId) throws SQLException {
    String sql = "delete from wf_executor_source where node_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void remove(int nodeId, int source, int responsibility)
      throws SQLException {
    String sql = "delete from wf_executor_source where node_id = ? and source = ? and responsibility = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      st.setInt(2, source);
      st.setInt(3, responsibility);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  private ExecutorSourceModel parseResultSet(ResultSet rs) throws SQLException {
    ExecutorSourceModel model = new ExecutorSourceModel();
    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull()) {
        model.setNodeId(null);
      }
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setExecutor(rs.getString("EXECUTOR"));
      if (rs.wasNull()) {
        model.setExecutor(null);
      }
    } catch (Exception e) {
      model.setExecutor(null);
    }

    try {
      model.setSource(rs.getInt("SOURCE"));
      if (rs.wasNull()) {
        model.setSource(null);
      }
    } catch (Exception e) {
      model.setSource(null);
    }

    try {
      model.setResponsibility(rs.getInt("RESPONSIBILITY"));
      if (rs.wasNull()) {
        model.setResponsibility(null);
      }
    } catch (Exception e) {
      model.setResponsibility(null);
    }

    return model;
  }

  private String convertSQL(String input) {
    String temp1, temp2;
    int tempIndex = 0, curIndex = 0;
    temp1 = input;
    while (true) {
      curIndex = temp1.indexOf('\'', tempIndex);
      if (curIndex == -1) {
        break;
      }
      temp2 = temp1;
      temp1 = temp2.substring(0, curIndex) + "'" + temp2.substring(curIndex);
      tempIndex = curIndex + 2;
    }
    return temp1;
  }
}
