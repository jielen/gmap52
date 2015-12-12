package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.TaskExecutorInfo;

public class TaskExecutorQuery {

  public TaskExecutorQuery() {
  }

  public TaskExecutorInfo getExecutor(int instanceId,
      int nodeId, String executor) throws SQLException {
    TaskExecutorInfo model = new TaskExecutorInfo();
    String sql = "select a.*, b.name node_name, b.executors_method, c.name staff_name, d.name instance_name from wf_task_executor a, wf_NODE b, wf_STAFF c, wf_INSTANCE d where a.node_id = b.node_id and a.executor = c.staff_id and a.instance_id = d.instance_id and a.instance_id = ? and a.node_id = ? and a.executor = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      st.setString(3, executor);
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

  public ArrayList getExecutorList(int instanceId, int nodeId)
      throws SQLException {
    return getExecutorList(-1, -1, instanceId, nodeId);
  }

  public ArrayList getExecutorList(int theBegin, int theEnd,
      int instanceId, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name node_name, b.executors_method, c.name staff_name, d.name instance_name from wf_task_executor a, wf_node b, wf_staff c, wf_instance d where a.node_id = b.node_id and a.executor = c.staff_id and a.instance_id = d.instance_id and a.instance_id = ? and a.node_id = ? order by a.executor_order";
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

  public ArrayList getExecutorList(int instanceId, int nodeId,
      String realOwner) throws SQLException {
    return getExecutorList(-1, -1, instanceId, nodeId, realOwner);
  }

  public ArrayList getExecutorList(int theBegin, int theEnd,
      int instanceId, int nodeId, String realOwner) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name node_name, b.executors_method, c.name staff_name, d.name instance_name from wf_task_executor a, wf_node b, wf_staff c, wf_instance d where a.node_id = b.node_id and a.executor = c.staff_id and a.instance_id = d.instance_id and a.instance_id = ? and a.node_id = ? and a.executor != ? order by a.executor_order";
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
      st.setString(3, realOwner);
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

  public ArrayList getForemostExecutorList(int instanceId,
      int nodeId) throws SQLException {
    return getForemostExecutorList(-1, -1, instanceId, nodeId);
  }

  /**
   * 获取某实例某节点上task_executor_order为1的执行者，为顺序签时调用。
   * 
   * @param conn
   * @param theBegin
   * @param theEnd
   * @param instanceId
   * @param nodeId
   * @return
   * @throws SQLException
   */
  public ArrayList getForemostExecutorList(int theBegin,
      int theEnd, int instanceId, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name node_name, b.executors_method, c.name staff_name, d.name instance_name from wf_task_executor a, wf_node b, wf_staff c, wf_instance d where a.node_id = b.node_id and a.executor = c.staff_id and a.instance_id = d.instance_id and a.instance_id = ? and a.node_id = ? and a.executor_order = 1";
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

  public ArrayList getFollowedExecutorList(int instanceId,
      int nodeId, String executor) throws SQLException {
    return getFollowedExecutorList(-1, -1, instanceId, nodeId, executor);
  }

  public ArrayList getFollowedExecutorList(int theBegin,
      int theEnd, int instanceId, int nodeId, String executor)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name node_name, b.executors_method, c.name EXECUTOR_NAME, d.name instance_name from wf_task_executor a, wf_node b, wf_staff c, wf_instance d, wf_task_executor e where a.node_id = b.node_id and a.executor = c.staff_id and a.instance_id = d.instance_id and a.instance_id = e.instance_id and a.node_id = e.node_id and a.executor_order = e.executor_order + 1 and e.instance_id = ? and e.node_id = ? and e.executor = ?";
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
      st.setString(3, executor);
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

  private TaskExecutorInfo parseResultSet(ResultSet rs) throws SQLException {
    TaskExecutorInfo model = new TaskExecutorInfo();
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
      model.setInstanceName(rs.getString("INSTANCE_NAME"));
      if (rs.wasNull())
        model.setInstanceName(null);
    } catch (Exception e) {
      model.setInstanceName(null);
    }

    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setNodeName(rs.getString("NODE_NAME"));
      if (rs.wasNull())
        model.setNodeName(null);
    } catch (Exception e) {
      model.setNodeName(null);
    }

    try {
      model.setExecutor(rs.getString("EXECUTOR"));
      if (rs.wasNull())
        model.setExecutor(null);
    } catch (Exception e) {
      model.setExecutor(null);
    }

    try {
      model.setExecutorName(rs.getString("EXECUTOR_NAME"));
      if (rs.wasNull())
        model.setExecutorName(null);
    } catch (Exception e) {
      model.setExecutorName(null);
    }

    try {
      model.setExecutorOrder(rs.getInt("EXECUTOR_ORDER"));
      if (rs.wasNull())
        model.setExecutorOrder(null);
    } catch (Exception e) {
      model.setExecutorOrder(null);
    }

    try {
      model.setExecutorsMethod(rs.getString("EXECUTORS_METHOD"));
      if (rs.wasNull())
        model.setExecutorsMethod(null);
    } catch (Exception e) {
      model.setExecutorsMethod(null);
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
}
