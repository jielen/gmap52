package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class PassBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(PassBean.class);

  public PassBean() {
  }

  public void delete(int passCountId) throws SQLException {
    String sql = "delete from WF_PASS where PASS_COUNT_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, passCountId);
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
    String sql = "select PASS_COUNT_ID, INSTANCE_ID, NODE_LINK_ID, CURRENT_NODE_ID, NEXT_NODE_ID from WF_PASS";
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

  public PassModel findByKey(int passCountId)
      throws SQLException {
    PassModel model = new PassModel();
    String sql = "select PASS_COUNT_ID, INSTANCE_ID, NODE_LINK_ID, CURRENT_NODE_ID, NEXT_NODE_ID from WF_PASS where PASS_COUNT_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, passCountId);
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

  public int insert(PassModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getPassCountIdModifyFlag()) {
      StringUtil.makeDynaParam("PASS_COUNT_ID", model.getPassCountId(), strList,valList);
          }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,valList);
          }
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID", model.getNodeLinkId(), strList,valList);
          }
    if (model.getCurrentNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("CURRENT_NODE_ID", model.getCurrentNodeId(), strList,valList);
          }
    if (model.getNextNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NEXT_NODE_ID",  model.getNextNodeId(), strList,valList);
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
    sql = "insert into WF_PASS(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(PassModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID = ", model.getInstanceId(), strList,valList);
        }
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID = ", model.getNodeLinkId(), strList,valList);
       }
    if (model.getCurrentNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("CURRENT_NODE_ID=", model.getCurrentNodeId(), strList,valList);
    }
    if (model.getNextNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NEXT_NODE_ID = ", model.getNextNodeId(), strList,valList);
    }
    if (strList.size() == 0) return 0;
    valList.add(model.getPassCountId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_PASS set " + updateString + " where PASS_COUNT_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByInstance(int instanceId)
      throws SQLException {
    String sql = "delete from wf_pass where instance_id = ?";
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

  public void removeByNode(int instanceId, int currentNodeId)
      throws SQLException {
    String sql = "delete from wf_pass where instance_id = ? and current_node_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.setInt(2, currentNodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  private PassModel parseResultSet(ResultSet rs) throws SQLException {
    PassModel model = new PassModel();
    try {
      model.setPassCountId(rs.getInt("PASS_COUNT_ID"));
      if (rs.wasNull())
        model.setPassCountId(null);
    } catch (Exception e) {
      model.setPassCountId(null);
    }

    try {
      model.setInstanceId(rs.getInt("INSTANCE_ID"));
      if (rs.wasNull())
        model.setInstanceId(null);
    } catch (Exception e) {
      model.setInstanceId(null);
    }

    try {
      model.setNodeLinkId(rs.getInt("NODE_LINK_ID"));
      if (rs.wasNull())
        model.setNodeLinkId(null);
    } catch (Exception e) {
      model.setNodeLinkId(null);
    }

    try {
      model.setCurrentNodeId(rs.getInt("CURRENT_NODE_ID"));
      if (rs.wasNull())
        model.setCurrentNodeId(null);
    } catch (Exception e) {
      model.setCurrentNodeId(null);
    }

    try {
      model.setNextNodeId(rs.getInt("NEXT_NODE_ID"));
      if (rs.wasNull())
        model.setNextNodeId(null);
    } catch (Exception e) {
      model.setNextNodeId(null);
    }

    return model;
  }
}