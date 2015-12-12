package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.VariableValueInfo;

public class VariableValueQuery {

  public VariableValueQuery() {
  }

  public ArrayList getValueListByInstance(int instanceId)
      throws SQLException {
    return getValueListByInstance(-1, -1, instanceId);
  }

  public ArrayList getValueListByInstance(int theBegin,
      int theEnd, int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select b.template_id, a.instance_id, a.variable_id, b.name, b.description, b.type, a.value_id, a.value from wf_variable_value a, wf_variable b where a.variable_id = b.variable_id and a.instance_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
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

  private VariableValueInfo parseResultSet(ResultSet rs) throws SQLException {
    VariableValueInfo model = new VariableValueInfo();
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
      model.setVariableId(rs.getInt("VARIABLE_ID"));
      if (rs.wasNull())
        model.setVariableId(null);
    } catch (Exception e) {
      model.setVariableId(null);
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
      model.setType(rs.getString("TYPE"));
      if (rs.wasNull())
        model.setType(null);
    } catch (Exception e) {
      model.setType(null);
    }

    try {
      model.setValueId(rs.getInt("VALUE_ID"));
      if (rs.wasNull())
        model.setValueId(null);
    } catch (Exception e) {
      model.setValueId(null);
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
