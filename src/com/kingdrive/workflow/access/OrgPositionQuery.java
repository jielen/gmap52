package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.OrgPositionInfo;

public class OrgPositionQuery {

  public OrgPositionQuery() {
  }

  public OrgPositionInfo getOrgPosition(String orgPositionId)
      throws SQLException {
    OrgPositionInfo model = new OrgPositionInfo();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and a.org_position_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, orgPositionId);
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

  public ArrayList getOrgPositionList() throws SQLException {
    return getOrgPositionList(-1, -1);
  }

  public ArrayList getOrgPositionList(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id";
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

  public ArrayList getOrgPositionListByCompany(String companyId)
      throws SQLException {
    return getOrgPositionListByCompany(-1, -1, companyId);
  }

  public ArrayList getOrgPositionListByCompany(int theBegin,
      int theEnd, String companyId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and c.company_id = ?";
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

  public ArrayList getOrgPositionListByOrg(String organizationId) throws SQLException {
    return getOrgPositionListByOrg(-1, -1, organizationId);
  }

  public ArrayList getOrgPositionListByOrg(int theBegin,
      int theEnd, String organizationId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and a.organization_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, organizationId);
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

  public ArrayList getOrgPositionListByPosition(
      String positionId) throws SQLException {
    return getOrgPositionListByPosition( -1, -1, positionId);
  }

  public ArrayList getOrgPositionListByPosition(int theBegin,
      int theEnd, String positionId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and a.position_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, positionId);
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

  public ArrayList getOrgPositionListByStaff(String staffId)
      throws SQLException {
    return getOrgPositionListByStaff( -1, -1, staffId);
  }

  public ArrayList getOrgPositionListByStaff(int theBegin,
      int theEnd, String staffId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d, wf_staff_position e where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and a.org_position_id = e.org_position_id and e.staff_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, staffId);
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

  public ArrayList getSuperOrgPositionList(String orgPositionId)
      throws SQLException {
    return getSuperOrgPositionList(-1, -1, orgPositionId);
  }

  public ArrayList getSuperOrgPositionList(int theBegin,
      int theEnd, String orgPositionId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.org_position_id, c.company_id, c.name as company_name, a.organization_id, b.name as organization_name, a.position_id, d.name as position_name from wf_org_position a, wf_org b, wf_company c, wf_position d, wf_org_position_level l where a.organization_id = b.organization_id and b.company_id = c.company_id and a.position_id = d.position_id and a.org_position_id = l.parent_id and l.org_position_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, orgPositionId);
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

  private OrgPositionInfo parseResultSet(ResultSet rs) throws SQLException {
    OrgPositionInfo model = new OrgPositionInfo();
    try {
      model.setOrgPositionId(rs.getString("ORG_POSITION_ID"));
      if (rs.wasNull())
        model.setOrgPositionId(null);
    } catch (Exception e) {
      model.setOrgPositionId(null);
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
      model.setOrganizationId(rs.getString("ORGANIZATION_ID"));
      if (rs.wasNull())
        model.setOrganizationId(null);
    } catch (Exception e) {
      model.setOrganizationId(null);
    }

    try {
      model.setOrganizationName(rs.getString("ORGANIZATION_NAME"));
      if (rs.wasNull())
        model.setOrganizationName(null);
    } catch (Exception e) {
      model.setOrganizationName(null);
    }

    try {
      model.setPositionId(rs.getString("POSITION_ID"));
      if (rs.wasNull())
        model.setPositionId(null);
    } catch (Exception e) {
      model.setPositionId(null);
    }

    try {
      model.setPositionName(rs.getString("POSITION_NAME"));
      if (rs.wasNull())
        model.setPositionName(null);
    } catch (Exception e) {
      model.setPositionName(null);
    }

    return model;
  }
}
