package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class InstanceBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(InstanceBean.class);

  public InstanceBean() {
  }

  public void delete(int instanceId) throws SQLException {
    String sql = "delete from WF_INSTANCE where INSTANCE_ID=?";
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

  public ArrayList find() throws SQLException {
    return find(-1, -1);
  }

  public ArrayList find(int theBegin, int theEnd)
    throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select INSTANCE_ID, NAME, DESCRIPTION, TEMPLATE_ID, OWNER, START_TIME, END_TIME, STATUS from WF_INSTANCE";
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

  public InstanceModel findByKey(int instanceId)
    throws SQLException {
    InstanceModel model = new InstanceModel();
    String sql = "select INSTANCE_ID, NAME, DESCRIPTION, TEMPLATE_ID, OWNER, START_TIME, END_TIME, STATUS, PARENT_INSTANCE_ID from WF_INSTANCE where INSTANCE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
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

  public int insert(InstanceModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,
        valList);
    }
    if (model.getNameModifyFlag()) {
      StringUtil
        .makeDynaParam("NAME", convertSQL(model.getName()), strList, valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION", convertSQL(model.getDescription()),
        strList, valList);
    }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,
        valList);
    }
    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER", convertSQL(model.getOwner()), strList,
        valList);
    }
    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME", convertSQL(model.getStartTime()),
        strList, valList);
    }
    if (model.getEndTimeModifyFlag()) {
      StringUtil.makeDynaParam("END_TIME", convertSQL(model.getEndTime()), strList,
        valList);
    }
    if (model.getStatusModifyFlag()) {
      StringUtil.makeDynaParam("STATUS", model.getStatus(), strList, valList);
    }
    if (model.getParentInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_INSTANCE_ID", model.getParentInstanceId(),
        strList, valList);
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
    sql = "insert into WF_INSTANCE(" + insertString + ") values(" + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(InstanceModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME =", convertSQL(model.getName()), strList,
        valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ", convertSQL(model.getDescription()),
        strList, valList);
    }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID = ", model.getTemplateId(), strList,
        valList);
    }
    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER = ", convertSQL(model.getOwner()), strList,
        valList);
    }
    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME = ", convertSQL(model.getStartTime()),
        strList, valList);
    }
    if (model.getEndTimeModifyFlag()) {
      StringUtil.makeDynaParam("END_TIME = ", convertSQL(model.getEndTime()),
        strList, valList);
    }
    if (model.getStatusModifyFlag()) {
      StringUtil.makeDynaParam("STATUS = ", model.getStatus(), strList, valList);
    }
    if (model.getParentInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_INSTANCE_ID =", model.getParentInstanceId(),
        strList, valList);
    }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getInstanceId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_INSTANCE set " + updateString + " where INSTANCE_ID=?";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  private InstanceModel parseResultSet(ResultSet rs) throws SQLException {
    InstanceModel model = new InstanceModel();
    try {
      model.setInstanceId(rs.getInt("INSTANCE_ID"));
      if (rs.wasNull())
        model.setInstanceId(null);
    } catch (Exception e) {
      model.setInstanceId(null);
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
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    try {
      model.setOwner(rs.getString("OWNER"));
      if (rs.wasNull())
        model.setOwner(null);
    } catch (Exception e) {
      model.setOwner(null);
    }

    try {
      model.setStartTime(rs.getString("START_TIME"));
      if (rs.wasNull())
        model.setStartTime(null);
    } catch (Exception e) {
      model.setStartTime(null);
    }

    try {
      model.setEndTime(rs.getString("END_TIME"));
      if (rs.wasNull())
        model.setEndTime(null);
    } catch (Exception e) {
      model.setEndTime(null);
    }

    try {
      model.setStatus(rs.getInt("STATUS"));
      if (rs.wasNull())
        model.setStatus(null);
    } catch (Exception e) {
      model.setStatus(null);
    }

    try {
      model.setParentInstanceId(rs.getInt("PARENT_INSTANCE_ID"));
      if (rs.wasNull())
        model.setParentInstanceId(null);
    } catch (Exception e) {
      model.setParentInstanceId(null);
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