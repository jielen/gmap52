package com.anyi.gp.domain.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Delta;
import com.opensymphony.webwork.ServletActionContext;

public class SaveNumPrivAction extends SavePrivAction{

  private static final long serialVersionUID = 7109187477107600979L;

  private String compoId;
  
  private String funcId;
  
  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }

  public void setFuncId(String funcId) {
    this.funcId = funcId;
  }

  public String execute() throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    boolean result = false;
    
    if(userId != null && userId.trim().length() > 0){
      result = service.saveUserFieldRight(userId, funcId, compoId, new Delta(delta));
    }
    else if(roleId != null && roleId.trim().length() > 0){
      result = service.saveRoleFieldRight(roleId, funcId, compoId, new Delta(delta));
    }
    
    if(result)
      request.setAttribute("result", "保存成功！");
    else
      request.setAttribute("result", "保存失败！");
    
    return SUCCESS;
  }
}
