package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.InstanceInfo;

public class InstanceQuery {

  public InstanceQuery() {
  }

  public InstanceInfo getInstance(int instanceId)
      throws SQLException {
    InstanceInfo model = new InstanceInfo();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.instance_id = ?";
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

  public ArrayList getInstanceList() throws SQLException {
    return getInstanceList(-1, -1);
  }

  public ArrayList getInstanceList(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id";
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

  public ArrayList getInstanceListByTemplate(int templateId)
      throws SQLException {
    return getInstanceListByTemplate(-1, -1, templateId);
  }

  public ArrayList getInstanceListByTemplate(int theBegin,
      int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.template_id = ?";
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

  public ArrayList getInstanceListByTemplate(int templateId,
      String owner) throws SQLException {
    return getInstanceListByTemplate(-1, -1, templateId, owner);
  }

  public ArrayList getInstanceListByTemplate(int theBegin,
      int theEnd, int templateId, String owner) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.template_id = ? and a.owner = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setString(2, owner);
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

  public ArrayList getActiveInstanceList() throws SQLException {
    return getActiveInstanceList(-1, -1);
  }

  public ArrayList getActiveInstanceList(int theBegin,
      int theEnd) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.status = 1";
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

  public ArrayList getActiveInstanceListByTemplate(int templateId) throws SQLException {
    return getActiveInstanceListByTemplate(-1, -1, templateId);
  }

  public ArrayList getActiveInstanceListByTemplate(int theBegin, int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.template_id = ? and a.status = 1";
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

  public ArrayList getActiveInstanceListByTemplate(String templateType) throws SQLException {
    return getActiveInstanceListByTemplate(-1, -1, templateType);
  }

  public ArrayList getActiveInstanceListByTemplate(int theBegin, int theEnd, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and b.template_type like ? and a.status = 1";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, templateType);
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
    	DBHelper.closeConnection(conn,st,null);
    }

    return list;
  }

  public ArrayList getActiveInstanceListByTemplate(
      int templateId, String owner) throws SQLException {
    return getActiveInstanceListByTemplate(-1, -1, templateId, owner);
  }

  public ArrayList getActiveInstanceListByTemplate(
      int theBegin, int theEnd, int templateId, String owner)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name template_name, c.name owner_name from wf_instance a, wf_template b, wf_staff c where a.template_id = b.template_id and a.owner = c.staff_id and a.status = 1 and a.template_id = ? and a.owner = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setString(2, owner);
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
  public ArrayList getChildInstanceListByParentInstance(int parentInstanceId) throws SQLException {
        ArrayList list = new ArrayList();
        String sql = "select a.*, b.name template_name, c.name owner_name " +
        		"from wf_instance a, wf_template b, wf_staff c where " +
        		"a.template_id = b.template_id and a.owner = c.staff_id and a.parent_instance_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
        	conn = ConnectionFactory.getConnection();
          st = conn.prepareStatement(sql);
          st.setInt(1, parentInstanceId);
          rs = st.executeQuery();
          while (rs.next()) {
            list.add(parseResultSet(rs));
          }
        } catch (SQLException e) {
          e.printStackTrace();
          throw new SQLException(e.getMessage());
        } finally {
        	DBHelper.closeConnection(conn,st,rs);
        }

        return list;
      }
  private InstanceInfo parseResultSet(ResultSet rs) throws SQLException {
    InstanceInfo model = new InstanceInfo();
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

		try{
			model.setStatus(rs.getInt("STATUS"));
			if(rs.wasNull()) model.setStatus(null); 
		}catch(Exception e){
			model.setStatus(null);
		}

		try{
			model.setParentInstanceId(rs.getInt("PARENT_INSTANCE_ID"));
			if(rs.wasNull()) model.setParentInstanceId(null); 
		}catch(Exception e){
			model.setParentInstanceId(null);
		}

    return model;
  }
}
