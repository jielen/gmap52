package com.anyi.gp.desktop;

import org.w3c.dom.Document;

import com.anyi.gp.BusinessException;

public interface TreeBuilder {

  public Tree generateTree(Object params) throws BusinessException;
  
  public Tree generateTree(Document document);
  
  public void saveTree(Tree tree, boolean saveToNew);
  
}
