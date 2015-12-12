package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.CurrentTaskInfo;

public class CurrentTaskQuery {

  public CurrentTaskQuery() {
  }

  public CurrentTaskInfo getCurrentTask(int currentTaskId, int isValid)
    throws SQLException {
    CurrentTaskInfo model = new CurrentTaskInfo();
    String validSql = "select * from v_wf_current_task";
    String invalidSql = "select * from v_wf_current_task_invalid";
    String condition = " where current_task_id = ?";
    String sql = null;
    if (isValid == -1) {
      sql = invalidSql;
    } else if (isValid == 0) {
      sql = validSql + condition + " union " + invalidSql;
    } else {//==1
      sql = validSql;
    }
    sql += condition;
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, currentTaskId);
      if (isValid == 0) {
        st.setInt(2, currentTaskId);
      }
      rs = st.executeQuery();
      if (rs.next()) {
        model = parseResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return model;
  }

  public ArrayList getTodoList() throws SQLException {
    return getTodoList(-1, -1);
  }

  public ArrayList getTodoList(int theBegin, int theEnd) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task";
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByTemplate(String templateType) throws SQLException {
    return getTodoListByTemplate(-1, -1, templateType);
  }

  public ArrayList getTodoListByTemplate(int theBegin, int theEnd,
    String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where template_type like ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, templateType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByTemplate(int templateId) throws SQLException {
    return getTodoListByTemplate(-1, -1, templateId);
  }

  public ArrayList getTodoListByTemplate(int theBegin, int theEnd, int templateId)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where template_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByInstance(int instanceId, int isValid)
    throws SQLException {
    return getTodoListByInstance(-1, -1, instanceId, isValid);
  }

  public ArrayList getTodoListByInstance(int theBegin, int theEnd, int instanceId,
    int isValid) throws SQLException {
    ArrayList list = new ArrayList();
    String validSql = "select * from v_wf_current_task";
    String invalidSql = "select * from v_wf_current_task_invalid";
    String condition = " where instance_id =?";
    String sql = null;
    if (isValid == -1) {
      sql = invalidSql;
    } else if (isValid == 0) {
      sql = validSql + condition + " union " + invalidSql;
    } else {//==1
      sql = validSql;
    }
    sql += condition;
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, instanceId);
      if (isValid == 0) {
        st.setInt(2, instanceId);
      }
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public List getTodoListByUserCompo(String userId, String compoId)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner ='" + userId + "'";
    if (null != compoId && !("".equals(compoId))) {
      sql += " and compo_id ='" + compoId + "'";
    }
    sql += " group by compo_id";
    Statement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      while (rs.next()) {
        list.add(parseResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;

  }

  public ArrayList getTodoListByNode(int instanceId, int nodeId) throws SQLException {
    return getTodoListByNode(-1, -1, instanceId, nodeId);
  }

  public ArrayList getTodoListByNode(int theBegin, int theEnd, int instanceId,
    int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where node_id =? and instance_id=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
      st.setInt(2, instanceId);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByNode(String businessType) throws SQLException {
    return getTodoListByNode(-1, -1, businessType);
  }

  public ArrayList getTodoListByNode(int theBegin, int theEnd, String businessType)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where business_type =?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, businessType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutor(String executor) throws SQLException {
    return getTodoListByExecutor(-1, -1, executor);
  }

  public ArrayList getTodoListByExecutor(int theBegin, int theEnd, String executor)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where executor = ? order by compo_id";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  /**
   * 取得executor的待办的部件列表
   *
   * @param conn
   * @param executor
   * @return
   * @throws SQLException
   */
  public ArrayList getTodoCompoListByExecutor(String executor) throws SQLException {
    return getTodoCompoListByExecutor(-1, -1, executor);
  }

  public ArrayList getTodoCompoListByExecutor(int theBegin, int theEnd,
    String executor) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select distinct COMPO_ID from v_wf_current_task where executor = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      rs = st.executeQuery();
      if (theBegin > 1)
        rs.absolute(theBegin - 1);
      while (rs.next()) {
        list.add(rs.getString("COMPO_ID"));
        System.out.println("CurrentTaskQuery.getTodoCompoListByExecutor():"+rs.getString("COMPO_ID"));
        if (rs.getRow() == theEnd)
          break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }
System.out.println("CurrentTaskQuery.getTodoCompoListByExecutor():"+sql+" "+executor);
    return list;
  }

  public ArrayList getTodoInstListByExecutor(String executor) throws SQLException {
    return getTodoInstListByExecutor(-1, -1, executor, true);
  }

  public ArrayList getInvalidTodoInstListByExecutor(String executor)
    throws SQLException {
    return getTodoInstListByExecutor(-1, -1, executor, false);
  }

  public ArrayList getTodoInstListByExecutor(int theBegin, int theEnd,
    String executor, boolean needTodo) throws SQLException {
    ArrayList list = new ArrayList();
    String sql;
    if (needTodo) {
      sql = "select instance_id from v_wf_current_task"
        + " where executor = ? order by compo_id";
    } else {
      sql = "select instance_id from v_wf_current_task_invalid "
        + " where executor = ? order by compo_id";
    }
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      rs = st.executeQuery();
      if (theBegin > 1)
        rs.absolute(theBegin - 1);
      while (rs.next()) {
        list.add(rs.getString("instance_id"));
        if (rs.getRow() == theEnd)
          break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutorAndTemplate(String executor,
    String templateType) throws SQLException {
    return getTodoListByExecutorAndTemplate(-1, -1, executor, templateType);
  }

  public ArrayList getTodoListByExecutorAndTemplate(int theBegin, int theEnd,
    String executor, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where executor = ? and template_type like ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setString(2, templateType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutorAndTemplate(String executor, int templateId)
    throws SQLException {
    return getTodoListByExecutorAndTemplate(-1, -1, executor, templateId);
  }

  public ArrayList getTodoListByExecutorAndTemplate(int theBegin, int theEnd,
    String executor, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where executor = ? and template_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setInt(2, templateId);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutorAndInstance(String executor, int instanceId,
    int isValid) throws SQLException {
    return getTodoListByExecutorAndInstance(-1, -1, executor, instanceId, isValid);
  }

  public ArrayList getTodoListByExecutorAndInstance(int theBegin, int theEnd,
    String executor, int instanceId, int isValid) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = null;
    String validSql = "select * from v_wf_current_task";
    String invalidSql = "select * from v_wf_current_task_invalid";
    String condition = " where executor = ? and instance_id = ?";
    if (isValid == -1) {
      sql = invalidSql;
    } else if (isValid == 0) {
      sql = validSql + condition + " union " + invalidSql;
    } else {
      sql = validSql;
    }
    sql += condition;
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setInt(2, instanceId);
      if (isValid == 0) {
        st.setString(3, executor);
        st.setInt(4, instanceId);
      }
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutorAndNode(String executor, String businessType)
    throws SQLException {
    return getTodoListByExecutorAndNode(-1, -1, executor, businessType);
  }

  public ArrayList getTodoListByExecutorAndNode(int theBegin, int theEnd,
    String executor, String businessType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where executor = ? and business_type =? ";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setString(2, businessType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByExecutorAndNode(String executor, int nodeId)
    throws SQLException {
    return getTodoListByExecutorAndNode(-1, -1, executor, nodeId);
  }

  public ArrayList getTodoListByExecutorAndNode(int theBegin, int theEnd,
    String executor, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where executor = ? and node_id =? ";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwner(String owner) throws SQLException {
    return getTodoListByOwner(-1, -1, owner);
  }

  public ArrayList getTodoListByOwner(int theBegin, int theEnd, String owner)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwnerAndTemplate(String owner, String templateType)
    throws SQLException {
    return getTodoListByOwnerAndTemplate(-1, -1, owner, templateType);
  }

  public ArrayList getTodoListByOwnerAndTemplate(int theBegin, int theEnd,
    String owner, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ? and template_type like ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
      st.setString(2, templateType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwnerAndTemplate(String owner, int templateId)
    throws SQLException {
    return getTodoListByOwnerAndTemplate(-1, -1, owner, templateId);
  }

  public ArrayList getTodoListByOwnerAndTemplate(int theBegin, int theEnd,
    String owner, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ? and template_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
      st.setInt(2, templateId);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwnerAndInstance(String owner, int instanceId)
    throws SQLException {
    return getTodoListByOwnerAndInstance(-1, -1, owner, instanceId);
  }

  public ArrayList getTodoListByOwnerAndInstance(int theBegin, int theEnd,
    String owner, int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ? and instance_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
      st.setInt(2, instanceId);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwnerAndNode(String owner, String businessType)
    throws SQLException {
    return getTodoListByOwnerAndNode(-1, -1, owner, businessType);
  }

  public ArrayList getTodoListByOwnerAndNode(int theBegin, int theEnd, String owner,
    String businessType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ? and business_type =? ";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
      st.setString(2, businessType);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public ArrayList getTodoListByOwnerAndNode(String owner, int nodeId)
    throws SQLException {
    return getTodoListByOwnerAndNode(-1, -1, owner, nodeId);
  }

  public ArrayList getTodoListByOwnerAndNode(int theBegin, int theEnd, String owner,
    int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_current_task where owner = ? and node_id =? ";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  private CurrentTaskInfo parseResultSet(ResultSet rs) throws SQLException {
    CurrentTaskInfo model = new CurrentTaskInfo();
    try {
      model.setCurrentTaskId(rs.getInt("CURRENT_TASK_ID"));
      if (rs.wasNull())
        model.setCurrentTaskId(null);
    } catch (Exception e) {
      model.setCurrentTaskId(null);
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
      model.setInstanceDescription(rs.getString("INSTANCE_DESCRIPTION"));
      if (rs.wasNull())
        model.setInstanceDescription(null);
    } catch (Exception e) {
      model.setInstanceDescription(null);
    }

    try {
      model.setInstanceStartTime(rs.getString("INSTANCE_START_TIME"));
      if (rs.wasNull())
        model.setInstanceStartTime(null);
    } catch (Exception e) {
      model.setInstanceStartTime(null);
    }

    try {
      model.setInstanceEndTime(rs.getString("INSTANCE_END_TIME"));
      if (rs.wasNull())
        model.setInstanceEndTime(null);
    } catch (Exception e) {
      model.setInstanceEndTime(null);
    }

    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    try {
      model.setTemplateName(rs.getString("TEMPLATE_NAME"));
      if (rs.wasNull())
        model.setTemplateName(null);
    } catch (Exception e) {
      model.setTemplateName(null);
    }

    try {
      model.setTemplateType(rs.getString("TEMPLATE_TYPE"));
      if (rs.wasNull())
        model.setTemplateType(null);
    } catch (Exception e) {
      model.setTemplateType(null);
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
      model.setBusinessType(rs.getString("BUSINESS_TYPE"));
      if (rs.wasNull())
        model.setBusinessType(null);
    } catch (Exception e) {
      model.setBusinessType(null);
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
      model.setDelegationId(rs.getInt("DELEGATION_ID"));
      if (rs.wasNull())
        model.setDelegationId(null);
    } catch (Exception e) {
      model.setDelegationId(null);
    }

    try {
      model.setOwner(rs.getString("OWNER"));
      if (rs.wasNull())
        model.setOwner(null);
    } catch (Exception e) {
      model.setOwner(null);
    }

    try {
      model.setOwnerName(rs.getString("OWNER_NAME"));
      if (rs.wasNull())
        model.setOwnerName(null);
    } catch (Exception e) {
      model.setOwnerName(null);
    }

    try {
      model.setCreator(rs.getString("CREATOR"));
      if (rs.wasNull())
        model.setCreator(null);
    } catch (Exception e) {
      model.setCreator(null);
    }

    try {
      model.setCreatorName(rs.getString("CREATOR_NAME"));
      if (rs.wasNull())
        model.setCreatorName(null);
    } catch (Exception e) {
      model.setCreatorName(null);
    }

    try {
      model.setCreateTime(rs.getString("CREATE_TIME"));
      if (rs.wasNull())
        model.setCreateTime(null);
    } catch (Exception e) {
      model.setCreateTime(null);
    }

    try {
      model.setLimitExecuteTime(rs.getString("LIMIT_EXECUTE_TIME"));
      if (rs.wasNull())
        model.setLimitExecuteTime(null);
    } catch (Exception e) {
      model.setLimitExecuteTime(null);
    }

    try {
      model.setResponsibility(rs.getInt("RESPONSIBILITY"));
      if (rs.wasNull())
        model.setResponsibility(null);
    } catch (Exception e) {
      model.setResponsibility(null);
    }

    try {
      model.setParentTaskId(rs.getInt("PARENT_TASK_ID"));
      if (rs.wasNull())
        model.setParentTaskId(null);
    } catch (Exception e) {
      model.setParentTaskId(null);
    }
    try {
      model.setCompoId(rs.getString("COMPO_ID"));
      if (rs.wasNull())
        model.setCompoId(null);
    } catch (Exception e) {
      model.setParentTaskId(null);
    }
    return model;
  }

  /**
   * @param conn
   * @param parentTaskId
   * @return
   * @throws SQLException
   * @throws SQLException
   */
  public List getChildToDoListByParentId(int parentTaskId) throws SQLException {
    ArrayList list = new ArrayList();
    Connection conn = null;
    conn = ConnectionFactory.getConnection();
    String sql = "select * from v_wf_current_task where PARENT_TASK_ID =? ";
    PreparedStatement st = null;
    st = conn.prepareStatement(sql);
    ResultSet rs = null;
    st.setInt(1, parentTaskId);
    rs = st.executeQuery();
    try {
      while (rs.next()) {
        list.add(parseResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  /**
   * 根据ParentId获取任务列表
   *
   * @param conn
   * @param parentTaskId
   * @return
   * @throws SQLException
   */
  public ArrayList findTaskByParentId(int parentTaskId) throws SQLException {
    ArrayList result = new ArrayList();
    String sql = "select * from v_wf_current_task where PARENT_TASK_ID = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, parentTaskId);
      rs = st.executeQuery();
      while (rs.next()) {
        result.add(parseResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return result;
  }

  public ArrayList findTaskByTemplateIdAndExecutorAndParentId(int parentTaskId,
    int templateId, String executor) throws SQLException {
    ArrayList result = new ArrayList();
    String sql = "select * from v_wf_current_task where TEMPLATE_ID=? and PARENT_TASK_ID = ? and EXECUTOR= ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
      st.setInt(2, parentTaskId);
      st.setString(3, executor);
      rs = st.executeQuery();
      while (rs.next()) {
        result.add(parseResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return result;
  }
}
