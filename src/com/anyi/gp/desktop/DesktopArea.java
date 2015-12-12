package com.anyi.gp.desktop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.workflow.util.WFCompoType;
import com.anyi.gp.workflow.util.WFConst;

public class DesktopArea {
  
  public DesktopArea(HttpServletRequest request) {
    this.request = request;
  }

  public String getAreaID() {
    return areaID;
  }

  public void setAreaID(String areaID) {
    this.areaID = areaID;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String createAreaHTML(String lang) {
    int i = 0;
    StringBuffer tempStringBuffer = new StringBuffer();
    tempStringBuffer
      .append("             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=80%  height=100%>");
    tempStringBuffer.append("               <tr>");
    tempStringBuffer
      .append("                   <td align=left><table><tr><td><img src=\"/style/"
        + this.areaIMG + "\"></td><td align=left class=\"boldf\">" + this.areaName
        + "</td>");
    tempStringBuffer.append("               </tr></table></td></tr>");
    tempStringBuffer.append("               <tr>");
    tempStringBuffer.append("                     <ul>");
    Iterator iterator = this.desktopAreaItems.iterator();

    while (iterator.hasNext()) {
      if (i == this.getDisplayAmount()) {
        break;
      }
      ++i;
      IAreaItem desktopAreaItem = (IAreaItem) iterator.next();
      tempStringBuffer.append(desktopAreaItem.createAreaHTML(lang));
    }
    tempStringBuffer.append("                     </ul>");
    tempStringBuffer.append("               </tr>");
    tempStringBuffer.append("               <tr>");
    tempStringBuffer
      .append("                <td align=\"right\" valign=\"bottom\" height=100%>");
    tempStringBuffer.append("               </td>");
    tempStringBuffer.append("               </tr>");
    tempStringBuffer.append("            </table>");

    return tempStringBuffer.toString();
  }

  public static DesktopArea getInstanceFromDB(String areaID,
    HttpServletRequest request, String tempUserID) throws BusinessException {
    DesktopArea instance = new DesktopArea(request);
    instance.setAreaID(areaID);
    instance.setUserID(tempUserID);

    if (!instance.buildMyselfFromDB()) {
      throw new BusinessException("没有找到【此主键信息的对象】！", null);
    }
    if (instance.isIsDisplayRec()) {
      instance.buildMyRecFromDB();
    } else {
      instance.buildMyItemsFromDB();
    }

    return instance;
  }

  public static DesktopArea getInstanceFromDB(String areaID,
    HttpServletRequest request, String tempUserID, String strFilePath)
    throws BusinessException {
    DesktopArea instance = new DesktopArea(request);
    instance.setAreaID(areaID);
    instance.setUserID(tempUserID);

    if (!instance.buildMyselfFromDB()) {
      throw new BusinessException("没有找到【此主键信息的对象】！", null);
    }
    if (instance.isIsDisplayRec()) {
      instance.buildMyRecFromDB();
    } else {
      instance.buildMyItemsFromDB();
    }

    return instance;
  }


  public List getDesktopAreaItems() {
    return desktopAreaItems;
  }

  private boolean buildMyselfFromDB() throws BusinessException {
    String methodFunction = "从数据库查找并组装【此对象】";
    boolean hasInDB = false;
    StringBuffer sql = new StringBuffer();
    sql.append("select * from AS_DESKTOP ");
    sql.append("where AREA_ID = ? and USER_ID =?");
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    conn = DAOFactory.getInstance().getConnection();
    try {
      pstm = conn.prepareStatement(sql.toString());
      pstm.setString(1, this.getAreaID());
      pstm.setString(2, this.getUserID());
      rs = pstm.executeQuery();
      if (rs.next()) {
        hasInDB = true;
        this.setAreaName(rs.getString("AREA_NAME"));
        this.setIndex(rs.getInt("AREA_INDEX"));
        this.setAreaIMG(rs.getString("AREA_IMG"));
        String isDisplayRec = rs.getString("IS_DISPLAY_REC");
        boolean temBln = (isDisplayRec == null) ? false : isDisplayRec
          .equalsIgnoreCase("y");
        this.setIsDisplayRec(temBln);
        this.setDisplayAmount(rs.getInt("DISPLAY_AMOUNT"));
      }
    } catch (SQLException ex) {
      String message = "在" + methodFunction + "时出现SQL异常！";
      throw new BusinessException(message, null);
    } finally {
      DBHelper.closeConnection(conn, pstm, rs);
    }
    return hasInDB;
  }

  private boolean existItem(DesktopAreaItem desktopAreaItem) {
    for (int i = 0; i < desktopAreaItems.size(); i++) {
      DesktopAreaItem temp = (DesktopAreaItem) desktopAreaItems.get(i);
      String srcCompoIDMenuID = temp.getCompoID()
        + ((temp.getMenuID() == null) ? "" : temp.getMenuID());
      String destCompoIDMenuID = desktopAreaItem.getCompoID()
        + ((desktopAreaItem.getMenuID() == null) ? "" : desktopAreaItem.getMenuID());
      if (srcCompoIDMenuID.equalsIgnoreCase(destCompoIDMenuID)) {
        return true;
      }
    }
    return false;
  }

  private String fetchCompoIDFromDB(String userID) throws BusinessException {
    String returnStr = "";
    String methodFunction = "从数据库查找并组装【此对象】";
    StringBuffer sql = new StringBuffer();
    sql.append("select DISTINCT COMPO_ID ");
    sql.append("from AS_DESKTOP_AREA ");
    sql.append("where AREA_ID = ? ");
    sql.append("and USER_ID= ? ");

    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    conn = DAOFactory.getInstance().getConnection();
    try {
      pstm = conn.prepareStatement(sql.toString());
      pstm.setString(1, this.getAreaID());
      pstm.setString(2, userID);

      rs = pstm.executeQuery();
      if (rs.next()) {
        returnStr = rs.getString("COMPO_ID");
      }
    } catch (SQLException ex) {
      String message = "在" + methodFunction + "时出现SQL异常！";
      throw new BusinessException(message, null);
    } finally {
      DBHelper.closeConnection(conn, pstm, rs);
    }
    return returnStr;
  }

  private void buildMyRecFromDB() throws BusinessException {
    String compoIDStr = fetchCompoIDFromDB(userID);

    if (compoIDStr == null || compoIDStr.equalsIgnoreCase("")) {
      return;
    }
    CompoMeta tm = MetaManager.getCompoMeta(compoIDStr);
    if (tm == null) {
      throw new BusinessException("数据库中没有部门编号为：" + compoIDStr + "的信息！");
    }
    if (tm.getWfFlowType() != null && tm.getWfFlowType().length() > 0) {
      this.buildMyWorkflowRecFromDB();
    } else {
      this.buildRecsFromDByRule(tm);
    }
  }

  private void buildMyItemsFromDB() throws BusinessException {
    String methodFunction = "从数据库查找并组装【此对象】";
    StringBuffer sql = new StringBuffer();
    sql.append("select DISTINCT DA.COMPO_ID,MC.IS_GOTO_EDIT,MC.IS_ALWAYS_NEW,");
    sql.append("MC.URL,CP.ICON_NAME,DA.MENU_ID,CP.PARENT_COMPO ");
    sql.append("from AS_DESKTOP_AREA DA,AS_COMPO CP,V_AP_MENU_COMPO MC ");
    sql.append("where AREA_ID = ? ");
    sql.append("and USER_ID= ? ");
    sql.append("and CP.COMPO_ID=DA.COMPO_ID ");
    sql.append("and CP.COMPO_ID=MC.COMPO_ID ");
    sql.append("and DA.MENU_ID=MC.MENU_ID");
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    conn = DAOFactory.getInstance().getConnection();
    try {
      pstm = conn.prepareStatement(sql.toString());
      pstm.setString(1, this.getAreaID());
      pstm.setString(2, userID);

      rs = pstm.executeQuery();
      while (rs.next()) {
        DesktopAreaItem desktopAreaItem = new DesktopAreaItem();
        /**
         * 字段1含义，字段2含义，字段3含义，字段4含义
         */
        desktopAreaItem.setAreaID(this.getAreaID());
        desktopAreaItem.setCompoID(rs.getString("COMPO_ID"));
        desktopAreaItem.setParentCompo(rs.getString("PARENT_COMPO"));
        desktopAreaItem.setMenuID(rs.getString("MENU_ID"));
        if (existItem(desktopAreaItem)) {
          continue;
        }

        desktopAreaItem.setUserID(userID);
        desktopAreaItem.setIsGotoEdit(rs.getString("IS_GOTO_EDIT"));
        desktopAreaItem.setIsAlwaysNew(rs.getString("IS_ALWAYS_NEW") == null ? "N"
          : rs.getString("IS_ALWAYS_NEW"));

        desktopAreaItem.setStrURL(rs.getString("URL"));
        desktopAreaItem.setCompoIMG(rs.getString("ICON_NAME"));
        desktopAreaItems.add(desktopAreaItem);
      }
    } catch (SQLException ex) {
      String message = "在" + methodFunction + "时出现SQL异常！";
      throw new BusinessException(message, null);
    } finally {
      DBHelper.closeConnection(conn, pstm, rs);
    }

  }

  
  private void buildMyWorkflowRecFromDB() throws BusinessException {
    String compoIDStr = fetchCompoIDFromDB(userID);
    String tempTitleField;
    String tempTitleDate;

    if (compoIDStr == null || compoIDStr.equalsIgnoreCase("")) {
      return;
    }
    CompoMeta tm = MetaManager.getCompoMeta(compoIDStr);
    if (tm == null) {
      throw new BusinessException("数据库中没有部门编号为：" + compoIDStr + "的信息！");
    }

    // 先从部件名称上判断是否为已办与待办
    String tempStr = tm.getName();

    boolean isTodoOrDone = WFCompoType.WF_TODO.equalsIgnoreCase(tempStr);
    isTodoOrDone = isTodoOrDone || WFCompoType.WF_DONE.equalsIgnoreCase(tempStr);
    isTodoOrDone = isTodoOrDone || WFCompoType.WF_COMPO.equalsIgnoreCase(tempStr);
    isTodoOrDone = isTodoOrDone || WFCompoType.WF_COMPO_OTHER.equalsIgnoreCase(tempStr);
    if (!isTodoOrDone) {
     
    }
    
    tempTitleField = tm.getTitleField();

    if ((tempTitleField == null || tempTitleField.equalsIgnoreCase(""))
      && !isTodoOrDone) {
      disposeNullTilte(tm);
      return;
    }
    
    tempTitleDate = tm.getTitleDate();
    boolean isNullDate = tempTitleDate == null;
    
//    DesktopFacade desktopFacade = new DesktopFacade(request, tm.getCompoId(),
//      userID, null);
//
//    Delta delta = desktopFacade.getDeltaAll();
    Delta delta = new Delta();
    Iterator iterator = delta.iterator();
    
    while (iterator.hasNext()) {
      TableData td = (TableData) iterator.next();
      if (td.getName().equalsIgnoreCase("PARAS")) { 
        continue;
      }
      DesktopAreaRec desktopAreaRec = new DesktopAreaRec(this.request);
      desktopAreaRec.setAreaID(this.getAreaID());
      desktopAreaRec.setTabId(tm.getName());
      desktopAreaRec.setUserID(userID);
      desktopAreaRec.setCompoName(compoIDStr);
      desktopAreaRec.setTableData(td);
      
      if (isTodoOrDone) {
        desktopAreaRec.setTitle(td.getFieldValue("WF_INSTANCE_NAME"));
      } else {
        desktopAreaRec.setTitle(td.getFieldValue(tm.getTitleField()));
      }

      if (!isNullDate) {
        Date date = null;
        String dateFieldValues = td.getFieldValue(tempTitleDate);
        if (!(dateFieldValues == null || "".equals(dateFieldValues))) {
          try {
            date = DateFormat.getDateInstance().parse(dateFieldValues);
          } catch (ParseException ex) {
            System.out.println("从数据库中读取的日期格式不正确!部件名称名称:" + tm.getName());
            System.out.println("从数据库中读取的日期格式不正确!日期字段名称:" + tempTitleDate);
            System.out.println("日期字段值:" + td.getFieldValue(tempTitleDate));
            date = null;
          }
          desktopAreaRec.setTitleDate(date);
        }
      }
      StringBuffer url = new StringBuffer();
      url.append("Proxy?function=geteditpage&condition=");
      if (isTodoOrDone) {
        //url.append("&wf_template_id=");
        url.append("&").append(WFConst.WF_TEMPLATE_ID).append("=");
        url.append(td.getField(WFConst.WF_TEMPLATE_ID));
        //url.append("&process_inst_id=");
        url.append("&").append(WFConst.WF_INSTANCE_ID).append("=");
        url.append(td.getField(WFConst.WF_INSTANCE_ID));
        //url.append("&wf_activity_id=");
        url.append("&").append(WFConst.WF_NODE_ID).append("=");
        url.append(td.getField(WFConst.WF_NODE_ID));
        if (tempStr.equalsIgnoreCase(WFCompoType.WF_TODO)) {
          //url.append("&workitem_id=");
          url.append("&").append(WFConst.WF_TASK_ID).append("=");
          url.append(td.getField(WFConst.WF_TASK_ID));
        }
      } else {
        //url.append("&process_inst_id=");
        url.append("&").append(WFConst.WF_INSTANCE_ID).append("=");
        url.append(td.getField("PROCESS_INST_ID"));
      }
      url.append("&componame=");
      url.append(tm.getName());
      url.append("&fieldvalue=");
      url.append(tm.getName());
      url.append("_E");
      desktopAreaRec.setStrHref(url.toString());

      desktopAreaRec.setCompoIMG(tm.getIconName());
      desktopAreaItems.add(desktopAreaRec);
    }
  }

  private void disposeNullTilte(CompoMeta tm) {
    DesktopAreaRec desktopAreaRec = new DesktopAreaRec(this.request);

    desktopAreaRec.setAreaID(this.getAreaID());
    desktopAreaRec.setTabId(tm.getName());
    desktopAreaRec.setUserID(userID);
    desktopAreaRec.setTitle("没有在AS_COMPO表里的title_field没有描述!");
    Date date = new Date();
    desktopAreaRec.setTitleDate(date);
    desktopAreaRec.setCompoIMG(tm.getIconName());
    desktopAreaItems.add(desktopAreaRec);
  }

  private void buildRecsFromDByRule(CompoMeta tm) throws BusinessException {
    
    String compoIDStr = tm.getName();
    String tempTitleDate;

    tempTitleDate = tm.getTitleDate();
    String tempTitleField = tm.getTitleField();

    if ((tempTitleField == null || tempTitleField.equalsIgnoreCase(""))) {
      disposeNullTilte(tm);
      return;
    }

//    DesktopFacade desktopFacade = new DesktopFacade(request, tm.getCompoId(), userID, null);
//    Delta delta = desktopFacade.getDeltaAll();
    Delta delta = new Delta();
    if (delta == null) {
      System.out.println("没有记录");
      return;
    }

    Iterator iterator = delta.iterator();

    while (iterator.hasNext()) {
      TableData td = (TableData) iterator.next();
      if (td.getName().equalsIgnoreCase("PARAS")) { 
        continue;
      }
      DesktopAreaRec desktopAreaRec = new DesktopAreaRec(this.request);
      desktopAreaRec.setAreaID(this.getAreaID());
      desktopAreaRec.setTabId(tm.getName());
      desktopAreaRec.setUserID(userID);
      desktopAreaRec.setCompoName(compoIDStr);
      desktopAreaRec.setTableData(td);

      desktopAreaRec.setTitle(td.getFieldValue(tempTitleField));

      if (tempTitleDate != null) {
        Date date = null;
        String dateFieldValues = td.getFieldValue(tempTitleDate);
        if (!(dateFieldValues == null || "".equals(dateFieldValues))) {
          try {
            date = DateFormat.getDateInstance().parse(dateFieldValues);
          } catch (ParseException ex) {
            System.out.println("从数据库中读取的日期格式不正确!部件名称名称:" + tm.getName());
            System.out.println("从数据库中读取的日期格式不正确!日期字段名称:" + tempTitleDate);
            System.out.println("日期字段值:" + td.getFieldValue(tempTitleDate));
            date = null;
          }
          desktopAreaRec.setTitleDate(date);
        }
      }
      
      StringBuffer tempCondition = new StringBuffer();
//      for (int i = 0; i < tm.getKeyFieldNames().size(); i++) {
//
//        String tempStr = (String) tm.getKeyFieldNames().get(i);
//        Field field = tm.getField(tempStr);
//        String fieldType = field.getType();
//
//        tempCondition.append(tm.getName());
//        tempCondition.append(".");
//        tempCondition.append(tempStr);
//        tempCondition.append("=");
//        if (fieldType.equalsIgnoreCase("num")) {
//          tempCondition.append(td.getFieldValue(tempStr));
//        } else {
//          tempCondition.append("'");
//          tempCondition.append(td.getFieldValue(tempStr));
//          tempCondition.append("'");
//        }
//        if (i < tm.getKeyFieldNames().size() - 1) {
//          tempCondition.append(" and ");
//        }
//      }
      String strParentcompoID = tm.getParentName();
      String strHref;
      if (strParentcompoID == null || "".equalsIgnoreCase(strParentcompoID)) {
        strHref = desktopAreaRec.createHref(compoIDStr, tempCondition.toString());
      } else {
        strHref = desktopAreaRec.createHref(compoIDStr, strParentcompoID,
          tempCondition.toString());
      }
      desktopAreaRec.setStrHref(strHref);

      desktopAreaRec.setCompoIMG(tm.getIconName());
      desktopAreaItems.add(desktopAreaRec);
    }
  }

  public String getAreaIMG() {
    return areaIMG;
  }

  public void setAreaIMG(String areaIMG) {
    this.areaIMG = areaIMG;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUserID() {
    return this.userID;
  }

  public int getDisplayAmount() {
    return displayAmount;
  }

  public void setDisplayAmount(int displayAmount) {
    this.displayAmount = displayAmount;
  }

  public boolean isIsDisplayRec() {
    return isDisplayRec;
  }

  public void setIsDisplayRec(boolean isDisplayRec) {
    this.isDisplayRec = isDisplayRec;
  }

  private String userID;

  private String areaID;

  private HttpServletRequest request = null;

  private String areaName;

  private int index;

  private List desktopAreaItems = new ArrayList();

  private String areaIMG;

  private int displayAmount;

  private boolean isDisplayRec;
}
