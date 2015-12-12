package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.TemplateInfo;

public class TemplateQuery {

  public TemplateQuery() {
  }

  public TemplateInfo getTemplate(int templateId)
      throws SQLException {
    TemplateInfo model = new TemplateInfo();
    String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id=b.staff_id and a.template_id=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, templateId);
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

  public ArrayList getTemplateList() throws SQLException {
    return getTemplateList(-1, -1);
  }

  public ArrayList getTemplateList(int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id=b.staff_id order by template_id";
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

  public ArrayList getTemplateListByType(String templateType)
      throws SQLException {
    return getTemplateListByType(-1, -1, templateType);
  }

  public ArrayList getTemplateListByType(int theBegin,
      int theEnd, String templateType) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id = b.staff_id and a.template_type like ? order by template_id";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
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

  public ArrayList getActiveTemplateList(String currentTime)
      throws SQLException {
    return getActiveTemplateList(-1, -1, currentTime);
  }

  public ArrayList getActiveTemplateList(int theBegin,
      int theEnd, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    //String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id = b.staff_id and a.is_active='0' and ? between a.start_time and a.expire_time order by template_id";
    String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id = b.staff_id and a.is_active='0' order by template_id";//去掉有效日期的检查，以提高速度
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
      //st.setString(1, currentTime);
      rs = st.executeQuery();
      if (theBegin > 1)
        rs.absolute(theBegin - 1);
      while (rs.next()) {
        list.add(parseResultSet(rs));
        if (rs.getRow() == theEnd)
          break;
      }
    } finally {
    	DBHelper.closeConnection(conn,st,rs);
    }

    return list;
  }

  public ArrayList getActiveTemplateList(String templateType,
      String currentTime) throws SQLException {
    return getActiveTemplateList(-1, -1, templateType, currentTime);
  }

  public ArrayList getActiveTemplateList(int theBegin,
      int theEnd, String templateType, String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*,b.name create_staff_name from wf_TEMPLATE a,wf_STAFF b where a.create_staff_id = b.staff_id and a.template_type like ? and a.is_active='0' and ? between a.start_time and a.expire_time order by template_id";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
      st.setString(1, templateType);
      st.setString(2, currentTime);
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

  public ArrayList getActiveTemplateListByExecutor(
      String executor, String currentTime) throws SQLException {
    return getActiveTemplateListByExecutor(-1, -1, executor, currentTime);
  }

  public ArrayList getActiveTemplateListByExecutor(
      int theBegin, int theEnd, String executor, String currentTime)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name create_staff_name from wf_template a, wf_staff b, wf_node c, wf_link d, v_wf_executor_source e where a.is_active = '0' and a.create_staff_id = b.staff_id and a.template_id = c.template_id and c.node_id = d.next_node_id and d.current_node_id = -1 and c.node_id = e.node_id and e.executor = ? and ? between a.start_time and a.expire_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
      st.setString(1, executor);
      st.setString(2, currentTime);
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

  public ArrayList getActiveTemplateListByExecutor(
      String executor, String templateType, String currentTime)
      throws SQLException {
    return getActiveTemplateListByExecutor(-1, -1, executor,
        templateType, currentTime);
  }

  public ArrayList getActiveTemplateListByExecutor(
      int theBegin, int theEnd, String executor, String templateType,
      String currentTime) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select a.*, b.name create_staff_name from wf_template a, wf_staff b, wf_node c, wf_link d, v_wf_executor_source e where a.is_active = '0' and a.create_staff_id = b.staff_id and a.template_id = c.template_id and c.node_id = d.next_node_id and d.current_node_id = -1 and c.node_id = e.node_id and e.executor = ? and a.template_type like ? and ? between a.start_time and a.expire_time";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
      st.setString(1, executor);
      st.setString(2, templateType);
      st.setString(3, currentTime);
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

  private TemplateInfo parseResultSet(ResultSet rs) throws SQLException {
    TemplateInfo model = new TemplateInfo();
    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
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
      model.setVersion(rs.getString("VERSION"));
      if (rs.wasNull())
        model.setVersion(null);
    } catch (Exception e) {
      model.setVersion(null);
    }

    try {
      model.setStartTime(rs.getString("START_TIME"));
      if (rs.wasNull())
        model.setStartTime(null);
    } catch (Exception e) {
      model.setStartTime(null);
    }

    try {
      model.setExpireTime(rs.getString("EXPIRE_TIME"));
      if (rs.wasNull())
        model.setExpireTime(null);
    } catch (Exception e) {
      model.setExpireTime(null);
    }

    try {
      model.setCreateTime(rs.getString("CREATE_TIME"));
      if (rs.wasNull())
        model.setCreateTime(null);
    } catch (Exception e) {
      model.setCreateTime(null);
    }

    try {
      model.setCreateStaffId(rs.getString("CREATE_STAFF_ID"));
      if (rs.wasNull())
        model.setCreateStaffId(null);
    } catch (Exception e) {
      model.setCreateStaffId(null);
    }

    try {
      model.setCreateStaffName(rs.getString("CREATE_STAFF_NAME"));
      if (rs.wasNull())
        model.setCreateStaffName(null);
    } catch (Exception e) {
      model.setCreateStaffName(null);
    }

    try {
      model.setIsActive(rs.getString("IS_ACTIVE"));
      if (rs.wasNull())
        model.setIsActive(null);
    } catch (Exception e) {
      model.setIsActive(null);
    }

    try {
      model.setTemplateType(rs.getString("TEMPLATE_TYPE"));
      if (rs.wasNull())
        model.setTemplateType(null);
    } catch (Exception e) {
      model.setTemplateType(null);
    }

    return model;
  }
}
