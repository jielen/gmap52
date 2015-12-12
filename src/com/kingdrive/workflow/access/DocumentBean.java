package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class DocumentBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(DocumentBean.class);

  public DocumentBean() {
  }

  public void delete(int documentId) throws SQLException {
    String sql = "delete from WF_DOCUMENT where DOCUMENT_ID=?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, documentId);
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
    String sql = "select DOCUMENT_ID, NAME, INSTANCE_ID, TYPE, LINK_NAME, UPLOAD_TIME, DESCRIPTION from WF_DOCUMENT";
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

  public DocumentModel findByKey(int documentId)
      throws SQLException {
    DocumentModel model = new DocumentModel();
    String sql = "select DOCUMENT_ID, NAME, INSTANCE_ID, TYPE, LINK_NAME, UPLOAD_TIME, DESCRIPTION from WF_DOCUMENT where DOCUMENT_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, documentId);
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

  public int insert(DocumentModel model) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getDocumentIdModifyFlag()) {
      StringUtil.makeDynaParam("Document_ID", model.getDocumentId(), strList,valList);
        }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList,valList);
          }
    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList,valList);
          }
    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE",convertSQL(model.getType()), strList,valList);
          }
    if (model.getLinkNameModifyFlag()) {
      StringUtil.makeDynaParam("LINK_NAME",convertSQL(model.getLinkName()), strList,valList);
          }
    if (model.getUploadTimeModifyFlag()) {
      StringUtil.makeDynaParam("UPLOAD_TIME",convertSQL(model.getUploadTime()), strList,valList);
          }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION",convertSQL(model.getDescription()), strList,valList);
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
    sql = "insert into WF_DOCUMENT(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(DocumentModel model) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();

    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME =",convertSQL(model.getName()), strList,
        valList);
          }

    if (model.getInstanceIdModifyFlag()) {
      StringUtil.makeDynaParam("INSTANCE_ID=",model.getInstanceId(), strList,
        valList);
          }

    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE =",convertSQL(model.getType()), strList,
        valList);
          }

    if (model.getLinkNameModifyFlag()) {
      StringUtil.makeDynaParam("LINK_NAME = ",convertSQL(model.getLinkName()), strList,
        valList);
       }

    if (model.getUploadTimeModifyFlag()) {
      StringUtil.makeDynaParam("UPLOAD_TIME = ",convertSQL(model.getUploadTime()), strList,
        valList);
          }

    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()), strList,
        valList);
       }

    if (strList.size() == 0)
      return 0;
    valList.add(model.getDocumentId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_DOCUMENT set " + updateString + " where DOCUMENT_ID=?";
    DBHelper.executeUpdate(sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  public void removeByLinkName(String linkName)
      throws SQLException {
    String sql = "delete from wf_document where link_name = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setString(1, linkName);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public void removeByInstance(int instanceId)
      throws SQLException {
    String sql = "delete from wf_document where instance_id = ?";
    PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
    	DBHelper.closeConnection(conn,st,null);
    }
  }

  public ArrayList findByInstance(int instanceId)
      throws SQLException {
    return findByInstance(-1, -1, instanceId);
  }

  public ArrayList findByInstance(int theBegin, int theEnd,
      int instanceId) throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select * from wf_document where instance_id = ?";
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
      if (theEnd > 0)
        st.setFetchSize(theEnd);
      st = conn.prepareStatement(sql);
      st.setInt(1, instanceId);
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

  private DocumentModel parseResultSet(ResultSet rs) throws SQLException {
    DocumentModel model = new DocumentModel();
    try {
      model.setDocumentId(rs.getInt("DOCUMENT_ID"));
      if (rs.wasNull())
        model.setDocumentId(null);
    } catch (Exception e) {
      model.setDocumentId(null);
    }

    try {
      model.setName(rs.getString("NAME"));
      if (rs.wasNull())
        model.setName(null);
    } catch (Exception e) {
      model.setName(null);
    }

    try {
      model.setInstanceId(rs.getInt("INSTANCE_ID"));
      if (rs.wasNull())
        model.setInstanceId(null);
    } catch (Exception e) {
      model.setInstanceId(null);
    }

    try {
      model.setType(rs.getString("TYPE"));
      if (rs.wasNull())
        model.setType(null);
    } catch (Exception e) {
      model.setType(null);
    }

    try {
      model.setLinkName(rs.getString("LINK_NAME"));
      if (rs.wasNull())
        model.setLinkName(null);
    } catch (Exception e) {
      model.setLinkName(null);
    }

    try {
      model.setUploadTime(rs.getString("UPLOAD_TIME"));
      if (rs.wasNull())
        model.setUploadTime(null);
    } catch (Exception e) {
      model.setUploadTime(null);
    }

    try {
      model.setDescription(rs.getString("DESCRIPTION"));
      if (rs.wasNull())
        model.setDescription(null);
    } catch (Exception e) {
      model.setDescription(null);
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