package com.anyi.gp.desktop;

import java.util.Iterator;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

public class MenuFolder extends Folder{
  
  private static LangResource lr = LangResource.getInstance();
  
  private String iconName = Page.LOCAL_RESOURCE_PATH + "style/img/main/plus.gif";

  private Node oldNode = null;

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
    if(level == 0){
      result.append("<span id='");
      result.append(code);
      result.append("Span' class='menuFont' style='display:block'>");
      result.append("<span id=");
      result.append(code);
      result.append("Child style='display:block'>");
    }else{
      result.append("<span id='");
      result.append(code);
      result.append("Span' class='menuFont'>");
      result.append(StringTools.createNbsp(level));
      result.append("<img name='");
      result.append(code);
      result.append("' style='CURSOR:hand' height=16 ");
      result.append("src='" + iconName + "' width=16 border=0 onclick='");
      result.append("openBranch();'>&nbsp;");
      result.append("<font style='CURSOR:hand' name='");
      result.append(code);
      result.append("_txt' onclick='openBranch();' >");
      if(name == null || name.length() == 0)
        result.append(lr.getLang(code));
      else
        result.append(name);
      result.append("</font> <br> ");
      result.append("<span id=");
      result.append(code);
      result.append("Child style='DISPLAY: none'>");  
    }
    
    Node node = null;
    for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
      node = (Node) iter.next();
      node.setLevel(level + 1);
      result.append(node.toHtml());
    }
    result.append("</span></span>");

    return result.toString();
  }
  
  public String toXml(){
    StringBuffer sb = new StringBuffer();
    sb.append("<folder code=\"");
    sb.append(code);
    sb.append("\" name=\"");
    if(name == null || name.length() == 0)
      sb.append(lr.getLang(code));
    else
      sb.append(name);
    sb.append("\" ord_index=\"");
    sb.append(index);
    sb.append("\" type=\"menu\">\n");
    
    Node node = null;
    for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
      node = (Node) iter.next();
      sb.append(node.toXml());
    }
    
    sb.append("</folder>\n");
    return sb.toString();
  }
}
