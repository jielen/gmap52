package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class NodeStateBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(NodeStateBean.class);

  public NodeStateBean() {
  }

  public void delete(int nodeStateId) throws SQLException {
    String sql = "delete from WF_NODE_STATE where NODE_STATE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeStateId);
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
    String sql = "select NODE_STATE_ID, NODE_ID, STATE_ID, STATE_VALUE from WF_NODE_STATE";
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

  public NodeStateModel findByKey(int nodeStateId)
      throws SQLException {
    NodeStateModel model = new NodeStateModel();
    String sql = "select NODE_STATE_ID, NODE_ID, STATE_ID, STATE_VALUE from WF_NODE_STATE where NODE_STATE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeStateId);
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

  public int insert(NodeStateModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeStateIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_STATE_ID", model.getNodeStateId(), strList,valList);
          }
    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID", model.getNodeId(), strList,valList);
          }
    if (model.getStateIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_ID", model.getStateId(), strList,valList);
          }
    if (model.getStateValueModifyFlag()) {
      StringUtil.makeDynaParam("STATE_VALUE",convertSQL(model.getStateValue()), strList,valList);
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
    sql = "insert into WF_NODE_STATE(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(NodeStateModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_ID = ", model.getNodeId(), strList,valList);
          }
    if (model.getStateIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_ID = ", model.getStateId(), strList,valList);
          }
    if (model.getStateValueModifyFlag()) {
      StringUtil.makeDynaParam("STATE_VALUE = ", convertSQL(model.getStateValue()), strList,valList);
       }
    if (strList.size() == 0) return 0;
    valList.add(model.getNodeStateId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_NODE_STATE set " + updateString + " where NODE_STATE_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByState(int stateId) throws SQLException {
    String sql = "delete from wf_node_state where state_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, stateId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByTemplate(int templateId)
      throws SQLException {
    // String sql = "delete a from wf_node_state a, wf_node b where a.node_id =
    // b.node_id and b.template_id = ?";
    String sql = "delete from wf_node_state where NODE_STATE_ID in (select a.NODE_STATE_ID from wf_node_state a, wf_node b where a.node_id = b.node_id and b.template_id = ?)";
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

  public void removeByNode(int nodeId) throws SQLException {
    String sql = "delete from wf_node_state where node_id = ?";
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

  public ArrayList getStateListByNode(int nodeId)
      throws SQLException {
    return getStateListByNode(-1, -1, nodeId);
  }

  public ArrayList getStateListByNode(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_node_state where node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
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

  private NodeStateModel parseResultSet(ResultSet rs) throws SQLException {
    NodeStateModel model = new NodeStateModel();
    try {
      model.setNodeStateId(rs.getInt("NODE_STATE_ID"));
      if (rs.wasNull())
        model.setNodeStateId(null);
    } catch (Exception e) {
      model.setNodeStateId(null);
    }

    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setStateId(rs.getInt("STATE_ID"));
      if (rs.wasNull())
        model.setStateId(null);
    } catch (Exception e) {
      model.setStateId(null);
    }

    try {
      model.setStateValue(rs.getString("STATE_VALUE"));
      if (rs.wasNull())
        model.setStateValue(null);
    } catch (Exception e) {
      model.setStateValue(null);
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
