package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class TaskExecutorBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(TaskExecutorBean.class);

  public TaskExecutorBean() {
  }

  public void delete(int taskExecutorOrderId)
      throws SQLException {
    String sql = "delete from WF_TASK_EXECUTOR where TASK_EXECUTOR_ORDER_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, taskExecutorOrderId);
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
    String sql = "select TASK_EXECUTOR_ORDER_ID, INSTANCE_ID, NODE_ID, EXECUTOR, EXECUTOR_ORDER, RESPONSIBILITY from WF_TASK_EXECUTOR";
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

  public TaskExecutorModel findByKey(int taskExecutorOrderId)
      throws SQLException {
    TaskExecutorModel model = new TaskExecutorModel();
    String sql = "select TASK_EXECUTOR_ORDER_ID, INSTANCE_ID, NODE_ID, EXECUTOR, EXECUTOR_ORDER, RESPONSIBILITY from WF_TASK_EXECUTOR where TASK_EXECUTOR_ORDER_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, taskExecutorOrderId);
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

  public int insert(TaskExecutorModel model)
      throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getTaskExecutorOrderIdModifyFlag()) {
      StringUtil.makeDynaParam("TASK_EXECUTOR_ORDER_ID",model.getTaskExecutorOrderId(), strList,valList);
          }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID",model.getInstanceId(), strList,valList);
          }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID",model.getNodeId(), strList,valList);
          }
    if (model.getExecutorModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR",convertSQL(model.getExecutor()), strList,valList);
          }
    if (model.getExecutorOrderModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR_ORDER",model.getExecutorOrder(), strList,valList);
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
    sql = "insert into WF_TASK_EXECUTOR(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(TaskExecutorModel model)
      throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID = ", model.getInstanceId(), strList,valList);
         }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID = ",model.getNodeId(), strList,valList);
          }
    if (model.getExecutorModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR = ",convertSQL(model.getExecutor()), strList,valList);
          }
    if (model.getExecutorOrderModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR_ORDER = ",model.getExecutorOrder(), strList,valList);
          }
    if (model.getResponsibilityModifyFlag()) {
      StringUtil.makeDynaParam("RESPONSIBILITY = ",model.getResponsibility(), strList,valList);
           }
    if (strList.size() == 0) return 0;
    valList.add(model.getTaskExecutorOrderId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_TASK_EXECUTOR set " + updateString + " where TASK_EXECUTOR_ORDER_ID=?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByExecutor(int instanceId, int nodeId,
      String executor) throws SQLException {
    String sql = "delete from wf_task_executor where instance_id = ? and node_id = ? and executor = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      st.setString(3, executor);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByInstance(int instanceId)
      throws SQLException {
    String sql = "delete from wf_task_executor where instance_id = ?";
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

  public void updateOrder(int executorOrder, int id)
      throws SQLException {
    String sql = "update wf_task_executor set executor_order = ? where task_executor_order_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, executorOrder);
      st.setInt(2, id);
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
    String sql = "delete from wf_task_executor where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void resetOrderByExecutor(int instanceId, int nodeId,
      String executor) throws SQLException {
    String sql = "update wf_task_executor current_order set executor_order = executor_order -1 where current_order.instance_id = ? and current_order.node_id = ? and current_order.executor_order > (select max(executor_order) from wf_task_executor previous_order where previous_order.instance_id  = current_order.instance_id and previous_order.node_id  = current_order.node_id and previous_order.executor = ?)";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      st.setString(3, executor);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  private TaskExecutorModel parseResultSet(ResultSet rs) throws SQLException {
    TaskExecutorModel model = new TaskExecutorModel();
    try {
      model.setTaskExecutorOrderId(rs.getInt("TASK_EXECUTOR_ORDER_ID"));
      if (rs.wasNull())
        model.setTaskExecutorOrderId(null);
    } catch (Exception e) {
      model.setTaskExecutorOrderId(null);
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
      model.setExecutor(rs.getString("EXECUTOR"));
      if (rs.wasNull())
        model.setExecutor(null);
    } catch (Exception e) {
      model.setExecutor(null);
    }

    try {
      model.setExecutorOrder(rs.getInt("EXECUTOR_ORDER"));
      if (rs.wasNull())
        model.setExecutorOrder(null);
    } catch (Exception e) {
      model.setExecutorOrder(null);
    }

    try {
      model.setResponsibility(rs.getInt("RESPONSIBILITY"));
      if (rs.wasNull())
        model.setResponsibility(null);
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
      if (curIndex == -1)
        break;
      temp2 = temp1;
      temp1 = temp2.substring(0, curIndex) + "'" + temp2.substring(curIndex);
      tempIndex = curIndex + 2;
    }
    return temp1;
  }
}