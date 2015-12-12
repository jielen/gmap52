package com.anyi.gp.desktop.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.MenuTreeBuilder;
import com.anyi.gp.desktop.Tree;
import com.anyi.gp.desktop.TreeBuilder;
import com.anyi.gp.util.XMLTools;

/**
 * 
 * ≤Àµ•µº»Î
 * 
 */
public class ImpMenuInfoAction extends AjaxAction {

  private static final long serialVersionUID = -8585028327172080933L;

  private static final Logger log = Logger.getLogger(ImpMenuInfoAction.class);

  private BaseDao dao;

  private String root;

  private String pageId;

  private String pageTitle;
  
  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String doExecute() throws Exception {

    boolean bFromFile = true;
    String resultStr = "";
    String flag = "false";
    String FileName = root;
    File myFile = new File(FileName);
    if (!myFile.exists()) {
      bFromFile = false;
    }

    BufferedReader in = null;
    try {
      if (bFromFile) {
        in = new BufferedReader(new FileReader(myFile));
        String str;
        while ((str = in.readLine()) != null) {
          resultStr += str;
        }
      } else {
        resultStr = root;
      }
      saveToDB(resultStr);
      flag = "true";

    } catch (IOException e) {
      resultStr = e.getMessage();
    } finally {
      if (in != null)
        in.close();
    }

    this.resultstring = this.wrapResultStr(flag, resultStr);
    return SUCCESS;
  }

  public void saveToDB(String str){
    final String deleteApMenu = "delete from ap_menu m where m.parent_id in (select menu_id from ap_menu where parent_id = ?)"
      + "or m.parent_id = ? or m.menu_id = ?";
    final String deleteApMenuCompo = "delete from ap_menu_compo a where a.MENU_ID in (select menu_id from ap_menu menu"
      + " where menu.parent_id in (select menu_id from ap_menu where parent_id = ?)"
      + " or menu.parent_id = ?) or a.menu_id = ?";
    
    Object params1[] = { pageId, pageId, pageId };
    dao.executeBySql(deleteApMenuCompo, params1);
    dao.executeBySql(deleteApMenu, params1);
    
    Document doc = XMLTools.stringToDocument(str);
    doc.getDocumentElement().setAttribute("code", pageId);
    doc.getDocumentElement().setAttribute("name", pageTitle);
    doc.getDocumentElement().setAttribute("ord_index", "0");
    
    TreeBuilder builder = new MenuTreeBuilder();
    Tree tree = builder.generateTree(doc);
    builder.saveTree(tree, true);
  }
}
