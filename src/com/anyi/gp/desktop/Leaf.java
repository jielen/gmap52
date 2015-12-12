package com.anyi.gp.desktop;

import java.util.List;

public abstract class Leaf implements Node{
  
  protected String code;
  
  protected String name;
  
  protected Node parent;
  
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
  
  public void setChildren(List children) {
    
  }
  
  public void addChild(Node child){
    
  }
  
  public List getChildren() {
    return null;
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
  
}
