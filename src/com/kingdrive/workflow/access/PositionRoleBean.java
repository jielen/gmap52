package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class PositionRoleBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(PositionRoleBean.class);

  public PositionRoleBean() {
  }

  public void delete(String positionId, String roleId) throws SQLException {
    String sql = "delete from WF_POSITION_ROLE where POSITION_ID=? and ROLE_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, positionId);
      st.setString(2, roleId);
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

  public ArrayList find(int theBegin, int theEnd) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select POSITION_ID, ROLE_ID from WF_POSITION_ROLE";
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

  public PositionRoleModel findByKey(String positionId, String roleId)
    throws SQLException {
    PositionRoleModel model = new PositionRoleModel();
    String sql = "select POSITION_ID, ROLE_ID from WF_POSITION_ROLE where POSITION_ID=? and ROLE_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, positionId);
      st.setString(2, roleId);
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

  public int insert(PositionRoleModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getPositionIdModifyFlag()) {
      StringUtil.makeDynaParam("POSITION_ID", convertSQL(model.getPositionId()),
        strList, valList);
    }
    if (model.getRoleIdModifyFlag()) {
      StringUtil.makeDynaParam("ROLE_ID", convertSQL(model.getRoleId()), strList,
        valList);
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
    sql = "insert into WF_POSITION_ROLE(" + insertString + ") values(" + valsString
      + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  /**
   * 此为视图，没有修改的列，所以暂时没改
   * @param model
   * @param con
   * @return
   * @throws SQLException
   */
  public int update(PositionRoleModel model) throws SQLException {
    String sql = null;
    String updateString = null;
    String whereString = null;
    Statement st = null;

    if (updateString == null)
      return 0;

    if (whereString == null) {
      whereString = "";
    } else {
      whereString += " and ";
    }
    if (model.getPositionId() == null) {
      return 0;
    }
    whereString += "POSITION_ID = '" + convertSQL(model.getPositionId()) + "'";
    if (whereString == null) {
      whereString = "";
    } else {
      whereString += " and ";
    }
    if (model.getRoleId() == null) {
      return 0;
    }
    whereString += "ROLE_ID = '" + convertSQL(model.getRoleId()) + "'";
    if (whereString == null)
      return 0;

    sql = "update WF_POSITION_ROLE set " + updateString + " where " + whereString;
    Connection con = null;
    int rc = 0;
    try {
      con = ConnectionFactory.getConnection();
      st = con.createStatement();
      rc = st.executeUpdate(sql);
      logger.info(sql);
      st.close();
    } finally {
      DBHelper.closeConnection(con, st, null);
    }
    return rc;
  }

  public void add(String positionId, String roleId) throws SQLException {
    String sql = "insert into wf_position_role(position_id, role_id) values(?, ?)";
    PreparedStatement st = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, positionId);
      st.setString(2, roleId);
      st.executeUpdate();
      logger.info(sql);

    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, null);
    }
  }

  public void removeByPosition(String positionId) throws SQLException {
    String sql = "delete from wf_position_role where position_id = ?";
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
      DBHelper.closeConnection(conn, st, null);
    }
  }

  public void removeByRole(String roleId) throws SQLException {
    String sql = "delete from wf_position_role where role_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
      conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, roleId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, null);
    }
  }

  private PositionRoleModel parseResultSet(ResultSet rs) throws SQLException {
    PositionRoleModel model = new PositionRoleModel();
    try {
      model.setPositionId(rs.getString("POSITION_ID"));
      if (rs.wasNull())
        model.setPositionId(null);
    } catch (Exception e) {
      model.setPositionId(null);
    }

    try {
      model.setRoleId(rs.getString("ROLE_ID"));
      if (rs.wasNull())
        model.setRoleId(null);
    } catch (Exception e) {
      model.setRoleId(null);
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