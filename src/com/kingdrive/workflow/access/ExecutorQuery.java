package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.ExecutorInfo;

public class ExecutorQuery {

  public ExecutorQuery() {
  }

  public ArrayList getExecutorListBySource(int nodeId)
      throws SQLException {
    return getExecutorListBySource(-1, -1, nodeId);
  }

  public ArrayList getExecutorListBySource(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    /* v_wf_executor_source1可以获取到所有主办人和辅办人，并过滤掉重复的执行人，
     * 更详细请参考试图v_wf_executor_source1 */
    String sql = "select distinct * from v_wf_executor_source1 " 
      + "where node_id = ? order by responsibility desc, executor";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
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

  public ArrayList getExecutorListByOrder(int nodeId)
      throws SQLException {
    return getExecutorListByOrder(-1, -1, nodeId);
  }

  public ArrayList getExecutorListByOrder(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name executor_name from wf_executor_order a, wf_staff b where a.executor = b.staff_id and a.node_id = ? order by executor_order, responsibility desc,  executor";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
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

  private ExecutorInfo parseResultSet(ResultSet rs) throws SQLException {
    ExecutorInfo model = new ExecutorInfo();
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
      model.setResponsibility(rs.getInt("RESPONSIBILITY"));
      if (rs.wasNull())
        model.setResponsibility(null);
    } catch (Exception e) {
      model.setResponsibility(null);
    }

    return model;
  }
}
