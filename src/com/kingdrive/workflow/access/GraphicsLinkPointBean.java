package com.kingdrive.workflow.access;

import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.*;
import com.kingdrive.workflow.util.StringUtil;

import java.util.ArrayList;
import java.sql.*;

public class GraphicsLinkPointBean {
  // log4j for log
  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
      .getLogger(GraphicsLinkPointBean.class);

  public GraphicsLinkPointBean() {
  }

  public void delete(Connection conn, int graphicsLinkPointId)
      throws SQLException {
    String sql = "delete from WF_GRAPHICS_LINK_POINT where GRAPHICS_LINK_POINT_ID=?";
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement(sql);
      st.setInt(1, graphicsLinkPointId);
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
    String sql = "select GRAPHICS_LINK_POINT_ID, NODE_LINK_ID, SORT, X, Y, TEMPLATE_ID from WF_GRAPHICS_LINK_POINT";
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

  public GraphicsLinkPointModel findByKey(Connection conn,
      int graphicsLinkPointId) throws SQLException {
    GraphicsLinkPointModel model = new GraphicsLinkPointModel();
    String sql = "select GRAPHICS_LINK_POINT_ID, NODE_LINK_ID, SORT, X, Y, TEMPLATE_ID from WF_GRAPHICS_LINK_POINT where GRAPHICS_LINK_POINT_ID=?";
    PreparedStatement st = null;
    ResultSet rs = null;
    try {
      st = conn.prepareStatement(sql);
      st.setInt(1, graphicsLinkPointId);
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

  public int insert(GraphicsLinkPointModel model, Connection con)
      throws SQLException {
    String sql = null;
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    if (model.getGraphicsLinkPointIdModifyFlag()) {
      StringUtil.makeDynaParam("GRAPHICS_LINK_POINT_ID",model.getGraphicsLinkPointId(), strList,valList);
          }
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID", model.getNodeLinkId(), strList,valList);
          }
    if (model.getSortModifyFlag()) {
      StringUtil.makeDynaParam("SORT", model.getSort(), strList,valList);
          }
    if (model.getXModifyFlag()) {
      StringUtil.makeDynaParam("X",  model.getX(), strList,valList);
       }
    if (model.getYModifyFlag()) {
      StringUtil.makeDynaParam("Y",  model.getY(), strList,valList);
       }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID", model.getTemplateId(), strList,valList);
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
    sql = "insert into WF_GRAPHICS_LINK_POINT(" + insertString + ") values("
        + valsString + ")";
    DBHelper.executeUpdate(con, sql, valList.toArray());
    logger.info(sql);
    return 1;
  }

  public int update(GraphicsLinkPointModel model, Connection con)
      throws SQLException {
    String sql = null;
    String updateString = "";
    ArrayList strList = new ArrayList();
    ArrayList valList = new ArrayList();
    
    if (model.getNodeLinkIdModifyFlag()) {
      StringUtil.makeDynaParam("NODE_LINK_ID=",model.getNodeLinkId(), strList,valList);
       }
    if (model.getSortModifyFlag()) {
      StringUtil.makeDynaParam("SORT = ",model.getSort(), strList,valList);
       }
    if (model.getXModifyFlag()) {
      StringUtil.makeDynaParam("X =", model.getX(), strList,valList);
       }
    if (model.getYModifyFlag()) {
      StringUtil.makeDynaParam("Y =", model.getY(), strList,valList);
       }
    if (model.getTemplateIdModifyFlag()) {
      StringUtil.makeDynaParam("TEMPLATE_ID = ", model.getTemplateId(), strList,valList);
       }
    if (strList.size() == 0)
      return 0;
    valList.add(model.getGraphicsLinkPointId());
    int length = strList.size();
    if (length == 0) {
      updateString = null;
    } else {
      for (int i = 0; i <= length - 1; i++) {
        updateString += strList.get(i) + "?,";
      }
      updateString = updateString.substring(0, updateString.length() - 1);
    }
    sql = "update WF_GRAPHICS_LINK_POINT set " + updateString + " where GRAPHICS_LINK_POINT_ID=?";
    DBHelper.executeUpdate(con,sql,valList.toArray());
    logger.info(sql);
    return 1;
  }

  private GraphicsLinkPointModel parseResultSet(ResultSet rs)
      throws SQLException {
    GraphicsLinkPointModel model = new GraphicsLinkPointModel();
    try {
      model.setGraphicsLinkPointId(rs.getInt("GRAPHICS_LINK_POINT_ID"));
      if (rs.wasNull())
        model.setGraphicsLinkPointId(null);
    } catch (Exception e) {
      model.setGraphicsLinkPointId(null);
    }

    try {
      model.setNodeLinkId(rs.getInt("NODE_LINK_ID"));
      if (rs.wasNull())
        model.setNodeLinkId(null);
    } catch (Exception e) {
      model.setNodeLinkId(null);
    }

    try {
      model.setSort(rs.getInt("SORT"));
      if (rs.wasNull())
        model.setSort(null);
    } catch (Exception e) {
      model.setSort(null);
    }

    try {
      model.setX(rs.getInt("X"));
      if (rs.wasNull())
        model.setX(null);
    } catch (Exception e) {
      model.setX(null);
    }

    try {
      model.setY(rs.getInt("Y"));
      if (rs.wasNull())
        model.setY(null);
    } catch (Exception e) {
      model.setY(null);
    }

    try {
      model.setTemplateId(rs.getInt("TEMPLATE_ID"));
      if (rs.wasNull())
        model.setTemplateId(null);
    } catch (Exception e) {
      model.setTemplateId(null);
    }

    return model;
  }
}