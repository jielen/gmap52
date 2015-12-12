package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.OrgInfo;

public class OrgQuery {

  public OrgQuery() {
  }

  public OrgInfo getOrg(String organizationId)
      throws SQLException {
    OrgInfo model = new OrgInfo();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b where a.company_id = b.company_id and a.organization_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, organizationId);
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

  public ArrayList getOrgList() throws SQLException {
    return getOrgList(-1, -1);
  }

  public ArrayList getOrgList(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b where a.company_id = b.company_id";
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

  public ArrayList getOrgListByExecutor(int nodeId,
      int responsibility) throws SQLException {
    return getOrgListByExecutor(-1, -1, nodeId, responsibility);
  }

  public ArrayList getOrgListByExecutor(int theBegin,
      int theEnd, int nodeId, int responsibility) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b, wf_executor_source c where a.company_id = b.company_id and a.organization_id = c.executor and c.source = 2 and c.node_id =? and c.responsibility = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setInt(1, nodeId);
      st.setInt(2, responsibility);
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

  public ArrayList getOrgListByNonExecutor(int nodeId)
      throws SQLException {
    return getOrgListByNonExecutor(-1, -1, nodeId);
  }

  public ArrayList getOrgListByNonExecutor(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b where a.company_id = b.company_id and not exists (select 1 from  wf_executor_source c where a.organization_id = c.executor and c.source = 2 and c.node_id =?)";
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

  public ArrayList getOrgListByParent(String parentId)
      throws SQLException {
    return getOrgListByParent(-1, -1, parentId);
  }

  public ArrayList getOrgListByParent(int theBegin,
      int theEnd, String parentId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b where a.company_id = b.company_id and a.parent_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, parentId);
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

  public ArrayList getOrgListByCompany(String companyId)
      throws SQLException {
    return getOrgListByCompany(-1, -1, companyId);
  }

  public ArrayList getOrgListByCompany(int theBegin,
      int theEnd, String companyId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name company_name from wf_org a, wf_company b where a.company_id = b.company_id and a.company_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, companyId);
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

  private OrgInfo parseResultSet(ResultSet rs) throws SQLException {
    OrgInfo model = new OrgInfo();
    try {
      model.setOrganizationId(rs.getString("ORGANIZATION_ID"));
      if (rs.wasNull())
        model.setOrganizationId(null);
    } catch (Exception e) {
      model.setOrganizationId(null);
    }

    try {
      model.setCompanyId(rs.getString("COMPANY_ID"));
      if (rs.wasNull())
        model.setCompanyId(null);
    } catch (Exception e) {
      model.setCompanyId(null);
    }

    try {
      model.setCompanyName(rs.getString("COMPANY_NAME"));
      if (rs.wasNull())
        model.setCompanyName(null);
    } catch (Exception e) {
      model.setCompanyName(null);
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
      model.setParentId(rs.getString("PARENT_ID"));
      if (rs.wasNull())
        model.setParentId(null);
    } catch (Exception e) {
      model.setParentId(null);
    }

    return model;
  }
}
