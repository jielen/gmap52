package com.anyi.gp.pub;

/**
 * <p>Title: 编号器</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Tang.xn
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.opensymphony.webwork.ServletActionContext;

public class NumTool {

  public NumTool() {

  }

  /**
   * @param compoId
   *          部件ID
   * @param noField
   *          被编号的字段名
   * @param entity
   *          TableData包
   * @return num 编号
   */
  public String getNo(String compoId, String noField, TableData entity) {
    String numToolId = "";
    String rCode = "";
    String isCont = "N";
    String isFillZero= "Y";
    int numLen = 0;
    String fixPreFix = "";
    String fixAftFix= "";
    String preFix = "";
    String mTab = compoId;
    long toolNo = 0;

    if (compoId == null || compoId.length() == 0) {
      // System.out.println("NumTool：部件代码为空！");
      return "";
    }
    if (noField == null || noField.length() == 0) {
      // System.out.println("NumTool：编号字段为空！");
      return "";
    }
    if (entity == null) {
      // System.out.println("NumTool：数据包为空！");
      return "";
    }

    List segs = new ArrayList();
    String sqlStr = "";
    Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      // 根据compoid，nofield查表as_no_rule，得到编码信息和编号器ID
      sqlStr = "select NO_PREFIX,NO_AFTFIX, IS_FILL_ZERO,NO_INDEX_LEN,NUM_TOOL_ID,RULE_CODE from AS_NO_RULE where COMPO_ID = ? and NO_FIELD = ? ";
      st = conn.prepareStatement(sqlStr);

      int index = 1;
      st.setString(index++, compoId);
      st.setString(index++, noField);

      rs = st.executeQuery();
      if (rs.next()) {
        fixPreFix = rs.getString("NO_PREFIX");
        fixAftFix= rs.getString("NO_AFTFIX");
        isFillZero= rs.getString("IS_FILL_ZERO");
        numLen = rs.getInt("NO_INDEX_LEN");
        numToolId = rs.getString("NUM_TOOL_ID");
        rCode = rs.getString("RULE_CODE");
      }
      DBHelper.closeConnection(null, st, rs);
      if (numToolId == null || numToolId.equals("")) {
        return "";
      }
      if (rCode == null || rCode.equals("")) {
        return "";
      }

      if (fixPreFix == null)
        fixPreFix = "";
      if (isCont == null)
        isCont = "N";
      if (fixAftFix== null) fixAftFix= "";
      if (isFillZero== null) isFillZero= "Y";

      sqlStr = "select MASTER_TAB_ID from AS_COMPO where COMPO_ID = ? ";
      st = conn.prepareStatement(sqlStr);

      index = 1;
      st.setString(index++, compoId);

      rs = st.executeQuery();
      if (rs.next()) {
        mTab = rs.getString(1);
      }
      DBHelper.closeConnection(null, st, rs);
      
      HttpServletRequest request = ServletActionContext.getRequest();
      
      // 根据rulecode查表as_no_rule_seg，得到编码规则
      sqlStr = " select SEG_FIELD, SEG_DELI, DATE_FMT, SEG_LEN, SEG_FILL_POSI, SEG_FILL, SEG_SV, SEG_CONST "
             + " from AS_NO_RULE_SEG where RULE_CODE = ? and COMPO_ID = ? order by ORD_INDEX ";
      st = conn.prepareStatement(sqlStr);

      index = 1;
      st.setString(index++, rCode);
      st.setString(index++, compoId);

      rs = st.executeQuery();
      while (rs.next()) {
        boolean isField = false;
        String segSv = rs.getString(7);//增加对环境变量的支持
        String segConst = rs.getString(8);
        String tmp = rs.getString(1);
        if(tmp != null && tmp.length() > 0 ){
          isField = true;
        }else if(request != null && segSv != null && segSv.length() > 0){
          tmp = SessionUtils.getAttribute(request, segSv);
        }else{
          tmp = segConst;
        }
        
        if (tmp == null || tmp.equals(""))
          continue;
        segs.add(tmp);
        
        tmp = rs.getString(2);
        if (tmp == null)
          tmp = "";

        segs.add(tmp);
        segs.add(rs.getString(3));
        segs.add(rs.getString(4));

        tmp = rs.getString(5);
        if (tmp == null || tmp.equals(""))
          tmp = "1";
        segs.add(tmp);

        tmp = rs.getString(6);
        if (tmp == null)
          tmp = " ";
        segs.add(tmp);
        
        segs.add(isField + "");
      }
      DBHelper.closeConnection(null, st, rs);
      for (int i = 0; i < segs.size(); i += 7) {
        boolean isField = Pub.parseBool(segs.get(i + 6));        
        String fVal = null;
        String isD = "N";
        
        if(isField){
          fVal = entity.getFieldValue((String)segs.get(i));
          sqlStr = "select DATA_TYPE from AS_TAB_COL where TAB_ID = ? and DATA_ITEM = ? ";
          st = conn.prepareStatement(sqlStr);
  
          index = 1;
          st.setString(index++, mTab);
          st.setObject(index++, segs.get(i));
  
          rs = st.executeQuery();
          if (rs.next()) {
            if (rs.getString(1).equalsIgnoreCase("DATE"))
              isD = "Y";
          }
        }else{
          fVal = (String)segs.get(i);
        }
        
        if (isD.equals("Y")) {
          preFix += transDate(fVal, segs.get(i + 2));
        } else {
          int fLen = fVal.length();
          String fixLen = (String)segs.get(i + 3);
          int fixL = 0;
          if (fixLen != null)
            fixL = Integer.parseInt(fixLen);
          if (fixL > fLen) {
            fixL = fixL - fLen;
            String addC = (String)segs.get(i + 5);
            String addS = "";
            while (fixL > 0) {
              addS += addC;
              fixL--;
            }
            // 1 补前空
            if (segs.get(i + 4).equals("1"))
              preFix = preFix + addS + fVal;
            else
              preFix = preFix + fVal + addS;
          } else
            preFix += fVal;
        }
        //分隔符放后面;leidh;20060411;
        preFix += segs.get(i + 1);
        DBHelper.closeConnection(null, st, rs);
      }

      // 取得编号器属性，是否连续编号
      sqlStr = "select IS_CONT FROM AS_NUM_TOOL where NUM_TOOL_ID = ?";
      st = conn.prepareStatement(sqlStr);

      index = 1;
      st.setString(index++, numToolId);

      rs = st.executeQuery();
      if (rs.next()) {
        isCont = rs.getString(1);
      }
      if (isCont == null || isCont.equals(""))
        isCont = "N";

      lockCounter(numToolId, isCont, fixPreFix, preFix);
      toolNo = getToolNo(numToolId, isCont, preFix);
      //saveNo(numToolId, isCont, fixPreFix, preFix, toolNo);
      DBHelper.closeConnection(null, st, rs);
    } catch (SQLException se) {
      // System.out.println("NumTool->" + sqlStr + "出错:\n"+se.getMessage());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }

    String result = String.valueOf(toolNo);
    int addL = numLen - result.length();
    if(isFillZero.equalsIgnoreCase("Y")){
    while (addL > 0) {
        result = "0" + result;
        addL--;
      }
    }
    result = fixPreFix + preFix + result+ fixAftFix;
    // System.out.println("numtool:" + result);
    return result;

  }

  private long getToolNo(String numToolId, String isCont, String preFix) {
    long serialNumber = 1;
    String gStr = " select NUM_NO from AS_NUM_TOOL_NO where NUM_TOOL_ID = ? ";
    if(!isCont.equals("Y")){
      gStr += " and ALT_PREFIX = ? ";
    }

    if (preFix.equals(""))
      preFix = "noPreFix";

    Connection gConn = DAOFactory.getInstance().getConnection();
    PreparedStatement gst = null;
    ResultSet grs = null;

    try {
      gst = gConn.prepareStatement(gStr);
      int index = 1;
      gst.setString(index++, numToolId);
      if(!isCont.equals("Y"))
        gst.setString(index++, preFix);

      grs = gst.executeQuery();
      if (grs.next()) {
        serialNumber = grs.getLong(1);
      }

    } catch (SQLException se) {
      // System.out.println("NumTool->getToolNo出错：\n" + gStr + "\n" +
      // se.getMessage());
    } finally {
      DBHelper.closeConnection(gConn, gst, grs);
    }
    return serialNumber;
  }

  private String transDate(String d, Object dateFmt) {
    if (d.equals(""))
      return "";

    if (dateFmt == null || dateFmt.equals(""))
      return d;

    String dateStr = dateFmt.toString();
    String dYear = d.substring(0, 4);
    String dMon = d.substring(5, 7);
    String dDay = d.substring(8, 10);
    String sYear = dYear.substring(2);

    int i = dateStr.indexOf("YYYY");
    while (i > -1) {
      dateStr = dateStr.substring(0, i) + dYear + dateStr.substring(i + 4);
      i = dateStr.indexOf("YYYY");
    }
    i = dateStr.indexOf("YY");
    while (i > -1) {
      dateStr = dateStr.substring(0, i) + sYear + dateStr.substring(i + 2);
      i = dateStr.indexOf("YY");
    }
    i = dateStr.indexOf("MM");
    while (i > -1) {
      dateStr = dateStr.substring(0, i) + dMon + dateStr.substring(i + 2);
      i = dateStr.indexOf("MM");
    }
    i = dateStr.indexOf("DD");
    while (i > -1) {
      dateStr = dateStr.substring(0, i) + dDay + dateStr.substring(i + 2);
      i = dateStr.indexOf("DD");
    }

    return dateStr;
  }

  private void lockCounter(String numToolId, String isCont, String fixPreFix, String preFix){

    if (preFix.equals(""))
      preFix = "noPreFix";
    if (fixPreFix.equals(""))
      fixPreFix = "noPreFix";

    List params = new ArrayList();
    String sql = "update AS_NUM_TOOL_NO set NUM_NO = NUM_NO + 1 where NUM_TOOL_ID = ? ";
    params.add(numToolId);
    if (!isCont.equals("Y")) {
      sql += " and ALT_PREFIX = ? ";
      params.add(preFix);
    }
    int rows = DBHelper.executeUpdate(DAOFactory.getDataSource(), sql, params.toArray());
    if(rows == 0){
      sql = " insert into AS_NUM_TOOL_NO (NUM_TOOL_ID,FIX_PREFIX,ALT_PREFIX,NUM_NO) values(?, ?, ?, 1)";
      DBHelper.executeUpdate(DAOFactory.getDataSource(), sql, new Object[]{numToolId, fixPreFix, preFix});
    }
  }

  private void saveNo(String numToolId, String isCont, String fixPreFix,
      String preFix, long toolNo) {

    String sStr = "select * from AS_NUM_TOOL_NO where NUM_TOOL_ID = ? and FIX_PREFIX = ? and ALT_PREFIX = ? ";

    if (preFix.equals(""))
      preFix = "noPreFix";

    if (fixPreFix.equals(""))
      fixPreFix = "noPreFix";

    Connection sConn = DAOFactory.getInstance().getConnection();
    PreparedStatement sst = null;
    ResultSet srs = null;

    try {
      sst = sConn.prepareStatement(sStr);

      int index = 1;
      sst.setString(index++, numToolId);
      sst.setString(index++, fixPreFix);
      sst.setString(index++, preFix);

      srs = sst.executeQuery();
      if (!srs.next()) {
        sStr = "insert into AS_NUM_TOOL_NO(NUM_TOOL_ID,FIX_PREFIX,ALT_PREFIX,NUM_NO) values(?, ?, ?, ?)";
        sst = sConn.prepareStatement(sStr);

        index = 1;
        sst.setString(index++, numToolId);
        sst.setString(index++, fixPreFix);
        sst.setString(index++, preFix);
        sst.setLong(index++, toolNo);

        sst.executeUpdate();
        DBHelper.closeConnection(null, sst, srs);
      }

      sStr = "update AS_NUM_TOOL_NO set NUM_NO = ? where NUM_TOOL_ID = ? ";
      if (!isCont.equals("Y")) {
        sStr += " and ALT_PREFIX = ? ";
      }
      sst = sConn.prepareStatement(sStr);

      index = 1;
      sst.setLong(index++, toolNo);
      sst.setString(index++, numToolId);
      if(!isCont.equals("Y"))
        sst.setString(index++, preFix);

      sst.executeUpdate();
      DBHelper.closeConnection(null, sst, null);
    } catch (SQLException se) {
      // System.out.println("NumTool->saveNo出错：\n" + sStr + "\n" +
      // se.getMessage());
    } finally {
      DBHelper.closeConnection(sConn, sst, srs);
    }
  }

//  public long newGetSN(String numToolId, String isCont, String fixPreFix,
//      String preFix) {
//    long tmpSN = getToolNo(numToolId, isCont, preFix);
//    if (tmpSN == 0)
//      tmpSN = 1;
//    saveNo(numToolId, isCont, fixPreFix, preFix, tmpSN);
//    return tmpSN;
//  }
//
//  public String newGetSNstr(String numToolId, String isCont, String fixPreFix,
//      String preFix) {
//    return String.valueOf(newGetSN(numToolId, isCont, fixPreFix, preFix));
//  }

}
