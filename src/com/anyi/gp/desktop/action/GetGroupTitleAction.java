package com.anyi.gp.desktop.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.desktop.TitleControl;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

public class GetGroupTitleAction extends AjaxAction{

  private static final long serialVersionUID = 4163587994263335246L;

  private String groupId;
  
  private String coCode;
  
  private String orgCode;
  
  private String userId;
  
  private boolean isHtml;
  
  public void setIsHtml(boolean isHtml) {
    this.isHtml = isHtml;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public void setHtml(boolean isHtml) {
    this.isHtml = isHtml;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setCoCode(String coCode) {
    this.coCode = coCode;
  }

  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }

  public String doExecute() throws Exception {
    TitleControl control = new TitleControl();
    control.setGroupId(groupId);
    control.setUserId(userId);
    control.setCoCode(coCode);
    control.setOrgCode(orgCode);
    HttpServletRequest request = ServletActionContext.getRequest();
    control.setPosiCode((String)SessionUtils.getAttribute(request,"svPoCode"));
    if(isHtml)
      resultstring = control.getTitleHtml();
    else{
      resultstring = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";
      resultstring += control.getTitleXml();
    }
    
    return SUCCESS;
  }

}
