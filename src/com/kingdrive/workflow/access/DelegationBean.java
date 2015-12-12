package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class DelegationBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(DelegationBean.class);

  public DelegationBean() {
  }

  public void delete(int delegationId) throws SQLException {
    String sql = "delete from WF_DELEGATION where DELEGATION_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
    	st = conn.prepareStatement(sql);
      st.setInt(1, delegationId);
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
    String sql = "select DELEGATION_ID, DESCRIPTION, TEMPLATE_ID, NODE_ID, SENDER, OWNER, RECEIVER, PARENT_ID, DELEGATION_TIME, START_TIME, END_TIME from WF_DELEGATION";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
    	st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
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

  public DelegationModel findByKey(int delegationId)
    throws SQLException {
    DelegationModel model = new DelegationModel();
    String sql = "select DELEGATION_ID, DESCRIPTION, TEMPLATE_ID, NODE_ID, SENDER, OWNER, RECEIVER, PARENT_ID, DELEGATION_TIME, START_TIME, END_TIME from WF_DELEGATION where DELEGATION_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, delegationId);
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

  public int insert(DelegationModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getDelegationIdModifyFlag()) {
      StringUtil.makeDynaParam("DELEGATION_ID", model.getDelegationId(), strList,
        valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION", convertSQL(model.getDescription()),
        strList, valList);
    }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,
        valList);
    }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID", model.getNodeId(), strList, valList);
    }
    if (model.getSenderModifyFlag()) {
      StringUtil.makeDynaParam("SENDER", convertSQL(model.getSender()), strList,
        valList);
    }
    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER", convertSQL(model.getOwner()), strList,
        valList);

    }
    if (model.getReceiverModifyFlag()) {
      StringUtil.makeDynaParam("RECEIVER", convertSQL(model.getReceiver()), strList,
        valList);
    }
    if (model.getParentIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_ID", model.getParentId(), strList, valList);
    }
    if (model.getDelegationTimeModifyFlag()) {
      StringUtil.makeDynaParam("DELEGATION_TIME", convertSQL(model
        .getDelegationTime()), strList, valList);
    }
    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME", convertSQL(model.getStartTime()),
        strList, valList);
    }
    if (model.getEndTimeModifyFlag()) {
      StringUtil.makeDynaParam("END_TIME", convertSQL(model.getEndTime()), strList,
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
    sql = "insert into WF_DELEGATION(" + insertString + ") values(" + valsString
      + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(DelegationModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION =",convertSQL(model.getDescription()), strList,
        valList);
      }

    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID=",model.getTemplateId(), strList,
        valList);
      }

    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID=",model.getNodeId(), strList,
        valList);
          }

    if (model.getSenderModifyFlag()) {
      StringUtil.makeDynaParam("SENDER =",convertSQL(model.getSender()), strList,
        valList);
          }

    if (model.getOwnerModifyFlag()) {
      StringUtil.makeDynaParam("OWNER = ",convertSQL(model.getOwner()), strList,
        valList);
          }

    if (model.getReceiverModifyFlag()) {
      StringUtil.makeDynaParam("RECEIVER = ",convertSQL(model.getReceiver()), strList,
        valList);
          }

    if (model.getParentIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_ID=",model.getParentId(), strList,
        valList);
          }

    if (model.getDelegationTimeModifyFlag()) {
      StringUtil.makeDynaParam("DELEGATION_TIME =",convertSQL(model.getDelegationTime()), strList,
        valList);
          }

    if (model.getStartTimeModifyFlag()) {
      StringUtil.makeDynaParam("START_TIME = ",convertSQL(model.getStartTime()), strList,
        valList);
          }

    if (model.getEndTimeModifyFlag()) {
      StringUtil.makeDynaParam("END_TIME = ",convertSQL(model.getEndTime()), strList,
        valList);
          }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getDelegationId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }

    sql = "update WF_DELEGATION set " + updateString + " where DELEGATION_ID=?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeOutOfDate(String currentTime)
    throws SQLException {
    String sql = "delete from wf_delegation where end_time < ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
    	st = conn.prepareStatement(sql);
      st.setString(1, currentTime);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  private DelegationModel parseResultSet(ResultSet rs) throws SQLException {
    DelegationModel model = new DelegationModel();
    try {
      model.setDelegationId(rs.getInt("DELEGATION_ID"));
      if (rs.wasNull())
        model.setDelegationId(null);
    } catch (Exception e) {
      model.setDelegationId(null);
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
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setSender(rs.getString("SENDER"));
      if (rs.wasNull())
        model.setSender(null);
    } catch (Exception e) {
      model.setSender(null);
    }

    try {
      model.setOwner(rs.getString("OWNER"));
      if (rs.wasNull())
        model.setOwner(null);
    } catch (Exception e) {
      model.setOwner(null);
    }

    try {
      model.setReceiver(rs.getString("RECEIVER"));
      if (rs.wasNull())
        model.setReceiver(null);
    } catch (Exception e) {
      model.setReceiver(null);
    }

    try {
      model.setParentId(rs.getInt("PARENT_ID"));
      if (rs.wasNull())
        model.setParentId(null);
    } catch (Exception e) {
      model.setParentId(null);
    }

    try {
      model.setDelegationTime(rs.getString("DELEGATION_TIME"));
      if (rs.wasNull())
        model.setDelegationTime(null);
    } catch (Exception e) {
      model.setDelegationTime(null);
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