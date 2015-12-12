/*
 * 20050321增加REMIND_EXECUTE_TERM的支持．
 */
package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.NodeInfo;

public class NodeQuery {

  public NodeQuery() {
  }

  public NodeInfo getNode(int nodeId) throws SQLException {
    NodeInfo model = new NodeInfo();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b where a.template_id = b.template_id and a.node_id = ?";
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

  public ArrayList getNodeListByTemplate(int templateId)
      throws SQLException {
    return getNodeListByTemplate(-1, -1, templateId);
  }

  public ArrayList getNodeListByTemplate(int theBegin,
      int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b where a.template_id = b.template_id and a.template_id = ?";
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

  public ArrayList getTaskNodeListByTemplate(int templateId)
      throws SQLException {
    return getTaskNodeListByTemplate(-1, -1, templateId);
  }

  public ArrayList getTaskNodeListByTemplate(int theBegin,
      int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b where a.type = '2' and a.template_id = b.template_id and a.template_id = ?";
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

  public NodeInfo getStartNode(int templateId)
      throws SQLException {
    NodeInfo model = new NodeInfo();
    //String sql = "select a.*, b.name template_name from wf_node a, wf_template b, wf_link c where a.template_id = b.template_id and a.template_id = c.template_id and a.node_id = c.next_node_id and c.current_node_id = -1 and a.template_id = ?";
    String sql = "select a.*, '' template_name from wf_node a, wf_link c "
      + " where a.template_id = ? and a.template_id = c.template_id "
      + " and c.current_node_id = -1 and a.node_id = c.next_node_id ";

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
 
  public ArrayList getNodeListByStaff(String executor)
      throws SQLException {
    return getNodeListByStaff(-1, -1, executor);
  }

  public ArrayList getNodeListByStaff(int theBegin,
      int theEnd, String executor) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b, v_wf_executor_source c where a.type = '2' and a.template_id = b.template_id and b.is_active = '0' and a.node_id = c.node_id and c.executor = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
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

  public ArrayList getNodeListByStaff(String executor,
      int templateId) throws SQLException {
    return getNodeListByStaff(-1, -1, executor, templateId);
  }

  public ArrayList getNodeListByStaff(int theBegin,
      int theEnd, String executor, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b, v_wf_executor_source c where a.type = '2' and a.template_id = b.template_id and b.is_active = '0' and a.node_id = c.node_id and d.executor = ? and a.template_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setInt(2, templateId);
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

  public ArrayList getNodeListByStaff(String executor,
      String templateType) throws SQLException {
    return getNodeListByStaff(-1, -1, executor, templateType);
  }

  public ArrayList getNodeListByStaff(int theBegin,
      int theEnd, String executor, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name from wf_node a, wf_template b, v_wf_executor_source c where a.type = '2' and a.template_id = b.template_id and b.is_active = '0' and a.node_id = c.node_id and d.executor = ? and b.template_type like ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, executor);
      st.setString(2, templateType);
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

  private NodeInfo parseResultSet(ResultSet rs) throws SQLException {
    NodeInfo model = new NodeInfo();
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
      model.setTemplateName(rs.getString("TEMPLATE_NAME"));
      if (rs.wasNull())
        model.setTemplateName(null);
    } catch (Exception e) {
      model.setTemplateName(null);
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
      model.setRemindExecuteTerm(rs.getInt("REMIND_EXECUTE_TERM"));
      if (rs.wasNull())
        model.setRemindExecuteTerm(null);
    } catch (Exception e) {
      model.setRemindExecuteTerm(null);
    }

    return model;
  }
}
