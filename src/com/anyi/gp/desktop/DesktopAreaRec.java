
package com.anyi.gp.desktop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.TableData;

public class DesktopAreaRec implements IAreaItem {
  public DesktopAreaRec(HttpServletRequest request) {
  }

  public static void setCustomerDesktopItemCreator(String compoName,
    DefaultDeskTopItemCreator creator) {
    customerDesktopCreatorPool.put(compoName, creator);
  }

  public String getAreaID() {
    return areaID;
  }

  public void setAreaID(String areaID) {
    this.areaID = areaID;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getTabId() {
    return tabId;
  }

  public void setTabId(String tabId) {
    this.tabId = tabId;
  }

  public String getStrHref() {
    return strHref;
  }

  public void setStrHref(String strHref) {
    this.strHref = strHref;
  }

  public String getCompoIMG() {
    return compoIMG;
  }
  
  public void setCompoIMG(String CompoIMG) {
    this.compoIMG = CompoIMG;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getTitleDate() {
    return titleDate;
  }

  public void setTitleDate(Date titleDate) {
    this.titleDate = titleDate;
  }

  public String createHref(String compoID, String condition) {
    StringBuffer sb = new StringBuffer("getpage_");
    sb.append(compoID);
    sb.append(".action?function=geteditpage&condition=");
    sb.append(condition);
    sb.append("&componame=");
    sb.append(compoID);
    sb.append("&fieldvalue=");
    sb.append(compoID);
    sb.append("_E");
    return sb.toString();
  }

  public String createHref(String compoID, String parCompoID, String condition) {
    StringBuffer sb = new StringBuffer("getpage_");
    sb.append(compoID);
    sb.append(".action?function=geteditpage&condition=");
    sb.append(condition);
    sb.append("&componame=");
    sb.append(compoID);
    sb.append("&fieldvalue=");
    sb.append(parCompoID);
    sb.append("_E");
    return sb.toString();
  }
  
  public String createAreaHTML(String lang) {
    StringBuffer mainStr = new StringBuffer();
    mainStr.append("   <tr>\n");
    mainStr.append("   <td>\n");

    mainStr.append("<A class=\"taskList\"  name=\"" + tabId + "\"");
    mainStr.append("id=\"" + tabId + "IDF\"");
    if (this.strHref != null) {
      mainStr.append(" url=\"");
      mainStr.append(this.strHref + "\"");
      mainStr.append(" href=\"javascript:");
      mainStr.append("     clickItem();\"");
    }
    
    mainStr.append(" onmousedown=\"menuEvent()\" >");
    mainStr.append(this.instanceOfDesktopItemCreator().toHtml(this.tableData));
    mainStr.append("         </A>\n");
    mainStr.append("   </td>\n");
    mainStr.append("   </tr>\n");
    mainStr.append("   <tr><td background=\"/style/img/main/linebk.gif\" height=1></td></tr>\n");
    
    return mainStr.toString();
  }

  private DefaultDeskTopItemCreator instanceOfDesktopItemCreator() {
    if (customerDesktopCreatorPool.containsKey(this.compoName))
      return (DefaultDeskTopItemCreator) customerDesktopCreatorPool.get(compoName);
    return new DefaultDeskTopItemCreator();
  }
  private String areaID;

  private String userID;

  private String tabId;

  private String strHref = null;

  private String compoIMG;

  private String title;

  Date titleDate;

  private TableData tableData;

  private String compoName;

  private static Map customerDesktopCreatorPool = new HashMap();

  public class DefaultDeskTopItemCreator {

    public String toHtml(TableData td) {
      StringBuffer result = new StringBuffer();
      result.append("<img height=16 src=\"");
      result.append(getCompoIMG() == null ? "/style/img/main/dot.gif"
        : getCompoIMG());
      result.append("\" width=16 border=0>&nbsp;&nbsp;</img>");
      result.append(getTitle() == null ? "£€Œﬁ±ÍÃ‚£›" : getTitle());
      result.append("&nbsp;&nbsp;&nbsp;");
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
      if (titleDate == null) {
        result.append("");
      } else {
        result.append(simpleDateFormat.format(getTitleDate()));
      }
      return result.toString();
    }

  }

  /**
   * @return   Returns the compoName.
   * @uml.property   name="compoName"
   */
  public String getCompoName() {
    return compoName;
  }

  /**
   * @param compoName   The compoName to set.
   * @uml.property   name="compoName"
   */
  public void setCompoName(String compoName) {
    this.compoName = compoName;
  }

  /**
   * @return   Returns the tableData.
   * @uml.property   name="tableData"
   */
  public TableData getTableData() {
    return tableData;
  }

  /**
   * @param tableData   The tableData to set.
   * @uml.property   name="tableData"
   */
  public void setTableData(TableData tableData) {
    this.tableData = tableData;
  }
}
