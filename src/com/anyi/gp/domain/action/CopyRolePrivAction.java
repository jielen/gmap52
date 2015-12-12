package com.anyi.gp.domain.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.domain.RightService;

public class CopyRolePrivAction extends AjaxAction{

  private static final long serialVersionUID = 1L;

  private String roleIdS;
  
  private String roleIdD;
  
  private RightService service;
  
  public void setService(RightService service) {
    this.service = service;
  }

  public void setRoleIdD(String roleIdD) {
    this.roleIdD = roleIdD;
  }

  public void setRoleIdS(String roleIdS) {
    this.roleIdS = roleIdS;
  }

  public String doExecute() throws Exception {
    boolean result = service.roleCopy(roleIdS, roleIdD);
    if(result){
      resultstring = this.wrapResultStr("true", "权限复制成功！");
    }
    else{
      resultstring = this.wrapResultStr("false", "权限复制失败！");
    }
    return SUCCESS;
  }

}
