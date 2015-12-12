package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.ActionHistoryInfo;
import com.kingdrive.workflow.util.DateTime;

public class ActionHistoryQuery {

  public ActionHistoryQuery() {
  }

  public ArrayList getActionListByOwner(String owner,
    String startTime, String endTime) throws SQLException {
    return getActionListByOwner(-1, -1, owner, startTime, endTime);
  }

  public ArrayList getActionListByOwner(int theBegin, int theEnd,
    String owner, String startTime, String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where owner = ? and execute_time between ? and ? order by instance_id desc, execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, owner);
      st.setString(2, startTime);
      st.setString(3, endTime);
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

  public ArrayList getActionListByOwnerAndTemplate(String owner,
    int templateId, String startTime, String endTime) throws SQLException {
    return getActionListByOwnerAndTemplate(-1, -1, owner, templateId,
      startTime, endTime);
  }

  public ArrayList getActionListByOwnerAndTemplate(int theBegin,
    int theEnd, String owner, int templateId, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where owner = ? and template_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByOwnerAndTemplate(String owner,
    String templateType, String startTime, String endTime) throws SQLException {
    return getActionListByOwnerAndTemplate(-1, -1, owner, templateType,
      startTime, endTime);
  }

  public ArrayList getActionListByOwnerAndTemplate(int theBegin,
    int theEnd, String owner, String templateType, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where owner = ? and template_type like ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByOwnerAndInstance(String owner,
    int instanceId, int isValid) throws SQLException {
    return getActionListByOwnerAndInstance(-1, -1, owner, instanceId,isValid);
  }
  
  /*
   * isValid: -1,作废； 1，有效； 0，作废+有效
   */
  public ArrayList getActionListByOwnerAndInstance(int theBegin,
    int theEnd, String owner, int instanceId, int isValid) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = null;
    String invalidSql = "select * from v_wf_action_history_invalid ";
    String validSql = "select * from v_wf_action_history ";
    String condition = " where owner = ?  and instance_id = ? ";
    String order = " order by execute_time asc, node_id asc";
    if(isValid==-1){//in order
      sql = invalidSql + condition + order;        
    }else if(isValid==0){//no order 2008/08/09 为什么流程跟踪不排序？？
      sql = "select * from ( " + validSql + condition + " union " + invalidSql + condition
          + " ) " + order;
    }else{//should be 1, also as default, in order
      sql = validSql + condition + order; 
    }
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
      if(isValid==0){
        st.setString(3, owner);
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
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }
  /*
   * isValid: -1,作废； 1，有效； 0，作废+有效
   */
  public ArrayList getActionListByInstanceAndNode(int instanceId,
    int nodeId, int isValid) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = null;
    String invalidSql = "select * from v_wf_action_history_invalid ";
    String validSql = "select * from v_wf_action_history ";
    String condition = " where instance_id = ?  and node_id = ? ";
    String order = " order by execute_time asc";
    if(isValid==-1){//in order
      sql = invalidSql + condition + order;        
    }else if(isValid==0){//no order
      sql = "select * from ( " + validSql + condition + " union " + invalidSql + condition
          + " ) " + order;
    }else{//should be 1, also as default, in order
      sql = validSql + condition + order; 
    }
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, nodeId);
      if(isValid==0){
        st.setInt(3, instanceId);
        st.setInt(4, nodeId);
      }
      rs = st.executeQuery();
      while (rs.next()) {
        list.add(parseResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }
  public ArrayList getActionListByOwnerAndNode(String owner,
    int nodeId, String startTime, String endTime) throws SQLException {
    return getActionListByOwnerAndNode(-1, -1, owner, nodeId, startTime,
      endTime);
  }

  public ArrayList getActionListByOwnerAndNode(int theBegin,
    int theEnd, String owner, int nodeId, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where owner = ? and node_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByOwnerAndNode(String owner,
    String businessType, String startTime, String endTime) throws SQLException {
    return getActionListByOwnerAndNode(-1, -1, owner, businessType, startTime,
      endTime);
  }

  public ArrayList getActionListByOwnerAndNode(int theBegin,
    int theEnd, String owner, String businessType, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where owner = ? and business_type = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionList(String startTime, String endTime)
    throws SQLException {
    return getActionList(-1, -1, startTime, endTime);
  }

  public ArrayList getActionList(int theBegin, int theEnd,
    String startTime, String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, startTime);
      st.setString(2, endTime);
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

  public ArrayList getActionListByTemplate(int templateId,
    String startTime, String endTime) throws SQLException {
    return getActionListByTemplate(-1, -1, templateId, startTime, endTime);
  }

  public ArrayList getActionListByTemplate(int theBegin,
    int theEnd, int templateId, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where template_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setString(2, startTime);
      st.setString(3, endTime);
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

  public ArrayList getActionListByTemplate(String templateType,
    String startTime, String endTime) throws SQLException {
    return getActionListByTemplate(-1, -1, templateType, startTime, endTime);
  }

  public ArrayList getActionListByTemplate(int theBegin,
    int theEnd, String templateType, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where template_type like ? and execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, templateType);
      st.setString(2, startTime);
      st.setString(3, endTime);
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

  public ArrayList getActionListByInstance(int instanceId,
    int isValid)
    throws SQLException {
    return getActionListByInstance(-1, -1, instanceId, isValid);
  }

  public ArrayList getActionListByInstance(int theBegin,
    int theEnd, int instanceId, int isValid) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = null;
    String invalidSql = "select * from v_wf_action_history_invalid ";
    String validSql = "select * from v_wf_action_history ";
    String condition = " where instance_id = ? ";
    String order = " order by execute_time asc, node_id asc";
    if(isValid==-1){//in order
      sql = invalidSql + condition + order;        
    }else if(isValid==0){//no order
      sql = "select * from ("+validSql + condition + " union " + invalidSql + condition+") "+order;
    }else{//should be 1, also as default, in order
      sql = validSql + condition + order; 
    }
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, instanceId);
      if(isValid == 0){
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
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ArrayList getActionListByNode(int nodeId,
    String startTime, String endTime) throws SQLException {
    return getActionListByNode(-1, -1, nodeId, startTime, endTime);
  }

  public ArrayList getActionListByNode(int theBegin, int theEnd,
    int nodeId, String startTime, String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where node_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
      st.setString(2, startTime);
      st.setString(3, endTime);
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

  public ArrayList getActionListByNode(String businessType,
    String startTime, String endTime) throws SQLException {
    return getActionListByNode(-1, -1, businessType, startTime, endTime);
  }

  public ArrayList getActionListByNode(int theBegin, int theEnd,
    String businessType, String startTime, String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where business_type = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, businessType);
      st.setString(2, startTime);
      st.setString(3, endTime);
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

  public ArrayList getActionListByExecutor(String executor,
    String startTime, String endTime) throws SQLException {
    return getActionListByExecutor(-1, -1, executor, startTime, endTime);
  }

  public ArrayList getActionListByExecutor(int theBegin,
    int theEnd, String executor, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setString(1, executor);
      st.setString(2, startTime);
      st.setString(3, endTime);
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
  /**
   * 取得executor执行过的compo的id列表
   * @param conn
   * @param executor
   * @param startTime
   * @param endTime
   * @return
   * @throws SQLException
   */
  public ArrayList getActionCompoListByExecutor(String executor) throws SQLException {
    return getActionCompoListByExecutor(-1, -1, executor);
  }

  public ArrayList getActionCompoListByExecutor(int theBegin,
    int theEnd, String executor) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select distinct COMPO_ID from v_wf_action_history where "
      +"executor= ?";
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
  public ArrayList getActionInstListByExecutor(String executor,
    String startTime, String endTime) throws SQLException {
    return getActionInstListByExecutor(-1, -1, executor, 
      startTime, endTime,true);
  }
  public ArrayList getInvalidDoneInstListByExecutor( String executor) throws SQLException {
    return getActionInstListByExecutor(-1, -1, executor, 
      null, null, false);
  }
  public ArrayList getActionInstListByExecutor(int theBegin,
    int theEnd, String executor, String startTime, String endTime,boolean isValid)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql;
    if(isValid){
      sql = "select instance_id from v_wf_action_history where executor = ?"
         + " and execute_time between ? and ? order by execute_time asc, node_id asc";
    }else{
      sql = "select instance_id from v_wf_action_history_invalid "
        + " where executor = ? order by execute_time asc, node_id asc";
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
      if(isValid){
        st.setString(2, startTime);
        st.setString(3, endTime);
      }
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
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ArrayList getActionListByExecutorAndTemplate(String executor, int templateId, String startTime, String endTime)
    throws SQLException {
    return getActionListByExecutorAndTemplate(-1, -1, executor, templateId,
      startTime, endTime);
  }

  public ArrayList getActionListByExecutorAndTemplate(int theBegin,int theEnd, String executor, int templateId, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and template_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByExecutorAndTemplate(String executor, String templateType, String startTime, String endTime)
    throws SQLException {
    return getActionListByExecutorAndTemplate(-1, -1, executor, templateType,
      startTime, endTime);
  }

  public ArrayList getActionListByExecutorAndTemplate(int theBegin,
    int theEnd, String executor, String templateType, String startTime,
    String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and template_type like ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByExecutorAndInstance(String executor, int instanceId) throws SQLException {
    return getActionListByExecutorAndInstance(-1, -1, executor, instanceId);
  }

  public ArrayList getActionListByExecutorAndInstance(int theBegin,
    int theEnd, String executor, int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and instance_id = ? order by execute_time asc, node_id asc";
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

  public ArrayList getActionListByExecutorAndNode(String executor,
    int nodeId, String startTime, String endTime) throws SQLException {
    return getActionListByExecutorAndNode(-1, -1, executor, nodeId, startTime,
      endTime);
  }

  public ArrayList getActionListByExecutorAndNode(int theBegin,
    int theEnd, String executor, int nodeId, String startTime, String endTime)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and node_id = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  public ArrayList getActionListByExecutorAndNode(String executor,
    String businessType, String startTime, String endTime) throws SQLException {
    return getActionListByExecutorAndNode(-1, -1, executor, businessType,
      startTime, endTime);
  }

  public ArrayList getActionListByExecutorAndNode(int theBegin,
    int theEnd, String executor, String businessType, String startTime,
    String endTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from v_wf_action_history where executor = ? and business_type = ? and execute_time between ? and ? order by execute_time asc, node_id asc";
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
      st.setString(3, startTime);
      st.setString(4, endTime);
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

  private ActionHistoryInfo parseResultSet(ResultSet rs) throws SQLException {
    ActionHistoryInfo model = new ActionHistoryInfo();
    try {
      model.setActionHistoryId(rs.getInt("ACTION_HISTORY_ID"));
      if (rs.wasNull())
        model.setActionHistoryId(null);
    } catch (Exception e) {
      model.setActionHistoryId(null);
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
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    try {
      model.setParentInstanceId(rs.getInt("PARENT_INSTANCE_ID"));
      if (rs.wasNull())
        model.setParentInstanceId(null);
    } catch (Exception e) {
      model.setParentInstanceId(null);
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
      model.setExecutorName(rs.getString("EXECUTOR_NAME"));
      if (rs.wasNull())
        model.setExecutorName(null);
    } catch (Exception e) {
      model.setExecutorName(null);
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
      model.setOwnerName(rs.getString("OWNER_NAME"));
      if (rs.wasNull())
        model.setOwnerName(null);
    } catch (Exception e) {
      model.setOwnerName(null);
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
  public boolean isLastAction(int instanceId, 
    int oneNodeId){
    boolean result= false;
    String sql = "select count(*) from wf_action a "
      +" where (execute_time between  "
      +" (select execute_time from wf_action where instance_id=? and node_id=? ) " 
      +" and ?) "      
      +" and a.instance_id=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, oneNodeId);
      st.setString(3, DateTime.getSysTime());
      st.setInt(4, instanceId);
      rs = st.executeQuery();
      if (rs.next()){
        int num = rs.getInt(1);
        if(num==1){
          result = true;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }
    return result;    
  }
}
