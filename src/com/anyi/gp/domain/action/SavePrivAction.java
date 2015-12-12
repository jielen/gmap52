package com.anyi.gp.domain.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Delta;
import com.anyi.gp.domain.RightService;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class SavePrivAction extends ActionSupport {
  private static final long serialVersionUID = -4348586858572003805L;

  protected String roleId;

  protected String userId;

  protected String delta;

  protected RightService service;

  public void setDelta(String delta) {
    this.delta = delta;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setService(RightService service) {
    this.service = service;
  }

  public String execute() throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    boolean result = false;
    
    if(userId != null && userId.trim().length() > 0){
      result = service.saveUserPriv(userId, new Delta(delta));
    }
    else if(roleId != null && roleId.trim().length() > 0){
      result = service.saveRolePriv(roleId, new Delta(delta));
    }
    if(result)
      request.setAttribute("result", "保存成功！");
    else
      request.setAttribute("result", "保存失败！");
    
    return SUCCESS;
  }

}
