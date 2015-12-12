package com.anyi.gp.desktop;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

public class MenuLeaf extends Leaf {
  
  private Node oldNode = null;
  
  private static LangResource lr = LangResource.getInstance();
  
  private String isAlwaysNew = null;

  private String isGotoEdit = null;

  private String parentCompo = null;

  private String isInMenu = null;
  
  private String url = null;

  private String iconName = Page.LOCAL_RESOURCE_PATH + "style/img/main/dot.gif";

  public String getIconName() {
    return iconName;
  }

  public void setIconName(String iconName) {
    this.iconName = iconName;
  }

  public String getIsAlwaysNew() {
    return isAlwaysNew;
  }

  public void setIsAlwaysNew(String isAlwaysNew) {
    this.isAlwaysNew = isAlwaysNew;
  }

  public String getIsGotoEdit() {
    return isGotoEdit;
  }

  public void setIsGotoEdit(String isGotoEdit) {
    this.isGotoEdit = isGotoEdit;
  }

  public String getParentCompo() {
    return parentCompo;
  }

  public void setParentCompo(String parentCompo) {
    this.parentCompo = parentCompo;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIsInMenu() {
    return isInMenu;
  }

  public void setIsInMenu(String isInMenu) {
    this.isInMenu = isInMenu;
  }

  public Node getOldNode(){
    return this.oldNode;
  }
  
  public void cloneOldNode(Node node){
    if(this.oldNode == null){
      this.oldNode = new MenuLeaf();
    }
    this.oldNode.setCode(node.getCode());
    this.oldNode.setName(node.getName());
    this.oldNode.setIndex(node.getIndex());
    if(node.getParent() != null){
      MenuFolder parent = new MenuFolder();
      parent.setCode(node.getParent().getCode());
      parent.setName(node.getParent().getName());
      parent.setIndex(node.getParent().getIndex());
      this.oldNode.setParent(parent);
    }
  }
  
  public String toHtml() {
    StringBuffer result = new StringBuffer();
    result.append("<span name='");
    result.append(code);
    result.append("'>");
    result.append(StringTools.createNbsp(level));
    result.append("<img height=16 src='");
    result.append(iconName);
    result.append("' width=16 border=0><span style='CURSOR:hand' name='");
    result.append(code);
    result.append("' id='");
    result.append(parent.getCode());
    result.append(code);
    result.append("ID'  class='menuFont' isgotoedit='");
    result.append(isGotoEdit);
    result.append("' isalwaysnew='");
    result.append(isAlwaysNew);
    result.append("' url=\"");
    result.append(url);
    result.append("\" parentcompo='");
    result.append(parentCompo);
    result.append("'");
    
    if (this.isAlwaysNew != null && this.isAlwaysNew.equals("Y")) {
      result.append(" onclick='clickLeaf(true);'>&nbsp;");
    } else {
      result.append(" onclick='clickLeaf(false);'>&nbsp;");
    }
    if(name == null || name.length() == 0)
      result.append(lr.getLang(code));
    else
      result.append(name);
    result.append("</span><br></span>");
    
    return result.toString();
  }
  
  public String toXml(){
    StringBuffer sb = new StringBuffer();
    sb.append("<leaf code=\"");
    sb.append(code);
    sb.append("\" name=\"");
    if(name == null || name.length() == 0)
      sb.append(lr.getLang(code));
    else
      sb.append(name);
    sb.append("\" is_goto_edit=\"");
    sb.append(isGotoEdit);
    sb.append("\" is_always_new=\"");
    sb.append(isAlwaysNew);
    sb.append("\" is_in_menu=\"");
    sb.append(isInMenu);    
    sb.append("\" ord_index=\"");
    sb.append(index);
    sb.append("\" url=\"");
    if(url != null)
      sb.append(url.replaceAll("[&]", "amp;"));
    sb.append("\" type=\"compo\">");
    sb.append("</leaf>\n");
    return sb.toString();
  }
}
