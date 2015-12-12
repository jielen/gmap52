package com.anyi.gp.desktop.action;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.Pub;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.MenuFolder;
import com.anyi.gp.desktop.MenuLeaf;
import com.anyi.gp.util.XMLTools;

public class UpdateMenuTreeAction extends AjaxAction{

  private static final long serialVersionUID = 1L;

  private static Logger log = Logger.getLogger(UpdateMenuTreeAction.class);
  
  private static final String ACTION_INSERT = "insert";
  
  private static final String ACTION_DELETE = "delete";
  
  private static final String ACTION_UPDATE = "update";
  
  private static final String TYPE_MENU = "menu";
  
  private static final String TYPE_COMPO = "compo";
  
  private static final String SQL_INSERT_MENU = "gmap-priv.insertMenu";
  
  private static final String SQL_DELETE_MENU = "gmap-priv.deleteMenu";
  
  private static final String SQL_UPDATE_MENU = "gmap-priv.updateMenu";

  private static final String SQL_INSERT_COMPO = "gmap-priv.insertMenuCompo";
  
  private static final String SQL_DELETE_COMPO = "gmap-priv.deleteMenuCompo";
  
  private static final String SQL_DELETE_COMPO_WITH_MENU = "gmap-priv.deleteMenuCompoWithMenu";
  
  private static final String SQL_UPDATE_COMPO = "gmap-priv.updateMenuCompo";
  
  private String type;
  
  private String action;
  
  private String params;
    
  private com.anyi.gp.desktop.Node treeNode;
  
  private BaseDao dao;
  
  public String doExecute() throws Exception {
    try{
      if(ACTION_INSERT.equalsIgnoreCase(action))
        insertNode();
      else if(ACTION_DELETE.equalsIgnoreCase(action))
        deleteNode();
      else if(ACTION_UPDATE.equalsIgnoreCase(action))
        updateNode();
    }catch(SQLException e){
      log.error(e);
      resultstring = this.wrapResultStr("false", "²Ù×÷Ê§°Ü£¡");
      return SUCCESS;
    }

    resultstring = this.wrapResultStr("true", treeNode.getCode());
    
    return SUCCESS;
  }

  public void before(){
    Document doc = XMLTools.stringToDocument(params.replaceAll("[&]", "amp;"));
    if(doc != null){
        Node node = doc.getDocumentElement();
        NodeList titleNodes = node.getChildNodes();
        for(int j = 0; j < titleNodes.getLength(); j++){
          Node titleNode = titleNodes.item(j);
          if(titleNode.getNodeType() == Node.ELEMENT_NODE){
            if(TYPE_COMPO.equalsIgnoreCase(type)){
              MenuLeaf tmpNode = new MenuLeaf();
              tmpNode.setCode(XMLTools.getNodeAttr(titleNode, "compo_id"));
              tmpNode.setName(XMLTools.getNodeAttr(titleNode, "compo_name"));
              tmpNode.setIndex(Pub.parseInt(XMLTools.getNodeAttr(titleNode, "ord_index")));
              tmpNode.setIsAlwaysNew(XMLTools.getNodeAttr(titleNode, "is_always_new"));
              tmpNode.setIsGotoEdit(XMLTools.getNodeAttr(titleNode, "is_goto_edit"));
              tmpNode.setIsInMenu(XMLTools.getNodeAttr(titleNode, "is_in_menu"));
              tmpNode.setUrl(XMLTools.getNodeAttr(titleNode, "url"));
              com.anyi.gp.desktop.Node parent = new com.anyi.gp.desktop.MenuFolder();
              parent.setCode(XMLTools.getNodeAttr(titleNode, "menu_id"));
              tmpNode.setParent(parent);
              treeNode = tmpNode;
            }
            else if(TYPE_MENU.equalsIgnoreCase(type)){
              treeNode = new MenuFolder();
              treeNode.setCode(XMLTools.getNodeAttr(titleNode, "menu_id"));
              treeNode.setName(XMLTools.getNodeAttr(titleNode, "menu_name"));
              treeNode.setIndex(Pub.parseInt(XMLTools.getNodeAttr(titleNode, "ord_index")));
              com.anyi.gp.desktop.Node parent = new com.anyi.gp.desktop.MenuFolder();
              parent.setCode(XMLTools.getNodeAttr(titleNode, "parent_id"));
              treeNode.setParent(parent);
            }
          }
        }
      }
  }
  
  private void insertNode() throws SQLException{
    if(treeNode.getCode() == null || treeNode.getCode().length() == 0){
      treeNode.setCode(Pub.getUID());
    }
    
    if(TYPE_MENU.equalsIgnoreCase(type)){
      dao.insert(SQL_INSERT_MENU, treeNode);
    }
    else if(TYPE_COMPO.equalsIgnoreCase(type)){
      dao.insert(SQL_INSERT_COMPO, treeNode);
    }
  }
  
  private void deleteNode() throws SQLException{
    if(TYPE_MENU.equalsIgnoreCase(type)){
      dao.delete(SQL_DELETE_COMPO_WITH_MENU, treeNode.getCode());
      dao.delete(SQL_DELETE_MENU, treeNode);
    }
    else if(TYPE_COMPO.equalsIgnoreCase(type)){
      dao.delete(SQL_DELETE_COMPO, treeNode);
    }
  }
  
  private void updateNode() throws SQLException{
    if(TYPE_MENU.equalsIgnoreCase(type)){
      dao.update(SQL_UPDATE_MENU, treeNode);
    }
    else if(TYPE_COMPO.equalsIgnoreCase(type)){
      dao.update(SQL_UPDATE_COMPO, treeNode);
    }
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public BaseDao getDao() {
    return dao;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
}
