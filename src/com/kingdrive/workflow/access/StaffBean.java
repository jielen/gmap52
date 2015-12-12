package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class StaffBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(StaffBean.class);

  public StaffBean() {
  }

  public void delete(String staffId) throws SQLException {
    String sql = "delete from WF_STAFF where STAFF_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, staffId);
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
    String sql = "select STAFF_ID, NAME, PWD, DESCRIPTION, EMAIL, STATUS from WF_STAFF";
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

  public StaffModel findByKey(String staffId)
      throws SQLException {
    StaffModel model = new StaffModel();
    String sql = "select STAFF_ID, NAME, PWD, DESCRIPTION, EMAIL, STATUS from WF_STAFF where STAFF_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, staffId);
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

  public int insert(StaffModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getStaffIdModifyFlag()) {
      StringUtil.makeDynaParam("STAFF_ID", convertSQL(model.getStaffId()), strList,valList);
          }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList,valList);
          }
    if (model.getPwdModifyFlag()) {
      StringUtil.makeDynaParam("PWD", convertSQL(model.getPwd()), strList,valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION",convertSQL(model.getDescription()), strList,valList);
          }
    if (model.getEmailModifyFlag()) {
      StringUtil.makeDynaParam("EMAIL",convertSQL(model.getEmail()), strList,valList);
          }
    if (model.getStatusModifyFlag()) {
      StringUtil.makeDynaParam("STATUS",convertSQL(model.getStatus()), strList,valList);
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
    sql = "insert into WF_STAFF(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(StaffModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ",convertSQL(model.getName()), strList,valList);
         }
    if (model.getPwdModifyFlag()) {
      StringUtil.makeDynaParam("PWD = ",convertSQL(model.getPwd()), strList,valList);
    }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()), strList,valList);
    }
    if (model.getEmailModifyFlag()) {
      StringUtil.makeDynaParam("EMAIL = ",convertSQL(model.getEmail()), strList,valList);
      }
    if (model.getStatusModifyFlag()) {
      StringUtil.makeDynaParam("STATUS = ",convertSQL(model.getStatus()), strList,valList);
      }
    if (strList.size() == 0) return 0;
    valList.add(convertSQL(model.getStaffId()));
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_STAFF set " + updateString + " where STAFF_ID =  ?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public ArrayList getStaffListByExecutor(int nodeId,
      int responsibility) throws SQLException {
    return getStaffListByExecutor(-1, -1, nodeId, responsibility);
  }

  public ArrayList getStaffListByExecutor(int theBegin,
      int theEnd, int nodeId, int responsibility) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.* from wf_staff a, wf_executor_source b where a.staff_id = b.executor and b.source = 5 and b.node_id =? and b.responsibility = ?";
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

  public ArrayList getStaffListByNonExecutor(int nodeId)
      throws SQLException {
    return getStaffListByNonExecutor(-1, -1, nodeId);
  }

  public ArrayList getStaffListByNonExecutor(int theBegin,
      int theEnd, int nodeId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.* from wf_staff a where not exists (select 1 from wf_executor_source b where a.staff_id = b.executor and b.source = 5 and b.node_id =?)";
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

  public ArrayList getSuperStaffList(String staffId,
      String orgPositionId) throws SQLException {
    return getSuperStaffList(-1, -1, staffId, orgPositionId);
  }

  public ArrayList getSuperStaffList(int theBegin, int theEnd,
      String staffId, String orgPositionId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.* from wf_staff a, wf_staff_position b, "
      + "wf_org_position_level c, wf_staff_position d where a.staff_id = b.staff_id "
      + "and b.org_position_id = c.parent_id and c.org_position_id = d.org_position_id "
      + "and d.staff_id = ? and d.org_position_id =?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      if (theEnd > 0)
        st.setFetchSize(theEnd);

      st.setString(1, staffId);
      st.setString(2, orgPositionId);
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

  private StaffModel parseResultSet(ResultSet rs) throws SQLException {
    StaffModel model = new StaffModel();
    try {
      model.setStaffId(rs.getString("STAFF_ID"));
      if (rs.wasNull())
        model.setStaffId(null);
    } catch (Exception e) {
      model.setStaffId(null);
    }

    try {
      model.setName(rs.getString("NAME"));
      if (rs.wasNull())
        model.setName(null);
    } catch (Exception e) {
      model.setName(null);
    }

    try {
      model.setPwd(rs.getString("PWD"));
      if (rs.wasNull())
        model.setPwd(null);
    } catch (Exception e) {
      model.setPwd(null);
    }

    try {
      model.setDescription(rs.getString("DESCRIPTION"));
      if (rs.wasNull())
        model.setDescription(null);
    } catch (Exception e) {
      model.setDescription(null);
    }

    try {
      model.setEmail(rs.getString("EMAIL"));
      if (rs.wasNull())
        model.setEmail(null);
    } catch (Exception e) {
      model.setEmail(null);
    }

    try {
      model.setStatus(rs.getString("STATUS"));
      if (rs.wasNull())
        model.setStatus(null);
    } catch (Exception e) {
      model.setStatus(null);
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