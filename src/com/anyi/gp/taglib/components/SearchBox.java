package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class SearchBox implements Component {

  private final static String MATCH_TEXT_DEFAULT = "请输入要搜索的关键字";

  private String id = "";

  private String idsuffix = "_SEARCH";

  private String tablename = "";

  private String function = "getlistpagedata";

  private String pattern = "";

  private int tabindex = 0;

  private String groupid = "";

  private boolean isvisible = true;

  private boolean ismatchinputvisible = true;

  private boolean issearchbtnvisible = true;

  private boolean isadvancebtnvisible = true;

  private boolean isgroupbtnvisible = true;

  private String cssclass = "";

  private String style = "";

  private String oninit = "";

  private String kilofields = ""; // 逗号分隔;
  
  private Page ownerPage = null;

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeHTML(Writer out) throws IOException {
    init();
    out.write(make());
  }

  public void writeInitScript(Writer out) throws IOException {
    out.write("var " + getBoxScriptVariable() + " = new Search();\n");
    out.write(getBoxScriptVariable() + ".MATCH_TEXT_DEFAULT = \"");
    out.write(MATCH_TEXT_DEFAULT);
    out.write("\";\n");
    out.write(getBoxScriptVariable() + ".make('" + this.getId() + "');\n");
    out.write(getBoxScriptVariable()+ ".init();\n");
    out.write("PageX.regCtrlObj(\"" + this.getId() + "\", ");
    out.write(getBoxScriptVariable() +");\n");
  }
  
  public String getBoxScriptVariable(){
    return this.getGroupId() + "_SearchV";
  }

  private void init() {
    try {
      if (this.getId() == null || this.getId().length() == 0) {
        this.setId(this.getTableName() + "_" + Pub.getUID() + "_"
          + this.getIdsuffix());
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private String makeOuterPanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='" + this.getId() + "' " + this.makeAttr() + " style='"
      + this.getOuterpanelstyle() + this.style + "' hidefocus='true'>\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_2() {
    return "</div>\n";
  }

  private String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("tablename='" + this.tablename + "' ");
    voBuf.append("function4='" + this.function + "' ");
    voBuf.append("pattern=\"" + StringTools.replaceAll(this.pattern, "\"", "&quot;")
      + "\" ");
    voBuf.append("groupid='" + this.groupid + "' ");
    voBuf.append("tabindex=" + this.tabindex + " ");
    voBuf.append("kilofields='" + this.kilofields + "' ");
    return voBuf.toString();
  }

  private String makeK() {
    StringBuffer voBuf = new StringBuffer();
    
    boolean isExitSchema = false;
    String pageType = this.ownerPage.getPageType();
    if(Page.PAGE_TYPE_LIST.equals(pageType)){//列表页面
      String compoId = this.ownerPage.getCurrRequest().getParameter("componame");
      String userId = SessionUtils.getAttribute(this.ownerPage.getCurrRequest(), "svUserID");
      List scheList = GeneralFunc.getSearchSchema(compoId + "_search", userId);
      if(scheList.size() > 0){
        isExitSchema = true;
        voBuf.append(getSchemaNameMenuHTML("search", compoId, scheList));
      }
    }
    
    voBuf.append("<table border=0 cellpadding='0' cellspacing='1'>\n");
    voBuf.append("<tr>\n");

    voBuf.append("<td id=\"MatchValueInputTD\"  style=\"display:"
      + (this.ismatchinputvisible ? "" : "none") + ";\">\n");
    voBuf
      .append("<input type='text' id='MatchValueInput' value='"
        + MATCH_TEXT_DEFAULT
        + "' style='font-size:9pt;border:1 solid #8B8B89;background-color:transparent;' size='20'>");
    voBuf.append("</td>\n");

    voBuf.append("<td id=\"SearchButtonTD\" style=\"display:"
      + (this.issearchbtnvisible ? "" : "none") + ";\">\n");

    voBuf.append("<table border=0 cellpadding='0' cellspacing='0'>");
    voBuf.append("<tr>");
    voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonleft.gif\"></td>");
    voBuf
      .append("<td background=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonmid.gif\" valign=bottom align=center nowrap>");
    voBuf
      .append("<input type='button' id='SearchButton' class=\"clsListCallEdit\" value='搜索'>");
    voBuf.append("</td>");
    voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonright.gif\"></td>");
    voBuf.append("</tr>");
    voBuf.append("</table>");

    voBuf.append("</td>\n");

    voBuf.append("<td id=\"AdvanceButtonTD\" style=\"display:"
      + (this.isadvancebtnvisible ? "" : "none") + ";\">\n");

    voBuf.append("<table border=0 cellpadding='0' cellspacing='0'>");
    voBuf.append("<tr>");
    if(isExitSchema){
      voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/search_menu.jpg\"");
      voBuf.append("class=\"clsListCallEdit\" onclick=\"openMenu(event,'searchMenu')\"</td>");
      //voBuf.append("onMouseOver=\"menuChange(event)\" onMouseOut=\"menuBlur('highSearch')\"></td>");
      voBuf.append("<td><input type='hidden' id='AdvanceButton' class=\"clsListCallEdit\" value='高级搜索'>");
      voBuf.append("</td>");
    }else{
      voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonleft.gif\"></td>");
      voBuf
        .append("<td background=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonmid.gif\" valign=bottom align=center nowrap>");
      voBuf
        .append("<input type='button' id='AdvanceButton' class=\"clsListCallEdit\" value='高级搜索'>");
      voBuf.append("</td>");
      voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonright.gif\"></td>");
    }
    voBuf.append("</tr>");
    voBuf.append("</table>");

    voBuf.append("</td>\n");

    voBuf.append("<td id=\"GroupButtonTD\" style=\"display:none;\">\n");

    voBuf.append("<table border=0 cellpadding='0' cellspacing='0'>");
    voBuf.append("<tr>");
    voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonleft.gif\"></td>");
    voBuf
      .append("<td background=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonmid.gif\" valign=bottom align=center nowrap>");
    voBuf
      .append("<input type='button' id='GroupButton' class=\"clsListCallEdit\" value='统计'>");
    voBuf.append("</td>");
    voBuf.append("<td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/buttonright.gif\"></td>");
    voBuf.append("</tr>");
    voBuf.append("</table>");

    voBuf.append("</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</table>\n");
    return voBuf.toString();
  }

  private String make() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(this.makeOuterPanel_1());
    voBuf.append(this.makeK());
    voBuf.append(this.makeOuterPanel_2());
    return voBuf.toString();
  }

  private String getOuterpanelstyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("display:" + (this.isIsvisible() ? "" : "none") + ";");
        voBuf.append("cursor:hand;Z-INDEX:10000;");
    return voBuf.toString();
  }

  public void setTablename(String tablename) {
    this.tablename = tablename;
  }

  public String getTableName() {
    return tablename;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setOninit(String oninit) {
    this.oninit = oninit;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  public String getGroupId() {
    return groupid;
  }

  public void setGroupid(String groupid) {
    this.groupid = groupid;
  }

  public boolean isIsgroupbtnvisible() {
    return isgroupbtnvisible;
  }

  public void setIsgroupbtnvisible(boolean isgroupbtnvisible) {
    this.isgroupbtnvisible = isgroupbtnvisible;
  }

  public boolean isIsmatchinputvisible() {
    return ismatchinputvisible;
  }

  public void setIsmatchinputvisible(boolean ismatchinputvisible) {
    this.ismatchinputvisible = ismatchinputvisible;
  }

  public boolean isIssearchbtnvisible() {
    return issearchbtnvisible;
  }

  public void setIssearchbtnvisible(boolean issearchbtnvisible) {
    this.issearchbtnvisible = issearchbtnvisible;
  }

  public boolean isIsvisible() {
    return isvisible;
  }

  public void setIsvisible(boolean isvisible) {
    this.isvisible = isvisible;
  }

  /**
   * <applus:area> 接口方法的实现;
   */
  public void setTagAttributes(Node tagNode) {
    this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
    this.setIdsuffix(XMLTools.getNodeAttr(tagNode, "idsuffix", this.idsuffix));
    this.setTablename(XMLTools.getNodeAttr(tagNode, "tablename", this.tablename));
    this.setFunction(XMLTools.getNodeAttr(tagNode, "function", this.function));
    this.setPattern(XMLTools.getNodeAttr(tagNode, "pattern", this.pattern));
    this.setGroupid(XMLTools.getNodeAttr(tagNode, "groupid", this.groupid));
    this.setTabindex(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "tabindex", ""
      + this.tabindex)));

    this.setIsvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isvisible", "" + this.isvisible))
      .booleanValue());
    this.setIsmatchinputvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "ismatchinputvisible", ""
        + this.ismatchinputvisible)).booleanValue());
    this.setIssearchbtnvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "issearchbtnvisible", ""
        + this.issearchbtnvisible)).booleanValue());
    this.setIsadvancebtnvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isadvancebtnvisible", ""
        + this.isadvancebtnvisible)).booleanValue());
    this.setIsgroupbtnvisible(Boolean.valueOf(
      XMLTools
        .getNodeAttr(tagNode, "isgroupbtnvisible", "" + this.isgroupbtnvisible))
      .booleanValue());

    this.setCssclass(XMLTools.getNodeAttr(tagNode, "cssclass", this.cssclass));
    this.setStyle(XMLTools.getNodeAttr(tagNode, "style", this.style));
    this.setOninit(XMLTools.getNodeAttr(tagNode, "oninit", this.oninit));

    this.setKilofields(XMLTools.getNodeAttr(tagNode, "kilofields", this.kilofields));
  }

  private String getSchemaNameMenuHTML(String schemaType, String compoId, List scheList) {
    StringBuffer sMenu = new StringBuffer();
    StringBuffer sSystemMenu = new StringBuffer();
    StringBuffer sUserMenu = new StringBuffer();
    int maxLength = 0;
    if (schemaType.equalsIgnoreCase("search")) {
      //compoId = compoId + "_search";
      sMenu.append("<table class=menu ID=searchMenu style='display:none'><tr>\n");
    }
    
    int count = 0;
    for(int i = 0; i < scheList.size(); i++){
      count++;
      Map map = (Map)scheList.get(i);
      String scheDesc = (String)map.get("USER_SCHE_DESC");
      String isSystemSche = (String)map.get("IS_SYSTEM_SCHE");
      int temp = this.calculateSymbolNum(scheDesc);
      int tmpSLen = 0;
      if (temp > 0)
        tmpSLen = scheDesc.length() - (temp + 1) / 2;
      else
        tmpSLen = scheDesc.length();
      if (tmpSLen > maxLength)
        maxLength = tmpSLen;
      
      if (isSystemSche.equalsIgnoreCase("y") || isSystemSche.equals("1"))
        sSystemMenu.append(getMenuRowHTML(schemaType, scheDesc, compoId,i));
      else
        sUserMenu.append(getMenuRowHTML(schemaType, scheDesc, compoId, i));
    }
      
    sMenu.append(sSystemMenu.toString());
    if (!sSystemMenu.toString().equals("") && !sUserMenu.toString().equals(""))
      sMenu.append("<tr><td><hr size='1px' color='#7184A9'></td></tr>\n");
    sMenu.append(sUserMenu.toString());
    if (count <= 0 && schemaType.equalsIgnoreCase("search"))
      sMenu.append("<tr><td id=searchMenu1 value=\"\"></td></tr>\n");
    if (count <= 0 && schemaType.equalsIgnoreCase("stat"))
      sMenu.append("<tr><td id=statMenu1 value=\"\"></td></tr>\n");
    if (schemaType.equalsIgnoreCase("search")) {
      sMenu.append("<tr><td id=searchMaxLen value=\"");
      sMenu.append(maxLength);
      sMenu.append("\"></td></tr>\n");
    }
    if (schemaType.equalsIgnoreCase("stat")) {
      sMenu.append("<tr><td id=statMaxLen value=\"");
      sMenu.append(maxLength);
      sMenu.append("\"></td></tr>\n");
    }
    
    sMenu.append("<tr><td><hr size='1px' color='#7184A9'></td></tr>\n");
    if (schemaType.equalsIgnoreCase("search"))
      sMenu.append("<tr><td id=searchM value=\"设置方案\" onclick='searchF()' onmouseover='doHight(event.toElement)' onmouseout='clearHight(event,\"searchM\")'>&nbsp&nbsp设置方案&nbsp&nbsp</td></tr>\n");
    
    sMenu.append("</tr></table>");
    return sMenu.toString();
  }

  private String getMenuRowHTML(String schemaType, String tmpS, String compoId, int index) {
    StringBuffer sMenu = new StringBuffer();
    sMenu.append("<tr><td id=");
    if (schemaType.equalsIgnoreCase("search")) {
      sMenu.append("searchMenu" + index);
      sMenu.append(" value=");
      sMenu.append(tmpS);
      sMenu.append(" onclick='directSearchF(\"");
      sMenu.append(tmpS + "\",\"" + compoId + "\",\"" + id);
      sMenu.append("\")' ");
      sMenu.append("onmouseover='doHight(event.toElement)' ");
      sMenu.append("onmouseout='clearHight(event,\"searchMenu");
      sMenu.append(index);
    }
    if (schemaType.equalsIgnoreCase("stat")) {
      sMenu.append("statMenu" + index);
      sMenu.append(" value=");
      sMenu.append(tmpS);
      sMenu.append(" onclick='directStatSearch(\"");
      sMenu.append(tmpS + "\",\"" + compoId + "\",\"" + id);
      sMenu.append("\")' ");
      sMenu.append("onmouseover='doHight(event.toElement)' ");
      sMenu.append("onmouseout='clearHight(event,\"statMenu");
      sMenu.append(index);
    }
    sMenu.append("\")'>&nbsp&nbsp");
    sMenu.append(tmpS);
    sMenu.append("&nbsp&nbsp</td></tr>\n");
    return sMenu.toString();
  }
  
  /**
   * 计算数字符号的个数
   *
   * @param str
   *          String
   */
  private int calculateSymbolNum(String str) {
    String symbol = "1234567890abcdefghijklmnopqrstuvwxyz!@#$%^&*(){}:?><,./';]['-+=";
    int number = 0;
    for (int i = 0; i < str.length(); i++) {
      if ((symbol.indexOf((str.substring(i, i + 1)).toLowerCase())) >= 0)
        number++;
    }
    return number;
  }
  
  public String getIdsuffix() {
    return idsuffix;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public boolean isIsadvancebtnvisible() {
    return isadvancebtnvisible;
  }

  public void setIsadvancebtnvisible(boolean isadvancebtnvisible) {
    this.isadvancebtnvisible = isadvancebtnvisible;
  }

  public String getKilofields() {
    return kilofields;
  }

  public void setKilofields(String kilofields) {
    this.kilofields = kilofields;
  }

}
