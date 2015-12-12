package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class GraphicsBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(GraphicsBean.class);

  public GraphicsBean() {
  }

  public void delete(Connection conn, int elementId) throws SQLException {
    String sql = "delete from WF_GRAPHICS where ELEMENT_ID=?";
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(sql);
      st.setInt(1, elementId);
      st.executeUpdate();
      logger.info(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      try {
        if (st != null)
          st.close();
      } catch (SQLException fe) {
        fe.printStackTrace();
        throw new SQLException(fe.getMessage());
      }
      st = null;
    }
  }

  public ArrayList find(Connection conn) throws SQLException {
    return find(conn, -1, -1);
  }

  public ArrayList find(Connection conn, int theBegin, int theEnd)
      throws SQLException {
    ArrayList list = new ArrayList();
    String sql = "select TYPE, ELEMENT_ID, OWNER_ID, TEMPLATE_ID, OBJECT_ID, ELEMENT_X, ELEMENT_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT, NAME, DESCRIPTION from WF_GRAPHICS";
    PreparedStatement st = null;
    ResultSet rs = null;
    try {
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
      DBHelper.closeConnection(null,st,rs);
    }

    return list;
  }

  public GraphicsModel findByKey(Connection conn, int elementId)
      throws SQLException {
    GraphicsModel model = new GraphicsModel();
    String sql = "select TYPE, ELEMENT_ID, OWNER_ID, TEMPLATE_ID, OBJECT_ID, ELEMENT_X, ELEMENT_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT, NAME, DESCRIPTION from WF_GRAPHICS where ELEMENT_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    try {
      st = conn.prepareStatement(sql);
      st.setInt(1, elementId);
      rs = st.executeQuery();
      if (rs.next()) {
        model = parseResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(null,st,rs);
    }

    return model;
  }

  public int insert(GraphicsModel model, Connection con) throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();;
    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE", model.getType(), strList,valList);
      }
    if (model.getElementIdModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_ID",model.getElementId(), strList,valList);
      }
    if (model.getOwnerIdModifyFlag()) {
      StringUtil.makeDynaParam("OWNER_ID",model.getOwnerId(), strList,valList);
       }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID",model.getTemplateId(), strList,valList);
      }
    if (model.getObjectIdModifyFlag()) {
      StringUtil.makeDynaParam("OBJECT_ID", model.getObjectId(), strList,valList);
      }
    if (model.getElementXModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_X", model.getElementX(), strList,valList);
      }
    if (model.getElementYModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_Y", model.getElementY(), strList,valList);
       }
    if (model.getElementWidthModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_WIDTH", model.getElementWidth(), strList,valList);
      }
    if (model.getElementHeightModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_HEIGHT", model.getElementHeight(), strList,valList);
      }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME", convertSQL(model.getName()), strList,valList);
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
    sql = "insert into WF_GRAPHICS(" + insertString + ") values(" + valsString
        + ")";
    DBHelper.executeUpdate(con, sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(GraphicsModel model, Connection con) throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getTypeModifyFlag()) {
      StringUtil.makeDynaParam("TYPE = ",model.getType(), strList,valList);   
      }
    if (model.getOwnerIdModifyFlag()) {
      StringUtil.makeDynaParam("OWNER_ID = ",model.getOwnerId(), strList,valList);   
      }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID =",model.getTemplateId(), strList,valList);   
      }
    if (model.getObjectIdModifyFlag()) {
      StringUtil.makeDynaParam("OBJECT_ID=",model.getObjectId(), strList,valList);   
      }
    if (model.getElementXModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_X = ",model.getElementX(), strList,valList);   
      }
    if (model.getElementYModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_Y = ",model.getElementY(), strList,valList);   
      }
    if (model.getElementWidthModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_WIDTH = ",model.getElementWidth(), strList,valList);   
       }
    if (model.getElementHeightModifyFlag()) {
      StringUtil.makeDynaParam("ELEMENT_HEIGHT = ",model.getElementHeight(), strList,valList);   
       }
    if (model.getNameModifyFlag()) {
      StringUtil.makeDynaParam("NAME = ",convertSQL(model.getName()), strList,valList);   
       }
    if (model.getDescriptionModifyFlag()) {
      StringUtil.makeDynaParam("DESCRIPTION = ",convertSQL(model.getDescription()), strList,valList);   
     }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getElementId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_GRAPHICS set " + updateString + " where ELEMENT_ID=?";
    DBHelper.executeUpdate(con,sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  private GraphicsModel parseResultSet(ResultSet rs) throws SQLException {
    GraphicsModel model = new GraphicsModel();
    try {
      model.setType(rs.getInt("TYPE"));
      if (rs.wasNull())
        model.setType(null);
    } catch (Exception e) {
      model.setType(null);
    }

    try {
      model.setElementId(rs.getInt("ELEMENT_ID"));
      if (rs.wasNull())
        model.setElementId(null);
    } catch (Exception e) {
      model.setElementId(null);
    }

    try {
      model.setOwnerId(rs.getInt("OWNER_ID"));
      if (rs.wasNull())
        model.setOwnerId(null);
    } catch (Exception e) {
      model.setOwnerId(null);
    }

    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    try {
      model.setObjectId(rs.getInt("OBJECT_ID"));
      if (rs.wasNull())
        model.setObjectId(null);
    } catch (Exception e) {
      model.setObjectId(null);
    }

    try {
      model.setElementX(rs.getInt("ELEMENT_X"));
      if (rs.wasNull())
        model.setElementX(null);
    } catch (Exception e) {
      model.setElementX(null);
    }

    try {
      model.setElementY(rs.getInt("ELEMENT_Y"));
      if (rs.wasNull())
        model.setElementY(null);
    } catch (Exception e) {
      model.setElementY(null);
    }

    try {
      model.setElementWidth(rs.getInt("ELEMENT_WIDTH"));
      if (rs.wasNull())
        model.setElementWidth(null);
    } catch (Exception e) {
      model.setElementWidth(null);
    }

    try {
      model.setElementHeight(rs.getInt("ELEMENT_HEIGHT"));
      if (rs.wasNull())
        model.setElementHeight(null);
    } catch (Exception e) {
      model.setElementHeight(null);
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