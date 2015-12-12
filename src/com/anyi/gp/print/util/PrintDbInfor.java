/* $Id: PrintDbInfor.java,v 1.2 2008/03/15 07:39:04 liuxiaoyong Exp $ */
package com.anyi.gp.print.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

/**
 * <p>
 * Title: PrintDbInfor 数据库中存储的打印信息
 * </p>
 * <p>
 * Description: 打印辅助信息 1。JASPER文件名（一个或多个）
 * 2。JASPERReport输出文件类型0-pdf,1-xls,2-html,3-csv，4-xml五种类型
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 *
 * @author zuodf
 * @version 1.0
 */

public class PrintDbInfor {
  public PrintDbInfor() {
  }

  /**
   * 从数据库中读取模板文件名
   *
   * @param tempName
   *          模板代码
   * @return
   */
  public String getJasperName(String tmplCode) {
    String JasperName;
    Connection myConn = null;
    // String sqlStr = "select PRN_TPL_NAME from AS_PRINT_JASPERTEMP where
    // PRN_TPL_JPCODE=? and PRN_TPL_REPORTTYPE=?";
    String sqlStr = "select PRN_TPL_NAME from AS_PRINT_JASPERTEMP where PRN_TPL_JPCODE=? ";
    String[] para = new String[1];
    para[0] = tmplCode;
    // para[1] = "主表";
    Object OJasperName = null;
    try {
      myConn = DAOFactory.getInstance().getConnection();
      OJasperName = DBHelper.queryOneValue(myConn, sqlStr, para);
      if (OJasperName == null) {
        JasperName = "";
      } else {
        JasperName = OJasperName.toString();
      }
    } catch (Exception ex) {
      throw new RuntimeException("EditPrint.getJasperNameFromDB调用错误："
          + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn);
    }
    return JasperName;

  }

  /**
   * 从数据库中读取输出文件类型0-pdf,1-xls,2-html,3-csv,4-xml五种类型
   *
   * @param tempName
   *          模板代码
   * @return Int JasperOutType
   */
  public int getJasperOutType(String tmplCode) {
    int iOutType;
    Connection myConn = null;
    // String sqlStr = "select PRN_TPL_OUTTYPE from AS_PRINT_JASPERTEMP where
    // PRN_TPL_JPCODE=? and PRN_TPL_REPORTTYPE=?";
    String sqlStr = "select PRN_TPL_OUTTYPE from AS_PRINT_JASPERTEMP where PRN_TPL_JPCODE=?";
    String[] para = new String[1];
    para[0] = tmplCode;
    // para[1] = "主表";
    Object iOOutType = null;
    try {
      myConn = DAOFactory.getInstance().getConnection();
      iOOutType = DBHelper.queryOneValue(myConn, sqlStr, para);
      if (iOOutType == null) {
        iOutType = 0;
      } else {
        iOutType = Integer.parseInt(iOOutType.toString());
      }
    } catch (Exception ex) {
      throw new RuntimeException("EditPrint.getJasperOutTypeFromDB调用错误："
          + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn);
    }
    return iOutType;

  }

  /**
   * 从数据库中读取输出文件类型0-pdf,1-xls,2-html,3-csv,4-xml五种类型
   *
   * @param tempName
   *          模板代码
   * @return Int JasperOutType
   */
  public int getFixRowCount(String tmplCode) {
    int iFixRowCount;
    Connection myConn = null;
    // String sqlStr = "select PRN_TPL_FIXROWCOUNT from AS_PRINT_JASPERTEMP
    // where PRN_TPL_CODE=? and PRN_TPL_REPORTTYPE=?";
    String sqlStr = "select PRN_TPL_FIXROWCOUNT from AS_PRINT_JASPERTEMP where PRN_TPL_JPCODE=?";
    String[] para = new String[1];
    para[0] = tmplCode;
    // para[1] = "主表";
    Object iOFixRowCount = null;
    try {
      myConn = DAOFactory.getInstance().getConnection();
      iOFixRowCount = DBHelper.queryOneValue(myConn, sqlStr, para);
      if (iOFixRowCount == null) {
        iFixRowCount = 0;
      } else {
        iFixRowCount = Integer.parseInt(iOFixRowCount.toString());
      }
    } catch (Exception ex) {
      throw new RuntimeException("EditPrint.getJasperOutTypeFromDB调用错误："
          + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn);
    }
    return iFixRowCount;
  }

  ////取oracle优化策略
  public static String addDBOptimizerMode(String componame, String tablename, String sql){
      if(sql == null || sql.trim().length() == 0){
          return sql;
      }
      String optimizerMode = getDBOptimizerMode(componame, tablename);
      if(optimizerMode == null || optimizerMode.trim().length() == 0){
          return sql;
      }
      if(sql.trim().toUpperCase().startsWith("SELECT")){
          sql = sql.substring(0,6) + " /*+ " + optimizerMode + " */ " + sql.substring(6);
      }
      return sql;
  }

  public static String getDBOptimizerMode(String componame, String tablename){
      String result = null;
      String sql = "SELECT OPTIMIZER_MODE FROM AS_DB_OPTIMIZER_MODE WHERE COMPO_ID = '" + componame + "' AND TAB_ID = '" + tablename + "'";
      Connection myConn = null;
      Statement sm = null;
      ResultSet rs = null;
      try {
          myConn = DAOFactory.getInstance().getConnection();
          sm = myConn.createStatement();
          rs = sm.executeQuery(sql);
          if(rs.next()){
              result = rs.getString("OPTIMIZER_MODE");
          }
      } catch (SQLException e) {
          //e.printStackTrace();
      }
      finally {
        DBHelper.closeConnection(myConn, sm, rs);
      }
      return result;
  }


}
