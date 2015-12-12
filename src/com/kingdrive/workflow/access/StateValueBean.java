package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class StateValueBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(StateValueBean.class);

  public StateValueBean() {
  }

  public void delete(int stateValueId) throws SQLException {
    String sql = "delete from WF_STATE_VALUE where STATE_VALUE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, stateValueId);
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
    String sql = "select STATE_VALUE_ID, INSTANCE_ID, STATE_ID, VALUE from WF_STATE_VALUE";
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

  public StateValueModel findByKey(int stateValueId)
    throws SQLException {
    StateValueModel model = new StateValueModel();
    String sql = "select STATE_VALUE_ID, INSTANCE_ID, STATE_ID, VALUE from WF_STATE_VALUE where STATE_VALUE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, stateValueId);
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

  public int insert(StateValueModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getStateValueIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_VALUE_ID", model.getStateValueId(), strList,
        valList);
    }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,
        valList);
    }
    if (model.getStateIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_ID", model.getStateId(), strList, valList);
    }
    if (model.getValueModifyFlag()) {
      StringUtil.makeDynaParam("VALUE", convertSQL(model.getValue()), strList,
        valList);
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
    sql = "insert into WF_STATE_VALUE(" + insertString + ") values(" + valsString
      + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(StateValueModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID = ", model.getInstanceId(), strList,
        valList);
    }
    if (model.getStateIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_ID = ", model.getStateId(), strList, valList);
    }
    if (model.getValueModifyFlag()) {
      StringUtil.makeDynaParam("VALUE = ", convertSQL(model.getValue()), strList,
        valList);
    }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getStateValueId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_STATE_VALUE set " + updateString + " where STATE_VALUE_ID= ?";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList findByValue(String value) throws SQLException {
    return findByValue(-1, -1, value);
  }

  public ArrayList findByValue(int theBegin, int theEnd,
    String value) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_state_value where value = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, value);
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

  public ArrayList findByInstanceIdAndStateId(int instanceId,
    int stateId) throws SQLException {
    return findByInstanceIdAndStateId(-1, -1, instanceId, stateId);
  }

  public ArrayList findByInstanceIdAndStateId(int theBegin,
    int theEnd, int instanceId, int stateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_state_value where instance_id = ? and state_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, instanceId);
      st.setInt(2, stateId);
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

  public void removeByInstance(int instanceId) throws SQLException {
    String sql = "delete from wf_state_value where instance_id = ?";
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

  public ArrayList findByInstanceId(int instanceId)
    throws SQLException {
    return findByInstanceId(-1, -1, instanceId);
  }

  public ArrayList findByInstanceId(int theBegin, int theEnd,
    int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_state_value where instance_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, instanceId);
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

  private StateValueModel parseResultSet(ResultSet rs) throws SQLException {
    StateValueModel model = new StateValueModel();
    try {
      model.setStateValueId(rs.getInt("STATE_VALUE_ID"));
      if (rs.wasNull())
        model.setStateValueId(null);
    } catch (Exception e) {
      model.setStateValueId(null);
    }

    try {
      model.setInstanceId(rs.getInt("INSTANCE_ID"));
      if (rs.wasNull())
        model.setInstanceId(null);
    } catch (Exception e) {
      model.setInstanceId(null);
    }

    try {
      model.setStateId(rs.getInt("STATE_ID"));
      if (rs.wasNull())
        model.setStateId(null);
    } catch (Exception e) {
      model.setStateId(null);
    }

    try {
      model.setValue(rs.getString("VALUE"));
      if (rs.wasNull())
        model.setValue(null);
    } catch (Exception e) {
      model.setValue(null);
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