package com.anyi.gp.desktop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Folder implements Node{
  
  protected String code;
  
  protected String name;
  
  protected Node parent;
  
  protected List children = new ArrayList();

  protected int level;
  
  protected int index;
  
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }
  
  public List getChildren() {
    return children;
  }

  public void setChildren(List children) {
    this.children = children;
  }
  
  public void addChild(Node child){
    for(int i = 0; i < children.size(); i++){
      Node tmpNode = (Node)children.get(i);
      if(child.getCode().equals(tmpNode.getCode()))
        return;
    }
    this.children.add(child);
  }
  
  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void removeEmptyChild() {
    
    Iterator itera = children.iterator();
    while(itera.hasNext()){
      Object obj = itera.next();
      if(obj instanceof Folder){
        ((Folder)obj).removeEmptyChild();
        if(((Folder)obj).children.isEmpty()){
          itera.remove();
        }
      }
    }
    
  }
}
