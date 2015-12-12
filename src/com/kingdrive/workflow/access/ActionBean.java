package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.ActionModel;
import com.kingdrive.workflow.util.StringUtil;

public class ActionBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(ActionBean.class);

  public ActionBean() {
  }

  public void delete(int actionId) throws SQLException {
    String sql = "delete from WF_ACTION where ACTION_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, actionId);
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
    String sql = "select ACTION_ID, INSTANCE_ID, NODE_ID, ACTION_NAME, EXECUTOR, EXECUTE_TIME, DESCRIPTION, OWNER, LIMIT_EXECUTE_TIME from WF_ACTION";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      rs = st.executeQuery();
      if (theBegin > 1)
        rs.absolute(theBegin - 1);
      while (rs.next()) {
        list.add(parseResultSet(rs));
        if (rs.getRow() == theEnd)
          break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ActionModel findByKey(int actionId)
      throws SQLException {
    ActionModel model = new ActionModel();
    String sql = "select ACTION_ID, INSTANCE_ID, NODE_ID, ACTION_NAME, EXECUTOR, EXECUTE_TIME, DESCRIPTION, OWNER, LIMIT_EXECUTE_TIME from WF_ACTION where ACTION_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, actionId);
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

  public int insert(ActionModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getActionIdModifyFlag()) {
      StringUtil.makeDynaParam("ACTION_ID", model.getActionId(), strList,valList);
       }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,valList);
    }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID",model.getNodeId(), strList,valList);
    }
    if (model.getActionNameModifyFlag()) {
      StringUtil.makeDynaParam("ACTION_NAME",model.getActionName(), strList,valList);
    }
    if (model.getExecutorModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR",convertSQL(model.getExecutor()), strList,valList);
    }
    if (model.getExecuteTimeModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTE_TIME",convertSQL(model.getExecuteTime()), strList,valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION",convertSQL(model.getDescription()), strList,valList);
    }
    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER",convertSQL(model.getOwner()), strList,valList);
    }
    if (model.getLimitExecuteTimeModifyFlag()) {
      StringUtil.makeDynaParam("LIMIT_EXECUTE_TIME",convertSQL(model.getLimitExecuteTime()), strList,valList);
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
    sql = "insert into WF_ACTION(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(ActionModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID=", model.getInstanceId(), strList,valList);
        }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID=", model.getNodeId(), strList,valList);
    }
    if (model.getActionNameModifyFlag()) {
      StringUtil.makeDynaParam("ACTION_NAME =", convertSQL(model.getActionName()), strList,valList);
    }
    if (model.getExecutorModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR = ", convertSQL(model.getExecutor()), strList,valList);
    }
    if (model.getExecuteTimeModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTE_TIME = ", convertSQL(model.getExecuteTime()), strList,valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()), strList,valList);
    }
    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER = ",convertSQL(model.getOwner()), strList,valList);     
    }
    if (model.getLimitExecuteTimeModifyFlag()) {
      StringUtil.makeDynaParam("LIMIT_EXECUTE_TIME = ",convertSQL(model.getLimitExecuteTime()), strList,valList);
    }
    if (strList.size() == 0) return 0;
    valList.add(model.getActionId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_ACTION set " + updateString + " where ACTION_ID=?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getActionByNode(int instanceId, int nodeId)
      throws SQLException {
    return getActionByNode(-1, -1, instanceId, nodeId);
  }

  public ArrayList getActionByNode(int theBegin, int theEnd,
      int instanceId, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_action where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      rs = st.executeQuery();
      if (theBegin > 1)
        rs.absolute(theBegin - 1);
      while (rs.next()) {
        list.add(parseResultSet(rs));
        if (rs.getRow() == theEnd)
          break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public void removeByInstance(int instanceId)
      throws SQLException {
    String sql = "delete from wf_action where instance_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByNode(int instanceId, int nodeId)
      throws SQLException {
    String sql = "delete from wf_action where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      int index = 1;
      st.setInt(index++, instanceId);
      st.setInt(index++, nodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }
  private ActionModel parseResultSet(ResultSet rs) throws SQLException {
    ActionModel model = new ActionModel();
    try {
      model.setActionId(rs.getInt("ACTION_ID"));
      if (rs.wasNull())
        model.setActionId(null);
    } catch (Exception e) {
      model.setActionId(null);
    }

    try {
      model.setInstanceId(rs.getInt("INSTANCE_ID"));
      if (rs.wasNull())
        model.setInstanceId(null);
    } catch (Exception e) {
      model.setInstanceId(null);
    }

    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setActionName(rs.getString("ACTION_NAME"));
      if (rs.wasNull())
        model.setActionName(null);
    } catch (Exception e) {
      model.setActionName(null);
    }

    try {
      model.setExecutor(rs.getString("EXECUTOR"));
      if (rs.wasNull())
        model.setExecutor(null);
    } catch (Exception e) {
      model.setExecutor(null);
    }

    try {
      model.setExecuteTime(rs.getString("EXECUTE_TIME"));
      if (rs.wasNull())
        model.setExecuteTime(null);
    } catch (Exception e) {
      model.setExecuteTime(null);
    }

    try {
      model.setDescription(rs.getString("DESCRIPTION"));
      if (rs.wasNull())
        model.setDescription(null);
    } catch (Exception e) {
      model.setDescription(null);
    }

    try {
      model.setOwner(rs.getString("OWNER"));
      if (rs.wasNull())
        model.setOwner(null);
    } catch (Exception e) {
      model.setOwner(null);
    }

    try {
      model.setLimitExecuteTime(rs.getString("LIMIT_EXECUTE_TIME"));
      if (rs.wasNull())
        model.setLimitExecuteTime(null);
    } catch (Exception e) {
      model.setLimitExecuteTime(null);
    }

    return model;
  }

  private String convertSQL(String input) {
    String temp1, temp2;
    int tempIndex = 0, curIndex = 0;
    temp1 = input;
    while (true) {
      curIndex = temp1.indexOf('\'', tempIndex);
      if (curIndex == -1)
        break;
      temp2 = temp1;
      temp1 = temp2.substring(0, curIndex) + "'" + temp2.substring(curIndex);
      tempIndex = curIndex + 2;
    }
    return temp1;
  }
}