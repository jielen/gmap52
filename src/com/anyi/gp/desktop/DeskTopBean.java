package com.anyi.gp.desktop;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

public class DeskTopBean {
  
  private final Logger log = Logger.getLogger(DeskTopBean.class);
  
  public DeskTopBean() {
  }

  public void init() {

    Connection conn = null;
    Statement statement = null;
    ResultSet rs = null;
    int i = 0;
    StringBuffer sql = new StringBuffer();
    StringBuffer insertSql = new StringBuffer();
    try {
      DesktopArea desktopArea;
      sql.append("select DISTINCT AREA_ID, AREA_INDEX, DISPLAY_AMOUNT, ");
      sql.append("IS_DISPLAY_REC from AS_DESKTOP where USER_ID='");
      sql.append(this.getUserID());
      sql.append("' order by AREA_INDEX ");
      conn = DAOFactory.getInstance().getConnection();

      statement = conn.createStatement();
      rs = statement.executeQuery(sql.toString());
      while (rs.next()) {
        boolean tempBln = rs.getString("IS_DISPLAY_REC").equalsIgnoreCase("Y");
        desktopArea = DesktopArea.getInstanceFromDB(rs.getString("AREA_ID"),
          this.request, this.getUserID());
        desktopArea.setDisplayAmount(rs.getInt("DISPLAY_AMOUNT"));
        desktopArea.setIsDisplayRec(tempBln);
        this.deskTopAreas.put(new Integer(i + 1), desktopArea);
        i++;
        if (i == 4) {
          break;
        }
      }
      if (i == 0) {
        insertSql.append("insert into AS_DESKTOP (AREA_ID, USER_ID,AREA_NAME,  AREA_INDEX, AREA_IMG) ");
        insertSql.append("SELECT AREA_ID,'");
        insertSql.append(this.getUserID());
        insertSql.append("', AREA_NAME, AREA_INDEX, AREA_IMG ");
        insertSql.append("FROM AS_DESKTOP WHERE USER_ID='sa'");
        if (statement.executeUpdate(insertSql.toString()) > 0) {
          rs = statement.executeQuery(sql.toString());
          while (rs.next()) {
            desktopArea = DesktopArea.getInstanceFromDB(rs.getString("AREA_ID"),
              this.request, this.getUserID());
            this.deskTopAreas.put(new Integer(i + 1), desktopArea);
            i++;
            if (i == 4) {
              break;
            }
          }
        }
      }
    } catch (SQLException ex) {
      if(i == 0){
        System.out.println(insertSql);
      }else{
        System.out.println(sql);
      }
      ex.printStackTrace();
      log.error(ex);
      throw new RuntimeException("DeskTopBean类的init方法：" + "SQL语句执行错误！",
        ex);
    } catch (BusinessException ex) {
      log.error(ex);
      throw new RuntimeException("DeskTopBean类的init方法：" + "出现业务异常！" , ex);
    } finally {
      DBHelper.closeConnection(conn, statement, rs);
    }
  }

  public void setRequest(HttpServletRequest newRequest) {
    this.request = newRequest;
  }
 
  public Map getDeskTopAreas() {
    return deskTopAreas;
  }

  public String getie5menu() {
    StringBuffer sb = new StringBuffer();

    sb.append("<div id=\"ie5menu\" class=\"rightKeyMenu\" onMouseover=\"highlightie5()\" onMouseout=\"lowlightie5()\">");
    sb.append("<div id=\"ie5new\" class=\"menuitems\" onclick=\"gotoNewPage()\">&nbsp;&nbsp;新窗口打开</div>");
    sb.append("      <hr>");
    //sb.append(" <div id=\"ie5add\" class=\"menuitems\" onclick=\"addToFavorite()\">&nbsp;&nbsp;加入收藏夹</div>");
    //sb.append("<div id=\"ie5del\" class=\"menuitems\" onclick=\"delFromFavorite()\">&nbsp;&nbsp;从收藏夹删除</div>");
    Connection conn = null;
    Statement statement = null;
    ResultSet rs = null;
    int i = 0;
    try {
      StringBuffer sql = new StringBuffer();
      sql
        .append("select DISTINCT AREA_ID,AREA_NAME,AREA_INDEX from AS_DESKTOP where USER_ID= '");
      sql.append(this.userID);
      sql.append("'");
      sql.append(" order by AREA_INDEX");
      conn = DAOFactory.getInstance().getConnection();

      statement = conn.createStatement();
      rs = statement.executeQuery(sql.toString());

      while (rs.next()) {
        sb.append("         <div id=\"ie5add" + i
          + "\" class=\"menuitems\" onclick=\"addToA()\">&nbsp;&nbsp;");
        sb.append("加入");
        sb.append(rs.getString("AREA_NAME"));
        sb.append("</div>");
        i++;
        if (i == 4) {
          break;
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("DeskTopBean类的getIe5menu方法：" + "SQL语句执行错误！"
        + ex.toString());
    }

    finally {
      DBHelper.closeConnection(conn, statement, rs);
    }
    sb.append("  </div>");

    return sb.toString();
  }

  public String getdesktophtml() {
    StringBuffer tempStringBuffer = new StringBuffer();
    DesktopArea tempDesktopArea;
    tempStringBuffer.append("<tr>\n");
    tempStringBuffer.append("<td align=\"center\" width=\"50%\"  height=\"48%\">");
    tempDesktopArea = (DesktopArea) this.getDeskTopAreas().get(new Integer(1));
    if (null != tempDesktopArea) {
      tempStringBuffer.append(tempDesktopArea.createAreaHTML(this.lang));
    }

    tempStringBuffer.append("         </td>");
    tempStringBuffer.append("<td align=\"center\" width=\"50%\" height=\"48%\">");

    tempDesktopArea = (DesktopArea) this.getDeskTopAreas().get(new Integer(2));
    if (null != tempDesktopArea) {
      tempStringBuffer.append(tempDesktopArea.createAreaHTML(this.lang));
    }

    tempStringBuffer.append("         </td>");
    tempStringBuffer.append("     </tr>");
    tempStringBuffer
      .append("     <tr align=\"center\" width=\"50%\"    height=\"48%\">");
    tempStringBuffer
      .append(" <td align=\"center\" width=\"50%\"    height=\"48%\" >");
    tempDesktopArea = (DesktopArea) this.getDeskTopAreas().get(new Integer(3));
    if (null != tempDesktopArea) {
      tempStringBuffer.append(tempDesktopArea.createAreaHTML(this.lang));
    }

    tempStringBuffer.append("               </td>");
    tempStringBuffer
      .append("                    <td align=\"center\" width=\"50%\"    height=\"48%\" >");
    tempDesktopArea = (DesktopArea) this.getDeskTopAreas().get(new Integer(4));
    if (null != tempDesktopArea) {
      tempStringBuffer.append(tempDesktopArea.createAreaHTML(this.lang));
    }

    tempStringBuffer.append("               </td>\n");
    tempStringBuffer.append("     </tr>");

    return tempStringBuffer.toString();
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUserID() {
    return this.userID;
  }

  public boolean getIsShowRec(String areaID, String userName)
    throws BusinessException {
    boolean returnBln = false;

    Connection myConn = DAOFactory.getInstance().getConnection();
    Statement statement = null;
    try {
      statement = myConn.createStatement();
      String tempSql = "select IS_DISPLAY_REC from AS_DESKTOP " + "where AREA_ID='"
        + areaID + "' and USER_ID='" + userName + "'";
      ResultSet rs = statement.executeQuery(tempSql);
      if (rs.next()) {
        String tempStr = rs.getString("IS_DISPLAY_REC");
        returnBln = tempStr == null ? false : tempStr.equalsIgnoreCase("y");
      } else {
        throw new BusinessException("数据库中没有，区域号：" + areaID + "   用户名：" + userName
          + " 的信息！");
      }

    } catch (SQLException ex) {
      throw new RuntimeException("DeskTopBean类的areaNametoID方法：在桌面的区域中"
        + "加入部件时，SQL语句执行错误！");
    } finally {
      DBHelper.closeConnection(myConn, statement, null);
    }
    return returnBln;

  }

  private String userID;

  private HttpServletRequest request = null;

  private Map deskTopAreas = new HashMap();

  private String lang;

}
