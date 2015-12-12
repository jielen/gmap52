package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class LinkBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(LinkBean.class);

  public LinkBean() {
  }

  public void delete(int nodeLinkId) throws SQLException {
    String sql = "delete from WF_LINK where NODE_LINK_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeLinkId);
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
    String sql = "select NODE_LINK_ID, NAME, DESCRIPTION, LINK_TYPE, TEMPLATE_ID, CURRENT_NODE_ID, NEXT_NODE_ID, EXECUTOR_RELATION, EXECUTORS_METHOD, NUMBER_OR_PERCENT, PASS_VALUE, EXPRESSION, DEFAULT_PATH, ACTION_NAME from WF_LINK";
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
  public LinkModel findByActivityKey(int nodeId,int nextNodeId)
	  throws SQLException {
	LinkModel model = new LinkModel();
	String sql = "select NODE_LINK_ID, NAME, DESCRIPTION, LINK_TYPE, "
		+ "TEMPLATE_ID, CURRENT_NODE_ID, NEXT_NODE_ID, EXECUTOR_RELATION, " 
		+ "EXECUTORS_METHOD, NUMBER_OR_PERCENT, PASS_VALUE, EXPRESSION, "
		+ "DEFAULT_PATH, ACTION_NAME from WF_LINK where CURRENT_NODE_ID=? AND NEXT_NODE_ID=?";
	PreparedStatement st = null;
	ResultSet rs = null;
  Connection conn = null;
  try {
  	conn = ConnectionFactory.getConnection();
	  st = conn.prepareStatement(sql);
	  st.setInt(1, nodeId);
	  st.setInt(1, nextNodeId);
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

  public LinkModel findByKey(int nodeLinkId)
      throws SQLException {
    LinkModel model = new LinkModel();
    String sql = "select NODE_LINK_ID, NAME, DESCRIPTION, LINK_TYPE, TEMPLATE_ID, CURRENT_NODE_ID, NEXT_NODE_ID, EXECUTOR_RELATION, EXECUTORS_METHOD, NUMBER_OR_PERCENT, PASS_VALUE, EXPRESSION, DEFAULT_PATH, ACTION_NAME from WF_LINK where NODE_LINK_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, nodeLinkId);
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

  public int insert(LinkModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID", model.getNodeLinkId(), strList, valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList, valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION", convertSQL(model.getDescription()), strList, valList);
          }
    if (model.getLinkTypeModifyFlag()) {
      StringUtil.makeDynaParam("LINK_TYPE", convertSQL(model.getLinkType()), strList, valList);
          }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList, valList);
          }
    if (model.getCurrentNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("CURRENT_NODE_ID", model.getCurrentNodeId(), strList, valList);
          }
    if (model.getNextNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NEXT_NODE_ID", model.getNextNodeId(), strList, valList);
          }
    if (model.getExecutorRelationModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR_RELATION", convertSQL(model.getExecutorRelation()), strList, valList);
          }
    if (model.getExecutorsMethodModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTORS_METHOD", convertSQL(model.getExecutorsMethod()), strList, valList);
          }
    if (model.getNumberOrPercentModifyFlag()) {
      StringUtil.makeDynaParam("NUMBER_OR_PERCENT", convertSQL(model.getNumberOrPercent()), strList, valList);
          }
    if (model.getPassValueModifyFlag()) {
      StringUtil.makeDynaParam("PASS_VALUE", model.getPassValue(), strList, valList);
          }
    if (model.getExpressionModifyFlag()) {
      StringUtil.makeDynaParam("EXPRESSION",convertSQL(model.getExpression()), strList, valList);
          }
    if (model.getDefaultPathModifyFlag()) {
      StringUtil.makeDynaParam("DEFAULT_PATH",convertSQL(model.getDefaultPath()), strList, valList);
          }
    if (model.getActionNameModifyFlag()) {
      StringUtil.makeDynaParam("ACTION_NAME",convertSQL(model.getActionName()), strList, valList);
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
    sql = "insert into WF_LINK(" + insertString + ") values(" + valsString
          + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(LinkModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ", convertSQL(model.getName()),
        strList, valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()),
        strList, valList);
       }

    if (model.getLinkTypeModifyFlag()) {
      StringUtil.makeDynaParam("LINK_TYPE = ",convertSQL(model.getLinkType()),
        strList, valList);
      }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID = ",model.getTemplateId(),
        strList, valList);
         }

    if (model.getCurrentNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("CURRENT_NODE_ID = ",model.getCurrentNodeId(),
        strList, valList);
       }
    if (model.getNextNodeIdModifyFlag()) {
      StringUtil.makeDynaParam("NEXT_NODE_ID = ",model.getNextNodeId(),
        strList, valList);
    }
    if (model.getExecutorRelationModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTOR_RELATION = ",convertSQL(model.getExecutorRelation()),
        strList, valList);
        }
    if (model.getExecutorsMethodModifyFlag()) {
      StringUtil.makeDynaParam("EXECUTORS_METHOD = ",convertSQL(model.getExecutorsMethod()),
        strList, valList);
       }
    if (model.getNumberOrPercentModifyFlag()) {
      StringUtil.makeDynaParam("NUMBER_OR_PERCENT = ",convertSQL(model.getNumberOrPercent()),
        strList, valList);
    }
    if (model.getPassValueModifyFlag()) {
      StringUtil.makeDynaParam("PASS_VALUE = ",model.getPassValue(),
        strList, valList);
          }
    if (model.getExpressionModifyFlag()) {
      StringUtil.makeDynaParam("EXPRESSION = ",convertSQL(model.getExpression()),
        strList, valList);
       }
    if (model.getDefaultPathModifyFlag()) {
      StringUtil.makeDynaParam("DEFAULT_PATH = ",convertSQL(model.getDefaultPath()),
        strList, valList);
          }
    if (model.getActionNameModifyFlag()) {
      StringUtil.makeDynaParam("ACTION_NAME = ",convertSQL(model.getActionName()),
        strList, valList);
          }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getNodeLinkId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_LINK set " + updateString + " where NODE_LINK_ID= ?";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getLinkList(int templateId)
      throws SQLException {
    return getLinkList(-1, -1, templateId);
  }

  public ArrayList getLinkList(int theBegin, int theEnd,
      int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_link where template_id = ?";
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

  public ArrayList getPrecedingLinkList(int templateId,
      int nextNodeId) throws SQLException {
    return getPrecedingLinkList(-1, -1, templateId, nextNodeId);
  }

  public ArrayList getPrecedingLinkList(int theBegin,
      int theEnd, int templateId, int nextNodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_link where template_id = ? and next_node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setInt(2, nextNodeId);
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

  public void removeByTemplate(int templateId)
      throws SQLException {
    String sql = "delete from wf_link where template_id = ?";
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

  public void removeByNode(int currentNodeId, int nextNodeId)
      throws SQLException {
    String sql = "delete from wf_link where current_node_id = ? or next_node_id = ?";
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

  public void removeDefaultPath(int currentNodeId)
      throws SQLException {
    String sql = "update wf_link set default_path='0' where current_node_id = ? and default_path='1'";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, currentNodeId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public ArrayList getFollowedLinkList(int templateId,
      int currentNodeId) throws SQLException {
    return getFollowedLinkList(-1, -1, templateId, currentNodeId);
  }

  public ArrayList getFollowedLinkList(int theBegin,
      int theEnd, int templateId, int currentNodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_link where template_id = ? and current_node_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setInt(2, currentNodeId);
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

  private LinkModel parseResultSet(ResultSet rs) throws SQLException {
    LinkModel model = new LinkModel();
    try {
      model.setNodeLinkId(rs.getInt("NODE_LINK_ID"));
      if (rs.wasNull())
        model.setNodeLinkId(null);
    } catch (Exception e) {
      model.setNodeLinkId(null);
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
      model.setLinkType(rs.getString("LINK_TYPE"));
      if (rs.wasNull())
        model.setLinkType(null);
    } catch (Exception e) {
      model.setLinkType(null);
    }

    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
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

    try {
      model.setExecutorRelation(rs.getString("EXECUTOR_RELATION"));
      if (rs.wasNull())
        model.setExecutorRelation(null);
    } catch (Exception e) {
      model.setExecutorRelation(null);
    }

    try {
      model.setExecutorsMethod(rs.getString("EXECUTORS_METHOD"));
      if (rs.wasNull())
        model.setExecutorsMethod(null);
    } catch (Exception e) {
      model.setExecutorsMethod(null);
    }

    try {
      model.setNumberOrPercent(rs.getString("NUMBER_OR_PERCENT"));
      if (rs.wasNull())
        model.setNumberOrPercent(null);
    } catch (Exception e) {
      model.setNumberOrPercent(null);
    }

    try {
      model.setPassValue(rs.getDouble("PASS_VALUE"));
      if (rs.wasNull())
        model.setPassValue(null);
    } catch (Exception e) {
      model.setPassValue(null);
    }

    try {
      model.setExpression(rs.getString("EXPRESSION"));
      if (rs.wasNull())
        model.setExpression(null);
    } catch (Exception e) {
      model.setExpression(null);
    }

    try {
      model.setDefaultPath(rs.getString("DEFAULT_PATH"));
      if (rs.wasNull())
        model.setDefaultPath(null);
    } catch (Exception e) {
      model.setDefaultPath(null);
    }

    try {
      model.setActionName(rs.getString("ACTION_NAME"));
      if (rs.wasNull())
        model.setActionName(null);
    } catch (Exception e) {
      model.setActionName(null);
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