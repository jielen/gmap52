package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class OrgBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(OrgBean.class);

  public OrgBean() {
  }

  public void delete(String organizationId)
      throws SQLException {
    String sql = "delete from WF_ORG where ORGANIZATION_ID=?";
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

  public ArrayList find() throws SQLException {
    return find(-1, -1);
  }

  public ArrayList find(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select ORGANIZATION_ID, COMPANY_ID, NAME, DESCRIPTION, PARENT_ID from WF_ORG";
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

  public OrgModel findByKey(String organizationId)
      throws SQLException {
    OrgModel model = new OrgModel();
    String sql = "select ORGANIZATION_ID, COMPANY_ID, NAME, DESCRIPTION, PARENT_ID from WF_ORG where ORGANIZATION_ID=?";
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

  public int insert(OrgModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getOrganizationIdModifyFlag()) {
      StringUtil.makeDynaParam("ORGANIZATION_ID",convertSQL(model.getOrganizationId()), strList,valList);
          }
    if (model.getCompanyIdModifyFlag()) {
      StringUtil.makeDynaParam("COMPANY_ID",convertSQL(model.getCompanyId()), strList,valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME",convertSQL(model.getName()), strList,valList);
        }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION",convertSQL(model.getDescription()), strList,valList);
          }
    if (model.getParentIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_ID",convertSQL(model.getParentId()), strList,valList);
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
    sql = "insert into WF_ORG(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(OrgModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getCompanyIdModifyFlag()) {
      StringUtil.makeDynaParam("COMPANY_ID = ", convertSQL(model.getCompanyId()), strList,valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ",convertSQL(model.getName()), strList,valList);
    } 
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()), strList,valList);
         }
    if (model.getParentIdModifyFlag()) {
      StringUtil.makeDynaParam("PARENT_ID = ",convertSQL(model.getParentId()), strList,valList);
          }
    if (strList.size() == 0) return 0;
    valList.add(convertSQL(model.getOrganizationId()));
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_ORG set " + updateString + " where ORGANIZATION_ID = ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByCompany(String companyId)
      throws SQLException {
    String sql = "delete from wf_org where company_id = ?";
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

  public void clearParent(String parentId) throws SQLException {
    String sql = "update wf_org set parent_id = '-1' where parent_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, parentId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  private OrgModel parseResultSet(ResultSet rs) throws SQLException {
    OrgModel model = new OrgModel();
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