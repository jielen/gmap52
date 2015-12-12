package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.StateValueInfo;

public class StateValueQuery {

  public StateValueQuery() {
  }

  public StateValueInfo getStateValue(int stateValueId)
      throws SQLException {
    StateValueInfo model = new StateValueInfo();
    String sql = "select a.*, b.template_id, b.name, b.description from wf_state_value a, wf_state b where a.state_id = b.state_id and a.state_value_id = ?";
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

  public ArrayList getValueListByInstance(int instanceId)
      throws SQLException {
    return getValueListByInstance(-1, -1, instanceId);
  }

  public ArrayList getValueListByInstance(int theBegin,
      int theEnd, int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.template_id, b.name, b.description from wf_state_value a, wf_state b where a.state_id = b.state_id and a.instance_id = ?";
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

  public ArrayList getValueListByState(int instanceId,
      int stateId) throws SQLException {
    return getValueListByState(-1, -1, instanceId, stateId);
  }

  public ArrayList getValueListByState(int theBegin,
      int theEnd, int instanceId, int stateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.template_id, b.name, b.description from wf_state_value a, wf_state b where a.state_id = b.state_id and a.instance_id = ? and a.state_id = ?";
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

  public ArrayList getValueListByValue(String value)
      throws SQLException {
    return getValueListByValue(-1, -1, value);
  }

  public ArrayList getValueListByValue(int theBegin,
      int theEnd, String value) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.template_id, b.name, b.description from wf_state_value a, wf_state b where a.state_id = b.state_id and a.value = ?";
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

  private StateValueInfo parseResultSet(ResultSet rs) throws SQLException {
    StateValueInfo model = new StateValueInfo();
    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
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
      model.setName(rs.getString("NAME"));
      if (rs.wasNull())
        model.setName(null);
    } catch (Exception e) {
      model.setName(null);
    }

    try {
      model.setDescription(rs.getString("DESCRIPTION"));
      if (rs.wasNull())
        model.setDescription(null);
    } catch (Exception e) {
      model.setDescription(null);
    }

    try {
      model.setStateValueId(rs.getInt("STATE_VALUE_ID"));
      if (rs.wasNull())
        model.setStateValueId(null);
    } catch (Exception e) {
      model.setStateValueId(null);
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
}
