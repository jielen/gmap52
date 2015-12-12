package com.anyi.gp.desktop;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.anyi.gp.Pub;

public abstract class AbstractTreeBuilder implements TreeBuilder{

  public Tree generateTree(Document document) {
    Tree tree = new Tree();
    
    Element root = document.getDocumentElement();
    com.anyi.gp.desktop.Node menuRoot = new MenuFolder();
    menuRoot.setCode(root.getAttributes().getNamedItem("code").getNodeValue());
    menuRoot.setName(root.getAttributes().getNamedItem("name").getNodeValue());
    menuRoot.setIndex(Pub.parseInt(root.getAttributes().getNamedItem("ord_index").getNodeValue()));
    menuRoot.setParent(null);
    
    _generateTree(menuRoot, root);
    tree.setRoot(menuRoot);
    
    return tree;
  }

  private void _generateTree(com.anyi.gp.desktop.Node menuNode, org.w3c.dom.Node node){
    if(node == null){
      return;
    }
        
    NodeList children = node.getChildNodes();
    if(children == null || children.getLength() == 0){
      return;
    }
    
    for(int i = 0; i < children.getLength(); i++){
      com.anyi.gp.desktop.Node newMenuNode = null;
      org.w3c.dom.Node child = children.item(i);
      org.w3c.dom.NamedNodeMap namedNodeMap = child.getAttributes();
      if(namedNodeMap == null) continue;
      
      if("menu".equals(namedNodeMap.getNamedItem("type").getNodeValue())){
        newMenuNode = new MenuFolder();
      }else{
        newMenuNode = new MenuLeaf();
        String isAlwaysNew = namedNodeMap.getNamedItem("is_always_new").getNodeValue();
        String isGotoEdit = namedNodeMap.getNamedItem("is_goto_edit").getNodeValue();
        String isInMenu = namedNodeMap.getNamedItem("is_in_menu").getNodeValue();
        ((MenuLeaf)newMenuNode).setIsAlwaysNew("null".equalsIgnoreCase(isAlwaysNew) ? "" : isAlwaysNew);
        ((MenuLeaf)newMenuNode).setIsGotoEdit("null".equalsIgnoreCase(isGotoEdit) ? "" : isGotoEdit);
        ((MenuLeaf)newMenuNode).setIsInMenu("null".equalsIgnoreCase(isInMenu) ? "" : isInMenu);
        ((MenuLeaf)newMenuNode).setUrl(namedNodeMap.getNamedItem("url").getNodeValue());
      }
      newMenuNode.setCode(namedNodeMap.getNamedItem("code").getNodeValue());
      newMenuNode.setName(namedNodeMap.getNamedItem("name").getNodeValue());
      newMenuNode.setIndex(Pub.parseInt(namedNodeMap.getNamedItem("ord_index").getNodeValue()));
      
      newMenuNode.setParent(menuNode);
      menuNode.addChild(newMenuNode);
      
      _generateTree(newMenuNode, child);
    }
  }
  
}
