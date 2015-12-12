/*
 * 20050323 zhangcheng 增加对REMIND_EXECUTE_TERM的支持
 */
package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.NodeModel;
import com.kingdrive.workflow.util.StringUtil;

/**
 * 
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
 * Company: 用友政务
 * </p>
 * 
 * @author unauthorized
 * @time 2005-3-23
 * 
 */
public class NodeBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(NodeBean.class);

  public NodeBean() {
  }

  public void delete(int nodeId) throws SQLException {
    String sql = "delete from WF_NODE where NODE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
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
    String sql = "select NODE_ID, NAME, DESCRIPTION, TYPE, BUSINESS_TYPE, TEMPLATE_ID, EXECUTORS_METHOD, TASK_LISTENER, LIMIT_EXECUTE_TERM, REMIND_EXECUTE_TERM from WF_NODE";
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

  public NodeModel findByKey(int nodeId) throws SQLException {
    NodeModel model = new NodeModel();
    String sql = "select NODE_ID, NAME, DESCRIPTION, TYPE, BUSINESS_TYPE, TEMPLATE_ID, EXECUTORS_METHOD, TASK_LISTENER, LIMIT_EXECUTE_TERM, REMIND_EXECUTE_TERM from WF_NODE where NODE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeId);
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

  public int insert(NodeModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID", model.getNodeId(), strList, valList);
    }
    if (model.getNameModifyFlag()) {
      StringUtil
        .makeDynaParam("NAME", convertSQL(model.getName()), strList, valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION", convertSQL(model.getDescription()),
        strList, valList);
    }
    if (model.getTypeModifyFlag()) {
      StringUtil
        .makeDynaParam("TYPE", convertSQL(model.getType()), strList, valList);
    }
    if (model.getBusinessTypeModifyFlag()) {
      StringUtil.makeDynaParam("BUSINESS_TYPE", convertSQL(model.getBusinessType()),
        strList, valList);
    }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,
        valList);
    }
    if (model.getExecutorsMethodModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTORS_METHOD", convertSQL(model
        .getExecutorsMethod()), strList, valList);
    }
    if (model.getTaskListenerModifyFlag()) {
      StringUtil.makeDynaParam("TASK_LISTENER", convertSQL(model.getTaskListener()),
        strList, valList);
    }
    if (model.getLimitExecuteTermModifyFlag()) {
      StringUtil.makeDynaParam("LIMIT_EXECUTE_TERM", model.getLimitExecuteTerm(),
        strList, valList);
    }
    if (model.getRemindExecuteTermModifyFlag()) {
      StringUtil.makeDynaParam("REMIND_EXECUTE_TERM", model.getRemindExecuteTerm(),
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
    sql = "insert into WF_NODE(" + insertString + ") values(" + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(NodeModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ", convertSQL(model.getName()), strList,
        valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ", convertSQL(model.getDescription()),
        strList, valList);
    }
    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE = ", convertSQL(model.getType()), strList,
        valList);
    }
    if (model.getBusinessTypeModifyFlag()) {
      StringUtil.makeDynaParam("BUSINESS_TYPE = ", convertSQL(model
        .getBusinessType()), strList, valList);
    }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID = ", model.getTemplateId(), strList,
        valList);
    }
    if (model.getExecutorsMethodModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTORS_METHOD = ", convertSQL(model
        .getExecutorsMethod()), strList, valList);
    }
    if (model.getTaskListenerModifyFlag()) {
      StringUtil.makeDynaParam("TASK_LISTENER = ", convertSQL(model
        .getTaskListener()), strList, valList);
    }
    if (model.getLimitExecuteTermModifyFlag()) {
      StringUtil.makeDynaParam("LIMIT_EXECUTE_TERM = ", model.getLimitExecuteTerm(),
        strList, valList);
    }
    if (model.getRemindExecuteTermModifyFlag()) {
      StringUtil.makeDynaParam("REMIND_EXECUTE_TERM = ", model
        .getRemindExecuteTerm(), strList, valList);
    }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getNodeId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_NODE set " + updateString + " where NODE_ID= ?";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByTemplate(int templateId) throws SQLException {
    String sql = "delete from wf_node where template_id = ?";
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

  private NodeModel parseResultSet(ResultSet rs) throws SQLException {
    NodeModel model = new NodeModel();
    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
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
      model.setBusinessType(rs.getString("BUSINESS_TYPE"));
      if (rs.wasNull())
        model.setBusinessType(null);
    } catch (Exception e) {
      model.setBusinessType(null);
    }

    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    try {
      model.setExecutorsMethod(rs.getString("EXECUTORS_METHOD"));
      if (rs.wasNull())
        model.setExecutorsMethod(null);
    } catch (Exception e) {
      model.setExecutorsMethod(null);
    }

    try {
      model.setTaskListener(rs.getString("TASK_LISTENER"));
      if (rs.wasNull())
        model.setTaskListener(null);
    } catch (Exception e) {
      model.setTaskListener(null);
    }

    try {
      model.setLimitExecuteTerm(rs.getInt("LIMIT_EXECUTE_TERM"));
      if (rs.wasNull())
        model.setLimitExecuteTerm(null);
    } catch (Exception e) {
      model.setLimitExecuteTerm(null);
    }

    try {
      model.setLimitExecuteTerm(rs.getInt("REMIND_EXECUTE_TERM"));
      if (rs.wasNull())
        model.setRemindExecuteTerm(null);
    } catch (Exception e) {
      model.setRemindExecuteTerm(null);
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