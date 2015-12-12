package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class TemplateBean {

  public TemplateBean() {
  }

  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(TemplateBean.class);

  public void delete(int templateId) throws SQLException {
    String sql = "delete from WF_TEMPLATE where TEMPLATE_ID=?";
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

  public ArrayList find() throws SQLException {
    return find(-1, -1);
  }

  public ArrayList find(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select TEMPLATE_ID, NAME, DESCRIPTION, TEMPLATE_TYPE, VERSION, START_TIME, EXPIRE_TIME, CREATE_TIME, CREATE_STAFF_ID, IS_ACTIVE from WF_TEMPLATE";
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

  public TemplateModel findByKey(int templateId)
      throws SQLException {
    TemplateModel model = new TemplateModel();
    String sql = "select TEMPLATE_ID, NAME, DESCRIPTION, TEMPLATE_TYPE, VERSION, START_TIME, EXPIRE_TIME, CREATE_TIME, CREATE_STAFF_ID, IS_ACTIVE from WF_TEMPLATE where TEMPLATE_ID=?";
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

  public int insert(TemplateModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList,valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION",convertSQL(model.getDescription()), strList,valList);
          }
    if (model.getTemplateTypeModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_TYPE",convertSQL(model.getTemplateType()), strList,valList);
          }
    if (model.getVersionModifyFlag()) {
      StringUtil.makeDynaParam("VERSION",convertSQL(model.getVersion()), strList,valList);
          }
    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME",convertSQL(model.getStartTime()), strList,valList);
          }
    if (model.getExpireTimeModifyFlag()) {
      StringUtil.makeDynaParam("EXPIRE_TIME",convertSQL(model.getExpireTime()), strList,valList);
          }
    if (model.getCreateTimeModifyFlag()) {
      StringUtil.makeDynaParam("CREATE_TIME",convertSQL(model.getCreateTime()), strList,valList);
          }
    if (model.getCreateStaffIdModifyFlag()) {
      StringUtil.makeDynaParam("CREATE_STAFF_ID",convertSQL(model.getCreateStaffId()), strList,valList);
          }
    if (model.getIsActiveModifyFlag()) {
      StringUtil.makeDynaParam("IS_ACTIVE",convertSQL(model.getIsActive()), strList,valList);
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
    sql = "insert into WF_TEMPLATE(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(TemplateModel model) throws SQLException {
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
    if (model.getTemplateTypeModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_TYPE = ", convertSQL(model.getTemplateType()), strList,valList);
          }
    if (model.getVersionModifyFlag()) {
      StringUtil.makeDynaParam("VERSION = ",convertSQL(model.getVersion()), strList,valList);
          }
    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME = ",convertSQL(model.getStartTime()), strList,valList);
          }
    if (model.getExpireTimeModifyFlag()) {
      StringUtil.makeDynaParam("EXPIRE_TIME = ",convertSQL(model.getExpireTime()), strList,valList);
          }
    if (model.getCreateTimeModifyFlag()) {
      StringUtil.makeDynaParam("CREATE_TIME = ",convertSQL(model.getCreateTime()), strList,valList);
          }
    if (model.getCreateStaffIdModifyFlag()) {
      StringUtil.makeDynaParam("CREATE_STAFF_ID = ",convertSQL(model.getCreateStaffId()), strList,valList);
          }
    if (model.getIsActiveModifyFlag()) {
      StringUtil.makeDynaParam("IS_ACTIVE = ",convertSQL(model.getIsActive()), strList,valList);
          }

    if (strList.size() == 0) return 0;
    valList.add(model.getTemplateId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_TEMPLATE set " + updateString + " where TEMPLATE_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  private TemplateModel parseResultSet(ResultSet rs) throws SQLException {
    TemplateModel model = new TemplateModel();
    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
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
      model.setTemplateType(rs.getString("TEMPLATE_TYPE"));
      if (rs.wasNull())
        model.setTemplateType(null);
    } catch (Exception e) {
      model.setTemplateType(null);
    }

    try {
      model.setVersion(rs.getString("VERSION"));
      if (rs.wasNull())
        model.setVersion(null);
    } catch (Exception e) {
      model.setVersion(null);
    }

    try {
      model.setStartTime(rs.getString("START_TIME"));
      if (rs.wasNull())
        model.setStartTime(null);
    } catch (Exception e) {
      model.setStartTime(null);
    }

    try {
      model.setExpireTime(rs.getString("EXPIRE_TIME"));
      if (rs.wasNull())
        model.setExpireTime(null);
    } catch (Exception e) {
      model.setExpireTime(null);
    }

    try {
      model.setCreateTime(rs.getString("CREATE_TIME"));
      if (rs.wasNull())
        model.setCreateTime(null);
    } catch (Exception e) {
      model.setCreateTime(null);
    }

    try {
      model.setCreateStaffId(rs.getString("CREATE_STAFF_ID"));
      if (rs.wasNull())
        model.setCreateStaffId(null);
    } catch (Exception e) {
      model.setCreateStaffId(null);
    }

    try {
      model.setIsActive(rs.getString("IS_ACTIVE"));
      if (rs.wasNull())
        model.setIsActive(null);
    } catch (Exception e) {
      model.setIsActive(null);
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