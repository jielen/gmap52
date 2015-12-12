package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.StaffPositionInfo;

public class StaffPositionQuery {

  public StaffPositionQuery() {
  }

  public ArrayList getStaffPositionList(String staffId)
      throws SQLException {
    return getStaffPositionList(-1, -1, staffId);
  }

  public ArrayList getStaffPositionList(int theBegin,
      int theEnd, String staffId) throws SQLException {
    ArrayList list = new ArrayList();
    //此sql语句意在提取该user所任职的职位名、内部机构名、单位名
    //String sql = "select a.org_position_id, c.company_id, c.name as company_name, b.organization_id, c.name as organization_name, b.position_id, d.name as position_name, a.staff_id, f.name as staff_name from wf_staff_position a, wf_org_position b, wf_org c, wf_company d, wf_position e, wf_staff f where a.staff_id = ? and a.org_position_id = b.org_position_id and b.organization_id = c.organization_id and c.company_id = d.company_id and b.position_id = e.position_id and a.staff_id = f.staff_id and a.nd=d.ND";
    //原则上wf包中不允许使用wf_*以外的表，但上语句反复使用试图，速度实在太慢。
    //只好突破这一原则，以提高速度。
    String sql = "SELECT B.CO_CODE company_id,d.co_name company_name,"
      +"B.ORG_POSI_ID ORG_POSITION_ID,C.USER_ID STAFF_ID,c.emp_name staff_name,"
      + "a.org_code organization_id,e.org_name organization_name,"
      + "a.posi_code position_id,f.posi_name position_name "
      + "FROM AS_EMP_POSITION A, AS_ORG_POSITION B, AS_EMP C,ma_company d,"
      + "as_org e,as_position f "
      + "WHERE A.emp_code=? and A.CO_CODE = B.CO_CODE "
      + "AND A.ORG_CODE = B.ORG_CODE AND A.POSI_CODE = B.POSI_CODE "
      + "AND A.EMP_CODE = C.EMP_CODE AND A.ND = B.ND and a.co_code=d.co_code "
      + "and a.nd=d.nd and a.co_code=e.co_code and a.org_code=e.org_code "
      + "and a.nd=e.nd  and a.posi_code=f.posi_code";
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

  private StaffPositionInfo parseResultSet(ResultSet rs) throws SQLException {
    StaffPositionInfo model = new StaffPositionInfo();
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

    try {
      model.setStaffId(rs.getString("STAFF_ID"));
      if (rs.wasNull())
        model.setStaffId(null);
    } catch (Exception e) {
      model.setStaffId(null);
    }

    try {
      model.setStaffName(rs.getString("STAFF_NAME"));
      if (rs.wasNull())
        model.setStaffName(null);
    } catch (Exception e) {
      model.setStaffName(null);
    }

    return model;
  }
}
