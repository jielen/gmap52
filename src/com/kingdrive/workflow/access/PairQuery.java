package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.PairInfo;

public class PairQuery {

  public PairQuery() {
  }

  public ArrayList getBusinessByTemplate(int templateId)
      throws SQLException {
    return getBusinessByTemplate(-1, -1, templateId);
  }

  public ArrayList getBusinessByTemplate(int theBegin,
      int theEnd, int templateId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select distinct a.business_type id, a.business_type content, b.template_type reference from wf_node a, wf_template b where a.template_id = b.template_id and b.template_id = ?";
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

  public ArrayList getBusinessByTemplate(String templateType)
      throws SQLException {
    return getBusinessByTemplate(-1, -1, templateType);
  }

  public ArrayList getBusinessByTemplate(int theBegin,
      int theEnd, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select distinct a.business_type id, a.business_type content, b.template_type reference from wf_node a, wf_template b where a.template_id = b.template_id and b.template_type = ?";
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
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ArrayList getActionByNode(int templateId, int nodeId)
      throws SQLException {
    return getActionByNode(-1, -1, templateId, nodeId);
  }

  public ArrayList getActionByNode(int theBegin, int theEnd,
      int templateId, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
		String sql = "select distinct a.action_name id, a.action_name content, b.business_type reference" +
				", a.default_path defaultPath,a.node_link_id from wf_link a, wf_node b, wf_template c "+
				"where a.action_name is not null and a.current_node_id = b.node_id and b.template_id = c.template_id and c.template_id = ? and b.node_id = ? order by a.node_link_id";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, templateId);
      st.setInt(2, nodeId);
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

  public ArrayList getActionByNode(String templateType,
      String businessType) throws SQLException {
    return getActionByNode(-1, -1, templateType, businessType);
  }

	public ArrayList getActionByNode(int theBegin, int theEnd, 
	        String templateType, String businessType) throws SQLException {
		ArrayList list = new ArrayList();
		String sql = "select distinct a.action_name id, a.action_name content, " +
				"b.business_type reference, a.default_path defaultPath from wf_link a, wf_node b, wf_template c where a.action_name is not null and a.current_node_id = b.node_id and b.template_id = c.template_id and c.template_type = ? and b.business_type = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, templateType);
      st.setString(2, businessType);
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

  private PairInfo parseResultSet(ResultSet rs) throws SQLException {
    PairInfo model = new PairInfo();
    try {
      model.setId(rs.getString("id"));
      if (rs.wasNull())
        model.setId(null);
    } catch (Exception e) {
      model.setId(null);
    }

    try {
      model.setContent(rs.getString("content"));
      if (rs.wasNull())
        model.setContent(null);
    } catch (Exception e) {
      model.setContent(null);
    }

		try{
			model.setReference(rs.getString("reference"));
			if(rs.wasNull()) model.setReference(null); 
		}catch(Exception e){
			model.setReference(null);
		}

		try{
			model.setDefaultPath(rs.getString("defaultPath"));
			if(rs.wasNull()) model.setDefaultPath(null); 
		}catch(Exception e){
			model.setDefaultPath(null);
		}

    return model;
  }
}
