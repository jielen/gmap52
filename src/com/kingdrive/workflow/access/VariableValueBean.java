package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

/**
 * DataAcessObject
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: ÓÃÓÑÕþÎñ
 * </p>
 * 
 * @author unauthorized
 * 
 */
public class VariableValueBean {

  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(VariableValueBean.class);

  public VariableValueBean() {
  }

  /**
   * É¾³ý
   * 
   * @param conn
   * @param valueId
   * @throws SQLException
   * @throws
   * @see
   */
  public void delete(int valueId) throws SQLException {
    String sql = "delete from WF_VARIABLE_VALUE where VALUE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, valueId);
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
    String sql = "select VALUE_ID, INSTANCE_ID, VARIABLE_ID, VALUE from WF_VARIABLE_VALUE";
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

  public VariableValueModel findByKey(int valueId)
      throws SQLException {
    VariableValueModel model = new VariableValueModel();
    String sql = "select VALUE_ID, INSTANCE_ID, VARIABLE_ID, VALUE from WF_VARIABLE_VALUE where VALUE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, valueId);
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

  public int insert(VariableValueModel model)
      throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getValueIdModifyFlag()) {
      StringUtil.makeDynaParam("VALUE_ID", model.getValueId(), strList,valList);
          }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,valList);
          }
    if (model.getVariableIdModifyFlag()) {
      StringUtil.makeDynaParam("VARIABLE_ID", model.getVariableId(), strList,valList);
          }
    if (model.getValueModifyFlag()) {
      StringUtil.makeDynaParam("VALUE", convertSQL(model.getValue()), strList,valList);
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
    sql = "insert into WF_VARIABLE_VALUE(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(VariableValueModel model)
      throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID = ", model.getInstanceId(), strList,valList);
          }
    if (model.getVariableIdModifyFlag()) {
      StringUtil.makeDynaParam("VARIABLE_ID = ", model.getVariableId(), strList,valList);
        }
    if (model.getValueModifyFlag()) {
      StringUtil.makeDynaParam("VALUE = ", convertSQL(model.getValue()), strList,valList);
          }
    if (strList.size() == 0) return 0;
    valList.add(model.getValueId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_VARIABLE_VALUE set " + updateString + " where VALUE_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByInstance(int instanceId)
      throws SQLException {
    String sql = "delete from wf_variable_value where instance_id = ?";
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

  private VariableValueModel parseResultSet(ResultSet rs) throws SQLException {
    VariableValueModel model = new VariableValueModel();
    try {
      model.setValueId(rs.getInt("VALUE_ID"));
      if (rs.wasNull())
        model.setValueId(null);
    } catch (Exception e) {
      model.setValueId(null);
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