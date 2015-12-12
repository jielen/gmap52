package com.anyi.gp.desktop;

import java.util.List;

public interface Node {
  
  public String getCode();
  
  public void setCode(String code);

  public String getName();
  
  public void setName(String name);
  
  public int getIndex();
  
  public void setIndex(int index);
  
  public Node getParent();

  public void setParent(Node parent);
  
  public List getChildren();
  
  public void setChildren(List children);
  
  public void addChild(Node child);
  
  public int getLevel();
  
  public void setLevel(int level);
  
  public String toHtml();
  
  public String toXml();
}
