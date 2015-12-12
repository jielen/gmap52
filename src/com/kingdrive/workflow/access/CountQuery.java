package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.CountInfo;

public class CountQuery {

  public CountQuery() {
  }

  public CountInfo getActiveInstanceNumByTemplate(int templateId) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) count from wf_instance a, wf_template b where a.template_id = b.template_id and a.template_id = ? and a.status = 1";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
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

  public CountInfo getInstanceNumByTemplate(int templateId)
      throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) count from wf_instance a, wf_template b where a.template_id = b.template_id and a.template_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
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

  public CountInfo getActionNumByNode(int instanceId,
      int nodeId) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_action where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
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

  public CountInfo getTaskNumByNode(int instanceId, int nodeId)
      throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_current_task where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
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
  public CountInfo getMainTaskNumByNode(int instanceId, int nodeId)
	  throws SQLException {
	CountInfo model = new CountInfo();
	String sql = "select count(*) as count from wf_current_task where instance_id = ? and node_id = ?" 
		+" and RESPONSIBILITY = 1";//限于主办任务
	PreparedStatement st = null;
	ResultSet rs = null;
  Connection conn = null;
  try {
  	conn = ConnectionFactory.getConnection();
	  st = conn.prepareStatement(sql);
	  st.setInt(1, instanceId);
	  st.setInt(2, nodeId);
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
  public CountInfo getPassNum(int instanceId, int currentNodeId)
      throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_pass where instance_id = ? and current_node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, currentNodeId);
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

  public CountInfo getPassNum(int instanceId,
      int currentNodeId, int nextNodeId) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_pass where instance_id = ? and current_node_id = ? and next_node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, currentNodeId);
      st.setInt(3, nextNodeId);
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

  public CountInfo getTaskExecutorNum(int instanceId,
      int nodeId) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_task_executor where instance_id = ? and node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
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

  public CountInfo getMainTaskExecutorNum(int instanceId,
      int nodeId) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_task_executor where instance_id = ? and node_id = ? and responsibility = 1";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
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

  public CountInfo getSameOrderExecutorNum(int nodeId,
      String executor) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_executor_order count_order where count_order.node_id = ? and count_order.executor_order = (select max(executor_order) from wf_executor_order executor_order where executor_order.node_id = count_order.node_id and executor_order.executor = ?)";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
      st.setString(2, executor);
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

  public CountInfo getSameOrderExecutorNum(int instanceId,
      int nodeId, String executor) throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select count(*) as count from wf_task_executor count_order where count_order.instance_id = ? and count_order.node_id = ? and count_order.executor_order = (select executor_order from wf_task_executor executor_order where executor_order.instance_id = count_order.instance_id and executor_order.node_id = count_order.node_id and executor_order.executor = ?)";
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

  public CountInfo getMaxExecutorOrder(int nodeId)
      throws SQLException {
    CountInfo model = new CountInfo();
    String sql = "select max(executor_order) as count from wf_executor_order where node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
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

  private CountInfo parseResultSet(ResultSet rs) throws SQLException {
    CountInfo model = new CountInfo();
    try {
      model.setCount(rs.getInt("COUNT"));
      if (rs.wasNull())
        model.setCount(null);
    } catch (Exception e) {
      model.setCount(null);
    }

    return model;
  }
}
