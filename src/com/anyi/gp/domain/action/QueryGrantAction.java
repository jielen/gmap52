package com.anyi.gp.domain.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.domain.GrantService;

public class QueryGrantAction  extends AjaxAction{
    
  private static final long serialVersionUID = 5855920660933030468L;

  private String userId;
  
  private GrantService service;
  
  public void setService(GrantService service){
    this.service = service;
  }
  
  public void setUserId(String userId){
    this.userId = userId;
  }
  
  public String doExecute() throws Exception {
    resultstring = service.queryGrantByGrantedUser(userId).toString();
    return SUCCESS;
  }

}
