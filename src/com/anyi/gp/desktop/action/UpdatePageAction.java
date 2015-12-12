package com.anyi.gp.desktop.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.MenuFolder;
import com.anyi.gp.desktop.Title;
import com.anyi.gp.util.XMLTools;

public class UpdatePageAction extends AjaxAction{

  private static final long serialVersionUID = -5916918410018407122L;
  
  private static Logger log = Logger.getLogger(UpdatePageAction.class);
  
  private static final String ACTION_INSERT = "insert";
  
  private static final String ACTION_DELETE = "delete";
  
  private static final String ACTION_UPDATE = "update";
  
  private static final String SQL_INSERT = "gmap-priv.insertPage";
  
  private static final String SQL_INSERT_MENU = "gmap-priv.insertMenu";
  
  private static final String SQL_DELETE = "gmap-priv.deletePage";
  
  private static final String SQL_DELETE_MENU_WITH_PAGE = "gmap-priv.deleteMenuWithPage";
  
  private static final String SQL_DELETE_COMPO_WITH_MENU = "gmap-priv.deleteMenuCompoWithMenu";
  
  private static final String SQL_UPDATE = "gmap-priv.updatePage";
  
  private String action;
  
  private String params;
    
  private List titleList = new ArrayList();
  
  private BaseDao dao;
  
  public String doExecute() throws Exception {
    try{
      if(ACTION_INSERT.equalsIgnoreCase(action))
        insertPage();
      else if(ACTION_DELETE.equalsIgnoreCase(action))
        deletePage();
      else if(ACTION_UPDATE.equalsIgnoreCase(action))
        updatePage();
    }catch(SQLException e){
      log.error(e);
      resultstring = this.wrapResultStr("false", "²Ù×÷Ê§°Ü£¡");
    }
    
    resultstring = "";
    for(int i = 0; i < titleList.size(); i++){
      Title title = (Title)titleList.get(i);
      resultstring += title.getTitleId() + ",";
    }
    resultstring = this.wrapResultStr("true", resultstring.substring(0, resultstring.length() - 1));
    
    return SUCCESS;
  }

  public void before(){
    Document doc = XMLTools.stringToDocument(params.replaceAll("[&]", "amp;"));
    if(doc != null){
        Node node = doc.getDocumentElement();
        NodeList titleNodes = node.getChildNodes();
        for(int j = 0; j < titleNodes.getLength(); j++){
          Node titleNode = titleNodes.item(j);
          if(titleNode.getNodeType() == Node.ELEMENT_NODE)
            titleList.add(new Title(titleNode));
        }
      }
  }
  
  private void insertPage() throws SQLException{
    for(int i = 0; i < titleList.size(); i++){
      Title title = (Title)titleList.get(i);
      MenuFolder folder = new MenuFolder();
      folder.setCode(title.getTitleId());
      folder.setIndex(0);
      folder.setName(title.getTitleName());
      MenuFolder parent = new MenuFolder();
      parent.setCode("");
      folder.setParent(parent);
      dao.insert(SQL_INSERT_MENU, folder);
      dao.insert(SQL_INSERT, title);
    }
  }
  
  private void deletePage() throws SQLException{
    for(int i = 0; i < titleList.size(); i++){
      Title title = (Title)titleList.get(i);
      dao.delete(SQL_DELETE_COMPO_WITH_MENU, title.getTitleId());
      dao.delete(SQL_DELETE_MENU_WITH_PAGE, title.getTitleId());
      dao.delete(SQL_DELETE, title);
    }
  }
  
  private void updatePage() throws SQLException{
    for(int i = 0; i < titleList.size(); i++){
      Title title = (Title)titleList.get(i);
      dao.update(SQL_UPDATE, title);
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
  
}
