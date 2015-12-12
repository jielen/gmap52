package com.anyi.gp.desktop;

import com.anyi.gp.pub.LangResource;

public class DesktopAreaItem implements IAreaItem {
  public DesktopAreaItem() {
  }

  public String getAreaID() {
    return areaID;
  }

  public void setAreaID(String areaID) {
    this.areaID = areaID;
  }

  public String getCompoID() {
    return compoID;
  }

  public void setCompoID(String compoID) {
    this.compoID = compoID;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getIsGotoEdit() {
    String tempStr = (isGotoEdit == null) ? "n" : isGotoEdit;
    return tempStr.toLowerCase();
  }

  public void setIsGotoEdit(String isGotoEdit) {
    this.isGotoEdit = isGotoEdit;
  }

  public String getIsAlwaysNew() {
    String tempStr = (isAlwaysNew == null) ? "Y" : isAlwaysNew;
    return tempStr.toUpperCase();
  }

  public void setIsAlwaysNew(String isAlwaysNew) {
    this.isAlwaysNew = isAlwaysNew;
  }

  private String createNbsp(int n) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < n; i++) {
      result.append("&nbsp;&nbsp;&nbsp;");
    }
    return result.toString();
  }

  public String getStrURL() {
    return strURL;
  }

  public void setStrURL(String strURL) {
    this.strURL = strURL;
  }

  public String getCompoIMG() {
    return compoIMG;
  }

  public String getParentCompo() {
    return parentCompo;
  }

  public String getMenuID() {
    return menuID;
  }

  public void setCompoIMG(String CompoIMG) {
    this.compoIMG = CompoIMG;
  }

  public void setParentCompo(String parentCompo) {
    this.parentCompo = parentCompo;
  }

  public void setMenuID(String menuID) {
    this.menuID = menuID;
  }

  private String areaID;

  private String compoID;

  private String userID;

  private String isGotoEdit;

  private String isAlwaysNew;

  private String strURL;

  private String compoIMG;

  private LangResource lr = LangResource.getInstance();

  private String parentCompo;

  private String menuID;

  public String createAreaHTML(String lang) {
    StringBuffer mainStr = new StringBuffer();
    String compoId = this.compoID;

    mainStr.append("   <tr>\n");
    mainStr.append("   <td>\n");
    mainStr.append("<span name=\"" + compoId + "\">" + createNbsp(1) + "<img height=16 src=\"");
    mainStr.append(this.getCompoIMG() == null ? "/style/img/main/dot.gif" : this.getCompoIMG());
    mainStr.append("\" width=16 border=0><span style=\"" + "CURSOR:hand\" name=\""
      + compoId + "\" " + "id=\"" + this.getMenuID() + compoId + "ID\" "
      + "class=\"menuFont\" isgotoedit=\"" + this.getIsGotoEdit() + "\""
      + " isalwaysnew=\"" + this.getIsAlwaysNew() + "\"" + " url=\""
      + this.getStrURL() + "\"" + " parentcompo=\"" + this.getParentCompo() + "\"");
    
    if (this.getIsAlwaysNew() != null && this.getIsAlwaysNew().equalsIgnoreCase("y")) {
      mainStr.append(" onclick=\"clickLeaf(true);\">&nbsp;" + lr.getLang(compoId) + "</span><br></span>");
    } 
    else {
      mainStr.append(" onclick=\"clickLeaf(false);\">&nbsp;" + lr.getLang(compoId) + "</span><br></span>");
    }
    
    mainStr.append("   </td>\n");
    mainStr.append("   </tr>\n");
    mainStr.append("   <tr><td background=\"/style/img/main/linebk.gif\" height=1></td></tr>\n");
    
    return mainStr.toString();
  }

}
