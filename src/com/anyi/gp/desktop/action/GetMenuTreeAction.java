package com.anyi.gp.desktop.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.desktop.Tree;
import com.anyi.gp.desktop.TreeBuilder;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

public class GetMenuTreeAction extends AjaxAction{

  private static final long serialVersionUID = -1976938265431779966L;
  
  private String pageId;
  
  private boolean isHtml;
  
  private boolean isRemoveEmpty;
  
  private boolean isOnlyInMenu;
  
  private String userId;
  
  private String coCode;
  
  private String orgCode;
  
  private TreeBuilder builder;
  
  public GetMenuTreeAction(TreeBuilder builder){
    this.builder = builder;
  }

  public String getPageId() {
    return pageId;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public void setBuilder(TreeBuilder builder) {
    this.builder = builder;
  }

  public void setIsHtml(boolean isHtml) {
    this.isHtml = isHtml;
  }

  public void setIsOnlyInMenu(boolean isOnlyInMenu) {
    this.isOnlyInMenu = isOnlyInMenu;
  }
  
  public void setIsRemoveEmpty(boolean isRemoveEmpty) {
    this.isRemoveEmpty = isRemoveEmpty;
  }

  public void setCoCode(String coCode) {
    this.coCode = coCode;
  }

  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String doExecute() throws Exception {
    if(isHtml)
      resultstring = getMenuString();
    else{
      resultstring = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";
      resultstring += getMenuString();
    }
    return SUCCESS;
  }
  
  private String getMenuString() throws BusinessException{
    StringBuffer sb = new StringBuffer();
    Tree tree = getTree();
    if(isHtml)
      sb.append(tree.toHtml());
    else
      sb.append(tree.toXml());
    
    return sb.toString();
  }
  
  private Tree getTree() throws BusinessException{
    
    Map params = new HashMap();
    params.put("rootCode", pageId);
    params.put("userId", userId);
    params.put("isOnlyInMenu", isOnlyInMenu + "");
    params.put("coCode", coCode == null ? "" : coCode);
    params.put("orgCode", orgCode == null ? "" : orgCode);
    params.put("isRemoveEmpty", isRemoveEmpty + "");
    HttpServletRequest request = ServletActionContext.getRequest();
    params.put("posiCode", SessionUtils.getAttribute(request, "svPoCode"));//增加职位权限过滤
    return builder.generateTree(params);
  }
}
