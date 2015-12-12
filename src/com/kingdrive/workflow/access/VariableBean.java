package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.VariableModel;
import com.kingdrive.workflow.util.StringUtil;

public class VariableBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(VariableBean.class);

  public VariableBean() {
  }

  public void delete(int variableId) throws SQLException {
    String sql = "delete from WF_VARIABLE where VARIABLE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, variableId);
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
    String sql = "select VARIABLE_ID, NAME, DESCRIPTION, TYPE, TEMPLATE_ID from WF_VARIABLE";
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

  public VariableModel findByKey(int variableId)
      throws SQLException {
    VariableModel model = new VariableModel();
    String sql = "select VARIABLE_ID, NAME, DESCRIPTION, TYPE, TEMPLATE_ID from WF_VARIABLE where VARIABLE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, variableId);
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

  public int insert(VariableModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getVariableIdModifyFlag()) {
      StringUtil.makeDynaParam("VARIABLE_ID", model.getVariableId(), strList,valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList,valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION", convertSQL(model.getDescription()), strList,valList);
          }
    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE", convertSQL(model.getType()), strList,valList);
          }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,valList);
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
    sql = "insert into WF_VARIABLE(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(VariableModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ", convertSQL(model.getName()), strList,valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ", convertSQL(model.getDescription()), strList,valList);
          }

    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE = ", convertSQL(model.getType()), strList,valList);
          }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID = ", model.getTemplateId(), strList,valList);
       }
    if (strList.size() == 0) return 0;
    valList.add(model.getVariableId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_VARIABLE set " + updateString + " where VARIABLE_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getVariableListByTemplate(int templateId)
      throws SQLException {
    return getVariableListByTemplate(-1, -1, templateId);
  }

  public ArrayList getVariableListByTemplate(int theBegin,
      int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_variable where template_id = ?";
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
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public VariableModel getVariable(int templateId, String name)
      throws SQLException {
    VariableModel model = new VariableModel();
    String sql = "select * from wf_variable where template_id = ? and name = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
      st.setString(2, name);
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

  public void removeByTemplate(int templateId)
      throws SQLException {
    String sql = "delete from wf_variable where template_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn,st,null);
    }
  }

  private VariableModel parseResultSet(ResultSet rs) throws SQLException {
    VariableModel model = new VariableModel();
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
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
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