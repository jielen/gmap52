package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.OrgPositionModel;
import com.kingdrive.workflow.util.StringUtil;

public class OrgPositionBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(OrgPositionBean.class);

  public OrgPositionBean() {
  }

  public void delete(String orgPositionId) throws SQLException {
    String sql = "delete from WF_ORG_POSITION where ORG_POSITION_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, orgPositionId);
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
    String sql = "select ORG_POSITION_ID, ORGANIZATION_ID, POSITION_ID from WF_ORG_POSITION";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
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

  public OrgPositionModel findByKey(String orgPositionId)
      throws SQLException {
    OrgPositionModel model = new OrgPositionModel();
    String sql = "select ORG_POSITION_ID, ORGANIZATION_ID, POSITION_ID from WF_ORG_POSITION where ORG_POSITION_ID=?";
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

  public int insert(OrgPositionModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getOrgPositionIdModifyFlag()) {
      StringUtil.makeDynaParam("ORG_POSITION_ID", convertSQL(model.getOrgPositionId()), strList,valList);
          }
    if (model.getOrganizationIdModifyFlag()) {
      StringUtil.makeDynaParam("ORGANIZATION_ID", convertSQL(model.getOrganizationId()), strList,valList);
          }
    if (model.getPositionIdModifyFlag()) {
      StringUtil.makeDynaParam("POSITION_ID", convertSQL(model.getPositionId()), strList,valList);
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
    sql = "insert into WF_ORG_POSITION(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(OrgPositionModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getOrganizationIdModifyFlag()) {
      StringUtil.makeDynaParam("ORGANIZATION_ID = ", convertSQL(model.getOrganizationId()), strList,valList);
         }
    if (model.getPositionIdModifyFlag()) {
      StringUtil.makeDynaParam("POSITION_ID = ", convertSQL(model.getPositionId()), strList,valList);
          }
    if (strList.size() == 0) return 0;
    valList.add(convertSQL(model.getOrgPositionId()));
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_ORG_POSITION set " + updateString + " where ORG_POSITION_ID = ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }
  
  public void removeByPosition(String positionId)
      throws SQLException {
    String sql = "delete from wf_org_position where position_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, positionId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByCompany(String companyId)
      throws SQLException {
    String sql = "delete a from wf_org_position a, wf_org b where a.organization_id = b.organization_id and b.company_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, companyId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByOrg(String organizationId)
      throws SQLException {
    String sql = "delete from wf_org_position where organization_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, organizationId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn,st,null);
    }
  }

  private OrgPositionModel parseResultSet(ResultSet rs) throws SQLException {
    OrgPositionModel model = new OrgPositionModel();
    try {
      model.setOrgPositionId(rs.getString("ORG_POSITION_ID"));
      if (rs.wasNull())
        model.setOrgPositionId(null);
    } catch (Exception e) {
      model.setOrgPositionId(null);
    }

    try {
      model.setOrganizationId(rs.getString("ORGANIZATION_ID"));
      if (rs.wasNull())
        model.setOrganizationId(null);
    } catch (Exception e) {
      model.setOrganizationId(null);
    }

    try {
      model.setPositionId(rs.getString("POSITION_ID"));
      if (rs.wasNull())
        model.setPositionId(null);
    } catch (Exception e) {
      model.setPositionId(null);
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