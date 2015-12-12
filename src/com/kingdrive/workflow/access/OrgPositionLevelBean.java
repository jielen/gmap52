package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.OrgPositionLevelModel;
import com.kingdrive.workflow.util.StringUtil;

public class OrgPositionLevelBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(OrgPositionLevelBean.class);

  public OrgPositionLevelBean() {
  }

  public void delete(String orgPositionId, String parentId)
      throws SQLException {
    String sql = "delete from WF_ORG_POSITION_LEVEL where ORG_POSITION_ID=? and PARENT_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, orgPositionId);
      st.setString(2, parentId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, null);
    }
  }

  public ArrayList find() throws SQLException {
    return find(-1, -1);
  }

  public ArrayList find(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select ORG_POSITION_ID, PARENT_ID from WF_ORG_POSITION_LEVEL";
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
      DBHelper.closeConnection(conn, st, rs);
    }

    return list;
  }

  public OrgPositionLevelModel findByKey(String orgPositionId,
      String parentId) throws SQLException {
    OrgPositionLevelModel model = new OrgPositionLevelModel();
    String sql = "select ORG_POSITION_ID, PARENT_ID from WF_ORG_POSITION_LEVEL where ORG_POSITION_ID=? and PARENT_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, orgPositionId);
      st.setString(2, parentId);
      rs = st.executeQuery();
      if (rs.next()) {
        model = parseResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    return model;
  }

  public int insert(OrgPositionLevelModel model)
      throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getOrgPositionIdModifyFlag()) {
      StringUtil.makeDynaParam("ORG_POSITION_ID",convertSQL(model.getOrgPositionId()), strList,valList);
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
    sql = "insert into WF_ORG_POSITION_LEVEL(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  /**
   * 没有可修改的列值，所以没有做改动
   * @param model
   * @param con
   * @return
   * @throws SQLException
   */
  public int update(OrgPositionLevelModel model)
      throws SQLException {
    String sql = null;
    String updateString = null;
    String whereString = null;
    Statement st = null;
    Connection conn = null;
    
    if (updateString == null)
      return 0;

    if (whereString == null) {
      whereString = "";
    } else {
      whereString += " and ";
    }
    if (model.getOrgPositionId() == null) {
      return 0;
    }
    whereString += "ORG_POSITION_ID = '" + convertSQL(model.getOrgPositionId())
        + "'";
    if (whereString == null) {
      whereString = "";
    } else {
      whereString += " and ";
    }
    if (model.getParentId() == null) {
      return 0;
    }
    whereString += "PARENT_ID = '" + convertSQL(model.getParentId()) + "'";
    if (whereString == null)
      return 0;

    sql = "update WF_ORG_POSITION_LEVEL set " + updateString + " where "
        + whereString;
    int rc = 0;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.createStatement();
      rc = st.executeUpdate(sql);
      logger.info(sql);
    } catch (SQLException ex) {
      throw ex;
    } finally {
      DBHelper.closeConnection(conn, st, null);
    }
    return rc;
  }

  public void removeParentByCompany(String companyId)
      throws SQLException {
    String sql = "delete a from wf_org_position_level a, wf_org_position b, wf_org c where a.parent_id = b.org_position_id and b.organization_id = c.organization_id and c.company_id = ?";
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

  public void removeByParent(String parentId)
      throws SQLException {
    String sql = "delete from wf_org_position_level where parent_id = ?";
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

  public void removeByOrgPosition(String orgPositionId)
      throws SQLException {
    String sql = "delete from wf_org_position_level where org_position_id = ?";
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

  public void removeParentByPosition(String positionId)
      throws SQLException {
    String sql = "delete a from wf_org_position_level a, wf_org_position b where a.parent_id = b.org_position_id and b.position_id = ?";
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

  public void removeByPosition(String positionId)
      throws SQLException {
    String sql = "delete a from wf_org_position_level a, wf_org_position b where a.org_position_id = b.org_position_id and b.position_id = ?";
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

  public void removeParentByOrg(String organizationId)
      throws SQLException {
    String sql = "delete a from wf_org_position_level a, wf_org_position b where a.parent_id = b.org_position_id and b.organization_id = ?";
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

  public void removeByCompany(String companyId)
      throws SQLException {
    String sql = "delete a from wf_org_position_level a, wf_org_position b, wf_org c where a.org_position_id = b.org_position_id and b.organization_id = c.organization_id and c.company_id = ?";
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
    String sql = "delete a from wf_org_position_level a, wf_org_position b where a.org_position_id = b.org_position_id and b.organization_id = ?";
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

  private OrgPositionLevelModel parseResultSet(ResultSet rs)
      throws SQLException {
    OrgPositionLevelModel model = new OrgPositionLevelModel();
    try {
      model.setOrgPositionId(rs.getString("ORG_POSITION_ID"));
      if (rs.wasNull())
        model.setOrgPositionId(null);
    } catch (Exception e) {
      model.setOrgPositionId(null);
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