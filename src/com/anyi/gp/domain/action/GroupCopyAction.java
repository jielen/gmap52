package com.anyi.gp.domain.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.domain.RightService;

public class GroupCopyAction extends AjaxAction {

  private static final long serialVersionUID = -8132690451092178519L;
  
  private RightService service;

  private String groupIdS;
  
  private String groupIdD;

  public String getGroupIdD() {
    return groupIdD;
  }

  public void setGroupIdD(String groupIdD) {
    this.groupIdD = groupIdD;
  }

  public String getGroupIdS() {
    return groupIdS;
  }

  public void setGroupIdS(String groupIdS) {
    this.groupIdS = groupIdS;
  }

  public RightService getService() {
    return service;
  }

  public void setService(RightService service) {
    this.service = service;
  }

  public String doExecute() throws Exception {

    String resultStr = "";
    String flag = "false";
    try {
      service.copyGroup(groupIdS,groupIdD);
      flag = "true";
      resultStr = "¸´ÖÆ³É¹¦!";
    } catch (Exception e) {
       resultStr = e.getMessage();
    }
    this.resultstring = this.wrapResultStr(flag, resultStr);
     
    return SUCCESS;
  
  }

}
