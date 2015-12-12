package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.DelegationInfo;

public class DelegationQuery {

  public DelegationQuery() {
  }

  public DelegationInfo getDelegation(int delegationId)
      throws SQLException {
    DelegationInfo model = new DelegationInfo();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.delegation_id = ?";
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

  public ArrayList findByReceiver(String receiver,
      String currentTime) throws SQLException {
    return findByReceiver(-1, -1, receiver, currentTime);
  }

  public ArrayList findByReceiver(int theBegin, int theEnd,
      String receiver, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.receiver = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setString(1, receiver);
      st.setString(2, currentTime);
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

  public ArrayList findByReceiver(String receiver,
      String templateType, String currentTime) throws SQLException {
    return findByReceiver(-1, -1, receiver, templateType, currentTime);
  }

  public ArrayList findByReceiver(int theBegin, int theEnd,
      String receiver, String templateType, String currentTime)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.receiver = ? and b.template_type like ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setString(1, receiver);
      st.setString(2, templateType);
      st.setString(3, currentTime);
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

  public ArrayList findByReceiver(int nodeId, String receiver,
      String currentTime) throws SQLException {
    return findByReceiver(-1, -1, nodeId, receiver, currentTime);
  }

  public ArrayList findByReceiver(int theBegin, int theEnd,
      int nodeId, String receiver, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.node_id = ? and a.receiver = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setInt(1, nodeId);
      st.setString(2, receiver);
      st.setString(3, currentTime);
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

  public ArrayList findBySender(String sender,
      String currentTime) throws SQLException {
    return findBySender(-1, -1, sender, currentTime);
  }

  public ArrayList findBySender(int theBegin, int theEnd,
      String sender, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.sender = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setString(1, sender);
      st.setString(2, currentTime);
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

  public ArrayList findBySender(int nodeId, String sender,
      String currentTime) throws SQLException {
    return findBySender(-1, -1, nodeId, sender, currentTime);
  }

  public ArrayList findBySender(int theBegin, int theEnd,
      int nodeId, String sender, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.node_id = ? and a.sender = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setInt(1, nodeId);
      st.setString(2, sender);
      st.setString(3, currentTime);
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

  public ArrayList findByOwner(String owner, String currentTime)
      throws SQLException {
    return findByOwner(-1, -1, owner, currentTime);
  }

  public ArrayList findByOwner(int theBegin, int theEnd,
      String owner, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.owner = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setString(1, owner);
      st.setString(2, currentTime);
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

  public ArrayList findByOwner(int nodeId, String owner,
      String currentTime) throws SQLException {
    return findByOwner(-1, -1, nodeId, owner, currentTime);
  }

  public ArrayList findByOwner(int theBegin, int theEnd,
      int nodeId, String owner, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.node_id = ? and a.owner = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setInt(1, nodeId);
      st.setString(2, owner);
      st.setString(3, currentTime);
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

  public ArrayList findNext(int parentId, String currentTime)
      throws SQLException {
    return findNext(-1, -1, parentId, currentTime);
  }

  public ArrayList findNext(int theBegin, int theEnd,
      int parentId, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name node_name, sender.name sender_name, owner.name owner_name, receiver.name receiver_name from wf_delegation a, wf_template b, wf_node c, wf_staff sender, wf_staff owner, wf_staff receiver where a.template_id = b.template_id and a.node_id = c.node_id and a.sender = sender.staff_id and a.owner = owner.staff_id and a.receiver = receiver.staff_id and a.parent_id = ? and ? between a.start_time and a.end_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st.setInt(1, parentId);
      st.setString(2, currentTime);
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

  private DelegationInfo parseResultSet(ResultSet rs) throws SQLException {
    DelegationInfo model = new DelegationInfo();
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
      model.setTemplateName(rs.getString("TEMPLATE_NAME"));
      if (rs.wasNull())
        model.setTemplateName(null);
    } catch (Exception e) {
      model.setTemplateName(null);
    }

    try {
      model.setNodeId(rs.getInt("NODE_ID"));
      if (rs.wasNull())
        model.setNodeId(null);
    } catch (Exception e) {
      model.setNodeId(null);
    }

    try {
      model.setNodeName(rs.getString("NODE_NAME"));
      if (rs.wasNull())
        model.setNodeName(null);
    } catch (Exception e) {
      model.setNodeName(null);
    }

    try {
      model.setSender(rs.getString("SENDER"));
      if (rs.wasNull())
        model.setSender(null);
    } catch (Exception e) {
      model.setSender(null);
    }

    try {
      model.setSenderName(rs.getString("SENDER_NAME"));
      if (rs.wasNull())
        model.setSenderName(null);
    } catch (Exception e) {
      model.setSenderName(null);
    }

    try {
      model.setOwner(rs.getString("OWNER"));
      if (rs.wasNull())
        model.setOwner(null);
    } catch (Exception e) {
      model.setOwner(null);
    }

    try {
      model.setOwnerName(rs.getString("OWNER_NAME"));
      if (rs.wasNull())
        model.setOwnerName(null);
    } catch (Exception e) {
      model.setOwnerName(null);
    }

    try {
      model.setReceiver(rs.getString("RECEIVER"));
      if (rs.wasNull())
        model.setReceiver(null);
    } catch (Exception e) {
      model.setReceiver(null);
    }

    try {
      model.setReceiverName(rs.getString("RECEIVER_NAME"));
      if (rs.wasNull())
        model.setReceiverName(null);
    } catch (Exception e) {
      model.setReceiverName(null);
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
}
