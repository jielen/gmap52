package com.anyi.gp.desktop;

import java.util.ArrayList;
import java.util.List;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;


public class Tree {
  
  public Node root;

  public Node getRoot() {
    return root;
  }

  public void setRoot(Node root) {
    this.root = root;
  }
  
  public String toHtml(){
    
    StringBuffer sb = new StringBuffer();
    List children = root.getChildren();
    for(int i = 0; i < children.size(); i++){
      Node child = (Node)children.get(i);
      child.setLevel(root.getLevel() + 1);
      sb.append(child.toHtml());
    }
    return sb.toString();
  }
  
  public String toXml(){
    StringBuffer sb = new StringBuffer("<root>\n");
    List children = root.getChildren();
    for(int i = 0; i < children.size(); i++){
      Node child = (Node)children.get(i);
      sb.append(child.toXml());
    }
    sb.append("</root>\n");
    return sb.toString();
  }
  
  public Delta toDelta(){
    Delta delta = new Delta();
    List children = root.getChildren();
    for(int i = 0; i < children.size(); i++){
      Node child = (Node) children.get(i);
      addToDelta(delta, child, false);
    }
    return delta;
  }
  
  public void addToDelta(Delta delta, Node node, boolean onlyMenu){
    if(node == null)
      return;
    
    if((!onlyMenu) || (onlyMenu && node instanceof Folder)){
      TableData data = new TableData();
      data.setField("CODE", node.getCode());
      data.setField("NAME", node.getName());
      if(node.getParent() != null)
        data.setField("P_CODE", node.getParent().getCode());
      else
        data.setField("P_CODE", "");
      delta.add(data);
    }
    
    List children = node.getChildren();
    if(children != null){
      for(int i = 0; i < children.size(); i++){
        Node child = (Node) children.get(i);
        addToDelta(delta, child, onlyMenu);
      }
    }
  }
  
  public List getAllNodes(){
    List result = new ArrayList();
    getAllNodes(root, result);
    return result;
  }
  
  private void getAllNodes(Node node, List result){
    if(node == null) return ;
    
    result.add(node.getCode());
    
    List children = node.getChildren();
    if(children != null){
      for(int i = 0; i < children.size(); i++){
        getAllNodes((Node)children.get(i), result);
      }
    }
  }
}