package com.anyi.gp.desktop;

import com.anyi.gp.Pub;
import com.anyi.gp.util.XMLTools;

public class Title {

  private String groupId;
  
  private String titleId;
  
  private String titleName;
  
  private int index;

  private String titleDesc;
  
  private String titleUrl;
  
  private int colCount;
  
  public int getColCount() {
    return colCount;
  }

  public void setColCount(int colCount) {
    this.colCount = colCount;
  }

  public String getTitleDesc() {
    return titleDesc;
  }

  public void setTitleDesc(String titleDesc) {
    this.titleDesc = titleDesc;
  }

  public String getTitleUrl() {
    return titleUrl;
  }

  public void setTitleUrl(String titleUrl) {
    this.titleUrl = titleUrl;
  }

  public Title(){}
  
  public Title(org.w3c.dom.Node node){
    groupId = XMLTools.getNodeAttr(node, "group_id");
    titleId = XMLTools.getNodeAttr(node, "page_id");
    titleName = XMLTools.getNodeAttr(node, "page_name");
    index = Pub.parseInt(XMLTools.getNodeAttr(node, "page_order"));
    if(titleId == null || titleId.length() == 0)
      titleId = Pub.getUID();
  }
  
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getTitleId() {
    return titleId;
  }

  public void setTitleId(String titleId) {
    this.titleId = titleId;
  }

  public String getTitleName() {
    return titleName;
  }

  public void setTitleName(String titleName) {
    this.titleName = titleName;
  }
  
  public String toHtml(){
    StringBuffer result = new StringBuffer();
    result.append("<td>");
    result.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
    result.append("<tr>");
    
    if(index == -1){
      result.append("<td id=\"" + titleId + "A\" height=\"19\" width=\"5\"><img src=\"/style/img/main/toptdleft.gif\"></td>");
      result.append("<td id=\"" + titleId + "B\" class=\"menunew2\"><a href=\"#\" onclick=\"OnFocus('" + titleId +"','" + titleName + "');\">");
      result.append(titleName);
      result.append("</a></td>");
      result.append("<td id=\"" + titleId + "C\" height=\"19\" width=\"5\"><img src=\"/style/img/main/toptdright.gif\"></td>");
    }else{
      result.append("<td id=\"" + titleId + "A\" height=\"19\" width=\"5\"></td>");
      result.append("<td id=\"" + titleId + "B\" class=\"menunew3\"><a href=\"#\" onclick=\"OnFocus('" + titleId + "','" + titleName + "');\">");
      result.append(titleName);
      result.append("</a></td>");
      result.append("<td id=\"" + titleId + "C\" height=\"19\" width=\"5\"></td>");
    }
    
    result.append("</tr>");
    result.append("</table>");
    result.append("</td>");
    
    return result.toString();
  }
}
