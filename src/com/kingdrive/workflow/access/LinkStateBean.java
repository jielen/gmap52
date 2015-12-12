package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.LinkStateModel;
import com.kingdrive.workflow.util.StringUtil;

public class LinkStateBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(LinkStateBean.class);

  public LinkStateBean() {
  }

  public void delete(int nodeLinkStateId) throws SQLException {
    String sql = "delete from WF_LINK_STATE where NODE_LINK_STATE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeLinkStateId);
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
    String sql = "select NODE_LINK_STATE_ID, NODE_LINK_ID, STATE_ID, STATE_VALUE from WF_LINK_STATE";
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

  public LinkStateModel findByKey(int nodeLinkStateId)
      throws SQLException {
    LinkStateModel model = new LinkStateModel();
    String sql = "select NODE_LINK_STATE_ID, NODE_LINK_ID, STATE_ID, STATE_VALUE from WF_LINK_STATE where NODE_LINK_STATE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeLinkStateId);
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

  public int insert(LinkStateModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeLinkStateIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_STATE_ID", model.getNodeLinkStateId(), strList,valList);
           }
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID", model.getNodeLinkId(), strList,valList);
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
    sql = "insert into WF_LINK_STATE(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(LinkStateModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID = ", model.getNodeLinkId(), strList,valList);
          }
    if (model.getStateIdModifyFlag()) {
      StringUtil.makeDynaParam("STATE_ID = ", model.getStateId(), strList,valList);
         }
    if (model.getStateValueModifyFlag()) {
      StringUtil.makeDynaParam("STATE_VALUE = ",convertSQL(model.getStateValue()), strList,valList);
          }
    if (strList.size() == 0) return 0;
    valList.add(model.getNodeLinkStateId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_LINK_STATE set " + updateString + " where NODE_LINK_STATE_ID= ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getStateListByLink(int linkId)
      throws SQLException {
    return getStateListByLink(-1, -1, linkId);
  }

  public ArrayList getStateListByLink(int theBegin,
      int theEnd, int linkId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_link_state where node_link_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, linkId);
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

  public void removeByState(int stateId) throws SQLException {
    String sql = "delete from wf_link_state where state_id = ?";
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
    // String sql = "delete a from wf_link_state a, wf_link b where
    // a.node_link_id = b.node_link_id and b.template_id = ?";
    String sql = "delete from wf_link_state where NODE_LINK_STATE_ID in (select a.NODE_LINK_STATE_ID from wf_link_state a, wf_link b where a.node_link_id = b.node_link_id and b.template_id = ?)";
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

  public void removeByLink(int linkId) throws SQLException {
    String sql = "delete from wf_link_state where node_link_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, linkId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByNode(int currentNodeId, int nextNodeId)
      throws SQLException {
    // String sql = "delete a from wf_link_state a, wf_link b where
    // a.node_link_id = b.node_link_id and (b.current_node_id = ? or
    // b.next_node_id = ?)";
    String sql = "delete from wf_link_state where NODE_LINK_STATE_ID in (select a.NODE_LINK_STATE_ID from wf_link_state a, wf_link b where a.node_link_id = b.node_link_id and (b.current_node_id = ? or b.next_node_id = ?))";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, currentNodeId);
      st.setInt(2, nextNodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn,st,null);
    }
  }

  private LinkStateModel parseResultSet(ResultSet rs) throws SQLException {
    LinkStateModel model = new LinkStateModel();
    try {
      model.setNodeLinkStateId(rs.getInt("NODE_LINK_STATE_ID"));
      if (rs.wasNull())
        model.setNodeLinkStateId(null);
    } catch (Exception e) {
      model.setNodeLinkStateId(null);
    }

    try {
      model.setNodeLinkId(rs.getInt("NODE_LINK_ID"));
      if (rs.wasNull())
        model.setNodeLinkId(null);
    } catch (Exception e) {
      model.setNodeLinkId(null);
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
